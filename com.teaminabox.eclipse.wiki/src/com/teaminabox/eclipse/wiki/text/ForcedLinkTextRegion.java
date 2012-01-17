/*
 * Contributions by KERAMIDAS
 */

package com.teaminabox.eclipse.wiki.text;

public final class ForcedLinkTextRegion extends WikiLinkTextRegion {

	private String	link;

	public ForcedLinkTextRegion(String text, int brackets) {
		super(text);
		int end = getText().indexOf(']');
		link = getText().substring(brackets, end).replaceAll(" ", "");

		int text_end, text_start;
		if (getText().indexOf("][") < 0) {
			text_end = getText().indexOf(']');
			text_start = brackets;
		} else {
			text_end = getText().indexOf("]]");
			text_start = getText().indexOf("][") + 2;
		}

		setDisplayText(new String(getText().substring(text_start, text_end)));
	}

	public String getWikiDocumentName() {
		return link.replaceAll("[|]", "");
	}

	public <T> T accept(TextRegionVisitor<T> textRegionVisitor) {
		return textRegionVisitor.visit(this);
	}

}
