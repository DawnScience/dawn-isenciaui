package com.isencia.passerelle.workbench.util;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;

import ptolemy.data.expr.StringParameter;
import ptolemy.kernel.util.Settable;

public class ResourceUtils {

	/**
	 * 
	 * @param parameter
	 * @return IResource
	 */
	public static IResource getResource(Settable parameter) {
		String     path;
		if (parameter instanceof StringParameter) {
			path = ((StringParameter)parameter).getExpression();
		} else {
			path = parameter.getValueAsString();
		}
		return getResource(path);
	}
	
	public static IResource getResource(String path) {
		if (path.startsWith("\"")) {
			path = path.substring(1);
		}
		if (path.endsWith("\"")) {
			path = path.substring(0,path.length()-1);
		}
		if (path==null||"".equals(path)||"\"\"".equals(path)) return null;
		final IWorkspace space = ResourcesPlugin.getWorkspace();
		return space.getRoot().findMember(path);
	}

}
