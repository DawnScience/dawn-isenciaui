/*
 * Created on 12.11.2004
 * 
 */
package com.teaminabox.eclipse.wiki.text;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Ronald Steinhau
 */
public class ProjectVisitor implements IResourceVisitor {
	private ArrayList<IProject>	fProjects;

	public ProjectVisitor() {
		fProjects = new ArrayList<IProject>();
	}

	public List<IProject> getProjectsFound(IResource res) throws CoreException {
		doVisit(res);
		return fProjects;
	}

	public List<IProject> getProjectsFound() throws CoreException {
		doVisit();
		return fProjects;
	}

	public final void doVisit(IResource res) throws CoreException {
		res.accept(this);
		fProjects.trimToSize();
	}

	public final void doVisit() throws CoreException {
		doVisit(ResourcesPlugin.getWorkspace().getRoot());
	}

	protected boolean isValidProject(IProject project) {
		return true;
	}

	public boolean visit(IResource res) {
		if (res instanceof IProject) {
			IProject proj = (IProject) res;
			if (!fProjects.contains(proj) && isValidProject(proj)) {
				fProjects.add(proj);
			}
			return false; // do not go into the project with this visitor
		}
		return true; // continue until a project is found
	}
}