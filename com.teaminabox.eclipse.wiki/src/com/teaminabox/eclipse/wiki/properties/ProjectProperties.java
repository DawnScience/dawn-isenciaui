package com.teaminabox.eclipse.wiki.properties;

import static com.teaminabox.eclipse.wiki.WikiPlugin.wikiPlugin;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import com.teaminabox.eclipse.wiki.WikiConstants;

public class ProjectProperties {

	public static final QualifiedName		RENDERER					= new QualifiedName(WikiConstants.PLUGIN_ID, "projectRenderer");
	public static final QualifiedName		PROJECT_PROPERTIES_ENABLED	= new QualifiedName(WikiConstants.PLUGIN_ID, "projectPropertiesEnabled");

	private static final ProjectProperties	INSTANCE					= new ProjectProperties();

	private PropertyChangeSupport			listeners					= new PropertyChangeSupport(this);

	private ProjectProperties() {
	}

	public static final ProjectProperties projectProperties() {
		return INSTANCE;
	}

	public boolean isProjectPropertiesEnabled(IProject project) {
		return Boolean.parseBoolean(getProjectProperty(project, PROJECT_PROPERTIES_ENABLED));
	}

	public void setProjectPropertiesEnabled(IProject project, boolean enabled) {
		setProjectProperty(project, PROJECT_PROPERTIES_ENABLED, Boolean.toString(enabled));
	}

	private String getProjectProperty(IProject project, QualifiedName qualifiedName) {
		try {
			return project.getPersistentProperty(qualifiedName);
		} catch (CoreException e) {
			wikiPlugin().log("Unable to get property for " + qualifiedName, e);
		}
		return null;
	}

	private void setProjectProperty(IProject project, QualifiedName qualifiedName, String value) {
		try {
			String oldValue = project.getPersistentProperty(qualifiedName);
			project.setPersistentProperty(qualifiedName, value);
			listeners.firePropertyChange(RENDERER.getLocalName(), oldValue, value);
		} catch (CoreException e) {
			wikiPlugin().log("Unable to get property for " + qualifiedName, e);
		}
	}

	public void clearRenderer(IProject project) {
		setRenderer(project, null);
	}

	public void setRenderer(IProject project, String renderer) {
		setProjectProperty(project, RENDERER, renderer);
	}

	public boolean isRendererSet(IProject project) {
		return getRenderer(project) != null;
	}

	public String getRenderer(IProject project) {
		return getProjectProperty(project, RENDERER);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}

	public void removeListeners() {
		PropertyChangeListener[] propertyChangeListeners = listeners.getPropertyChangeListeners();
		for (PropertyChangeListener propertyChangeListener : propertyChangeListeners) {
			listeners.removePropertyChangeListener(propertyChangeListener);
		}
	}

	public void setDefaults(IProject project) {
		setProjectPropertiesEnabled(project, false);
		clearRenderer(project);
	}

}
