package com.teaminabox.eclipse.wiki.util;

public class Strings {

	/**
	 * Put spaces before the capital of a wiki word (except the first character).
	 */
	public static String deCamelCase(String wikiWord) {
		return wikiWord.replaceAll("([A-Z])", " $0").replaceAll("  ", " ").trim();
	}

	public static boolean isWhiteSpaceCharacter(char c) {
		return c == ' ' || c == '\t' || c == '\n' || c == '\r';
	}

	public static int indexOfWhiteSpace(String text) {
		if (text.length() == 0) {
			return -1;
		}
		for (int i = 0; i < text.length(); i++) {
			if (Character.isWhitespace(text.charAt(i))) {
				return i;
			}
		}
		return -1;
	}
}
