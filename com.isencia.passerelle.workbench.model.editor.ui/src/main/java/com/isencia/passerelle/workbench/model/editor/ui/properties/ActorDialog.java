package com.isencia.passerelle.workbench.model.editor.ui.properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IViewSite;

import ptolemy.kernel.util.NamedObj;

import com.isencia.passerelle.workbench.model.editor.ui.views.ActorAttributesView;

public class ActorDialog extends Dialog {
	
	private IViewSite site;
	private NamedObj actor;
	public ActorDialog(IViewSite site, NamedObj actor) {
		super(site.getShell());
		this.site = site;
		this.actor = actor;
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		ActorAttributesView view = new ActorAttributesView();
		view.setSite(site);
		view.setActor(actor);
		view.createPartControl(composite);
		return composite;
	}


	public void create() {
		super.create();
		getShell().setText("Edit Attributes of '"+actor.getDisplayName()+"'");
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Close", true);
	}
}
