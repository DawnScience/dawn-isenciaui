package com.teaminabox.eclipse.wiki.editors;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;

import com.teaminabox.eclipse.wiki.renderer.SelectionRenderer;
import com.teaminabox.eclipse.wiki.text.TextRegion;
import com.teaminabox.eclipse.wiki.text.TextRegionBuilder;

public class WikiHover implements ITextHover {

	private WikiEditor	wikiEditor;

	public WikiHover(WikiEditor editor) {
		this.wikiEditor = editor;
	}

	public String getHoverInfo(final ITextViewer textViewer, final IRegion hoverRegion) {
		if (!wikiEditor.isLocal()) {
			return null;
		}

		return new SelectionRenderer(wikiEditor, textViewer, hoverRegion).render();
	}

	public IRegion getHoverRegion(final ITextViewer textViewer, final int offset) {
		TextRegion textRegion = TextRegionBuilder.getTextRegionAtCursor(wikiEditor, textViewer.getDocument(), offset);
		return new Region(offset - textRegion.getCursorPosition(), textRegion.getLength());
	}
}