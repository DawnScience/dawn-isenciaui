package com.teaminabox.eclipse.wiki.editors;

import static org.junit.Assert.assertEquals;

import org.eclipse.jface.text.IRegion;
import org.junit.Before;
import org.junit.*;

import com.teaminabox.eclipse.wiki.WikiTest;

public final class WikiHoverTest extends WikiTest {

	private static final String	WIKI_HOVER_CONTENT	= "Stuff";
	private static final String	WIKI_HOVER			= "WikiHover";
	private static final String	WIKI_CONTENT		= "A Test for " + WIKI_HOVER + ". NonExistentLink. more text";
	private WikiEditor			editor;
	private WikiHover			hover;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		create(WikiHoverTest.WIKI_HOVER_CONTENT, "WikiHover.wiki");
		editor = createWikiDocumentAndOpen(WikiHoverTest.WIKI_CONTENT).getEditor();
		hover = new WikiHover(editor);
	}

	private String getHoverInfo(final int offset) {
		return hover.getHoverInfo(editor.getTextViewerForTest(), new IRegion() {
			public int getLength() {
				return 0;
			}

			public int getOffset() {
				return offset;
			}
		});
	}

	@Test
	public void testGetHoverInfoAtBeginning() {
		assertEquals(WikiHoverTest.WIKI_HOVER_CONTENT, getHoverInfo(getOffsetAtBeginning()));
	}

	@Test
	public void testGetHoverInfoAtEnd() {
		assertEquals(WikiHoverTest.WIKI_HOVER_CONTENT, getHoverInfo(getOffsetAtEnd()));
	}

	@Test
	public void testGetHoverRegionBeforeLink() {
		IRegion hoverRegion = hover.getHoverRegion(editor.getTextViewerForTest(), getOffsetAtBeginning());
		assertEquals("length", 1, hoverRegion.getLength());
		assertEquals("offset", getOffsetAtBeginning() - 1, hoverRegion.getOffset());
	}

	@Test
	public void testGetHoverRegionInLink() {
		IRegion hoverRegion = hover.getHoverRegion(editor.getTextViewerForTest(), getOffsetAtBeginning() + 1);
		assertEquals("length", WikiHoverTest.WIKI_HOVER.length(), hoverRegion.getLength());
		assertEquals("offset", getOffsetAtBeginning(), hoverRegion.getOffset());
	}

	/**
	 * Get the offset of the beginning of the TextRegion with Hover info
	 */
	private int getOffsetAtBeginning() {
		return WikiHoverTest.WIKI_CONTENT.indexOf(WikiHoverTest.WIKI_HOVER);
	}

	/**
	 * Get the offset of the end of the TextRegion with Hover info
	 */
	private int getOffsetAtEnd() {
		return WikiHoverTest.WIKI_CONTENT.indexOf(WikiHoverTest.WIKI_HOVER) + WikiHoverTest.WIKI_HOVER.length();
	}

}
