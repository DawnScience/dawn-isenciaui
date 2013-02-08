package com.isencia.passerelle.workbench.model.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isencia.passerelle.model.Flow;
import com.isencia.passerelle.model.FlowManager;
import com.isencia.passerelle.model.util.MoMLParser;

/**
 * Class replaces submodel storage with a proper properties file. This allows
 * reliable operations where the old comma separated string did not.
 * 
 * @author gerring
 * 
 */
public class SubModelUtils {

	private static Logger logger = LoggerFactory.getLogger(SubModelUtils.class);

	private static Map<String, Flow> modelMap;

	public static Map<String, Flow> getSubModels() throws Exception {
		if (modelMap == null) {
			return modelMap = SubModelUtils.readSubModels();
		}
		return modelMap;
	}

	public static boolean isSubModel(final String name) {
		return modelMap != null && modelMap.keySet().contains(name);
	}

	public static void addSubModel(Flow flow) throws Exception {
		MoMLParser.putActorClass(flow.getName(), flow);
		registerSubModel(flow);
	}

	private static void registerSubModel(Flow flow) throws Exception {

		if (modelMap == null) {
			modelMap = getSubModels();
		}
		String model = flow.getName();

		modelMap.put(model, flow);

		final IFile store = getModelStore();
		final Properties models = PropUtils.loadProperties(store.getContents());

		models.put(model, "");

		PropUtils.storeProperties(models, store.getLocation().toOSString());
		store.refreshLocal(IResource.DEPTH_ONE, null);

	}

	public static Map<String, Flow> readSubModels() throws Exception {

		final Properties models = PropUtils.loadProperties(getModelStore().getContents());

		Set<Object> modelSet = new TreeSet<Object>();
		modelSet.addAll(models.keySet());

		// Use map that retains order
		final Map<String, Flow> modelList = new LinkedHashMap<String, Flow>();

		// Iterate loadSubModels till all subModels have been loaded. This iteration is necessary
		// because of dependencies between subModels: if a subModle cannot be loaded because it's
		// dependent on a subModel not yet loaded it's stored in a set of models not yet loaded
		// and this set is then loaded in the next iteration.
		boolean continueToIterate = true;
		while (continueToIterate) {
			int noModelsBeforeLoad = modelSet.size();
//			logger.debug("Number of composite models to load: " + noModelsBeforeLoad);
			modelSet = loadSubModels(modelSet, modelList, false);
			int noModelsAfterLoad = modelSet.size();
//			logger.debug("Number of composite models not loaded in this iteration: " + noModelsAfterLoad);
			if ((noModelsAfterLoad == 0) || (noModelsAfterLoad == noModelsBeforeLoad)) {
				continueToIterate = false;
			}
			
		}
		// If there are remaining models load them with exception enabled in order to expose error message
		if (modelSet.size() != 0) {
			modelSet = loadSubModels(modelSet, modelList, true);
		}
		return modelList;
	}


	public static Set<Object> loadSubModels(Set<Object> subModels,  Map<String, Flow> modelList, boolean throwException) throws Exception {
		final IProject pass = ModelUtils.getPasserelleProject();
		pass.refreshLocal(IResource.DEPTH_INFINITE, null);
		final Set<Object> subModelsNotLoaded = new TreeSet<Object>();
		for (Object modelOb : subModels) {

			final String modelName = (String)modelOb;
			if (modelName==null||"".equals(modelName)) continue;
			
			final IFile file = pass.getFile(modelName+".moml");
			try {
				if (file.exists()) {
//					logger.debug("Loading composite: " + modelName);

					Flow flow = FlowManager.readMoml(new InputStreamReader(file.getContents()));
//					flow.setSource(file.getLocation().toOSString());
					if (flow.isClassDefinition()) {
						MoMLParser.putActorClass(modelName, flow);
						flow.setName(modelName);
						modelList.put(modelName, flow);
					}

				}

			} catch (Exception e1) {
				if (throwException) {
					throw(e1);
				}
				subModelsNotLoaded.add(modelOb);
//				logger.debug("Failed to load composite (might be non-fatal, could be loaded in next iteration): "+modelName, e1.getMessage());
			}
		}
		pass.refreshLocal(IResource.DEPTH_INFINITE, null);
		return subModelsNotLoaded;

	}
	
	
	private static IFile getModelStore() throws Exception {
		final IFile file = ModelUtils.getPasserelleProject().getFile(
				"submodels.properties");
		if (!file.exists()) {
			file.create(
					new ByteArrayInputStream("# Passerelle Properties".getBytes()),
					true, null);
		}
		return file;
	}

	public static void deleteSubModel(final String name) throws Exception {

		if (modelMap == null) {
			modelMap = getSubModels();
		}

		modelMap.remove(name);

		final IFile store = getModelStore();
		final Properties models = PropUtils.loadProperties(store.getContents());

		models.remove(name);

		PropUtils.storeProperties(models, store.getLocation().toOSString());
		store.refreshLocal(IResource.DEPTH_ONE, null);
	}

}
