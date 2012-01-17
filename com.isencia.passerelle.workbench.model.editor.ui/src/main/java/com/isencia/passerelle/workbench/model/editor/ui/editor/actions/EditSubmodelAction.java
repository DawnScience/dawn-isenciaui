package com.isencia.passerelle.workbench.model.editor.ui.editor.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isencia.passerelle.workbench.model.editor.ui.Activator;
import com.isencia.passerelle.workbench.model.editor.ui.editor.PasserelleModelMultiPageEditor;
import com.isencia.passerelle.workbench.model.editor.ui.palette.SubModelPaletteItemDefinition;
import com.isencia.passerelle.workbench.model.ui.IPasserelleMultiPageEditor;
import com.isencia.passerelle.workbench.model.ui.utils.EclipseUtils;
import com.isencia.passerelle.workbench.model.utils.ModelUtils;

public class EditSubmodelAction extends Action {
	
	private static final Logger logger = LoggerFactory.getLogger(EditSubmodelAction.class);
	
	private final String icon = "icons/edit.gif";
	private Object definition;

	public EditSubmodelAction(Object actionOrGroup) {
		
		super();
		setId("EditSubModel");
		setText("Edit Composite");
		this.definition = actionOrGroup;
		Activator.getImageDescriptor(icon);
		setHoverImageDescriptor(Activator.getImageDescriptor(icon));
		setImageDescriptor(Activator.getImageDescriptor(icon));
		setDisabledImageDescriptor(Activator.getImageDescriptor(icon));
		setEnabled(checkEnabled());
	}

	protected boolean checkEnabled() {
        if (!(definition instanceof SubModelPaletteItemDefinition)) return false;
        SubModelPaletteItemDefinition item = (SubModelPaletteItemDefinition)definition;
		return item.getPath() != null && item.getWorkSpace() != null;
	}

	@Override
	public void run() {
        if (!(definition instanceof SubModelPaletteItemDefinition)) return;
		if (definition != null) {
			try {
			    final SubModelPaletteItemDefinition item = (SubModelPaletteItemDefinition)definition;
				final String name = item.getName();
				
				final IProject pass = ModelUtils.getPasserelleProject();
				final IFile    file = pass.getFile(name+".moml");

				final IPasserelleMultiPageEditor ed = (IPasserelleMultiPageEditor)EclipseUtils.openEditor(file, PasserelleModelMultiPageEditor.ID);
				ed.setPasserelleEditorActive();
				
			} catch (Exception e) {
				logger.error("Cannot edit submodel!", e);
			}
		}
	}

}
