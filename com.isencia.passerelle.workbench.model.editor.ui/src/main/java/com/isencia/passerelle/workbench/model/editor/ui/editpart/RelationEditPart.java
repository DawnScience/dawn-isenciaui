package com.isencia.passerelle.workbench.model.editor.ui.editpart;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ptolemy.kernel.Relation;
import ptolemy.kernel.util.ChangeListener;
import ptolemy.kernel.util.ChangeRequest;
import ptolemy.kernel.util.Changeable;
import ptolemy.kernel.util.NamedObj;

import com.isencia.passerelle.workbench.model.editor.ui.editor.PasserellRootEditPart;
import com.isencia.passerelle.workbench.model.editor.ui.editor.actions.RouterFactory;
import com.isencia.passerelle.workbench.model.editor.ui.editpolicy.RelationDeletePolicy;
import com.isencia.passerelle.workbench.model.editor.ui.editpolicy.RelationEndpointEditPolicy;
import com.isencia.passerelle.workbench.model.editor.ui.router.SCAManhattanConnectionRouter;

/**
 * Implements a Relation Editpart to represent a Wire like connection.
 * 
 */
public class RelationEditPart extends AbstractConnectionEditPart implements
		ChangeListener, EditPartListener {

	private static Logger logger = LoggerFactory
			.getLogger(RelationEditPart.class);

	private static final Color alive = new Color(Display.getDefault(), 0, 74,
			168), dead = new Color(Display.getDefault(), 0, 0, 0);

	private AccessibleEditPart acc;

	public Logger getLogger() {
		return logger;
	}

	public void activate() {
		super.activate();
		if (getSource() != null) {
			getSource().addEditPartListener(this);
		}
	}

	public void deactivate() {
		super.deactivate();
		if (getSource() != null) {
			getSource().removeEditPartListener(this);
		}
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

		installEditPolicy(EditPolicy.CONNECTION_ROLE,
				new RelationDeletePolicy());

		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new RelationEndpointEditPolicy());

	}

	/**
	 * Returns a newly created Figure to represent the connection.
	 * 
	 * @return The created Figure.
	 */
	protected IFigure createFigure() {

		final PolylineConnection connection = RouterFactory.getConnection();
		connection.setForegroundColor(ColorConstants.gray);
		final PasserellRootEditPart root = (PasserellRootEditPart) getRoot();
		connection.setConnectionRouter(RouterFactory.getRouter(root
				.getScaledLayers()));
		return connection;
	}

	public AccessibleEditPart getAccessibleEditPart() {
		if (acc == null)
			acc = new AccessibleGraphicalEditPart() {
				public void getName(AccessibleEvent e) {
					e.result = "Link";
					// e.result = LogicMessages.Wire_LabelText;
				}
			};

		return acc;
	}

	/**
	 * Returns the model of this represented as a Relation.
	 * 
	 * @return Model of this as <code>Relation</code>
	 */
	public Relation getRelation() {
		return (Relation) getModel();
	}

	/**
	 * Refreshes the visual aspects of this, based upon the model (Wire). It
	 * changes the wire color depending on the state of Wire.
	 * 
	 */
	protected void updateSelected() {

		final EditPart source = getSource();
		if (source != null) {

			final int sel = source.getSelected();
			final Polyline line = (Polyline) getFigure();
			if (sel != SELECTED_NONE) {
				line.setLineWidth(2);

			} else {
				line.setLineWidth(1);
			}
		}
	}

	@Override
	public void changeExecuted(ChangeRequest change) {
		getLogger().trace("ChangeRequest executed in RelationEditPart");
	}

	@Override
	public void changeFailed(ChangeRequest change, Exception exception) {
		getLogger().trace("Error executing ChangeRequest", exception);
	}

	@Override
	public void childAdded(EditPart child, int index) {
	}

	@Override
	public void partActivated(EditPart editpart) {
	}

	@Override
	public void partDeactivated(EditPart editpart) {
	}

	@Override
	public void removingChild(EditPart child, int index) {
	}

	@Override
	public void selectedStateChanged(EditPart editpart) {
		updateSelected();
	}

}
