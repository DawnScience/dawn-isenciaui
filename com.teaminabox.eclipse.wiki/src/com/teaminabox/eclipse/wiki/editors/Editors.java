package com.teaminabox.eclipse.wiki.editors;

import static com.teaminabox.eclipse.wiki.WikiPlugin.wikiPlugin;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public class Editors {

	private static Set<WikiEditor>	editors	= Collections.synchronizedSet(new HashSet<WikiEditor>());

	static {
		Editors.addResourceChangeListener();
	}

	private Editors() {
	}

	private static void addResourceChangeListener() {
		IResourceChangeListener resourceChangeListener = new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) {
				Editors.processResourceChange(event);
			}
		};
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeListener);
	}

	private static void processResourceChange(IResourceChangeEvent event) {
		try {
			if (event.getType() != IResourceChangeEvent.POST_CHANGE) {
				return;
			}
			Editors.refreshEditors();
		} catch (Exception e) {
			wikiPlugin().log("WikiEditor: Resource Change Error", e);
		}
	}

	private static void refreshEditors() throws IOException, CoreException {
		for (WikiEditor editor : Editors.editors) {
			if (editor.getContext() != null) {
				editor.getContext().loadEnvironment();
				editor.redrawTextAsync();
			}
		}
	}

	public static void registerEditor(WikiEditor editor) {
		Editors.editors.add(editor);
	}

	public static void unregisterEditor(WikiEditor editor) {
		Editors.editors.remove(editor);
	}

}
