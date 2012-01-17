package com.isencia.passerelle.workbench.model.editor.ui.editpart;

import org.eclipse.draw2d.Clickable;
import org.eclipse.jface.resource.ImageDescriptor;

import ptolemy.actor.Actor;
import ptolemy.actor.CompositeActor;

import com.isencia.passerelle.workbench.model.editor.ui.editor.PasserelleModelMultiPageEditor;
import com.isencia.passerelle.workbench.model.editor.ui.figure.CompositeActorFigure;
import com.isencia.passerelle.workbench.model.editor.ui.figure.SubModelActorFigure;
import com.isencia.passerelle.workbench.model.editor.ui.palette.SubModelPaletteItemDefinition;

public class SubModelEditPart extends CompositeActorEditPart {

	@Override
	protected ImageDescriptor getIcon() {
		// TODO Auto-generated method stub
		return SubModelPaletteItemDefinition.IMAGE_SUBMODEL;
	}

	public SubModelEditPart(boolean showChildren,
			PasserelleModelMultiPageEditor multiPageEditorPart) {
		super(showChildren, multiPageEditorPart);
		// TODO Auto-generated constructor stub
	}

	public SubModelEditPart(CompositeActor actor) {
		super(actor);
		// TODO Auto-generated constructor stub
	}
	protected CompositeActorFigure createCompositeActorFigure(Clickable button,
			Actor actorModel, ImageDescriptor imageDescriptor) {
		SubModelActorFigure actorFigure = new SubModelActorFigure(actorModel
				.getDisplayName(),getModel().getClass(), createImage(imageDescriptor),
				new Clickable[] { button });
		return actorFigure;
	}
//	protected void createNodeEditPolicies() {
//		
//	}
}
