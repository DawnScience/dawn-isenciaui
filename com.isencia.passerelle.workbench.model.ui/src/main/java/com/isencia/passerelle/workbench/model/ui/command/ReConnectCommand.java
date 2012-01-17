package com.isencia.passerelle.workbench.model.ui.command;

import org.eclipse.gef.commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ptolemy.actor.TypedIORelation;
import ptolemy.kernel.ComponentRelation;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.NamedObj;
import ptolemy.moml.Vertex;

import com.isencia.passerelle.workbench.model.ui.IPasserelleMultiPageEditor;
import com.isencia.passerelle.workbench.model.ui.VertexLink;
import com.isencia.passerelle.workbench.model.utils.ModelChangeRequest;

public class ReConnectCommand extends Command {

	@Override
	public boolean canExecute() {
		// TODO Auto-generated method stub
		return (((source != null && target != null)||vertexLink != null ) && (newTarget != null || newSource != null) );
	}

	private static final Logger logger = LoggerFactory
			.getLogger(ReConnectCommand.class);
	private IPasserelleMultiPageEditor editor;
	private ComponentRelation connection;
	private VertexLink vertexLink;

	public VertexLink getVertexLink() {
		return vertexLink;
	}

	public void setVertexLink(VertexLink vertexLink) {
		this.vertexLink = vertexLink;
	}

	protected NamedObj source;

	public IPasserelleMultiPageEditor getEditor() {
		return editor;
	}

	public void setEditor(IPasserelleMultiPageEditor editor) {
		this.editor = editor;
	}

	public NamedObj getSource() {
		return source;
	}

	public void setSource(NamedObj source) {
		this.source = source;
	}

	public NamedObj getTarget() {
		return target;
	}

	public void setTarget(NamedObj target) {
		this.target = target;
	}

	protected NamedObj newSource;

	public NamedObj getNewSource() {
		return newSource;
	}

	public void setNewSource(NamedObj newSource) {
		this.newSource = newSource;
	}

	protected NamedObj newTarget;

	public NamedObj getNewTarget() {
		return newTarget;
	}

	public void setNewTarget(NamedObj newTarget) {
		this.newTarget = newTarget;
	}

	protected NamedObj target;
	private CompositeEntity parent;

	public ReConnectCommand() {
		super("Reconnect");
	}

	public Logger getLogger() {
		return logger;
	}

	public void execute() {
		doExecute();
	}

	private Command delCommand;
	private CreateConnectionCommand conCommand;

	protected void doExecute() {
		// Perform Change in a ChangeRequest so that all Listeners are notified
		parent.requestChange(new ModelChangeRequest(this.getClass(),
				connection, "reconnect") {
			@SuppressWarnings("unchecked")
			@Override
			protected void _execute() throws Exception {
				if (vertexLink == null) {
					delCommand = new DeleteConnectionCommand();
					DeleteConnectionCommand deleteConnectionCommand = (DeleteConnectionCommand) delCommand;
					deleteConnectionCommand.setParent(parent);
					deleteConnectionCommand
							.setConnection((TypedIORelation) connection);

				} else {
					delCommand = new DeleteVertexConnectionCommand();
					DeleteVertexConnectionCommand deleteConnectionCommand = (DeleteVertexConnectionCommand) delCommand;
					deleteConnectionCommand.setParent(parent);
					deleteConnectionCommand
							.setConnection((TypedIORelation) vertexLink
									.getRelation());
					Vertex sourceVertex = vertexLink.getSourceVertex();
					if (sourceVertex != null)
						deleteConnectionCommand.setVertex(sourceVertex);
					Vertex targetVertex = vertexLink.getTargetVertex();
					if (targetVertex != null)
						deleteConnectionCommand.setVertex(targetVertex);
					deleteConnectionCommand.setPort(vertexLink.getPort());
				}
				delCommand.execute();
				CreateConnectionCommand conCommand = new CreateConnectionCommand(
						editor);
				conCommand.setContainer(parent);
				if (newTarget != null) {
					if (source != null){
						conCommand.setSource(source);
					}else{
						Vertex sourceVertex = vertexLink.getSourceVertex();
						if (sourceVertex != null){
							conCommand.setSource(sourceVertex);
						}else{
							conCommand.setSource(vertexLink.getPort());
							
						}
					}
					conCommand.setTarget(newTarget);
				} else {
					if (target != null){
						conCommand.setTarget(target);
					}else{
						Vertex targetVertex = vertexLink.getTargetVertex();
						if (targetVertex != null){
							conCommand.setTarget(targetVertex);
						}else{
							conCommand.setTarget(vertexLink.getPort());
							
						}
					}
					conCommand.setSource(newSource);
				}
				conCommand.execute();
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
				connection, "undo-reconnect") {
			@Override
			protected void _execute() throws Exception {
				conCommand.undo();
				delCommand.undo();
			}
		});
	}

}
