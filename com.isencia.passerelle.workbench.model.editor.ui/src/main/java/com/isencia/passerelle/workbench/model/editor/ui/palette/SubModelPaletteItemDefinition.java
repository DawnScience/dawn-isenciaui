package com.isencia.passerelle.workbench.model.editor.ui.palette;

import java.io.File;

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;

import com.isencia.passerelle.model.Flow;
import com.isencia.passerelle.workbench.model.editor.ui.Activator;
import com.isencia.passerelle.workbench.model.utils.ModelUtils;

public class SubModelPaletteItemDefinition extends PaletteItemDefinition {
	public static ImageDescriptor IMAGE_SUBMODEL = Activator
			.getImageDescriptor("icons/flow.png");
	private Flow flow;
	private String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Workspace getWorkSpace() {
		return workSpace;
	}

	public void setWorkSpace(Workspace workSpace) {
		this.workSpace = workSpace;
	}

	private Workspace workSpace;

	public Flow getFlow() {
		return flow;
	}

	public void setFlow(Flow flow) {
		this.flow = flow;
	}

	public SubModelPaletteItemDefinition(Flow flow, PaletteGroup group, String id, String name) throws Exception {
		super(group, id, name, Flow.class);
		setIcon(IMAGE_SUBMODEL);

		setFlow(flow);
		
		final IProject pass = ModelUtils.getPasserelleProject();
		path = pass.getLocation().toOSString() + "/"+name + ".moml";
		if (ResourcesPlugin.getWorkspace() instanceof Workspace)
			workSpace = (Workspace)ResourcesPlugin.getWorkspace();

	}

}
