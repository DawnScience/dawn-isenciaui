package com.isencia.passerelle.starter;

import java.util.Stack;
import java.util.StringTokenizer;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Initializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(Activator.class);

	private Stack<Bundle> bundles;
	private boolean       alreadyLoaded;
	
	public Initializer(BundleContext context) {
		
		bundles = new Stack<Bundle>();
		
	    //context.addBundleListener(this);

	    for (Bundle bundle : context.getBundles()) {
	      
	      if (!(bundle.getSymbolicName().equals("com.isencia.passerelle.workbench")) && !(bundle.getSymbolicName().contains("eclipse") && !bundle.getSymbolicName().contains("persistence")) && !bundle.equals(context.getBundle()))
	        bundles.push(bundle);

	    }

	}
	
	
	public void start() {
		
		if (alreadyLoaded) return;

		// IMPORTANT Doing this is evil! We spent many days attempting to 
		// find out why OSGI was broken in the UI product. The start exceptions
		// is not printed so dependencies can change which result in the wrong
		// thing being started at the wrong time. Resulting in these defects:
		// http://jira.diamond.ac.uk/browse/DAWNSCI-858
		// http://jira.diamond.ac.uk/browse/DAWNSCI-841
		// http://jira.diamond.ac.uk/browse/DAWNSCI-840
		// However if this plugin is only started in the workflow config, then
		// hopefully this is not an issue, although festering evil may escape one day...
	
		// while there are bundles still to be started...
		Bundle bundle = null;
	
		while (!bundles.isEmpty()) {
			bundle = bundles.pop();
			try {
				start(bundle);
			} catch (BundleException e) {
				e.printStackTrace();
			}
		}
		
		alreadyLoaded = true;

	}


	private void start(Bundle bundle) throws BundleException {

		// first start any required bundles
		String requiredBundles = (String) bundle.getHeaders().get("Require-Bundle");
		if (requiredBundles != null) {
			StringTokenizer tokenizer = new StringTokenizer(requiredBundles, ",");
			while (tokenizer.hasMoreTokens()) {
				String bundleName = tokenizer.nextToken();
				// strip version info
				int index = bundleName.indexOf(';');
				if (index > 0)
					bundleName = bundleName.substring(0, index);

				// look for the required bundle in the stack of bundles still to be started
				for (int i = 0; i < bundles.size(); i++) {
					Bundle requiredBundle = bundles.get(i);
					if (requiredBundle.getSymbolicName().equals(bundleName)) {
						// remove the required bundle from the stack
						bundles.remove(i);
						start(requiredBundle);
						break;
					}
				}
			}
		}

		// try to start the bundle
		bundle.start();
		LOGGER.debug(bundle.getSymbolicName() + " started.");
	}


	public void stop(BundleContext context) {
		//if (context!=null) context.removeBundleListener(this);
	}


}
