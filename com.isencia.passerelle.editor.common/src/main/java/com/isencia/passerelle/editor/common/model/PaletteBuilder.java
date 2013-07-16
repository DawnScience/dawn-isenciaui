package com.isencia.passerelle.editor.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import ptolemy.kernel.util.Attribute;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.Locatable;
import ptolemy.kernel.util.Location;
import ptolemy.kernel.util.NameDuplicationException;
import ptolemy.kernel.util.NamedObj;

import com.isencia.passerelle.editor.common.activator.Activator;
import com.isencia.passerelle.editor.common.utils.EditorUtils;
import com.isencia.passerelle.model.Flow;
import com.isencia.passerelle.project.repository.api.MetaData;
import com.isencia.passerelle.project.repository.api.RepositoryService;

public class PaletteBuilder implements Serializable {
  public static final String UTILITIES = "com.isencia.passerelle.actor.actorgroup.utilities";
  public static final String SUBMODELS = "com.isencia.passerelle.actor.actorgroup.submodels";
  List<PaletteGroup> paletteGroups;
  List<PaletteGroup> editablePaletteGroups;

  private Map<String, String> actorBundleMap = new HashMap<String, String>();

  public String getBuildId(String className) {
    return actorBundleMap.get(className);
  }

  private boolean rerender = false;

  public boolean isRerender() {
    return rerender;
  }

  public void setRerender(boolean rerender) {
    this.rerender = rerender;
  }

  public List<PaletteGroup> getPaletteGroups() {
    return paletteGroups;
  }

  public List<PaletteGroup> getRootPaletteGroups() {
    List<PaletteGroup> groups = new ArrayList<PaletteGroup>();
    for (PaletteGroup group : paletteGroups) {
      if (group.getParent() == null) {
        groups.add(group);
      }
    }
    return groups;
  }

  public List<PaletteGroup> getEditablePaletteGroups() {
    if (editablePaletteGroups == null) {
      editablePaletteGroups = new ArrayList<PaletteGroup>();
      for (PaletteGroup group : paletteGroups) {
        if (!UTILITIES.equals(group.getId()) && !SUBMODELS.equals(group.getId()) && group.getParent() == null && group.isAuthorized()) {
          editablePaletteGroups.add(group);
        }
      }
    }
    return editablePaletteGroups;
  }

  public PaletteBuilder() {
    super();
    paletteGroups = createCategories();
  }

  private Map<String, PaletteGroup> groups;
  private Map<String, PaletteItemDefinition> paletteItemMap;

  public PaletteItemDefinition getPaletteItem(String clazz) {

    return paletteItemMap.get(clazz);
  }

  public PaletteGroup getPaletteGroup(String id) {

    return groups.get(id);
  }

  public PaletteItemDefinition getPaletteItem(String groupName, String id) {
    if (groupName == null) {
      for (Map.Entry<String, PaletteGroup> entry : groups.entrySet()) {
        PaletteGroup group = entry.getValue();
        PaletteItemDefinition def = group.getPaletteItem(id);
        if (def != null) {
          return def;
        }
      }
    }

    PaletteGroup group = groups.get(groupName);
    if (group == null) {
      return null;
    }
    return group.getPaletteItem(id);
  }

  public Object getIcon(String clazzName) {
    if (Flow.class.getName().equals(clazzName)) {
      if (submodelDefinition != null) {
        return submodelDefinition.getIcon();
      }
    }
    PaletteItemDefinition itemDefinition = getPaletteItem(clazzName);
    if (itemDefinition != null) {
      return itemDefinition.getIcon();
    }
    return newDefaultIdeIcon();
  }

  protected Object newDefaultIdeIcon() {
    return null;
  }

  public String getType(String clazzName) {

    PaletteItemDefinition itemDefinition = getPaletteItem(clazzName);
    if (itemDefinition != null) {
      return itemDefinition.getName();
    }
    Class clazz = EditorUtils.loadClass(clazzName);
    if (clazz != null) {
      return clazz.getSimpleName();
    }
    return clazzName;
  }

