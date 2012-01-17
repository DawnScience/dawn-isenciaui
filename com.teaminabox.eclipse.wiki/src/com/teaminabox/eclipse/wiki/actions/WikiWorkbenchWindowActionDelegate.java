package com.teaminabox.eclipse.wiki.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.teaminabox.eclipse.wiki.editors.WikiBrowserEditor;
import com.teaminabox.eclipse.wiki.editors.WikiEditor;

public abstract class WikiWorkbenchWindowActionDelegate implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow	window;

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {
		if (window.getActivePage().getActiveEditor().getClass().equals(WikiBrowserEditor.class)) {
			WikiBrowserEditor editor = (WikiBrowserEditor) window.getActivePage().getActiveEditor();
			if (editor.isEditingSource()) {
				performAction(editor.getEditor());
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	protected abstract void performAction(WikiEditor editor);
}