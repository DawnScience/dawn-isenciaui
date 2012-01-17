package com.teaminabox.eclipse.wiki.renderer;

import static org.junit.Assert.assertEquals;

import org.junit.*;

public final class TwikiBrowserContentRendererTest extends AbstractContentRendererTest {

	@Test
	public void testNumberedOrderedList() {
		String markup = "   1. first\n   1. second\n      1. second, first\n   1. third";
		String expected = "<ol type=\"1\"><li>first</li><li>second</li><ol type=\"1\"><li>second, first</li></ol><li>third</li></ol>";
		assertRenderedContains(markup, expected);
	}

	protected AbstractContentRenderer getRenderer() {
		return new TwikiBrowserContentRenderer();
	}

	@Test
	public void testUpperCaseOrderedList() {
		String markup = "   A. first\n   A. second\n      A. second, first\n   A. third";
		String expected = "<ol type=\"A\"><li>first</li><li>second</li><ol type=\"A\"><li>second, first</li></ol><li>third</li></ol>";
		assertRenderedContains(markup, expected);
	}

	@Test
	public void testLowerCaseOrderedList() {
		String markup = "   a. first\n   a. second\n      a. second, first\n   a. third";
		String expected = "<ol type=\"a\"><li>first</li><li>second</li><ol type=\"a\"><li>second, first</li></ol><li>third</li></ol>";
		assertRenderedContains(markup, expected);
	}

	@Test
	public void testUpperRomanOrderedList() {
		String markup = "   I. first\n   I. second\n      I. second, first\n   I. third";
		String expected = "<ol type=\"I\"><li>first</li><li>second</li><ol type=\"I\"><li>second, first</li></ol><li>third</li></ol>";
		assertRenderedContains(markup, expected);
	}

	@Test
	public void testLowerCaseRomanOrderedList() {
		String markup = "   i. first\n   i. second\n      i. second, first\n   i. third";
		String expected = "<ol type=\"i\"><li>first</li><li>second</li><ol type=\"i\"><li>second, first</li></ol><li>third</li></ol>";
		assertRenderedContains(markup, expected);
	}

	@Test
	public void testEmptyHeader() {
		String markup = "---++";
		String expected = "<h2>---++</h2>";
		assertRenderedContains(markup, expected);
	}

	@Test
	public void testBold() {
		String markup = "*foo*";
		String expected = "<strong>foo</strong>";
		assertRenderedContains(markup, expected);
	}

	@Test
	public void testMultiBold() {
		String markup = "*foo*bar*";
		String expected = "<strong>foo*bar</strong>";
		assertRenderedContains(markup, expected);
	}

	@Test
	public void testUnderscore() {
		AbstractContentRenderer renderer = getRenderer();
		assertEquals("single underscore", "_foo", renderer.processTags("_foo"));
	}

	@Test
	public void testItalic() {
		String markup = "_foo bar_";
		String expected = "<em>foo bar</em>";
		assertRenderedContains(markup, expected);
	}

	@Test
	public void testMultiItalic() {
		String markup = "_foo bar_blah_";
		String expected = "<em>foo bar_blah</em>";
		assertRenderedContains(markup, expected);
	}

	@Test
	public void testBoldItalic() {
		String markup = "__foo__";
		String expected = "<strong><em>foo</em></strong>";
		assertRenderedContains(markup, expected);
	}

	@Test
	public void testBoldCode() {
		String markup = "==foo==";
		String expected = "<code><b>foo</b></code>";
		assertRenderedContains(markup, expected);
	}

	@Test
	public void testMultiBoldCode() {
		String markup = "==foo==bar==";
		String expected = "<code><b>foo==bar</b></code>";
		assertRenderedContains(markup, expected);
	}
}
