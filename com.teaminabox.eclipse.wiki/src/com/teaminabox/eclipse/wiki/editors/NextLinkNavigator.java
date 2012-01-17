package com.teaminabox.eclipse.wiki.editors;

import static com.teaminabox.eclipse.wiki.WikiPlugin.wikiPlugin;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;
import com.teaminabox.eclipse.wiki.text.TextRegion;

public class NextLinkNavigator extends LinkNavigator {

	private int			pos;
	private TextRegion	currentTextRegion;
	private int			endOfCurrentRegion;

	public NextLinkNavigator(WikiEditor editor) {
		super(editor);
	}

	public void next() {
		try {
			pos = getSelection().getOffset() + 1;
			if (pos >= getDocument().getLength()) {
				return;
			}
			initialise();
			move();
		} catch (Exception e) {
			wikiPlugin().logAndReport(WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_PROGRAMMATIC_ERROR_TITLE), WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_PROGRAMMATIC_ERROR_TEXT), e);
		}
	}

	private void initialise() {
		currentTextRegion = getTextRegionAtCursor(pos);
		endOfCurrentRegion = pos + currentTextRegion.getLength() - currentTextRegion.getCursorPosition();
		pos = endOfCurrentRegion + 1;
	}

	private void move() {
		while (pos < getDocument().getLength()) {
			currentTextRegion = getTextRegionAtCursor(pos);
			int textRegionIndex = pos - currentTextRegion.getCursorPosition();

			if (currentTextRegion.getLength() == 0) {
				pos++;
			} else if (currentTextRegion.isLink() && isAfterCurrentRegion(textRegionIndex)) {
				getEditor().selectAndReveal(pos - currentTextRegion.getCursorPosition(), 0);
				return;
			} else {
				pos = textRegionIndex + currentTextRegion.getLength() + 1;
			}
		}
	}

	private boolean isAfterCurrentRegion(int textRegionIndex) {
		return textRegionIndex > endOfCurrentRegion;
	}

}
