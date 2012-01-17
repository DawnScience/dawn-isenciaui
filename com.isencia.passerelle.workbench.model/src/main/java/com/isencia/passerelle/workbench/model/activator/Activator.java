package com.isencia.passerelle.workbench.model.activator;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.isencia.passerelle.workbench.model";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		
		if (System.getProperty("logback.configurationFile")==null) {
			final Bundle bundle = Platform.getBundle(PLUGIN_ID);
			System.setProperty("logback.configurationFile", bundle.getResource("logback.xml").toString());
			
			// For some reason slf4j still gives log4j errors, so we also configure log4j here:
			//PropertyConfigurator.configure(bundle.getResource("log4j.properties"));
		}

		super.start(context);

		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
