package com.teaminabox.eclipse.wiki.actions;

import com.teaminabox.eclipse.wiki.editors.WikiEditor;

public class NavigateToNextLinkAction extends WikiWorkbenchWindowActionDelegate {

	protected void performAction(WikiEditor editor) {
		editor.navigateToNextLink();
	}
}