package com.teaminabox.eclipse.wiki.renderer;

import static org.junit.Assert.assertTrue;

import org.junit.*;

public final class SnipSnapContentRendererTest extends AbstractContentRendererTest {

	@Test
	public void testIsHeader() {
		assertTrue(getRenderer().isHeader("1 Heading"));
	}

	protected AbstractContentRenderer getRenderer() {
		return new SnipSnapContentRenderer();
	}

	@Test
	public void testIsSubHeader() {
		assertTrue(getRenderer().isHeader("1.1 Heading"));
	}

	@Test
	public void testNumberedOrderedList() {
		String markup = "1. first\n1. second\n1. third";
		String expected = "<ol type=\"1\"><li>first</li><li>second</li><li>third</li></ol>";
		assertRenderedContains(markup, expected);
	}

	@Test
	public void testUpperCaseOrderedList() {
		String markup = "A. first\nA. second\nA. third";
		String expected = "<ol type=\"A\"><li>first</li><li>second</li><li>third</li></ol>";
		assertRenderedContains(markup, expected);
	}

	@Test
	public void testLowerCaseOrderedList() {
		String markup = "a. first\na. second\na. third";
		String expected = "<ol type=\"a\"><li>first</li><li>second</li><li>third</li></ol>";
		assertRenderedContains(markup, expected);
	}

	@Test
	public void testUpperRomanOrderedList() {
		String markup = "I. first\nI. second\nI. third";
		String expected = "<ol type=\"I\"><li>first</li><li>second</li><li>third</li></ol>";
		assertRenderedContains(markup, expected);
	}

	@Test
	public void testLowerCaseRomanOrderedList() {
		String markup = "i. first\ni. second\ni. third";
		String expected = "<ol type=\"i\"><li>first</li><li>second</li><li>third</li></ol>";
		assertRenderedContains(markup, expected);
	}

	@Test
	public void testTableMacro() {
		String markup = "{table}\nfoo|bar\none|two\n{table}";
		String expected = getRenderer().getTableTag() + "<tr><th>foo</th><th>bar</th></tr><tr><td>one</td><td>two</td></tr></table>";
		assertRenderedContains(markup, expected);
	}

	@Test
	public void testTableMacroEmptyCells() {
		String markup = "{table}\nfoo|bar|blah\none||three\n{table}";
		String expected = getRenderer().getTableTag() + "<tr><th>foo</th><th>bar</th><th>blah</th></tr><tr><td>one</td><td></td><td>three</td></tr></table>";
		assertRenderedContains(markup, expected);
	}
}
