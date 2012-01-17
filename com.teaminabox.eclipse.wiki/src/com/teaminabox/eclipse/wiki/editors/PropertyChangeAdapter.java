package com.teaminabox.eclipse.wiki.editors;

import static com.teaminabox.eclipse.wiki.WikiPlugin.wikiPlugin;
import static com.teaminabox.eclipse.wiki.properties.ProjectProperties.projectProperties;

import java.beans.PropertyChangeListener;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

public class PropertyChangeAdapter implements PropertyChangeListener, IPropertyChangeListener {

	private final PropertyListener	listener;

	public PropertyChangeAdapter(PropertyListener wikiEditor) {
		listener = wikiEditor;
		wikiPlugin().getPreferenceStore().addPropertyChangeListener(this);
		projectProperties().addPropertyChangeListener(this);
	}

	public void dispose() {
		wikiPlugin().getPreferenceStore().removePropertyChangeListener(this);
		projectProperties().removePropertyChangeListener(this);
	}

	public void propertyChange(PropertyChangeEvent event) {
		listener.propertyChanged();
	}

	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		listener.propertyChanged();
	}

}