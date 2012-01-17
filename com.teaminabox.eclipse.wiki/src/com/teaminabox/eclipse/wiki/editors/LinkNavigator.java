package com.teaminabox.eclipse.wiki.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;

import com.teaminabox.eclipse.wiki.text.TextRegion;
import com.teaminabox.eclipse.wiki.text.TextRegionBuilder;

public abstract class LinkNavigator {

	private final WikiEditor		editor;
	private final IDocument			document;
	private final ITextSelection	selection;

	public LinkNavigator(WikiEditor editor) {
		this.editor = editor;
		document = editor.getDocument();
		selection = (ITextSelection) editor.getSelectionProvider().getSelection();
	}

	protected IDocument getDocument() {
		return document;
	}

	protected WikiEditor getEditor() {
		return editor;
	}

	protected ITextSelection getSelection() {
		return selection;
	}

	protected TextRegion getTextRegionAtCursor(int pos) {
		return TextRegionBuilder.getTextRegionAtCursor(getEditor(), getDocument(), pos);
	}

}
