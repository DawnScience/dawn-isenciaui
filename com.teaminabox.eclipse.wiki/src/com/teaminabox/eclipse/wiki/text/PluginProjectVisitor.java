/*
 * Created on 12.11.2004
 * 
 */
package com.teaminabox.eclipse.wiki.text;

import org.eclipse.core.resources.IProject;

/**
 * @author Ronald Steinhau
 */
public class PluginProjectVisitor extends ProjectVisitor {

	private String	fPluginID;

	public PluginProjectVisitor(String pluginID) {
		fPluginID = pluginID;
	}

	protected boolean isValidProject(IProject proj) {
		if (PluginProjectSupport.isInaccessibleOrClosed(proj)) {
			return false;
		}
		String projPlugID = PluginProjectSupport.extractPlugID(proj);
		if (projPlugID == null) {
			return false;
		}
		if (fPluginID == null) {
			return true;
		}
		return fPluginID.equals(projPlugID);
	}
}