  PaletteItemDefinition submodelDefinition = null;
  PaletteGroup submodels = null;

  public List<PaletteGroup> createCategories() {
    List<PaletteGroup> actorGroups = new ArrayList<PaletteGroup>();
    Object folderIcon = newDefaultFolderIcon();
    groups = new HashMap<String, PaletteGroup>();
    paletteItemMap = new HashMap<String, PaletteItemDefinition>();
    try {
      IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor("com.isencia.passerelle.engine.actorGroups");
      if (config != null) {
        for (IConfigurationElement configurationElement : config) {
          String idAttribute = configurationElement.getAttribute("id");
          // if (visibleTreeNodes == null
          // || visibleTreeNodes.contains(idAttribute)) {

          String nameAttribute = configurationElement.getAttribute("name");

          String parentAttribute = configurationElement.getAttribute("parent");
          String priorityAttribute = configurationElement.getAttribute("priority");
          String expandedAttribute = configurationElement.getAttribute("open");
          String iconAttribute = configurationElement.getAttribute("icon");
          String iconLocationAttribute = configurationElement.getAttribute("iconClass");
          String featureAttribute = configurationElement.getAttribute("features");
          final String bundleId = configurationElement.getDeclaringExtension().getContributor().getName();
          Object icon = null;
          try {
            icon = createIcon(folderIcon, iconLocationAttribute, iconAttribute, bundleId);
          } catch (NoClassDefFoundError e) {
            icon = createIcon(null, iconLocationAttribute, iconAttribute, bundleId);

          }
          PaletteGroup e = new PaletteGroup(idAttribute, nameAttribute, parentAttribute);
          if (priorityAttribute != null) {
            try {
              e.setPriority(Integer.parseInt(priorityAttribute));
            } catch (Exception e2) {

            }
          }
          if (e.getParent() == null && !StringUtils.isEmpty(featureAttribute)) {
            e.setAuthorized(false);
            try {
              String[] features = featureAttribute.split(",");
              for (String feature : features) {
                if (checkPermission(feature)) {
                  e.setAuthorized(true);
                  break;
                }
              }
            } catch (Exception exp) {
              e.setAuthorized(true);
            }
          }
          if (expandedAttribute != null) {
            try {
              e.setExpanded(Boolean.valueOf(expandedAttribute));
            } catch (Exception e2) {

            }
          }
          e.setIcon(icon);
          actorGroups.add(e);
          groups.put(e.getId(), e);

        }
      }
      // }
      for (PaletteGroup group : groups.values()) {
        group.init(this);
      }
      Collections.sort(actorGroups);

    } catch (Exception e) {
      e.printStackTrace();
    }
    submodels = getSubModelGroup();

    // Find all Actors and add them to the corresponding container
    IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor("com.isencia.passerelle.engine.actors");
    if (config != null) {

      for (IConfigurationElement configurationElement : config) {
        String groupAttribute = configurationElement.getAttribute("group");
        String[] parents = groupAttribute.split(",");
        for (String parent : parents) {
          PaletteGroup group = groups.get(parent);
          if (group != null) {
            String nameAttribute = configurationElement.getAttribute("name");
            String colorAttribute = configurationElement.getAttribute("color");
            String idAttribute = configurationElement.getAttribute("id");
            String iconAttribute = configurationElement.getAttribute("icon");
            String iconLocationAttribute = configurationElement.getAttribute("iconClass");
            final String bundleId = configurationElement.getDeclaringExtension().getContributor().getName();

            Object icon = createIcon(null, iconLocationAttribute, iconAttribute, bundleId);
            if (group != null && submodels != null && submodels.getId().equals(group.getId())) {
              submodelDefinition = new PaletteItemDefinition(icon, null, idAttribute, nameAttribute, colorAttribute, Flow.class, null);
            } else {
              final Class<?> clazz = loadClass(configurationElement, bundleId);

              if (clazz != null && group != null) {
                PaletteItemDefinition item = new PaletteItemDefinition(icon, group, idAttribute, nameAttribute, colorAttribute, clazz, bundleId);
                group.addPaletteItem(item);
                actorBundleMap.put(clazz.getName(), bundleId);
                paletteItemMap.put(item.getClazz().getName(), item);
              }
            }
          }
        }
      }
    }

    try {
      RepositoryService repositoryService = Activator.getDefault().getRepositoryService();
      for (String actorClass : repositoryService.getAllSubmodels()) {
        MetaData metaData = repositoryService.getSubmodelMetaData(actorClass);
        String path = metaData != null ? metaData.getPath() : null;

        PaletteGroup group = getPaletteGroup(actorGroups, groups, path, submodels);
        SubModelPaletteItemDefinition item = addSubModel(submodelDefinition, group, actorClass);
        if (group != null) {
          group.addPaletteItem(item);
        }
      }
    } catch (Exception e) {

      // logError(e);
    }
    return actorGroups;
  }

