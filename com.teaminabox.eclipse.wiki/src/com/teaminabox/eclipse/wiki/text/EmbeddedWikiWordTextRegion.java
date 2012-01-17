package com.teaminabox.eclipse.wiki.text;

import org.eclipse.jface.text.rules.IToken;

import com.teaminabox.eclipse.wiki.editors.ColourManager;

public class EmbeddedWikiWordTextRegion extends TextRegion {

	private final TextRegion	embeddedTextRegion;

	public EmbeddedWikiWordTextRegion(String text, TextRegion region) {
		super(text);
		this.embeddedTextRegion = region;
	}

	public <T> T accept(TextRegionVisitor<T> textRegionVisitor) {
		return textRegionVisitor.visit(this);
	}

	public IToken getToken(ColourManager colourManager) {
		return embeddedTextRegion.getToken(colourManager);
	}

	public boolean isLink() {
		return embeddedTextRegion.isLink();
	}

	public TextRegion getEmbeddedTextRegion() {
		return embeddedTextRegion;
	}

}
