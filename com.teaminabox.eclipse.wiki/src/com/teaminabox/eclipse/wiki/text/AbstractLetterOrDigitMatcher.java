package com.teaminabox.eclipse.wiki.text;

import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;

public abstract class AbstractLetterOrDigitMatcher extends AbstractTextRegionMatcher {

	public TextRegion createTextRegion(String text, WikiDocumentContext context) {
		int matchLength = matchLength(text, context);
		if (matchLength > 0) {
			return new BasicTextRegion(new String(text.substring(0, matchLength)));
		}
		return null;
	}

	protected abstract int matchLength(String text, WikiDocumentContext context);

}
