package com.teaminabox.eclipse.wiki.text;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import com.teaminabox.eclipse.wiki.editors.WikiEditor;

abstract class AbstractTextRegionMatcher implements TextRegionMatcher {

	private WikiEditor	editor;

	public void setEditor(WikiEditor editor) {
		this.editor = editor;
	}

	public final IToken evaluate(ICharacterScanner scanner) {
		String text = getText(scanner);
		TextRegion region = createTextRegion(text, editor.getContext());
		unwind(scanner, text, region);
		if (region == null) {
			return Token.UNDEFINED;
		}
		return region.getToken(editor.getColourManager());
	}

	protected final String getText(ICharacterScanner scanner) {
		StringBuffer buffer = new StringBuffer(100);
		boolean firstCharacter = true;
		char c = (char) scanner.read();
		while (accepts(c, firstCharacter) && c != (char) ICharacterScanner.EOF && c != '\n' && c != '\r') {
			buffer.append(c);
			firstCharacter = false;
			c = (char) scanner.read();
		}
		// go back one as the scanner is one step further along than the text
		// we've found
		scanner.unread();
		return buffer.toString();
	}

	protected abstract boolean accepts(char c, boolean firstCharacter);

	protected final void unwind(ICharacterScanner scanner, String text, TextRegion textRegion) {
		int unwindLength = text.length();
		if (textRegion != null) {
			unwindLength -= textRegion.getLength();
		}
		for (int i = 0; i < unwindLength; i++) {
			scanner.unread();
		}
	}
}
