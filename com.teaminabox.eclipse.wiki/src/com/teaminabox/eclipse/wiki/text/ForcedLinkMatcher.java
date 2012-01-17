package com.teaminabox.eclipse.wiki.text;

import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;

public class ForcedLinkMatcher extends AbstractTextRegionMatcher {

	private final int		brackets;
	private final String	openingBrackets;
	private final String	closingBrackets;

	public ForcedLinkMatcher(int brackets) {
		this.brackets = brackets;
		openingBrackets = new String("[[".substring(0, brackets));
		closingBrackets = new String("]]".substring(0, brackets));
	}

	@Override
	protected boolean accepts(char c, boolean firstCharacter) {
		if (firstCharacter) {
			return c == '[';
		}
		return Character.isLetterOrDigit(c) || c == '[' || c == ']';
	}

	public TextRegion createTextRegion(String text, WikiDocumentContext context) {
		if (accepts(text, context)) {
			return createTextRegionImpl(text, context);
		}
		return null;
	}

	protected boolean accepts(String text, WikiDocumentContext context) {
		return text.length() > brackets * 2 + 1 && text.startsWith(openingBrackets) && text.indexOf(closingBrackets) > 0;
	}

	private int matchLength(String text, WikiDocumentContext context) {
		// + 'brackets' because the index of doesn't include the closing one or two brackets which we want
		return text.indexOf(closingBrackets) + brackets;
	}

	public TextRegion createTextRegionImpl(String text, WikiDocumentContext context) {
		return new ForcedLinkTextRegion(new String(text.substring(0, matchLength(text, context))), brackets);
	}
}
