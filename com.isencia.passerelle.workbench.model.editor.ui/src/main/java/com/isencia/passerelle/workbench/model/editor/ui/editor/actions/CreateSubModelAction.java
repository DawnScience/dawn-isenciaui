package com.isencia.passerelle.workbench.model.editor.ui.editor.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ptolemy.actor.CompositeActor;
import ptolemy.actor.Director;
import ptolemy.kernel.Entity;
import ptolemy.kernel.util.Attribute;

import com.isencia.passerelle.actor.gui.PasserelleActorControllerFactory;
import com.isencia.passerelle.actor.gui.PasserelleEditorFactory;
import com.isencia.passerelle.actor.gui.PasserelleEditorPaneFactory;
import com.isencia.passerelle.model.Flow;
import com.isencia.passerelle.model.FlowManager;
import com.isencia.passerelle.workbench.model.editor.ui.Activator;
import com.isencia.passerelle.workbench.model.editor.ui.editor.PasserelleModelMultiPageEditor;
import com.isencia.passerelle.workbench.model.editor.ui.editor.WizardWorkflowEditor;
import com.isencia.passerelle.workbench.model.editor.ui.palette.PaletteItemFactory;
import com.isencia.passerelle.workbench.model.ui.utils.EclipseUtils;
import com.isencia.passerelle.workbench.model.ui.utils.FileUtils;
import com.isencia.passerelle.workbench.model.ui.wizards.NameChecker;
import com.isencia.passerelle.workbench.model.ui.wizards.NameWizard;
import com.isencia.passerelle.workbench.model.utils.ModelUtils;
import com.isencia.passerelle.workbench.model.utils.SubModelUtils;

public class CreateSubModelAction extends SelectionAction implements NameChecker {
	
	private static final Logger logger = LoggerFactory.getLogger(CreateSubModelAction.class);
	
	private PasserelleModelMultiPageEditor parent;
	private final String icon = "icons/flow.png";
	public static String CREATE_SUBMODEL = "createSubModel";

	/**
	 * Creates an empty model
	 * @param part
	 */
	public CreateSubModelAction() {
		this(null,null);
		setId(ActionFactory.NEW.getId());
	}
	/**
	 * Creates an empty model
	 * @param part
	 */
	public CreateSubModelAction(final IEditorPart part) {
		this(part,null);
		setId(ActionFactory.NEW.getId());
	}
	/**
	 * Creates the model from the contents of the part
	 * @param part
	 * @param parent
	 */
	public CreateSubModelAction(final IEditorPart part, 
			                    final PasserelleModelMultiPageEditor parent) {
		super(part);
		this.parent = parent;
		setLazyEnablementCalculation(true);
		if (parent!=null) setId(ActionFactory.EXPORT.getId());
	}

	@Override
	protected void init() {
		
		super.init();
		Activator.getImageDescriptor(icon);
		setHoverImageDescriptor(Activator.getImageDescriptor(icon));
		setImageDescriptor(Activator.getImageDescriptor(icon));
		setDisabledImageDescriptor(Activator.getImageDescriptor(icon));
		setEnabled(false);

	}

	@Override
	public void run() {
		
		try {
			if (parent!=null) {
				final Entity entity = parent.getSelectedContainer();
				final String name   = getName(entity.getName());
				if (name!=null) {
					entity.setName(name);
					exportEntityToClassFile(entity);
					parent.getActorTreeViewPage().refresh();
				}
			} else {
				final String name = getName("emptyComposite");
				if (name!=null) {
					final IProject pass = ModelUtils.getPasserelleProject();
					final File     file = new File(pass.getLocation().toOSString()+ "/" + name + ".moml");
					final InputStream stream = ModelUtils.getEmptyCompositeStream(file.getAbsolutePath());
					FileUtils.write(stream, new FileOutputStream(file));
					
					PaletteItemFactory factory = PaletteItemFactory.getInstance();
					Flow flow = FlowManager.readMoml(new FileReader(file));
					flow.setName(name);
					factory.addSubModel(flow);
					SubModelUtils.addSubModel(flow);
                    
					// Open the editor for the composite.
					final IFile modelMoml = pass.getFile(name + ".moml");
                    final IEditorPart part = EclipseUtils.openEditor(modelMoml);
                    if (part != null && part instanceof WizardWorkflowEditor) {
                    	WizardWorkflowEditor ed = (WizardWorkflowEditor)part;
                    	ed.setActivePage(1);
                    }
                    
                    SubModelViewUtils.refreshPallette();
				}
			}
			
		} catch (Exception e) {
			logger.error("Cannot export sub-model", e);
		}
	}

