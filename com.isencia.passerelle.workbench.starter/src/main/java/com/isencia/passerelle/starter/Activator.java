package com.isencia.passerelle.starter;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activator that starts all installed and resolved bundles on the BundleContext, in the order defined by the
 * <code>Require-Bundle</code> list of each bundle.
 * 
 * @author Jan Vermeulen (verjan@isencia.com)
 */
public class Activator implements BundleActivator {


	private static Initializer initializer;

	public void start(BundleContext context) throws Exception {

 
		initializer = new Initializer(context);

		// For some reason if this plugin exists in the workspace, it can still sometimes be loaded
		// once a workflow project exists or has existed. Once it does get loaded then the defects
		// below happen again. Therefore we have a system property to switch this off.
		if (Boolean.getBoolean("org.dawnsci.passerelle.do.not.break.osgi")) return;

		
		initializer.start();

	}

	public void stop(BundleContext context) throws Exception {
		initializer.stop(context);
	}


	public static Initializer getInitializer() {
		return initializer;
	}
}