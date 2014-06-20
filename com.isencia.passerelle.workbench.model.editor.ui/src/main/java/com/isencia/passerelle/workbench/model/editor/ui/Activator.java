package com.isencia.passerelle.workbench.model.editor.ui;

import java.io.File;
import java.util.Stack;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isencia.passerelle.editor.common.model.MomlClassRegistry;
import com.isencia.passerelle.project.repository.api.RepositoryService;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

  private static final Logger LOGGER = LoggerFactory.getLogger(Activator.class);

  // The plug-in ID
  public static final String PLUGIN_ID = "com.isencia.passerelle.workbench.model.editor.ui";

  // The shared instance
  private static Activator plugin;
  private static Stack<Bundle> bundles;

  private BundleContext bundleContext;
  private ServiceTracker repoSvcTracker;
  private ServiceRegistration submodelSvcReg;

  /**
   * The constructor
   */
  public Activator() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
   */
  public void start(BundleContext context) throws Exception {
	  super.start(context);
	  this.bundleContext = context;
	  
	  bundles = new Stack<Bundle>();
	  for (Bundle bundle : context.getBundles()) {

		  if (!(bundle.getSymbolicName().equals("com.isencia.passerelle.workbench")) && !(bundle.getSymbolicName().contains("eclipse") && !bundle.getSymbolicName().contains("persistence")) && !bundle.equals(context.getBundle()))
			  bundles.push(bundle);

	  }

	  plugin = this;
	  repoSvcTracker = new ServiceTracker(context, RepositoryService.class.getName(), null);
	  repoSvcTracker.open();

	  MomlClassRegistry.setService(new MomlClassService());

	  IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	  String submodelPath = store.getString(RepositoryService.SUBMODEL_ROOT);
	  if (submodelPath == null || submodelPath.trim().equals("")) {
		  File userHome = new File(System.getProperty("user.home"));
		  File defaultSubmodelPath = new File(userHome, ".passerelle/submodel-repository");
		  submodelPath = System.getProperty(RepositoryService.SUBMODEL_ROOT, defaultSubmodelPath.getAbsolutePath());
		  store.setValue(RepositoryService.SUBMODEL_ROOT, submodelPath);
	  } else {
		  System.setProperty(RepositoryService.SUBMODEL_ROOT, submodelPath);
	  }
	  // just call this here, so we're sure the submodel folder pref has been read and applied to the repo svc,
	  // before opening any editor
	  getRepositoryService();

  }

  private static boolean alreadyLoaded = false;
  /**
   * If we call loadBundles() from the activator EVER, even when not auto starting
   * this plugin, there are circumstances where it will cause DAWN to malfunction. 
   * Probably any eclipse application in fact.
   */
  public static synchronized void loadBundles() {

	  if (alreadyLoaded) return;
	  
	  // If we do this during startup, it causes a defect to happen in DAWN RCP version if you use the -clean argument.
	  // Since eclipse automatically sets -clean when its installation changes, users see this issue.
	  // See http://jira.diamond.ac.uk/browse/DAWNSCI-858 for more information
	  // Now in DAWN we turn off this special starting by not starting this plugin automatically at startup.
	  while (!bundles.isEmpty()) {
		  Bundle bundle = bundles.pop();

		  try {
			  start(bundle, bundles);
		  } catch (BundleException e) {
			  e.printStackTrace();
		  }
	  }
	  alreadyLoaded = true;
  }

  private static void start(Bundle bundle, Stack<Bundle> bundles) throws BundleException {

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
					  start(requiredBundle, bundles);
					  break;
				  }
			  }
		  }
	  }

	  // try to start the bundle
	  bundle.start();
	  LOGGER.debug(bundle.getSymbolicName() + " started.");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
   */
  public void stop(BundleContext context) throws Exception {
    plugin = null;
    this.bundleContext = null;
    super.stop(context);
    repoSvcTracker.close();
  }

  /**
   * Returns the shared instance
   * 
   * @return the shared instance
   */
  public static Activator getDefault() {
    return plugin;
  }

  /**
   * Returns an image descriptor for the image file at the given plug-in relative path
   * 
   * @param path
   *          the path
   * @return the image descriptor
   */
  public static ImageDescriptor getImageDescriptor(String path) {
    return getImageDescriptor(PLUGIN_ID, path);
  }

  public static ImageDescriptor getImageDescriptor(String plugin, String path) {
    return imageDescriptorFromPlugin(plugin, path);
  }

  public RepositoryService getRepositoryService() {
    try {
      RepositoryService repositoryService = (RepositoryService) (repoSvcTracker != null ? repoSvcTracker.waitForService(3000) : null);
      if (repositoryService == null){
        return null;
      }
      File userHome = new File(System.getProperty("user.home"));
      File defaultSubmodelPath = new File(userHome, ".passerelle/submodel-repository");
      File folder = new File(System.getProperty(RepositoryService.SUBMODEL_ROOT, defaultSubmodelPath.getAbsolutePath()));
      if (!folder.exists()) {
        folder.mkdirs();
      }
      repositoryService.setSubmodelFolder(folder);
      return repositoryService;
    } catch (InterruptedException e) {
      return null;
    }
  }

  public BundleContext getBundleContext() {
    return this.bundleContext;
  }
}
