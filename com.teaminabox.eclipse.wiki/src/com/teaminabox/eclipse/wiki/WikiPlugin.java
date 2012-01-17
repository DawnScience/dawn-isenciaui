package com.teaminabox.eclipse.wiki;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.teaminabox.eclipse.wiki.preferences.WikiPreferences;

public class WikiPlugin extends AbstractUIPlugin {

	public static final String	RESOURCE_BUNDLE	= "com.teaminabox.eclipse.wiki.WikiPluginResources";

	private static WikiPlugin	plugin;
	private ResourceBundle		resourceBundle;

	public WikiPlugin() {
		WikiPlugin.plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		initialiseResourceBundle();
		initialiseImageRegistry();
		WikiPreferences.init(getPreferenceStore());
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		WikiPlugin.plugin = null;
		resourceBundle = null;
	}

	private void initialiseImageRegistry() {
		for (String element : WikiConstants.ICONS) {
			getImageRegistry().put(element, AbstractUIPlugin.imageDescriptorFromPlugin(WikiConstants.PLUGIN_ID, element));
		}
	}

	private void initialiseResourceBundle() {
		try {
			resourceBundle = ResourceBundle.getBundle(WikiPlugin.RESOURCE_BUNDLE);
		} catch (MissingResourceException e) {
			logAndReport("Error", "The Resource Bundle is missing!!", e);
		}
	}

	public static WikiPlugin wikiPlugin() {
		return WikiPlugin.plugin;
	}

	public static String getResourceString(String key) {
		ResourceBundle bundle = wikiPlugin().getResourceBundle();
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return "!" + key + "!";
		}
	}

	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	public void logAndReport(String title, String message, Exception e) {
		log(message, e);
		reportError(title, message);
	}

	public void log(String message) {
		wikiPlugin().getLog().log(new Status(IStatus.OK, WikiConstants.PLUGIN_ID, IStatus.OK, message, null));
	}

	public void log(String message, Exception e) {
		if (message == null) {
			message = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : e.getClass().getName();
		}
		wikiPlugin().getLog().log(new Status(IStatus.ERROR, WikiConstants.PLUGIN_ID, IStatus.OK, message, e));
	}

	public void reportError(String title, String message) {
		try {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.openError(shell, title, message);
		} catch (RuntimeException e) {
			log(e.getLocalizedMessage(), e);
		}
	}

}