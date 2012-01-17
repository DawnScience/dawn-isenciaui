package com.isencia.passerelle.workbench.model.ui.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;

import ptolemy.actor.CompositeActor;
import ptolemy.actor.Director;
import ptolemy.kernel.ComponentEntity;
import ptolemy.kernel.ComponentPort;
import ptolemy.kernel.Port;
import ptolemy.kernel.util.NamedObj;
import ptolemy.moml.Vertex;

import com.isencia.passerelle.workbench.model.ui.IPasserelleMultiPageEditor;
import com.isencia.passerelle.workbench.model.ui.Link;
import com.isencia.passerelle.workbench.model.ui.VertexLink;
import com.isencia.passerelle.workbench.model.utils.ModelUtils;

public class PasteNodeCommand extends Command {
	private IPasserelleMultiPageEditor editor;

	public PasteNodeCommand(IPasserelleMultiPageEditor editor) {
		super();
		this.editor = editor;
	}

	private HashMap<Object, org.eclipse.gef.commands.Command> list = new HashMap<Object, org.eclipse.gef.commands.Command>();

	@Override
	public boolean canExecute() {
		ArrayList clipBoardList = (ArrayList) Clipboard.getDefault()
				.getContents();
		if (clipBoardList == null || clipBoardList.isEmpty())
			return false;
		for (Object o : clipBoardList) {
			if (!(o instanceof NamedObj) && !(o instanceof Link)
					&& !(o instanceof VertexLink)) {
				return false;
			}
		}
		return true;
	}

	public boolean isPastableNamedObj(NamedObj namedObj) {
		if (namedObj instanceof Director)
			return false;
		return true;
	}

	private NamedObj getParent(NamedObj actor) {
		if (actor == null)
			return null;
		if (actor.getContainer() == null) {
			return actor;
		}
		return (getParent(actor.getContainer()));
	}

	@Override
	public void execute() {
		if (!canExecute())
			return;
		ArrayList clipboardList = (ArrayList) Clipboard.getDefault()
				.getContents();
		CompositeActor selectedContainer = editor.getSelectedContainer();

		Iterator<Object> it = clipboardList.iterator();
		list.clear();
		// first create all the copied nodes: the nodes have to exist before you
		// can create the copied connections
		while (it.hasNext()) {
			try {
				Object o = it.next();
				if (o instanceof NamedObj) {
					NamedObj child = (NamedObj) o;
					CreateComponentCommand createCommand = new CreateComponentCommand(
							editor);
					createCommand.setModel(child);
					createCommand.setParent(selectedContainer);
					createCommand.setClazz(child.getClass());
					double[] location = ModelUtils.getLocation(child);
					createCommand.setLocation(new double[] { location[0] + 100,
							location[1] + 100 });
					createCommand.execute();
					list.put(child, createCommand);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		it = clipboardList.iterator();
		while (it.hasNext()) {
			try {
				Object o = it.next();
				if (o instanceof Link || o instanceof VertexLink) {

					NamedObj destination = null;
					NamedObj source = null;

					if (o instanceof VertexLink) {
						VertexLink vr = (VertexLink) o;
						Vertex vertex = ModelUtils.getVertex(vr.getRelation());
						source = ((CreateComponentCommand) list.get(vertex))
								.getChild();
						destination = searchPort(vr.getPort());
					} else {
						Link rel = (Link) o;
						destination = searchCopy(rel.getDestination());
						source = searchCopy(rel.getSource());
					}
					if (source != null && destination != null) {
						CreateConnectionCommand connection = new CreateConnectionCommand(
								source, destination, editor);
						connection.setContainer(selectedContainer);
						connection.execute();
						list.put(o, connection);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				redo();
			}
		}

	}

	private ComponentPort searchPort(Object port) {
		ArrayList list = new ArrayList();
		list.add(port);
		return searchCopy(list);
	}

	private ComponentPort searchCopy(List enumeration) {
		for (Object o : enumeration) {
			NamedObj namedObj = (NamedObj) o;

			NamedObj node = namedObj.getContainer();
			org.eclipse.gef.commands.Command cmd = list.get(node);
			if (cmd instanceof CreateComponentCommand) {
				CreateComponentCommand createComponentCommand = (CreateComponentCommand) cmd;
				NamedObj obj = createComponentCommand.getChild();
				if (obj instanceof ComponentEntity) {
					ComponentEntity ce = (ComponentEntity) obj;
					for (Object p : ce.portList()) {
						Port cPort = (Port) p;
						if (cPort.getName().equals(namedObj.getName())) {
							if (cPort instanceof ComponentPort)
								return (ComponentPort) cPort;
						}
					}

				}
			}
		}
		return null;
	}

	private ComponentPort searchPort(NamedObj node, String name) {
		org.eclipse.gef.commands.Command cmd = list.get(node);
		if (cmd instanceof CreateComponentCommand) {
			CreateComponentCommand createComponentCommand = (CreateComponentCommand) cmd;
			NamedObj obj = createComponentCommand.getChild();
			if (obj instanceof ComponentEntity) {
				ComponentEntity ce = (ComponentEntity) obj;
				for (Object o : ce.portList()) {
					Port cPort = (Port) o;
					if (cPort.getName().equals(name)) {
						if (cPort instanceof ComponentPort)
							return (ComponentPort) cPort;
					}
				}

			}
		}
		return null;
	}

	@Override
	public void redo() {
		Iterator<org.eclipse.gef.commands.Command> it = list.values()
				.iterator();
		while (it.hasNext()) {
			org.eclipse.gef.commands.Command cmd = it.next();
			if (cmd != null)
				cmd.redo();
		}
	}

	@Override
	public boolean canUndo() {
		return !(list.isEmpty());
	}

	@Override
	public void undo() {
		Iterator<org.eclipse.gef.commands.Command> it = list.values()
				.iterator();
		while (it.hasNext()) {
			org.eclipse.gef.commands.Command cmd = it.next();
			cmd.undo();
		}
	}

}
