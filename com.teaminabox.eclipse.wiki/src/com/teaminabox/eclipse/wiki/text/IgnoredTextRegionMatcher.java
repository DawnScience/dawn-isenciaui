package com.teaminabox.eclipse.wiki.text;

import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;

public class IgnoredTextRegionMatcher extends AbstractTextRegionMatcher {

	private int matchLength(String text, WikiDocumentContext context) {
		for (String excluded : context.getExcludeSet()) {
			if (text.startsWith(excluded)) {
				return excluded.length();
			}
		}
		return 0;
	}

	public TextRegion createTextRegion(String text, WikiDocumentContext context) {
		int matchLengh = matchLength(text, context);
		if (matchLengh > 0) {
			return new BasicTextRegion(new String(text.substring(0, matchLength(text, context))));
		}
		return null;
	}

	protected boolean accepts(char c, boolean firstCharacter) {
		return c != ' ';
	}

}
