package com.teaminabox.eclipse.wiki.text;

/**
 * I match a region of text representing an escaped Wiki word based on a regex.
 */
public class EscapedWikiWordMatcher extends PatternMatcher {

	private final char		firstCharacter;
	private final String[]	toRemoveForDisplay;

	public EscapedWikiWordMatcher(String pattern, char firstCharacter, String[] toRemoveForDisplay) {
		super(pattern);
		this.firstCharacter = firstCharacter;
		this.toRemoveForDisplay = toRemoveForDisplay;
	}

	protected TextRegion createTextRegion(String text) {
		BasicTextRegion basicTextRegion = new BasicTextRegion(text);
		basicTextRegion.setDisplayText(getDisplayText(text));
		return basicTextRegion;
	}

	private String getDisplayText(String text) {
		String displayText = text;
		for (int i = 0; i < toRemoveForDisplay.length; i++) {
			displayText = displayText.replaceAll(toRemoveForDisplay[i], "");
		}
		return displayText;
	}

	protected boolean accepts(char c, boolean isFirstCharacter) {
		if (isFirstCharacter) {
			return c == firstCharacter;
		}
		return Character.isLetterOrDigit(c);
	}

}