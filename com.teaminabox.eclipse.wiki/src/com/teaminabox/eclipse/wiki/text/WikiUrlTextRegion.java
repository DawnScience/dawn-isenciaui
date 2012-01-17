package com.teaminabox.eclipse.wiki.text;

import org.eclipse.jface.text.rules.IToken;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.editors.ColourManager;

/**
 * A region of text that is an inter wiki url.
 */
public class WikiUrlTextRegion extends TextRegion {

	private String	link;

	public WikiUrlTextRegion(String text, String link) {
		super(text);
		this.link = link;
	}

	public IToken getToken(ColourManager colourManager) {
		return getToken(WikiConstants.WIKI_URL, colourManager);
	}

	public <T> T accept(TextRegionVisitor<T> textRegionVisitor) {
		return textRegionVisitor.visit(this);
	}

	/**
	 * This is a special Wiki region of text.
	 * 
	 * @return <code>true</code>
	 */
	public boolean isLink() {
		return true;
	}

	public String getLink() {
		return link;
	}

}