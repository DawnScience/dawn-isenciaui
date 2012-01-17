package com.teaminabox.eclipse.wiki.editors;

import static org.junit.Assert.assertEquals;

import org.junit.*;

public class PathWithLineNumberTest {

	@Test
	public void testPathWithoutLineNumber() throws Exception {
		assertPathEquals("a/b", 0, "a/b");
	}

	@Test
	public void testPathWithLineNumber() throws Exception {
		assertPathEquals("a/b:10", 9, "a/b");
	}

	private void assertPathEquals(String path, int expectedLineNumber, String expectedPath) {
		PathWithLineNumber pathWithLineNumber = new PathWithLineNumber(path);
		assertEquals(expectedLineNumber, pathWithLineNumber.getLine());
		assertEquals(expectedPath, pathWithLineNumber.getPath().toString());
	}
}
