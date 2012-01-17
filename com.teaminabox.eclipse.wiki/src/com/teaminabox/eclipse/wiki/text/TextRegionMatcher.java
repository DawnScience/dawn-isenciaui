package com.teaminabox.eclipse.wiki.text;

import org.eclipse.jface.text.rules.IRule;

import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;
import com.teaminabox.eclipse.wiki.editors.WikiEditor;

/**
 * I represent a region of text such as a WikiWord, a link, etc.
 */
public interface TextRegionMatcher extends IRule {

	/**
	 * Create the appropriate text region corresponding to this matcher
	 * 
	 * @param text
	 * @return a {@link TextRegion TextRegion} or <code>null</code> if this matcher cannot create one with the given
	 *         text.
	 */
	TextRegion createTextRegion(String text, WikiDocumentContext context);

	void setEditor(WikiEditor editor);
}