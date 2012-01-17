package com.teaminabox.eclipse.wiki.text;

import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;

/**
 * I match a single character which is not a letter or digit.
 * 
 * @see Character#isLetterOrDigit(char)
 */
public class NonLetterOrDigitMatcher extends AbstractLetterOrDigitMatcher {

	protected int matchLength(String text, WikiDocumentContext context) {
		if (text.length() > 0 && !Character.isLetterOrDigit(text.charAt(0))) {
			return 1;
		}
		return 0;
	}

	protected boolean accepts(char c, boolean firstCharacter) {
		return !Character.isLetterOrDigit(c);
	}

}