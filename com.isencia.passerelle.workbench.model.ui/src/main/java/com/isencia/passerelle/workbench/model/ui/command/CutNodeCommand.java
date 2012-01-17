package com.isencia.passerelle.workbench.model.ui.command;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;

import com.isencia.passerelle.workbench.model.ui.Link;

import ptolemy.actor.Director;
import ptolemy.actor.IOPort;
import ptolemy.actor.IORelation;
import ptolemy.actor.TypedIORelation;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.NamedObj;

public class CutNodeCommand extends Command {
	private HashMap<Object, DeleteComponentCommand> list = new HashMap<Object, DeleteComponentCommand>();
	private HashMap<Object, DeleteConnectionCommand> connectionList = new HashMap<Object, DeleteConnectionCommand>();

	public void emptyElementList() {
		list.clear();
		connectionList.clear();
	}

	public boolean addElement(Object NamedObj) {
		if (!(NamedObj instanceof IORelation)) {
			if (!list.keySet().contains(NamedObj)) {
				list.put(NamedObj, new DeleteComponentCommand());
				return true;
			}
		} else {
			if (!connectionList.keySet().contains(NamedObj)) {
				connectionList.put(NamedObj, new DeleteConnectionCommand());
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean canExecute() {
		if (list == null || list.isEmpty())
			return false;
		Iterator<Map.Entry<Object, DeleteComponentCommand>> it = list
				.entrySet().iterator();
		while (it.hasNext()) {
			if (!isCopyableNamedObj(it.next().getKey()))
				return false;
		}
		return true;
	}

	@Override
	public void execute() {
		if (canExecute()) {
			CompositeEntity container = null;
			Iterator it = connectionList.keySet().iterator();
			List<Link> relations = new ArrayList<Link>();
			while (it.hasNext()) {
				try {
					NamedObj child = (NamedObj) it.next();
					if (child instanceof IORelation) {
						relations.add(new Link(((IORelation) child)
								.linkedDestinationPorts(), ((IORelation) child)
								.linkedSourcePorts()));

						DeleteConnectionCommand deleteCommand = connectionList
								.get(child);
						deleteCommand.setParent(container);
						deleteCommand.setConnection(((TypedIORelation) child));
						deleteCommand.execute();
					}
				} catch (Exception e) {
				}
			}

			it = list.keySet().iterator();
			while (it.hasNext()) {
				try {
					NamedObj child = (NamedObj) it.next();
					if (!(child instanceof IORelation)) {
						DeleteComponentCommand deleteCommand = list.get(child);
						container = (CompositeEntity) child.getContainer();
						deleteCommand.setParent(container);

						deleteCommand.setChild(child);
						deleteCommand.execute();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			List cutObjects = new ArrayList();
			cutObjects.addAll(list.keySet());
			cutObjects.addAll(relations);
			Clipboard.getDefault().setContents(cutObjects);
		}
	}

	@Override
	public void undo() {
		Iterator<DeleteComponentCommand> it = list.values().iterator();
		while (it.hasNext()) {
			DeleteComponentCommand cmd = it.next();
			cmd.undo();
		}
		Iterator<DeleteConnectionCommand> it2 = connectionList.values().iterator();
		while (it2.hasNext()) {
			DeleteConnectionCommand cmd = it2.next();
			cmd.undo();
		}

	}

	@Override
	public void redo() {
		Iterator<DeleteComponentCommand> it = list.values().iterator();
		while (it.hasNext()) {
			DeleteComponentCommand cmd = it.next();
			cmd.redo();
		}
		Iterator<DeleteConnectionCommand> it2 = connectionList.values().iterator();
		while (it2.hasNext()) {
			DeleteConnectionCommand cmd = it2.next();
			cmd.redo();
		}
	}

	public boolean isCopyableNamedObj(Object NamedObj) {
		if (NamedObj instanceof Director)
			return false;
		return true;
	}

	private IOPort searchPort(Enumeration enumeration) {
		while (enumeration.hasMoreElements()) {
			return (IOPort) enumeration.nextElement();
		}
		return null;
	}

	@Override
	public boolean canUndo() {
		return !(list.isEmpty());
	}
}
