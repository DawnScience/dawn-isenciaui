package com.teaminabox.eclipse.wiki.text;

import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;

/**
 * I match letters only.
 * 
 * @see Character#isLetterOrDigit(char)
 */
public class LetterOrDigitMatcher extends AbstractLetterOrDigitMatcher {

	protected int matchLength(String text, WikiDocumentContext context) {
		for (int i = 0; i < text.length(); i++) {
			if (!accepts(text.charAt(i), false)) {
				return i;
			}
		}
		return text.length();
	}

	protected boolean accepts(char c, boolean firstCharacter) {
		return Character.isLetterOrDigit(c);
	}

}