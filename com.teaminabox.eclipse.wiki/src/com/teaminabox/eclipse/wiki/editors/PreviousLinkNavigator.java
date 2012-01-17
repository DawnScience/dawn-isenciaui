package com.teaminabox.eclipse.wiki.editors;

import static com.teaminabox.eclipse.wiki.WikiPlugin.wikiPlugin;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;
import com.teaminabox.eclipse.wiki.text.TextRegion;

public class PreviousLinkNavigator extends LinkNavigator {

	private int			pos;
	private TextRegion	currentTextRegion;

	public PreviousLinkNavigator(WikiEditor editor) {
		super(editor);
	}

	public void previous() {
		try {
			pos = getSelection().getOffset();
			currentTextRegion = getTextRegionAtCursor(pos);
			if (currentTextRegion.getLocationInDocument() == 0) {
				return;
			}
			move();
		} catch (Exception e) {
			wikiPlugin().logAndReport(WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_PROGRAMMATIC_ERROR_TITLE), WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_PROGRAMMATIC_ERROR_TEXT), e);
		}
	}

	private void move() {
		pos = currentTextRegion.getLocationInDocument() - 1;
		do {
			currentTextRegion = getTextRegionAtCursor(pos);
			if (currentTextRegion.isLink()) {
				getEditor().selectAndReveal(currentTextRegion.getLocationInDocument(), 0);
				return;
			} else if (currentTextRegion.getLength() == 0) {
				pos--;
			} else {
				pos = currentTextRegion.getLocationInDocument() - 1;
			}
			if (pos < 0) {
				return;
			}
		} while (pos > 0 && !currentTextRegion.isLink());
	}

}
