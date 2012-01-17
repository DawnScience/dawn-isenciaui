package com.teaminabox.eclipse.wiki.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import com.teaminabox.eclipse.wiki.util.Resources;

/**
 *
 * @author Ronald Steinhau
 */
public class PluginProjectSupport {

	private static Map<String, String>	projects	= new HashMap<String, String>();

	private PluginProjectSupport() {
	}

	public static IProject locateProjectInWorkspace(String pluginID) {
		IWorkspaceRoot rootFolder = ResourcesPlugin.getWorkspace().getRoot();
		List<IProject> projectList = new ArrayList<IProject>(1);
		try {
			PluginProjectVisitor ppVisitor = new PluginProjectVisitor(pluginID);
			rootFolder.accept(ppVisitor);
			projectList = ppVisitor.getProjectsFound();
		} catch (CoreException e) {
			throw new RuntimeException("error locating workspace folder", e);
		}
		if (projectList.size() > 0) {
			return projectList.get(0);
		}
		return null;
	}

	public static String extractPlugID(IProject pluginProject) {
		if (PluginProjectSupport.isInaccessibleOrClosed(pluginProject)) {
			return null;
		}
		if (!PluginProjectSupport.projects.containsKey(pluginProject.getName())) {
			PluginProjectSupport.findPluginId(pluginProject);
		}
		return PluginProjectSupport.projects.get(pluginProject.getName());
	}

	public static boolean isInaccessibleOrClosed(IProject pluginProject) {
		return !pluginProject.isAccessible() || !pluginProject.isOpen();
	}

	private static void findPluginId(IProject pluginProject) {
		IResource member = pluginProject.getFile("plugin.xml");
		String type = "plugin";
		if (member == null || !member.exists()) {
			member = pluginProject.getFile("fragment.xml");
			type = "fragment";
		}
		if (member != null && member.exists()) {
			try {
				PluginProjectSupport.findPluginId(pluginProject, member, type);
			} catch (Exception e) {
			}
		}
	}

	private static void findPluginId(IProject pluginProject, IResource pluginDescriptor, String type) throws CoreException, IOException {
		String content = Resources.getContents(((IFile) pluginDescriptor).getContents());
		int startPos = content.indexOf("<" + type);
		if (startPos > 0) {
			int endPos = content.indexOf(">", startPos);
			StringBuffer header = new StringBuffer();
			for (int i = startPos; i < endPos; i++) {
				if (content.charAt(i) != ' ') {
					header.append(content.charAt(i));
				}
			}
			content = header.toString();
			startPos = content.indexOf("id=");
			startPos = content.indexOf("\"", startPos);
			endPos = content.indexOf("\"", startPos + 1);
			String id = new String(content.substring(startPos + 1, endPos).trim());
			PluginProjectSupport.projects.put(pluginProject.getName(), id);
		}
	}

}
