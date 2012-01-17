package com.isencia.passerelle.workbench.model.editor.ui.editpolicy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.GroupRequest;

import ptolemy.actor.TypedIORelation;
import ptolemy.kernel.CompositeEntity;

import com.isencia.passerelle.workbench.model.ui.command.DeleteComponentCommand;
import com.isencia.passerelle.workbench.model.ui.command.DeleteConnectionCommand;

public class RelationDeletePolicy extends
		org.eclipse.gef.editpolicies.ConnectionEditPolicy {

	private DeleteConnectionCommand getDeleteConnectionCommand() {
		return new DeleteConnectionCommand();
	}

	protected Command getDeleteCommand(GroupRequest request) {
		DeleteConnectionCommand deleteCmd = getDeleteConnectionCommand();
		deleteCmd.setParent((CompositeEntity) getHost().getRoot().getContents()
				.getModel());
		deleteCmd.setConnection((TypedIORelation) getHost().getModel());
		return deleteCmd;
	}

}
