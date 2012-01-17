package com.isencia.passerelle.workbench.model.editor.ui.views;

import org.eclipse.emf.edit.ui.dnd.ViewerDragAdapter;
import org.eclipse.gef.dnd.TemplateTransfer;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isencia.passerelle.workbench.model.editor.ui.palette.PaletteItemDefinition;

public class DragTargetListener extends ViewerDragAdapter {
	public static PaletteItemDefinition data;

	@Override
	public void dragStart(DragSourceEvent event) {
		boolean doit = !getViewer().getSelection().isEmpty();
		if (doit) {
			TreeSelection selection = (TreeSelection)getViewer().getSelection();
			if (selection != null && !selection.isEmpty()){
				final Object selected = selection.getFirstElement();
				if (selected instanceof PaletteItemDefinition) {
					CreationFactory factory = new TreeViewCreationFactory((PaletteItemDefinition)selected);
					
					event.data = (PaletteItemDefinition)selected;
					TemplateTransfer.getInstance( ).setTemplate( factory );
				}else{
					doit = false;
				}
			}
		}
		event.doit = doit;
		

	}

	private static final Logger logger = LoggerFactory
			.getLogger(DragTargetListener.class);

	public DragTargetListener(TreeViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	protected boolean validateTransfer(Object transfer) {

		return transfer instanceof PaletteItemDefinition;
	}

	/**
	 * Returns viewer
	 * 
	 * @return viewer
	 */
	protected TreeViewer getViewer() {
		return (TreeViewer) viewer;
	}
}