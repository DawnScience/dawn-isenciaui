package com.teaminabox.eclipse.wiki.text;

import java.io.File;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;

/**
 * I match links to resources path (Eclipse, Plugin,...).
 * <P>
 * The resource must exist for there to be a match
 */
public abstract class ResourceMatcher extends AbstractTextRegionMatcher {
	private String	fPrefix;

	public ResourceMatcher(String prefix) {
		fPrefix = prefix;
	}

	protected boolean accepts(String text, WikiDocumentContext context) {
		return text.startsWith(fPrefix);
	}

	protected int matchLength(String candidate, WikiDocumentContext context) {
		String text = candidate;
		// get rid of the next link if there is one.
		if (text.indexOf(fPrefix, 1) > 0) {
			text = new String(text.substring(0, text.indexOf(fPrefix, 1)));
		}
		// now try to find the longest match
		for (int i = text.length(); i >= fPrefix.length(); i--) {
			String section = new String(text.substring(fPrefix.length(), i));
			if (section.length() > 0 && section.charAt(section.length() - 1) == WikiConstants.LINE_NUMBER_SEPARATOR) {
				continue;
			}
			File resource = findResourceFromPath(context, section);
			if (resource != null && resource.exists()) {
				// is there a line number too?
				if (i < text.length() && WikiConstants.LINE_NUMBER_SEPARATOR == text.charAt(i)) {
					// add 1 for the colon before the line number
					return fPrefix.length() + section.length() + getLineNumberLength(text, i) + 1;
				}
				return i;
			}
		}
		return fPrefix.length();
	}

	protected abstract File findResourceFromPath(WikiDocumentContext context, String section);

	private int getLineNumberLength(String text, int colon) {
		String number = new String(text.substring(colon + 1));
		if (number.trim().length() == 0) {
			return 0;
		}
		int index = 0;
		while (index < number.length() && Character.isDigit(number.charAt(index))) {
			index++;
		}
		return index;
	}

	public TextRegion createTextRegion(String text, WikiDocumentContext context) {
		if (accepts(text, context)) {
			return new PluginResourceTextRegion(new String(text.substring(0, matchLength(text, context))));
		}
		return null;
	}

	protected boolean accepts(char c, boolean firstCharacter) {
		if (firstCharacter) {
			return c == fPrefix.charAt(0);
		}
		return true;
	}

}