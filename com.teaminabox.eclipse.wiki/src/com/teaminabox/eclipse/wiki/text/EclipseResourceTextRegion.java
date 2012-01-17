package com.teaminabox.eclipse.wiki.text;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.rules.IToken;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.editors.ColourManager;
import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;

/**
 * A region of text referring to a resource in the Eclipse workspace.
 */
public class EclipseResourceTextRegion extends TextRegion {

	public EclipseResourceTextRegion(String text) {
		super(text);
	}

	/**
	 * @see com.teaminabox.eclipse.wiki.text.TextRegion#getToken(ColourManager colourManager)
	 */
	public IToken getToken(ColourManager colourManager) {
		return getToken(WikiConstants.ECLIPSE_RESOURCE, colourManager);
	}

	/**
	 * @see com.teaminabox.eclipse.wiki.text.TextRegion#accept(com.teaminabox.eclipse.wiki.text.TextRegionVisitor)
	 */
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

	public IResource getResource(WikiDocumentContext context) {
		String file = new String(getText().substring(WikiConstants.ECLIPSE_PREFIX.length()));
		return context.getResource(file);
	}

}