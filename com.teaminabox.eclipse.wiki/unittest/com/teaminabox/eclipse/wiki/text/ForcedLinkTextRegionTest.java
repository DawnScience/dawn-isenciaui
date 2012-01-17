package com.teaminabox.eclipse.wiki.text;

import static org.junit.Assert.assertEquals;

import org.junit.*;

public final class ForcedLinkTextRegionTest {

	@Test
	public void testGetWikiDocumentNameForTwoBrackets() {
		ForcedLinkTextRegion region = new ForcedLinkTextRegion("[[foo]]", 2);
		assertEquals("foo", region.getWikiDocumentName());
		assertEquals("foo", region.getDisplayText());
	}

	@Test
	public void testGetWikiDocumentNameForOneBracket() {
		ForcedLinkTextRegion region = new ForcedLinkTextRegion("[foo]", 1);
		assertEquals("foo", region.getWikiDocumentName());
		assertEquals("foo", region.getDisplayText());
	}

	@Test
	public void testGetWikiDocumentNameWithSpaceSeparatedName() {
		ForcedLinkTextRegion region = new ForcedLinkTextRegion("[[foo bar ]]", 2);
		assertEquals("foobar", region.getWikiDocumentName());
		assertEquals("foo bar ", region.getDisplayText());
	}

}
