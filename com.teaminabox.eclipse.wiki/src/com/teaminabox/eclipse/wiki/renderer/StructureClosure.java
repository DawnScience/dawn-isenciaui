package com.teaminabox.eclipse.wiki.renderer;

import org.eclipse.jface.text.BadLocationException;

public interface StructureClosure {
	/**
	 * @param header
	 *            the text of the header without markup
	 * @param line
	 *            the line number where the header is
	 * @throws BadLocationException
	 */
	void acceptHeader(String header, int line) throws BadLocationException;

	void applyListOrderedListTag(int level);

}
