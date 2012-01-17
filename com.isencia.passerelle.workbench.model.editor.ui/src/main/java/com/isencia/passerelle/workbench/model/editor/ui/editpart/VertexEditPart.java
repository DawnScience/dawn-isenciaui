package com.isencia.passerelle.workbench.model.editor.ui.editpart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.AccessibleAnchorProvider;
import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.DropRequest;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;

import ptolemy.actor.IOPort;
import ptolemy.actor.TypedIORelation;
import ptolemy.kernel.Port;
import ptolemy.kernel.Relation;
import ptolemy.kernel.util.ChangeRequest;
import ptolemy.kernel.util.NamedObj;
import ptolemy.moml.Vertex;

import com.isencia.passerelle.workbench.model.editor.ui.Activator;
import com.isencia.passerelle.workbench.model.editor.ui.editpolicy.ActorEditPolicy;
import com.isencia.passerelle.workbench.model.editor.ui.editpolicy.ComponentNodeDeletePolicy;
import com.isencia.passerelle.workbench.model.editor.ui.figure.VertexFigure;
import com.isencia.passerelle.workbench.model.editor.ui.properties.CommentPropertySource;
import com.isencia.passerelle.workbench.model.editor.ui.properties.EntityPropertySource;
import com.isencia.passerelle.workbench.model.ui.VertexLink;
import com.isencia.passerelle.workbench.model.ui.command.ChangeActorPropertyCommand;
import com.isencia.passerelle.workbench.model.ui.command.CreateConnectionCommand;
import com.isencia.passerelle.workbench.model.ui.command.DeleteComponentCommand;
import com.isencia.passerelle.workbench.model.ui.command.DeleteConnectionCommand;
import com.isencia.passerelle.workbench.model.ui.command.DeleteVertexConnectionCommand;
import com.isencia.passerelle.workbench.model.utils.ModelChangeRequest;
import com.isencia.passerelle.workbench.model.utils.ModelUtils;

/**
 * <code>PortEditPart</code> is the EditPart for the Port model objects
 * 
 * @author Dirk Jacobs
 */
public class VertexEditPart extends AbstractNodeEditPart implements IActorNodeEditPart{
	public static Map<Vertex, Map<NamedObj, VertexLink>> vertexRelationMap = new HashMap<Vertex, Map<NamedObj, VertexLink>>();
	public static Set<Vertex> vertexRelationSources = new HashSet<Vertex>();
	public static Set<Vertex> vertexRelationTargets = new HashSet<Vertex>();

	public static boolean isSource(Vertex source) {
		if (!vertexRelationSources.contains(source)
				&& !vertexRelationTargets.contains(source)) {
			vertexRelationSources.add(source);
			return true;
		}
		return vertexRelationSources.contains(source);
	}

	public static boolean isTarget(Vertex target) {
		if (!vertexRelationSources.contains(target)) {
			vertexRelationTargets.add(target);
			return true;
		}
		return vertexRelationTargets.contains(target);
	}

	public VertexEditPart() {
		super();
	}

	@Override
	protected IFigure createFigure() {
		return new VertexFigure(((Vertex) getModel()).getName(), Vertex.class,
				null);
	}

	@Override
	protected List getModelSourceConnections() {
		List relations = new ArrayList();
		TypedIORelation relation = (TypedIORelation) ((Vertex) getModel())
				.getContainer();
		List list = relation.linkedObjectsList();

		for (Object o : relation.linkedDestinationPortList()) {
			if (list.contains(o))
				relations.add(getRelation(relation, o, (Vertex) getModel(),
						true));
		}
		for (Object o : relation.linkedObjectsList()) {
			if (o instanceof TypedIORelation) {
				Object rel = getRelation(relation, ModelUtils
						.getVertex((Relation) o), (Vertex) getModel(), true);
				if (rel != null)
					relations.add(rel);

			}
		}
		return relations;
	}

	@Override
	protected List getModelTargetConnections() {
		List relations = new ArrayList();
		TypedIORelation relation = (TypedIORelation) ((Vertex) getModel())
				.getContainer();
		List list = relation.linkedObjectsList();

		for (Object o : relation.linkedSourcePortList()) {
			if (list.contains(o))
				relations.add(getRelation(relation, o, (Vertex) getModel(),
						false));
		}
		for (Object o : relation.linkedObjectsList()) {
			if (o instanceof TypedIORelation) {
				Object rel = getRelation(relation, (Vertex) getModel(),
						ModelUtils.getVertex((Relation) o), false);
				if (rel != null)
					relations.add(rel);
			}
		}

		return relations;
	}

