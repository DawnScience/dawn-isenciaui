package com.teaminabox.eclipse.wiki.text;

import static org.apache.commons.lang.StringUtils.join;

import com.teaminabox.eclipse.wiki.WikiConstants;

public class UrlMatcher extends PatternMatcher {

	private static final String	URL_REGEX	= "(" + join(WikiConstants.URL_PREFIXES, '|') + "):(//)?([-_\\.!~*';/?:@#&=+$%,\\p{Alnum}])+";

	public UrlMatcher() {
		super(UrlMatcher.URL_REGEX);
	}

	protected boolean accepts(char c, boolean firstCharacter) {
		if (firstCharacter) {
			for (int i = 0; i < WikiConstants.URL_PREFIXES.length; i++) {
				if (c == WikiConstants.URL_PREFIXES[i].charAt(0)) {
					return true;
				}
			}
		}
		return c != ' ';
	}

	protected TextRegion createTextRegion(String text) {
		return new UrlTextRegion(text);
	}

}