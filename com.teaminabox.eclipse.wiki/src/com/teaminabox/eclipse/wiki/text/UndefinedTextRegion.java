package com.teaminabox.eclipse.wiki.text;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import com.teaminabox.eclipse.wiki.editors.ColourManager;

/**
 * A region of text containing non text characters.
 */
public class UndefinedTextRegion extends TextRegion {

	public UndefinedTextRegion(String text) {
		super(text);
	}

	public IToken getToken(ColourManager colourManager) {
		return Token.UNDEFINED;
	}

	public <T> T accept(TextRegionVisitor<T> textRegionVisitor) {
		return textRegionVisitor.visit(this);
	}

	/**
	 * This is a not special Wiki region of text.
	 * 
	 * @return <code>false</code>
	 */
	public boolean isLink() {
		return false;
	}

}