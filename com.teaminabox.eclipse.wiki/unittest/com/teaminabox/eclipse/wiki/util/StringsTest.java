package com.teaminabox.eclipse.wiki.util;

import static com.teaminabox.eclipse.wiki.util.Strings.deCamelCase;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringsTest {

	@Test
	public void testIsWhiteSpaceCharacter() {
		assertTrue("\\n", Strings.isWhiteSpaceCharacter('\n'));
		assertTrue("space", Strings.isWhiteSpaceCharacter(' '));
		assertTrue("tab", Strings.isWhiteSpaceCharacter('\t'));
		assertTrue("\\r", Strings.isWhiteSpaceCharacter('\r'));
		assertFalse("non whitespace", Strings.isWhiteSpaceCharacter('a'));
	}

	@Test
	public void testIndexOfWhiteSpace() {
		assertEquals("starts with space", 0, Strings.indexOfWhiteSpace(" a"));
		assertEquals("ends with space", 1, Strings.indexOfWhiteSpace("a\r"));
		assertEquals("has space", 1, Strings.indexOfWhiteSpace("b a"));
		assertEquals("no space", -1, Strings.indexOfWhiteSpace("a"));
	}

	@Test
	public void testDeCamelCase() {
		assertEquals("Foo Bar", deCamelCase("FooBar"));
	}

	@Test
	public void testDeCamelCaseForForcedLink() {
		assertEquals("Forced Link", deCamelCase("Forced Link"));
	}
}