	private String getName(final String name) {
		
		NameWizard   wizard = new NameWizard(name, this);
		WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
		dialog.create();
		dialog.getShell().setSize(400, 300);
		dialog.setTitle("Name of Composite");
		dialog.setMessage("Please choose a unique name for your exported composite.");
		if (dialog.open() == WizardDialog.OK) {
			return wizard.getRenameValue();
		}
		return null;
	}
	public Entity exportEntityToClassFile(Entity entity) throws Exception {
		
		Entity entityAsClass = (Entity) entity.clone(entity.workspace());
		entityAsClass.setClassDefinition(true);

		if (entityAsClass instanceof CompositeActor) {
			CompositeActor compActor = ((CompositeActor) entityAsClass);
			Director d = compActor.getDirector();
			if (d != null) {
				// remove the director from the class definition
				d.setContainer(null);
			}

			Attribute ctrlFact = compActor.getAttribute("_controllerFactory");
			if (ctrlFact == null) {
				new PasserelleActorControllerFactory(compActor,
						"_controllerFactory");
			} else if (!(ctrlFact instanceof PasserelleActorControllerFactory)) {
				ctrlFact.setContainer(null);
				new PasserelleActorControllerFactory(compActor,
						"_controllerFactory");
			}
			Attribute editorFact = compActor.getAttribute("_editorFactory");
			if (editorFact == null) {
				new PasserelleEditorFactory(compActor, "_editorFactory");
			} else if (!(editorFact instanceof PasserelleEditorFactory)) {
				editorFact.setContainer(null);
				new PasserelleEditorFactory(compActor, "_editorFactory");
			}
			Attribute editorPaneFact = compActor
					.getAttribute("_editorPaneFactory");
			if (editorPaneFact == null) {
				new PasserelleEditorPaneFactory(compActor, "_editorPaneFactory");
			} else if (!(editorPaneFact instanceof PasserelleEditorPaneFactory)) {
				editorPaneFact.setContainer(null);
				new PasserelleEditorPaneFactory(compActor, "_editorPaneFactory");
			}
		}
		
		final IProject pass = ModelUtils.getPasserelleProject();
		final File     file = new File(pass.getLocation().toOSString() +"/"+ entityAsClass.getName() + ".moml");
		String name = entityAsClass.getName();
		String filename = file.getName();
		int period = filename.indexOf(".");
		if (period > 0) {
			name = filename.substring(0, period);
		} else {
			name = filename;
		}

		FileWriter fileWriter = new FileWriter(file);
		try {
			if (entityAsClass.getContainer() != null) {
				// in this case the exportMoML below does not add the xml
				// header itself
				// if the entity is a top-level one,without container the
				// exportMoML does add it
				fileWriter.write("<?xml version=\"1.0\" standalone=\"no\"?>\n"
						+ "<!DOCTYPE " + entityAsClass.getElementName()
						+ " PUBLIC " + "\"-//UC Berkeley//DTD MoML 1//EN\"\n"
						+ "    \"http://ptolemy.eecs.berkeley.edu"
						+ "/xml/dtd/MoML_1.dtd\">\n");
			}
			entityAsClass.exportMoML(fileWriter, 0, name);
		} finally {
			fileWriter.close();
		}
		PaletteItemFactory factory = PaletteItemFactory.getInstance();
		Flow flow = FlowManager.readMoml(new FileReader(file));
		factory.addSubModel(flow);
		SubModelUtils.addSubModel(flow);

		return entityAsClass;
	}

	@Override
	protected boolean calculateEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isNameValid(final String name) {
		return !SubModelUtils.isSubModel(name);
	}

	@Override
	public String getErrorMessage(String name) {
		if (SubModelUtils.isSubModel(name)) {
			return "'"+name+"' is already existing as a composite.";
		}
		return null;
	}
}
