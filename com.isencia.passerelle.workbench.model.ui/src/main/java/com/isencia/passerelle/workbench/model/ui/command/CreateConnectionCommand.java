package com.isencia.passerelle.workbench.model.ui.command;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ptolemy.actor.TypedIOPort;
import ptolemy.actor.TypedIORelation;
import ptolemy.kernel.ComponentPort;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.Relation;
import ptolemy.kernel.util.NamedObj;
import ptolemy.moml.Vertex;

import com.isencia.passerelle.workbench.model.ui.IPasserelleMultiPageEditor;
import com.isencia.passerelle.workbench.model.ui.utils.EclipseUtils;
import com.isencia.passerelle.workbench.model.utils.ModelChangeRequest;

public class CreateConnectionCommand extends Command implements IRefreshConnections{

	private IPasserelleMultiPageEditor editor;

	public CreateConnectionCommand(NamedObj source, NamedObj target,
			IPasserelleMultiPageEditor editor) {
		super();
		this.source = source;
		this.target = target;
		this.editor = editor;
	}

	public CreateConnectionCommand(IPasserelleMultiPageEditor editor) {
		super();
		this.editor = editor;

	}

	private final static Logger logger = LoggerFactory
			.getLogger(CreateConnectionCommand.class);

	public Logger getLogger() {
		return logger;
	}

	protected NamedObj container;

	public void setContainer(CompositeEntity container) {
		this.container = container;
	}

	protected TypedIORelation connection;
	protected NamedObj source;
	protected NamedObj target;

	public boolean canExecute() {
		return (source != null && target != null);
	}

	public void execute() {
		doExecute();
	}

	public void doExecute() {
		if (source != null && target != null) {
			NamedObj temp = getContainer(source, target);
			if (temp != null) {
				container = temp;
			}
			if (container == null) {
				return;
			}
			// Perform Change in a ChangeRequest so that all Listeners are
			// notified
			container.requestChange(new ModelChangeRequest(this.getClass(),
					container, "connection") {
				@Override
				protected void _execute() throws Exception {
					try {

						if (source instanceof ComponentPort
								&& target instanceof ComponentPort)
							connection = (TypedIORelation) ((CompositeEntity)container).connect(
									(ComponentPort) source,
									(ComponentPort) target);
						if (source instanceof Vertex
								&& target instanceof ComponentPort) {
							((ComponentPort) target)
									.link((Relation) ((Vertex) source)
											.getContainer());
						}
						if (target instanceof Vertex
								&& source instanceof ComponentPort) {
							((ComponentPort) source)
									.link((Relation) ((Vertex) target)
											.getContainer());
						}
						if (target instanceof Vertex
								&& source instanceof Vertex) {
							
							((TypedIORelation)((Vertex) source).getContainer()).link((TypedIORelation)((Vertex) target).getContainer());
						}
					} catch (Exception e) {
						logger.error("Unable to create connection",e);

						EclipseUtils.logError(e, "Unable to create connection", IStatus.ERROR);
					}
				}
			});
		}
	}

	private NamedObj getContainer(NamedObj source, NamedObj target) {

		if (editor != null
				&& (source instanceof TypedIOPort || target instanceof TypedIOPort)) {
			return editor.getSelectedPage().getContainer();
		}

		return  source.getContainer();
	}

	public String getLabel() {
		return "";
	}

	public NamedObj getSource() {
		return source;
	}

	public NamedObj getTarget() {
		return target;
	}

	public void redo() {
		doExecute();
	}

	public void setSource(NamedObj newSource) {
		source = newSource;
	}

	public void setTarget(NamedObj newTarget) {
		target = newTarget;
	}

	public void undo() {
		if (connection != null) {
			// Perform Change in a ChangeRequest so that all Listeners are
			// notified
			container.requestChange(new ModelChangeRequest(this.getClass(),
					container, "connection") {
				@Override
				protected void _execute() throws Exception {
					connection.setContainer(null);
				}
			});
		}
	}

}