  public PaletteGroup getPaletteGroup(List<PaletteGroup> groups, Map<String, PaletteGroup> groupMap, String path, PaletteGroup parent) {
    if (path == null) {
      return parent;
    }

    String id = parent.getId() + "/" + path;
    if (groupMap.containsKey(id)) {
      return groupMap.get(id);
    }

    PaletteGroup paletteGroup = new PaletteGroup(id, path, parent.getId());
    paletteGroup.setParent(parent);
    groupMap.put(id, paletteGroup);
    groups.add(paletteGroup);
    parent.addChild(paletteGroup);
    return paletteGroup;

  }

  public PaletteGroup getSubModelGroup() {
    return groups.get(SUBMODELS);
  }

  protected Object newDefaultFolderIcon() {
    return null;
  }

  protected boolean checkPermission(String feature) {
    return true;
  }

  public SubModelPaletteItemDefinition addSubModel(PaletteItemDefinition sd, PaletteGroup gr, String name) {
    PaletteGroup group = gr;
    PaletteItemDefinition submodelDef = sd;
    if (submodelDef == null) {
      submodelDef = submodelDefinition;
    }
    if (group == null) {
      group = submodels;
    }
    SubModelPaletteItemDefinition item = new SubModelPaletteItemDefinition(submodelDef != null ? submodelDef.getIcon() : null, group, name, name,
        submodelDef != null ? submodelDef.getColor() : null);

    return item;
  }

  /**
   * @param iconClazzAttribute
   * @param iconAttribute
   * @param bundleId
   * @return
   */
  protected Object createIcon(Object defaultIcon, String iconClazzAttribute, String iconAttribute, final String bundleId) {

    return defaultIcon;
  }

  public Class<?> loadClass(final IConfigurationElement configurationElement, final String bundleId) {

    String className = configurationElement.getAttribute("class");
    return EditorUtils.loadClass(className);

  }

  protected Class loadClassFromBundleId(final String bundleId, String iconClazz) throws ClassNotFoundException {
    Bundle bundle = Platform.getBundle(bundleId);
    Class dummy = bundle.loadClass(iconClazz);
    return dummy;
  }

  @SuppressWarnings("unchecked")
  public static void setLocation(NamedObj model, double[] location) {
    if (model instanceof Locatable) {
      try {
        ((Locatable) model).setLocation(location);
        NamedObj cont = model.getContainer();
        cont.attributeChanged((Attribute) model);
      } catch (IllegalActionException e) {
      }

    }
    List<Attribute> attributes = model.attributeList(Locatable.class);
    if (attributes == null) {
      return;
    }
    if (attributes.size() > 0) {
      Locatable locationAttribute = (Locatable) attributes.get(0);
      try {
        locationAttribute.setLocation(location);
        model.attributeChanged(attributes.get(0));
      } catch (IllegalActionException e) {
      }
    } else {
      try {
        new Location(model, "_location").setLocation(location);
      } catch (IllegalActionException e) {
      } catch (NameDuplicationException e) {
      }
    }
  }

  public static boolean isSubModelGroup(PaletteGroup group) {
    return group != null && group.getName().equals(SUBMODELS);
  }

  public void logError(Exception e) {

  }
}
