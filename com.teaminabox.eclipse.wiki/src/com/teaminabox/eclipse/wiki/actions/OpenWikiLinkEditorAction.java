package com.teaminabox.eclipse.wiki.actions;

import static com.teaminabox.eclipse.wiki.WikiPlugin.wikiPlugin;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;

import com.teaminabox.eclipse.wiki.editors.WikiBrowserEditor;

public class OpenWikiLinkEditorAction implements IEditorActionDelegate {

	private WikiBrowserEditor	browserEditor;

	public void selectionChanged(IAction action, ISelection selection) {
		if (!(selection instanceof TextSelection)) {
			return;
		}
		IWorkbenchPage activePage = wikiPlugin().getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (activePage.getActiveEditor().getClass().equals(WikiBrowserEditor.class)) {
			browserEditor = (WikiBrowserEditor) activePage.getActiveEditor();
		}

		action.setEnabled(true);
	}

	public void run(IAction action) {
		if (browserEditor != null) {
			browserEditor.getEditor().openWikiLinkOnSelection();
		}
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor != null && targetEditor.getClass().equals(WikiBrowserEditor.class)) {
			browserEditor = (WikiBrowserEditor) targetEditor;
		}
	}
}