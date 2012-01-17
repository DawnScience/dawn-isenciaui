package com.isencia.passerelle.workbench.model.editor.ui.editor.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isencia.passerelle.workbench.model.editor.ui.Activator;
import com.isencia.passerelle.workbench.model.editor.ui.palette.PaletteItemFactory;
import com.isencia.passerelle.workbench.model.editor.ui.palette.SubModelPaletteItemDefinition;
import com.isencia.passerelle.workbench.model.editor.ui.views.ActorTreeView;
import com.isencia.passerelle.workbench.model.ui.utils.EclipseUtils;
import com.isencia.passerelle.workbench.model.utils.SubModelUtils;

public class DeleteSubmodelAction extends Action {
	
	private static final Logger logger = LoggerFactory.getLogger(DeleteSubmodelAction.class);
	
	private final String icon = "icons/delete.gif";
	private Object definition;

	public DeleteSubmodelAction(Object actionOrGroup) {
		
		super();
		setId(getClass().getName());
		setText("Delete Composite");
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
				
				PaletteItemFactory factory = PaletteItemFactory.getInstance();
                factory.removeSubModel(name);
				SubModelUtils.deleteSubModel(name);
				
                SubModelViewUtils.refreshPallette();
			} catch (Exception e) {
				logger.error("Cannot edit submodel!", e);
			}
		}
	}

}
