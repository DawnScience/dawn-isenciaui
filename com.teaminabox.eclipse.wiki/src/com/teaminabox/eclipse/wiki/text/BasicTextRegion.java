package com.teaminabox.eclipse.wiki.text;

import org.eclipse.jface.text.rules.IToken;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.editors.ColourManager;

public class BasicTextRegion extends TextRegion {

	public BasicTextRegion(String word) {
		super(word);
	}

	/**
	 * @see com.teaminabox.eclipse.wiki.text.TextRegion#accept(com.teaminabox.eclipse.wiki.text.TextRegionVisitor)
	 */
	public <T> T accept(TextRegionVisitor<T> textRegionVisitor) {
		return textRegionVisitor.visit(this);
	}

	/**
	 * @see com.teaminabox.eclipse.wiki.text.TextRegion#getToken(ColourManager colourManager)
	 */
	public IToken getToken(ColourManager colourManager) {
		return getToken(WikiConstants.OTHER, colourManager);
	}

	/**
	 * This is not a special Wiki region of text.
	 * 
	 * @return <code>false</code>
	 */
	public boolean isLink() {
		return false;
	}

}