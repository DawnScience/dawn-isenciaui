package com.isencia.passerelle.workbench.model.editor.ui.editpart;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.eclipse.draw2d.Clickable;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.AccessibleAnchorProvider;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.DropRequest;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ptolemy.actor.Actor;
import ptolemy.actor.IOPort;
import ptolemy.actor.IORelation;
import ptolemy.actor.TypedIOPort;
import ptolemy.actor.TypedIORelation;
import ptolemy.kernel.Port;
import ptolemy.kernel.Relation;
import ptolemy.kernel.util.NamedObj;
import ptolemy.moml.Vertex;

import com.isencia.passerelle.core.ControlPort;
import com.isencia.passerelle.core.ErrorPort;
import com.isencia.passerelle.workbench.model.editor.ui.Activator;
import com.isencia.passerelle.workbench.model.editor.ui.editpolicy.ActorEditPolicy;
import com.isencia.passerelle.workbench.model.editor.ui.editpolicy.ComponentNodeDeletePolicy;
import com.isencia.passerelle.workbench.model.editor.ui.figure.ActorFigure;
import com.isencia.passerelle.workbench.model.editor.ui.figure.PortFigure;
import com.isencia.passerelle.workbench.model.editor.ui.figure.RectangularActorFigure;
import com.isencia.passerelle.workbench.model.editor.ui.palette.PaletteItemFactory;
import com.isencia.passerelle.workbench.model.editor.ui.properties.ActorDialog;
import com.isencia.passerelle.workbench.model.editor.ui.views.ActorAttributesView;
import com.isencia.passerelle.workbench.model.ui.utils.EclipseUtils;
import com.isencia.passerelle.workbench.model.utils.ModelUtils;

