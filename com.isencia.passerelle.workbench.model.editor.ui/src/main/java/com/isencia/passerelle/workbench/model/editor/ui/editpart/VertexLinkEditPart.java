package com.isencia.passerelle.workbench.model.editor.ui.editpart;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ptolemy.actor.IOPort;
import ptolemy.kernel.Relation;
import ptolemy.kernel.util.ChangeListener;
import ptolemy.kernel.util.ChangeRequest;
import ptolemy.kernel.util.Changeable;
import ptolemy.moml.Vertex;

import com.isencia.passerelle.workbench.model.editor.ui.editpolicy.RelationEndpointEditPolicy;
import com.isencia.passerelle.workbench.model.editor.ui.editpolicy.VertexRelationDeletePolicy;
import com.isencia.passerelle.workbench.model.ui.VertexLink;

/**
 * Implements a Relation Editpart to represent a Wire like connection.
 * 
 */
public class VertexLinkEditPart extends AbstractConnectionEditPart
		implements ChangeListener {
	public Relation getRelation() {
		return ((VertexLink) getModel()).getRelation();
	}

	public IOPort getPort() {
		return ((VertexLink) getModel()).getPort();
	}

	public Vertex getVertex() {
		return ((VertexLink) getModel()).getTargetVertex();
	}

	private static Logger logger = LoggerFactory
			.getLogger(VertexLinkEditPart.class);

	public static final Color alive = new Color(Display.getDefault(), 0, 74,
			168), dead = new Color(Display.getDefault(), 0, 0, 0);

	private AccessibleEditPart acc;

	public Logger getLogger() {
		return logger;
	}

	public void activate() {
		super.activate();
	}

	public void deactivate() {
		super.deactivate();
	}

	public void activateFigure() {
		super.activateFigure();
		if (getRelation() instanceof Changeable) {
			Changeable changeable = (Changeable) getRelation();
			changeable.addChangeListener(this);
		}
	}

	public void deactivateFigure() {
		if (getRelation() instanceof Changeable) {
			Changeable changeable = (Changeable) getRelation();
			changeable.removeChangeListener(this);
		}
		super.deactivateFigure();
	}

	/**
	 * Adds extra EditPolicies as required.
	 */
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new RelationEndpointEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_ROLE,
				new VertexRelationDeletePolicy());
	}

	/**
	 * Returns a newly created Figure to represent the connection.
	 * 
	 * @return The created Figure.
	 */
	protected IFigure createFigure() {
		PolylineConnection connection = new PolylineConnection();
		ManhattanConnectionRouter connectionRouter = new ManhattanConnectionRouter();
		connection.setConnectionRouter(connectionRouter);
		return connection;
	}

	public AccessibleEditPart getAccessibleEditPart() {
		if (acc == null)
			acc = new AccessibleGraphicalEditPart() {
				public void getName(AccessibleEvent e) {
					e.result = "Link";
				}
			};

		return acc;
	}

	/**
	 * Returns the Figure associated with this, which draws the Wire.
	 * 
	 * @return Figure of this.
	 */
	protected IFigure getWireFigure() {
		return (PolylineConnection) getFigure();
	}

	/**
	 * Refreshes the visual aspects of this, based upon the model (Wire). It
	 * changes the wire color depending on the state of Wire.
	 * 
	 */
	protected void refreshVisuals() {
	}

	@Override
	public void changeExecuted(ChangeRequest change) {
	}

	@Override
	public void changeFailed(ChangeRequest change, Exception exception) {
		getLogger().error("Error executing ChangeRequest", exception);
	}

}
