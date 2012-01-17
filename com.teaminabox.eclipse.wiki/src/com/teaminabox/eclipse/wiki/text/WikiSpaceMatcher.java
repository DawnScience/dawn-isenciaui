package com.teaminabox.eclipse.wiki.text;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;
import com.teaminabox.eclipse.wiki.util.Strings;

public class WikiSpaceMatcher extends AbstractTextRegionMatcher {

	protected int matchLength(String text, WikiDocumentContext context) {
		int whitespace = Strings.indexOfWhiteSpace(text);
		if (whitespace > 0) {
			return whitespace;
		}
		return text.length();
	}

	private boolean accepts(String text, WikiDocumentContext context) {
		int colon = text.indexOf(WikiConstants.WIKISPACE_DELIMITER);
		if (colon < 0) {
			return false;
		}

		return context.getWikiSpace().containsKey(new String(text.substring(0, colon)));
	}

	public TextRegion createTextRegion(String text, WikiDocumentContext context) {
		if (accepts(text, context)) {
			String textRegion = new String(text.substring(0, matchLength(text, context)));
			String link = getLink(textRegion, context);
			return new WikiUrlTextRegion(textRegion, link);
		}
		return null;
	}

	private String getLink(String textRegion, WikiDocumentContext context) {
		int colon = textRegion.indexOf(WikiConstants.WIKISPACE_DELIMITER);
		String linkPrefix = context.getWikiSpaceLink(new String(textRegion.substring(0, colon)));
		String linkSuffix = new String(textRegion.substring(colon + 1));
		return linkPrefix + linkSuffix;
	}

	@Override
	protected boolean accepts(char c, boolean firstCharacter) {
		return c != ' ';
	}

}