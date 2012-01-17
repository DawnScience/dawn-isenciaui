package com.teaminabox.eclipse.wiki.renderer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.*;

public final class WardsBrowserContentRendererTest extends AbstractContentRendererTest {

	protected AbstractContentRenderer getRenderer() {
		return new WardsBrowserContentRenderer();
	}

	@Test
	public void testIsHeaderWithHeader() {
		assertTrue(getRenderer().isHeader("'''foo'''"));
	}

	@Test
	public void testIsHeaderWithHeaderMarkupFollowedByText() {
		assertFalse(getRenderer().isHeader("'''foo'''" + " foo"));
	}

	@Test
	public void testIsHeaderWithEmphasis() {
		assertFalse(getRenderer().isHeader("'''''foo'''''"));
	}

	/**
	 * Test for [ 1089118 ] WardsWiki: markup in quote section is ignored.
	 */
	@Test
	public void testQuoteWithMarkup() {
		String markup = "\t :\t'''foo'''";
		String expected = "<p class=\"quote\"><b>foo</b></p>";
		assertRenderedContains(markup, expected);
	}

	@Test
	public void testWhiteSpacePreservedInMonospace() {
		String markup = " foo bar  ";
		String expected = "<pre class=\"monospace\"> foo bar  </pre>";
		assertRenderedContains(markup, expected);
	}

}
