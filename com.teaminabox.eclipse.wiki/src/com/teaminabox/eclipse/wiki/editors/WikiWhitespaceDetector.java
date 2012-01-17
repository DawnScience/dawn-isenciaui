package com.teaminabox.eclipse.wiki.editors;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

import com.teaminabox.eclipse.wiki.util.Strings;

public class WikiWhitespaceDetector implements IWhitespaceDetector {

	public boolean isWhitespace(char c) {
		return Strings.isWhiteSpaceCharacter(c);
	}

}