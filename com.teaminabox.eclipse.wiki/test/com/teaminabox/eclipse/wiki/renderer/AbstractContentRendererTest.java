package com.teaminabox.eclipse.wiki.renderer;

import static com.teaminabox.eclipse.wiki.WikiPlugin.wikiPlugin;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiTest;
import com.teaminabox.eclipse.wiki.editors.WikiBrowserEditor;
import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;

public abstract class AbstractContentRendererTest extends WikiTest {

	private static final String	CLASS_SOURCE	= "package com.teaminabox.foo;\npublic class BigClass { class InnerClass {} }";
	private static final String	CLASS_NAME		= "com.teaminabox.foo.BigClass";
	private static final String	CLASS_FILE_NAME	= AbstractContentRendererTest.CLASS_NAME.replaceAll("\\.", "/") + ".java";

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		create(AbstractContentRendererTest.CLASS_SOURCE, CLASS_FILE_NAME);
	}

	protected abstract AbstractContentRenderer getRenderer();

	@Test
	public void testReplacePair() {
		AbstractContentRenderer renderer = getRenderer();
		assertEquals("XfooY", renderer.replacePair("|||foo|||", "|||", "X", "Y"));
		assertEquals("XfooY ", renderer.replacePair("|||foo||| ", "|||", "X", "Y"));
		assertEquals(" XfooY ", renderer.replacePair(" |||foo||| ", "|||", "X", "Y"));

		assertEquals("_foo", renderer.replacePair("_foo", "_", "X", "Y"));
		assertEquals("foo_", renderer.replacePair("foo_", "_", "X", "Y"));
		assertEquals("foo", renderer.replacePair("foo", "_", "X", "Y"));
		assertEquals("XfooY", renderer.replacePair("*foo*", "*", "X", "Y"));
		assertEquals("*", renderer.replacePair("*", "*", "X", "Y"));
		assertEquals("", renderer.replacePair("", "*", "X", "Y"));
	}

	@Test
	public void testFunctional() throws IOException {
		String functionalTest = getFunctionalTestFileName();
		String wikiDocument = functionalTest + ".wiki";
		String content = load(wikiDocument);
		content = content.replaceAll("TEST_PROJECT_NAME", getJavaProject().getElementName());
		WikiBrowserEditor editor = createWikiDocumentAndOpen(content, wikiDocument);

		String expected = load(functionalTest + ".expected");
		expected = expected.replaceAll("TEST_PROJECT_NAME", getJavaProject().getElementName());

		create(load("EmbeddedContent.wiki"), "EmbeddedContent.wiki");

		wikiPlugin().getPluginPreferences().setValue(WikiConstants.BROWSER_RENDERER, getRenderer().getClass().getName());
		WikiDocumentContext context = editor.getEditor().getContext();

		String html = getRenderer().render(context, new IdeLinkMaker(context), false);
		assertEquals(expected, convertWindowsHtmlToMac(html));
	}

	private String convertWindowsHtmlToMac(String actual) {
		actual = actual.replaceFirst("windows-1252", "MacRoman");
		actual = actual.replaceAll("\\x0D\\x0A", "\n");
		return actual;
	}

	protected final String getFunctionalTestFileName() {
		String name = getClass().getName();
		return name.substring(name.lastIndexOf('.') + 1);
	}

	@Test
	public void testSplit() {
		assertTrue(Arrays.equals(new String[] { "a", "b", "", "c", "d" }, getRenderer().split("|a|b||c|d|", "|")));
	}

	protected void assertRenderedContains(String markup, String fragment) {
		String html = getHtml(markup);
		if (html.indexOf(fragment) < 0) {
			// this assert will always fail, but this way JUnit Runner will show us the difference
			assertEquals(html, fragment);
		}
	}

	protected String getHtml(String text) {
		WikiBrowserEditor editor = createWikiDocumentAndOpen(text);
		WikiDocumentContext context = editor.getEditor().getContext();
		String html = getRenderer().render(context, new IdeLinkMaker(context), false);
		// \n doesn't work on HTML rendered in Windows, so we need to filter out CR & LF chars separately
		return html.replaceAll("[\\x0A|\\x0D|\\n]", "");
	}
}
