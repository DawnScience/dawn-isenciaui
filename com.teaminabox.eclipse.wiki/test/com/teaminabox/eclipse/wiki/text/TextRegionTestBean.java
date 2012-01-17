package com.teaminabox.eclipse.wiki.text;

/**
 * I encapsulate text and the corresponding TextRegion that could be formed from it.
 */
public final class TextRegionTestBean {

	private final String		text;
	private final TextRegion	textRegion;

	public TextRegionTestBean(String text, TextRegion textRegion) {
		this.text = text;
		this.textRegion = textRegion;
	}

	public String getText() {
		return text;
	}

	public TextRegion getTextRegion() {
		return textRegion;
	}

}