public class ActorEditPart extends AbstractNodeEditPart implements
		IActorNodeEditPart {

	private final static Logger logger = LoggerFactory
			.getLogger(ActorEditPart.class);

	public final static ImageDescriptor IMAGE_DESCRIPTOR_PROPERTIES = Activator
			.getImageDescriptor("icons/input.gif");

	public final static Color COLOR_ERROR_PORT = new Color(null, 192, 20, 20);
	public final static Color COLOR_CONTROL_PORT = new Color(null, 50, 50, 255);

	public final static ImageDescriptor IMAGE_COMMENT = Activator
			.getImageDescriptor("icons/comment.png");
	public final static ImageDescriptor IMAGE_DESCRIPTOR_ACTOR = Activator
			.getImageDescriptor("icons/actor.gif");
	public final static ImageDescriptor IMAGE_DESCRIPTOR_PARAMETER = Activator
			.getImageDescriptor("icons/parameter.gif");
	public final static ImageDescriptor IMAGE_DESCRIPTOR_INPUTPORT = Activator
			.getImageDescriptor("icons/input.gif");
	public final static ImageDescriptor IMAGE_DESCRIPTOR_OUTPUTPORT = Activator
			.getImageDescriptor("icons/output.gif");

	public Logger getLogger() {
		return logger;
	}

	protected void createEditPolicies() {
		if (getParent() instanceof DiagramEditPart) {
			installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
					new ActorEditPolicy(((DiagramEditPart) getParent())
							.getMultiPageEditorPart(), this));
		}
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new ComponentNodeDeletePolicy());
	}

	/**
	 * Returns a newly created Figure to represent this.
	 * 
	 * @return Figure of this EditPart
	 */
	protected IFigure createFigure() {
		Actor actorModel = getActor();
		ImageFigure drillDownImageFigure = new ImageFigure(
				createImage(IMAGE_DESCRIPTOR_ACTOR));
		drillDownImageFigure.setAlignment(PositionConstants.SOUTH);
		drillDownImageFigure.setBorder(new MarginBorder(0, 0, 5, 0));

		ImageDescriptor imageDescriptor = PaletteItemFactory.getInstance().getIcon(
				actorModel.getClass());
		if (imageDescriptor == null) {
			imageDescriptor = IMAGE_DESCRIPTOR_ACTOR;
		}
		ActorFigure actorFigure = getActorFigure(actorModel.getDisplayName(),
				createImage(imageDescriptor), new Clickable[] {  });
		List<TypedIOPort> inputPortList = actorModel.inputPortList();
		if (inputPortList != null) {
			for (TypedIOPort inputPort : inputPortList) {
				PortFigure portFigure = actorFigure.addInput(inputPort
						.getName(), inputPort.getDisplayName());
				if (inputPort instanceof ErrorPort) {
					portFigure.setFillColor(COLOR_ERROR_PORT);
				} else if (inputPort instanceof ControlPort) {
					portFigure.setFillColor(COLOR_CONTROL_PORT);
				}
			}
		}
		// Add SourceConnectionAnchors
		List<TypedIOPort> outputPortList = actorModel.outputPortList();
		if (outputPortList != null) {
			for (TypedIOPort outputPort : outputPortList) {
				PortFigure portFigure = actorFigure.addOutput(outputPort
						.getName(), outputPort.getDisplayName());
				if (outputPort instanceof ErrorPort) {
					portFigure.setFillColor(COLOR_ERROR_PORT);
				} else if (outputPort instanceof ControlPort) {
					portFigure.setFillColor(COLOR_CONTROL_PORT);
				}
			}
		}
		return actorFigure;
	}

	/**
	 * Overide to return alternative actor figures
	 * 
	 * @param displayName
	 * @param createImage
	 * @param clickables
	 * @return
	 */
	protected ActorFigure getActorFigure(String displayName, Image createImage,
			Clickable[] clickables) {
		return new RectangularActorFigure(displayName, getModel().getClass(),
				createImage, clickables);
	}

	public Object getAdapter(Class key) {
		if (key == AccessibleAnchorProvider.class)
			return new DefaultAccessibleAnchorProvider() {
				public List<Point> getSourceAnchorLocations() {
					List<Point> list = new ArrayList<Point>();
					Vector<ConnectionAnchor> sourceAnchors = getComponentFigure()
							.getSourceConnectionAnchors();
					for (int i = 0; i < sourceAnchors.size(); i++) {
						ConnectionAnchor anchor = (ConnectionAnchor) sourceAnchors
								.get(i);
						list.add(anchor.getReferencePoint()
								.getTranslated(0, -3));
					}
					return list;
				}

				public List<Point> getTargetAnchorLocations() {
					List<Point> list = new ArrayList<Point>();
					Vector<ConnectionAnchor> targetAnchors = getComponentFigure()
							.getTargetConnectionAnchors();
					for (int i = 0; i < targetAnchors.size(); i++) {
						ConnectionAnchor anchor = (ConnectionAnchor) targetAnchors
								.get(i);
						list
								.add(anchor.getReferencePoint().getTranslated(
										0, 3));
					}
					return list;
				}
			};
		return super.getAdapter(key);
	}

	/**
	 * Returns the Figure of this as a ActorFigure.
	 * 
	 * @return ActorFigure of this.
	 */
	public ActorFigure getComponentFigure() {
		return (ActorFigure) getFigure();
	}

	/**
	 * Returns the model of this as a Actor.
	 * 
	 * @return Model of this as an Actor.
	 */
	public Actor getActor() {
		Object model = getModel();
		if (model instanceof Actor)
			return (Actor) model;
		return null;
	}

	public void setSelected(int i) {
		super.setSelected(i);
		refreshVisuals();
	}

	@Override
	protected List getModelSourceConnections() {
		Set<Relation> connectedRelations = ModelUtils.getConnectedRelations(
				(NamedObj) getModel(), ModelUtils.ConnectionType.SOURCE);
		List modelSourceConnections = new ArrayList();
		for (Relation rel : connectedRelations) {
			Vertex vertex = getVertex(rel);
			if (vertex != null) {
				List<IOPort> ports = ModelUtils.getPorts(rel,
						(NamedObj) getModel());
				for (IOPort port : ports) {
					Object relation = VertexEditPart.getRelation(
							(TypedIORelation) rel, port, vertex, false);
					modelSourceConnections.add(relation);
				}
			} else {
				modelSourceConnections.add(rel);
			}
		}
		return modelSourceConnections;
	}

	@Override
	protected List getModelTargetConnections() {
		Set<Relation> connectedRelations = ModelUtils.getConnectedRelations(
				(NamedObj) getModel(), ModelUtils.ConnectionType.TARGET);
		List modelTargetConnections = new ArrayList();
		for (Relation rel : connectedRelations) {
			Vertex vertex = getVertex(rel);
			if (vertex != null) {
				for (IOPort port : ModelUtils.getPorts(rel,
						(NamedObj) getModel())) {
					modelTargetConnections.add(VertexEditPart.getRelation(
							(TypedIORelation) rel, port, vertex, false));
				}

			} else {
				modelTargetConnections.add(rel);
			}
		}
		return modelTargetConnections;
	}

	public Vertex getVertex(Relation model) {
		Enumeration attributes = model.getAttributes();
		while (attributes.hasMoreElements()) {
			Object temp = attributes.nextElement();
			if (temp instanceof Vertex) {
				return (Vertex) temp;
			}
		}
		return null;
	}

	/**
	 * Returns the connection anchor for the given ConnectionEditPart's source.
	 * 
	 * @return ConnectionAnchor.
	 */
	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connEditPart) {
		getLogger().trace(
				"Get SourceConnectionAnchor based on ConnectionEditPart");
		Port port = null;
		if (connEditPart instanceof VertexLinkEditPart) {
			port = ((VertexLinkEditPart) connEditPart).getPort();

		} else {
			Relation relation = (Relation) connEditPart.getModel();
			List linkedPortList = ((IORelation) relation)
					.linkedSourcePortList();
			if (linkedPortList == null || linkedPortList.size() == 0)
				return null;
			port = (Port) linkedPortList.get(0);

		}
		ConnectionAnchor connectionAnchor = getComponentFigure()
				.getConnectionAnchor(port.getName());
		return connectionAnchor;
	}

	/**
	 * Returns the connection anchor of a source connection which is at the
	 * given point.
	 * 
	 * @return ConnectionAnchor.
	 */
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		Point pt = new Point(((DropRequest) request).getLocation());
		return getComponentFigure().getSourceConnectionAnchorAt(pt);
	}

	/**
	 * Returns the connection anchor for the given ConnectionEditPart's target.
	 * 
	 * @return ConnectionAnchor.
	 */
	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connEditPart) {
		getLogger().trace(
				"Get TargetConnectionAnchor based on ConnectionEditPart");
		Port port = null;
		if (connEditPart instanceof VertexLinkEditPart) {
			port = ((VertexLinkEditPart) connEditPart).getPort();

		} else {

			IORelation relation = (IORelation) connEditPart.getModel();
			List linkedPortList = relation.linkedDestinationPortList();
			if (linkedPortList == null || linkedPortList.size() == 0)
				return null;
			port = (Port) linkedPortList.get(0);
		}

		ConnectionAnchor connectionAnchor = getComponentFigure()
				.getConnectionAnchor(port.getName());
		return connectionAnchor;
	}

	/**
	 * Returns the Output Port based on a given Anchor
	 * 
	 * @return Port.
	 */
	public Port getSourcePort(ConnectionAnchor anchor) {
		getLogger().trace("Get Source port  based on anchor");

		ActorFigure anchorFigure = getComponentFigure();
		List outputPortList = getActor().outputPortList();
		for (Iterator iterator = outputPortList.iterator(); iterator.hasNext();) {
			Port port = (Port) iterator.next();
			String connectionAnchorName = anchorFigure
					.getConnectionAnchorName(anchor);
			String portName = port.getName();
			if (portName != null && portName.equals(connectionAnchorName)) {
				return port;
			}
		}
		return null;
	}

	/**
	 * Returns the Input Port based on a given Anchor
	 * 
	 * @return Port.
	 */
	public Port getTargetPort(ConnectionAnchor anchor) {
		getLogger().trace("Get Target port  based on anchor");

		ActorFigure anchorFigure = getComponentFigure();
		List inputPortList = getActor().inputPortList();
		for (Iterator iterator = inputPortList.iterator(); iterator.hasNext();) {
			Port port = (Port) iterator.next();
			if (port.getName() != null
					&& port.getName().equals(
							anchorFigure.getConnectionAnchorName(anchor)))
				return port;
		}
		return null;
	}

	/**
	 * Returns the connection anchor of a terget connection which is at the
	 * given point.
	 * 
	 * @return ConnectionAnchor.
	 */
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		Point pt = new Point(((DropRequest) request).getLocation());
		return getComponentFigure().getTargetConnectionAnchorAt(pt);
	}

	/**
	 * Returns the name of the given connection anchor.
	 * 
	 * @return The name of the ConnectionAnchor as a String.
	 */
	final protected String mapConnectionAnchorToTerminal(ConnectionAnchor c) {
		return getComponentFigure().getConnectionAnchorName(c);
	}

}
