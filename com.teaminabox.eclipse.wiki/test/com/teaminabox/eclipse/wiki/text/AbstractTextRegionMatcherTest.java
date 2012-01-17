package com.teaminabox.eclipse.wiki.text;

import junit.framework.Assert;

import org.junit.*;

import com.teaminabox.eclipse.wiki.WikiTest;
import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;
import com.teaminabox.eclipse.wiki.editors.WikiEditor;

public abstract class AbstractTextRegionMatcherTest extends WikiTest {

	protected static final String	DEFAULT_EDITOR_CONTENT	= "Test editor content.";

	private WikiEditor				editor;
	private TextRegionMatcher		matcher;

	private WikiDocumentContext		context;

	public WikiDocumentContext getContext() {
		return context;
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		editor = createWikiDocumentAndOpen(AbstractTextRegionMatcherTest.DEFAULT_EDITOR_CONTENT).getEditor();
		context = editor.getContext();
		matcher = getMatcher();
	}

	protected abstract TextRegionMatcher getMatcher();

	@Test
	public void testAcceptsWhenTrue() {
		TextRegionTestBean[] cases = getAcceptableCases();
		for (int i = 0; i < cases.length; i++) {
			TextRegion textRegion = matcher.createTextRegion(cases[i].getText(), context);
			Assert.assertNotNull(cases[i].getText(), textRegion);
			Assert.assertEquals(textRegion, cases[i].getTextRegion());
		}
	}

	protected abstract TextRegionTestBean[] getAcceptableCases();

	@Test
	public void testAcceptsWhenFalse() {
		String[] text = getUnacceptableText();
		for (int i = 0; i < text.length; i++) {
			Assert.assertTrue(text[i], matcher.createTextRegion(text[i], context) == null);
		}
	}

	protected abstract String[] getUnacceptableText();

	@Test
	public void testCreateTextRegion() {
		TextRegionTestBean[] cases = getAcceptableCases();

		for (int i = 0; i < cases.length; i++) {
			TextRegion returned = matcher.createTextRegion(cases[i].getText(), context);
			Assert.assertEquals(cases[i].getText(), cases[i].getTextRegion(), returned);
		}
	}

}
