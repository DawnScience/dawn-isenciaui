package com.isencia.passerelle.workbench.model.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.commands.Command;

import ptolemy.actor.Actor;
import ptolemy.actor.IOPort;
import ptolemy.actor.IORelation;
import ptolemy.actor.TypedIOPort;
import ptolemy.actor.TypedIORelation;
import ptolemy.kernel.ComponentEntity;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.Port;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;
import ptolemy.kernel.util.Nameable;
import ptolemy.kernel.util.NamedObj;
import ptolemy.vergil.kernel.attributes.TextAttribute;

import com.isencia.passerelle.workbench.model.ui.command.DeleteConnectionCommand;
import com.isencia.passerelle.workbench.model.ui.command.DeleteVertexConnectionCommand;
import com.isencia.passerelle.workbench.model.utils.ModelUtils;
import com.isencia.passerelle.workbench.model.utils.ModelUtils.ConnectionType;

public abstract class ComponentUtility {
	public static void setContainer(NamedObj child, NamedObj container) {
		try {
			if (child instanceof ComponentEntity) {

				((ComponentEntity) child)
						.setContainer((CompositeEntity) container);

			} else if (child instanceof TextAttribute) {
				((TextAttribute) child)
						.setContainer((CompositeEntity) container);
			} else if (child instanceof TypedIOPort) {
				((TypedIOPort) child).setContainer((CompositeEntity) container);
			}
		} catch (IllegalActionException e) {
			e.printStackTrace();
		} catch (NameDuplicationException e) {
			e.printStackTrace();
		}
	}

	public static List<Command> deleteConnections(NamedObj model) {
		List<Command> delecteConnectionCommands = new ArrayList<Command>();
		if (!((model instanceof Actor) || ((model instanceof Port)))) {
			return delecteConnectionCommands;
		}

		Iterator<?> sourceIterator = ModelUtils.getConnectedRelations(model,
				ConnectionType.SOURCE, true).iterator();
		while (sourceIterator.hasNext()) {
			List<Command> cmds = generateDeleteCommand(model,
					(IORelation) sourceIterator.next());
			for (Command cmd : cmds) {
				cmd.execute();
				delecteConnectionCommands.add(cmd);
			}
		}
		Iterator<?> targetIterator = ModelUtils.getConnectedRelations(model,
				ConnectionType.TARGET, true).iterator();
		while (targetIterator.hasNext()) {
			List<Command> cmds = generateDeleteCommand(model,
					(IORelation) targetIterator.next());
			for (Command cmd : cmds) {

				cmd.execute();
				delecteConnectionCommands.add(cmd);
			}
		}
		return delecteConnectionCommands;
	}

	private static List<Command> generateDeleteCommand(NamedObj model,
			IORelation relation) {
		List<Command> cmds = new ArrayList<Command>();
		if (!ModelUtils.containsVertex(relation)) {
			cmds.add(generateDeleteConnectionCommand(model, relation));
		} else {
			for (IOPort port : ModelUtils.getPorts(relation, model)) {
				cmds.add(generateDeleteVertexConnectionCommand(model, relation,
						port));
			}
		}
		return cmds;
	}

	private static DeleteConnectionCommand generateDeleteConnectionCommand(
			Nameable actor, IORelation relation) {
		DeleteConnectionCommand cmd = new DeleteConnectionCommand();
		cmd.setParent((CompositeEntity) actor.getContainer());
		cmd.setConnection((TypedIORelation) relation);
		cmd.setLinkedPorts(((TypedIORelation) relation).linkedPortList());
		return cmd;
	}

	private static DeleteVertexConnectionCommand generateDeleteVertexConnectionCommand(
			Nameable actor, IORelation relation, Port port) {
		DeleteVertexConnectionCommand cmd = new DeleteVertexConnectionCommand();
		cmd.setParent((CompositeEntity) actor.getContainer());
		cmd.setConnection((TypedIORelation) relation);
		cmd.setPort((IOPort) port);
		return cmd;
	}

}
