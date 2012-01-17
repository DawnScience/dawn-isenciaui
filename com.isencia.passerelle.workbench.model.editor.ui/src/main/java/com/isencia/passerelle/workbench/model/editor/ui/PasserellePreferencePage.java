package com.isencia.passerelle.workbench.model.editor.ui;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class PasserellePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage{

	public PasserellePreferencePage() {
		super();
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Preferences for the workflow editor and for running the workflow.");

	}
	@Override
	protected void createFieldEditors() {
		final BooleanFieldEditor expert = new BooleanFieldEditor(PreferenceConstants.EXPERT, "Expert mode", getFieldEditorParent());
      	addField(expert);

	}
	
	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}

}
