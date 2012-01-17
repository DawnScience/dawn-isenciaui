package com.teaminabox.eclipse.wiki.renderer;

import static org.junit.Assert.assertEquals;

import org.junit.*;

public class LinkMakerTest {

	private TestLinkMaker	linkMaker	= new TestLinkMaker();

	@Test
	public void testGetLinkForUrl() throws Exception {
		assertEquals("<a href=\"http://www.google.com\">Google</a>", linkMaker.getLink("http://www.google.com", "Google"));
	}

	@Test
	public void testGetLinkForImageUrl() throws Exception {
		assertEquals("<img alt=\"Google\" src=\"http://www.google.com/image.gif\"/>", linkMaker.getLink("http://www.google.com/image.gif", "Google"));
	}

	@Test
	public void testGetLinkForImage() throws Exception {
		assertEquals("<img alt=\"Google\" src=\"http://a.link.to/an/image.jpg\"/>", linkMaker.getLink("http://a.link.to/an/image.jpg", "Google"));
	}

}