	public static Object getRelation(TypedIORelation relation, Object o,
			Vertex vertex, boolean isSource) {
		VertexLink relationObject = null;
		Map<NamedObj, VertexLink> map = vertexRelationMap.get(vertex);
		if (map == null) {
			map = new HashMap<NamedObj, VertexLink>();
			vertexRelationMap.put(vertex, map);
		}
		if (map.containsKey(o)) {
			relationObject = map.get(o);
		} else {

			if (o instanceof IOPort) {
				relationObject = new VertexLink((IOPort) o, vertex, isSource);
			} else {
				if ((isSource && isSource((Vertex) o))
						|| (!isSource && isTarget(vertex)))
					relationObject = new VertexLink((Vertex) o, vertex,
							isSource);
			}

			map.put((NamedObj) o, relationObject);

		}
		return relationObject;
	}

	public VertexFigure getVertexFigure() {
		return (VertexFigure) getFigure();
	}

	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connEditPart) {
		getLogger().debug(
				"Get SourceConnectionAnchor based on ConnectionEditPart");
		return getVertexFigure().getInputAnchor(
				getLocation(connEditPart, true),
				ModelUtils.getLocation((Vertex) getModel()));
	}

	private double[] getLocation(ConnectionEditPart connEditPart,
			boolean isSource) {
		Object model = connEditPart.getModel();
		double[] location = { 0, 0 };
		if (model instanceof VertexLink) {
			VertexLink relation = (VertexLink) model;
			if (relation.getPort() != null) {
				location = ModelUtils.getLocation(((VertexLink) model)
						.getPort().getContainer());
			} else if (!isSource && relation.getTargetVertex() != null) {
				location = ModelUtils.getLocation(((VertexLink) model)
						.getTargetVertex());
			} else if (isSource && relation.getSourceVertex() != null) {
				location = ModelUtils.getLocation(((VertexLink) model)
						.getSourceVertex());
			}
		}
		return location;
	}

	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connEditPart) {
		getLogger().debug(
				"Get TargetConnectionAnchor based on ConnectionEditPart");

		return getVertexFigure().getOutputAnchor(
				getLocation(connEditPart, false),
				ModelUtils.getLocation((Vertex) getModel()));
	}

	protected void createEditPolicies() {
		if (getParent() instanceof DiagramEditPart)
			installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
					new ActorEditPolicy(((DiagramEditPart) getParent())
							.getMultiPageEditorPart(), this));
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new ComponentNodeDeletePolicy());
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		Point pt = new Point(((DropRequest) request).getLocation());
		return getVertexFigure().getSourceConnectionAnchorAt(pt);
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		Point pt = new Point(((DropRequest) request).getLocation());
		return getVertexFigure().getTargetConnectionAnchorAt(pt);
	}

	public Object getAdapter(Class key) {
		if (key == AccessibleAnchorProvider.class)
			return new DefaultAccessibleAnchorProvider() {
				public List<Point> getSourceAnchorLocations() {
					List<Point> list = new ArrayList<Point>();
					List<ConnectionAnchor> sourceAnchors = getVertexFigure()
							.getInputAnchors();
					for (ConnectionAnchor sourceAnchor : sourceAnchors) {
						list.add(sourceAnchor.getReferencePoint()
								.getTranslated(0, -3));
					}
					return list;
				}

				public List<Point> getTargetAnchorLocations() {
					List<Point> list = new ArrayList<Point>();
					List<ConnectionAnchor> outputAnchors = getVertexFigure()
							.getOutputAnchors();
					for (ConnectionAnchor outputAnchor : outputAnchors) {
						list.add(outputAnchor.getReferencePoint()
								.getTranslated(0, 3));
					}

					return list;
				}
			};
		return super.getAdapter(key);
	}

	@Override
	public Port getSourcePort(ConnectionAnchor anchor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Port getTargetPort(ConnectionAnchor anchor) {
		// TODO Auto-generated method stub
		return null;
	}

}
