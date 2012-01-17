package com.teaminabox.eclipse.wiki.text;

import static com.teaminabox.eclipse.wiki.WikiPlugin.wikiPlugin;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.editors.ColourManager;

/**
 * I am an atomic region of text.
 */
public abstract class TextRegion {

	private String	text;
	private String	displayText;

	/**
	 * The position of the cursor relative to the start of this TextRegion
	 */
	private int		cursorPosition;
	private boolean	cursorPositionSet;
	private int		locationInDocument;

	public TextRegion(String text) {
		setText(text);
		setDisplayText(text);
	}

	public TextRegion() {
		text = "";
	}

	public abstract <T> T accept(TextRegionVisitor<T> textRegionVisitor);

	public abstract boolean isLink();

	public int getLength() {
		return text.length();
	}

	public String getText() {
		return text;
	}

	public String getDisplayText() {
		return displayText;
	}

	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	public abstract IToken getToken(ColourManager colourManager);

	public final void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "Region text: " + text + ", length = " + text.length() + (cursorPositionSet ? ", cursor position = " + cursorPosition : ", cursor position not set.");
	}

	/**
	 * Get the position of the cursor relative to the start of this TextRegion. This only makes sense if the position
	 * has been {@link #setCursorPosition (int) set}.
	 *
	 * @return int the position of the cursor relative to the start of this TextRegion
	 */
	public int getCursorPosition() {
		return cursorPosition;
	}

	/**
	 * Sets the position of the cursor relative to the start of this TextRegion.
	 *
	 * @param cursorPosition
	 */
	public void setCursorPosition(int cursorPosition) {
		this.cursorPosition = cursorPosition;
		cursorPositionSet = true;
	}

	/**
	 * @return String the text up to the cursor position.
	 * @see #getCursorPosition()
	 * @see #setCursorPosition(int)
	 */
	public String getTextToCursor() {
		if (cursorPosition <= 0) {
			return "";
		} else if (cursorPosition >= text.length()) {
			return text;
		} else {
			return new String(text.substring(0, cursorPosition));
		}
	}

	protected IToken getToken(String preferenceKey, ColourManager colourManager) {
		RGB rgb = PreferenceConverter.getColor(wikiPlugin().getPreferenceStore(), preferenceKey + WikiConstants.SUFFIX_FOREGROUND);
		Color foreground = colourManager.getColor(rgb);
		String style = wikiPlugin().getPreferenceStore().getString(preferenceKey + WikiConstants.SUFFIX_STYLE);
		boolean bold = WikiConstants.STYLE_BOLD.equals(style);
		return new Token(new TextAttribute(foreground, null, bold ? SWT.BOLD : SWT.NORMAL));
	}

	public void append(String newText) {
		this.text = this.text + newText;
	}

	public int getLocationInDocument() {
		return locationInDocument;
	}

	public void setLocationInDocument(int i) {
		locationInDocument = i;
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || !object.getClass().equals(getClass())) {
			return false;
		}
		TextRegion other = (TextRegion) object;
		return text.equals(other.getText()) && getLocationInDocument() == other.getLocationInDocument() && getCursorPosition() == other.getCursorPosition();
	}

	@Override
	public int hashCode() {
		return getText().hashCode() ^ getLocationInDocument() ^ getCursorPosition();
	}
}