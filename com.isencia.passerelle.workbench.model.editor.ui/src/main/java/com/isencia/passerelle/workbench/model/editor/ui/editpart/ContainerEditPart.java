package com.isencia.passerelle.workbench.model.editor.ui.editpart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import ptolemy.actor.CompositeActor;
import ptolemy.actor.IOPort;
import ptolemy.data.expr.Parameter;
import ptolemy.kernel.Relation;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.moml.Vertex;
import ptolemy.vergil.kernel.attributes.TextAttribute;

import com.isencia.passerelle.model.Flow;

/**
 * Provides support for Container EditParts.
 */
abstract public class ContainerEditPart extends AbstractBaseEditPart {

	private boolean showChildren = true;
	// This actor will be used as offset. It's not possible to have multiple editors with different model
	private CompositeActor actor;
	public CompositeActor getCompositeActor() {
		return actor;
	}

	public ContainerEditPart(CompositeActor actor) {
		super();
		this.actor = actor;
	}

	public ContainerEditPart(boolean showChildren) {
		super();
		this.showChildren = showChildren;
	}

	/**
	 * Installs the desired EditPolicies for this.
	 */
	protected void createEditPolicies() {
	}

	/**
	 * Returns the model of this as a CompositeActor.
	 * 
	 * @return CompositeActor of this.
	 */
	protected CompositeActor getModelDiagram(CompositeActor actor) {
		if (actor == null)
			return (CompositeActor) getModel();
		return actor;
	}

	/**
	 * Returns the children of this through the model.
	 * 
	 * @return Children of this as a List.
	 */
	protected List getModelChildren() {
		if (!showChildren)
			return Collections.EMPTY_LIST;
		CompositeActor modelDiagram = getModelDiagram(actor);

		ArrayList children = new ArrayList();
		
		List entities = modelDiagram.entityList();
		if (entities != null)
			children.addAll(entities);

		if (modelDiagram.getContainer() == null
				&& modelDiagram.getDirector() != null)
			children.add(modelDiagram.getDirector());

		
		children.addAll(modelDiagram.attributeList(TextAttribute.class));
		children.addAll(modelDiagram.attributeList(IOPort.class));
		children.addAll(modelDiagram.inputPortList());
		children.addAll(modelDiagram.outputPortList());

		Enumeration relations = modelDiagram.getRelations();
		while (relations.hasMoreElements()) {

			Object nextElement = relations.nextElement();
			children.addAll(getVertexModelChildren((Relation) nextElement));
		}
		return children;
	}

	protected List getVertexModelChildren(Relation relation) {
		ArrayList children = new ArrayList();

		Enumeration attributes = relation.getAttributes();
		while (attributes.hasMoreElements()) {

			Object nextElement = attributes.nextElement();
			if (nextElement instanceof Vertex)
				children.add(nextElement);
		}
		return children;
	}

}
