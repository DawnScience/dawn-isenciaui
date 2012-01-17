package com.isencia.passerelle.workbench.model.editor.ui.views;

import org.eclipse.gef.requests.CreationFactory;

import com.isencia.passerelle.workbench.model.editor.ui.palette.PaletteItemDefinition;
import com.isencia.passerelle.workbench.model.editor.ui.palette.SubModelPaletteItemDefinition;

public class TreeViewCreationFactory implements CreationFactory {
	PaletteItemDefinition selected;
	public TreeViewCreationFactory(PaletteItemDefinition selected) {
		super();
		this.selected = selected;
	}

	@Override
	public Object getObjectType() {
		// TODO Auto-generated method stub
		PaletteItemDefinition selected2 = (PaletteItemDefinition)selected;

		return selected2.getClazz();
	}
	
	@Override
	public Object getNewObject() {
		// TODO Auto-generated method stub
		PaletteItemDefinition selected2 = (PaletteItemDefinition)selected;
		if (selected2 instanceof SubModelPaletteItemDefinition){
			return (SubModelPaletteItemDefinition)selected2;
		}
		return selected2.getName();
	}

}
