package com.isencia.passerelle.workbench.model.editor.ui.editpolicy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ptolemy.actor.Actor;
import ptolemy.actor.TypedIORelation;
import ptolemy.kernel.ComponentPort;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.Port;
import ptolemy.kernel.util.NamedObj;

import com.isencia.passerelle.workbench.model.editor.ui.editor.PasserelleModelMultiPageEditor;
import com.isencia.passerelle.workbench.model.editor.ui.editpart.ActorEditPart;
import com.isencia.passerelle.workbench.model.editor.ui.editpart.IActorNodeEditPart;
import com.isencia.passerelle.workbench.model.editor.ui.editpart.RelationEditPart;
import com.isencia.passerelle.workbench.model.editor.ui.editpart.VertexEditPart;
import com.isencia.passerelle.workbench.model.editor.ui.editpart.VertexLinkEditPart;
import com.isencia.passerelle.workbench.model.ui.VertexLink;
import com.isencia.passerelle.workbench.model.ui.command.CreateConnectionCommand;
import com.isencia.passerelle.workbench.model.ui.command.ReConnectCommand;

/**
 * <code>ActorEditPolicy</code> creates commands for connection initiation and
 * completion. It uses the default feedback provided by the
 * <code><b>GraphicalNodeEditPolicy</b></code> base class
 * 
 * @author Dirk Jacobs
 * 
 */
public class ActorEditPolicy extends GraphicalNodeEditPolicy {
	private Map<ReconnectRequest, ReConnectCommand> reconnectMap = new HashMap<ReconnectRequest, ReConnectCommand>();
	private final static Logger logger = LoggerFactory
			.getLogger(ActorEditPolicy.class);
	private CreateConnectionCommand CreateConnectionCommand;
	private PasserelleModelMultiPageEditor editor;

	public PasserelleModelMultiPageEditor getEditor() {
		return editor;
	}

	private CreateConnectionCommand getCreateConnectionCommand() {
		if (CreateConnectionCommand == null) {
			return CreateConnectionCommand = new CreateConnectionCommand(editor);
		}
		return CreateConnectionCommand;
	}

	private IActorNodeEditPart node;

	public ActorEditPolicy(PasserelleModelMultiPageEditor editor,
			IActorNodeEditPart node) {
		super();
		this.editor = editor;
		this.node = node;
	}

	public Logger getLogger() {
		return logger;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#
	 * getConnectionCreateCommand
	 * (org.eclipse.gef.requests.CreateConnectionRequest)
	 */
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		IActorNodeEditPart editPart = getActorEditPart();
		if (getLogger().isDebugEnabled())
			getLogger().trace(
					"getConnectionCreateCommand for editPart : " + editPart);
		NamedObj port = null;
		if (editPart instanceof VertexEditPart) {
			port = (NamedObj) editPart.getModel();
		} else {
			ConnectionAnchor anchor = editPart
					.getSourceConnectionAnchor(request);
			port = (ComponentPort) ((IActorNodeEditPart) editPart)
					.getSourcePort(anchor);
		}
		if (port != null) {
			CreateConnectionCommand command = getCreateConnectionCommand();
			command.setSource(port);
			request.setStartCommand(command);
			return command;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#
	 * getConnectionCompleteCommand
	 * (org.eclipse.gef.requests.CreateConnectionRequest)
	 */
	protected Command getConnectionCompleteCommand(
			CreateConnectionRequest request) {
		IActorNodeEditPart editPart = getActorEditPart();
		if (getLogger().isDebugEnabled())
			getLogger().debug(
					"getConnectionCompleteCommand for editPart : " + editPart);
		NamedObj port = null;
		if (editPart instanceof VertexEditPart) {
			port = (NamedObj) editPart.getModel();
		} else {
			ConnectionAnchor anchor = editPart
					.getTargetConnectionAnchor(request);
			port = (ComponentPort) ((IActorNodeEditPart) editPart)
					.getTargetPort(anchor);
		}

		if (port != null) {
			CreateConnectionCommand command = (CreateConnectionCommand) request
					.getStartCommand();
			command.setTarget(port);
			return command;
		}
		return null;
	}

	protected Actor getActorModel() {
		return (Actor) getHost().getModel();
	}

	protected IActorNodeEditPart getActorEditPart() {
		return (IActorNodeEditPart) getHost();
	}

	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		ReConnectCommand cmd = createReconnectCommand(request,true);

		EditPart target = request.getTarget();
		if (target instanceof ActorEditPart) {
			ActorEditPart actorEditPart = (ActorEditPart) target;
			ConnectionAnchor anchor = actorEditPart
					.getTargetConnectionAnchor(request);
			Port port = actorEditPart.getTargetPort(anchor);
			if (!port.equals(cmd.getTarget())) {
				cmd.setNewTarget(port);
			}

		} else if (target instanceof VertexEditPart) {
			if (!target.getModel().equals(cmd.getTarget())) {
				cmd.setNewTarget((NamedObj) target.getModel());
			}

		}
		return cmd;
	}

	private ReConnectCommand createReconnectCommand(ReconnectRequest request,
			boolean target) {
		ReConnectCommand cmd = reconnectMap.get(request);
		if (cmd == null) {
			cmd = new ReConnectCommand();
			cmd.setEditor(editor);
			reconnectMap.put(request, cmd);
			ConnectionEditPart connection = request.getConnectionEditPart();
			if (connection instanceof RelationEditPart) {
				TypedIORelation model = (TypedIORelation) connection.getModel();
				cmd.setConnection(model);
				cmd.setParent((CompositeEntity) getHost().getRoot()
						.getContents().getModel());
				List linkedPortList = model.linkedDestinationPortList();
				cmd.setTarget((Port) linkedPortList.get(0));
				linkedPortList = model.linkedSourcePortList();
				cmd.setSource((Port) linkedPortList.get(0));

			} else if (connection instanceof VertexLinkEditPart) {
				VertexLinkEditPart link = (VertexLinkEditPart) connection;
				cmd.setVertexLink((VertexLink) link.getModel());
				cmd.setParent((CompositeEntity) getHost().getRoot()
						.getContents().getModel());
			}
		}
		return cmd;
	}

	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		ReConnectCommand cmd = createReconnectCommand(request,false);

		EditPart target = request.getTarget();
		if (target instanceof IActorNodeEditPart) {
			IActorNodeEditPart actorEditPart = (IActorNodeEditPart) target;
			ConnectionAnchor anchor = actorEditPart
					.getSourceConnectionAnchor(request);
			Port port = actorEditPart.getSourcePort(anchor);
			if (!port.equals(cmd.getSource())) {
				cmd.setNewSource(port);
			}

		} else if (target instanceof VertexEditPart) {
			if (!target.getModel().equals(cmd.getSource())) {
				cmd.setNewSource((NamedObj) target.getModel());
			}

		}
		return cmd;
	}

}
