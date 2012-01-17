package com.teaminabox.eclipse.wiki.editors;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.PlatformUI;

import com.teaminabox.eclipse.wiki.renderer.SelectionRenderer;

public class Preview {

	private final ITextSelection	selection;
	private final ISourceViewer	sourceViewer;
	private final WikiEditor	editor;

	public Preview(WikiEditor editor, ITextSelection selection, ISourceViewer sourceViewer) {
		this.editor = editor;
		this.selection = selection;
		this.sourceViewer = sourceViewer;
	}

	public void show() {
		SelectionRenderer renderer = new SelectionRenderer(editor, sourceViewer, new IRegion() {
			public int getLength() {
				return selection.getLength();
			}

			public int getOffset() {
				return selection.getOffset();
			}
		});
		String info = renderer.render();
		if (info == null) {
			return;
		}
		MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Preview", info);
	}

}
