package com.isencia.passerelle.workbench.model.editor.ui.editpolicy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.GroupRequest;

import ptolemy.actor.TypedIORelation;
import ptolemy.kernel.CompositeEntity;

import com.isencia.passerelle.workbench.model.ui.VertexLink;
import com.isencia.passerelle.workbench.model.ui.command.DeleteVertexConnectionCommand;

public class VertexRelationDeletePolicy extends
		org.eclipse.gef.editpolicies.ConnectionEditPolicy {

	private DeleteVertexConnectionCommand getDeleteVertexConnectionCommand() {
		return new DeleteVertexConnectionCommand();
	}

	protected Command getDeleteCommand(GroupRequest request) {
		DeleteVertexConnectionCommand deleteCmd = getDeleteVertexConnectionCommand();
		deleteCmd.setParent((CompositeEntity) getHost().getRoot().getContents()
				.getModel());
		VertexLink vr = (VertexLink) getHost().getModel();
		deleteCmd.setConnection((TypedIORelation) vr.getRelation());
		if (vr.getPort() != null)
			deleteCmd.setPort(vr.getPort());
		else
			deleteCmd.setVertex(vr.getTargetVertex());
		return deleteCmd;
	}

}
