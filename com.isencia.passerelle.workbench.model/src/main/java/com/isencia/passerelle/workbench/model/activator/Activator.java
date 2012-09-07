package com.isencia.passerelle.workbench.model.activator;

import java.io.File;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	private static Logger logger = LoggerFactory.getLogger(Activator.class);

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
		
   		
		// Configure the workflow log. First check if the property 'workflow.logback.configurationFile'exists:
		String logConfigFile = System.getProperty("workflow.logback.configurationFile");
		if (logConfigFile != null) {
			// Ok, it exists - check if the corresponding log configuration file exists
			File file = new File(logConfigFile);
			if (file.exists()) {
				if (logger.isDebugEnabled()) logger.debug("Workflow logging configuration file found at '"+logConfigFile+"'");
			} else {
				if (logger.isWarnEnabled()) logger.warn("Workflow logging configuration file not found at '"+logConfigFile+"'");
				logConfigFile = null;
			}
		} else {
			// Check if the property 'logback.configurationFile' exists:
			logConfigFile = System.getProperty("logback.configurationFile");
			if (logConfigFile != null) {
				File file = new File(logConfigFile);
				if (file.exists()) {
					if (logger.isDebugEnabled()) logger.debug("Logging configuration file found at '"+logConfigFile+"'");
					System.setProperty("workflow.logback.configurationFile", logConfigFile);
				} else {
					if (logger.isWarnEnabled()) logger.warn("Logging configuration file not found at '"+logConfigFile+"'");
					logConfigFile = null;
				}
			}
		}
		
		// Load default config file if no property set or config file not found
		if (logConfigFile == null) {
			try {			
				// Find the default configuration file
				ProtectionDomain pd = Activator.class.getProtectionDomain();
				CodeSource cs = pd.getCodeSource();
				URL url = cs.getLocation();
				File file = new File(url.getFile(), "config/logback.xml");
				String absPath = file.getAbsolutePath().toString();
				if (file.exists()) {
					if (logger.isDebugEnabled()) logger.debug("Default workflow logging configuration file found at '"+absPath+"'");
					System.setProperty("workflow.logback.configurationFile", absPath);
				} else {
					if (logger.isErrorEnabled()) logger.error("Default workflow logging configuration file not found at '"+absPath+"'");
				}
			} catch (Exception e) {
				logger.error("Could not set up default workflow logging");
				e.printStackTrace();
			} 
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
