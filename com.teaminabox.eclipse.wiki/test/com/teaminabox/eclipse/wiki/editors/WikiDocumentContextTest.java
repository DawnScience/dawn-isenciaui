package com.teaminabox.eclipse.wiki.editors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IResource;
import org.junit.Before;
import org.junit.*;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiTest;
import com.teaminabox.eclipse.wiki.preferences.WikiPreferences;
import com.teaminabox.eclipse.wiki.text.WikiLinkTextRegion;
import com.teaminabox.eclipse.wiki.text.WikiWordTextRegion;

public final class WikiDocumentContextTest extends WikiTest {

	private static final String	WIKIDOC	= "WikiDoc";
	private WikiDocumentContext	context;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		WikiBrowserEditor editor = createWikiDocumentAndOpen("", WikiDocumentContextTest.WIKIDOC + WikiConstants.WIKI_FILE_EXTENSION);
		context = editor.getEditor().getContext();
	}

	@Test
	public void testGetWorkingLocation() {
		assertEquals(getJavaProject().getResource(), context.getWorkingLocation());
	}

	@Test
	public void testGetFileForWikiNameWithNoFile() {
		context.getFileForWikiName("FooBar");
		IResource resource = getJavaProject().getProject().findMember("FooBar" + WikiConstants.WIKI_FILE_EXTENSION);
		assertTrue(resource == null);
	}

	@Test
	public void testGetFileForWikiName() {
		create("", "FooBar" + WikiConstants.WIKI_FILE_EXTENSION);
		context.getFileForWikiName("FooBar");
		IResource resource = getJavaProject().getProject().findMember("FooBar" + WikiConstants.WIKI_FILE_EXTENSION);
		assertTrue(resource != null && resource.exists());
	}

	@Test
	public void testGetWikiNameBeingEdited() {
		assertEquals(WikiDocumentContextTest.WIKIDOC, context.getWikiNameBeingEdited());
	}

	@Test
	public void testHasWikiSiblingWhenFalse() {
		WikiLinkTextRegion region = new WikiWordTextRegion("FooBar");
		assertFalse(context.hasWikiSibling(region));
	}

	@Test
	public void testHasWikiSiblingWhenTrue() {
		create("", "FooBar" + WikiConstants.WIKI_FILE_EXTENSION);
		WikiLinkTextRegion region = new WikiWordTextRegion("FooBar");
		assertTrue(context.hasWikiSibling(region));
	}

	@Test
	public void testGetWikiSpaceWithNoLocalSpace() {
		assertEquals(WikiPreferences.getWikiSpace().size(), context.getWikiSpace().size());
		assertTrue(WikiPreferences.getWikiSpace().keySet().containsAll(context.getWikiSpace().keySet()));
	}

	@Test
	public void testGetWikiSpaceWithLocalSpace() {
		create("A=B", "wikispace.properties");
		assertEquals(WikiPreferences.getWikiSpace().size() + 1, context.getWikiSpace().size());
		assertTrue(context.getWikiSpace().keySet().containsAll(WikiPreferences.getWikiSpace().keySet()));
		assertTrue(context.getWikiSpace().containsKey("A"));
	}

	@Test
	public void testGetWikiSpaceLinkWithNoLocalSpace() {
		assertTrue(context.getWikiSpaceLink("nothing there") == null);
	}

	@Test
	public void testGetWikiSpaceLinkWithLocalSpace() {
		create("A=B", "wikispace.properties");
		assertEquals("B", context.getWikiSpaceLink("A"));
	}

	@Test
	public void testIsExcluded() {
		create("IgnoreMe", "wiki.exclude");
		assertTrue(context.isExcluded("IgnoreMe"));
	}

}
