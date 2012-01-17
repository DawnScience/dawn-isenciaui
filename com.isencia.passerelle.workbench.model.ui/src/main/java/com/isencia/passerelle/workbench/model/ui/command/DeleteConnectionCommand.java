package com.isencia.passerelle.workbench.model.ui.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ptolemy.actor.TypedIORelation;
import ptolemy.kernel.ComponentPort;
import ptolemy.kernel.ComponentRelation;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.Port;
import ptolemy.kernel.Relation;
import ptolemy.kernel.util.IllegalActionException;

import com.isencia.passerelle.workbench.model.utils.ModelChangeRequest;

public class DeleteConnectionCommand extends Command implements IRefreshConnections{

	private static final Logger logger = LoggerFactory
			.getLogger(DeleteConnectionCommand.class);

	private ComponentRelation connection;
	private CompositeEntity parent;
	private List<ComponentPort> linkedPorts = new ArrayList<ComponentPort>();
	private List<Relation> linkedRelations = new ArrayList<Relation>();

	public void setLinkedPorts(List<ComponentPort> linkedPorts) {
		this.linkedPorts = linkedPorts;
	}

	public DeleteConnectionCommand() {
		super("DeleteConnection");
	}

	public Logger getLogger() {
		return logger;
	}

	public void execute() {
		doExecute();
	}

	protected void doExecute() {
		// Perform Change in a ChangeRequest so that all Listeners are notified
		parent.requestChange(new ModelChangeRequest(this.getClass(),
				connection, "delete") {
			@SuppressWarnings("unchecked")
			@Override
			protected void _execute() throws Exception {
				List<ComponentPort> linkedPortList = connection
						.linkedPortList();
				for (Iterator<ComponentPort> iterator = linkedPortList
						.iterator(); iterator.hasNext();) {
					ComponentPort port = (ComponentPort) iterator.next();
					linkedPorts.add(port);
				}
				List objects = connection.linkedObjectsList();
				for (Object o : objects) {
					if (o instanceof Relation) {
						linkedRelations.add((Relation) o);
					}
				}
				if (linkedRelations.size() > 0) {
					unlinkRelation(connection, connection);
				} else {
					connection.setContainer(null);
				}
			}
		});

	}

	public void redo() {
		doExecute();
	}

	public void setConnection(TypedIORelation c) {
		connection = c;
	}

	public void setParent(CompositeEntity p) {
		parent = p;
	}

	public void undo() {
		// Perform Change in a ChangeRequest so that all Listeners are notified
		parent.requestChange(new ModelChangeRequest(this.getClass(),
				connection, "undo-delete") {
			@Override
			protected void _execute() throws Exception {
				ComponentPort port1 = linkedPorts.get(0);
				ComponentPort port2 = linkedPorts.get(1);
				connection = parent.connect(port1, port2);
			}
		});
	}

	private void unlinkRelation(ComponentRelation connection, Object port)
			throws IllegalActionException {
		List linkedPortList = connection.linkedObjectsList();
		connection.unlinkAll();
		for (Iterator<Object> iterator = linkedPortList.iterator(); iterator
				.hasNext();) {
			Object temp = (Object) iterator.next();
			if (temp instanceof Port) {
				((Port) temp).link(connection);
			}

		}
	}

}
