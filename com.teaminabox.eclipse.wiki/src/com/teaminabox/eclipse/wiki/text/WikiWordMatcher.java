package com.teaminabox.eclipse.wiki.text;

/**
 * I match a region of text representing a Wiki word based on a regex.
 */
public class WikiWordMatcher extends PatternMatcher {

	public WikiWordMatcher(String pattern) {
		super(pattern);
	}

	protected TextRegion createTextRegion(String text) {
		return new WikiWordTextRegion(text);
	}

	protected boolean accepts(char c, boolean firstCharacter) {
		if (firstCharacter) {
			return Character.isUpperCase(c);
		}
		return Character.isLetterOrDigit(c);
	}

}