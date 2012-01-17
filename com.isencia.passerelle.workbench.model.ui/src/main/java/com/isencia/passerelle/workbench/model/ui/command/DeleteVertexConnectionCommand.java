package com.isencia.passerelle.workbench.model.ui.command;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ptolemy.actor.IOPort;
import ptolemy.actor.TypedIORelation;
import ptolemy.kernel.ComponentRelation;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.Port;
import ptolemy.kernel.Relation;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NamedObj;
import ptolemy.moml.Vertex;

import com.isencia.passerelle.workbench.model.ui.utils.EclipseUtils;
import com.isencia.passerelle.workbench.model.utils.ModelChangeRequest;
import com.isencia.passerelle.workbench.model.utils.ModelUtils;

public class DeleteVertexConnectionCommand extends Command implements IRefreshConnections{

	private static final Logger logger = LoggerFactory
			.getLogger(DeleteVertexConnectionCommand.class);

	private ComponentRelation connection;
	private Vertex vertex;

	public Vertex getVertex() {
		return vertex;
	}

	public void setVertex(Vertex vertex) {
		this.vertex = vertex;
	}

	private IOPort port;

	public IOPort getPort() {
		return port;
	}

	public void setPort(IOPort port) {
		this.port = port;
	}

	private CompositeEntity parent;

	public DeleteVertexConnectionCommand() {
		super("DeleteVertexConnection");
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
				try{
					if (getPort() != null)
						unlinkRelation(connection, getPort());
					else
						unlinkRelation(connection, getVertex());
				}catch(IllegalActionException e){
					logger.error("Unable to delete targetConnection",e);
					
					EclipseUtils.logError(e, "Unable to delete connection", IStatus.ERROR);
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
				if (getPort() != null)
					getPort().link(connection);
				else
					((TypedIORelation) getVertex().getContainer())
							.link(connection);

			}
		});
	}

	private void unlinkRelation(ComponentRelation connection, NamedObj namedObj)
			throws IllegalActionException {
		List linkedPortList = connection.linkedObjectsList();
		connection.unlinkAll();
		for (Iterator<Object> iterator = linkedPortList.iterator(); iterator
				.hasNext();) {
			Object temp = (Object) iterator.next();
			if (temp instanceof Port && !temp.equals(namedObj)) {
				((Port) temp).link(connection);
			}
			if (temp instanceof TypedIORelation && (!(namedObj instanceof Vertex)
					|| (temp instanceof TypedIORelation && !(((Vertex) namedObj)
							.getContainer().equals(connection))))) {
				((Relation) temp).link(connection);
			}

		}
	}

}
