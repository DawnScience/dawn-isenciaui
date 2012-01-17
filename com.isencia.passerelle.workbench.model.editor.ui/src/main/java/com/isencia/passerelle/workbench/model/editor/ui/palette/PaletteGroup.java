package com.isencia.passerelle.workbench.model.editor.ui.palette;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;

public class PaletteGroup implements Serializable, Comparable<PaletteGroup> {
	
	private Map<String, PaletteItemDefinition> paletteItemMap = new HashMap<String, PaletteItemDefinition>();
	private Map<String, PaletteGroup> paletteGroupMap = new HashMap<String, PaletteGroup>();
	private List<PaletteItemDefinition> paletteItems = new ArrayList<PaletteItemDefinition>();

	public List<PaletteItemDefinition> getPaletteItems() {
		return paletteItems;
	}

	private List<PaletteGroup> paletteGroups = new ArrayList<PaletteGroup>();

	public List<PaletteGroup> getPaletteGroups() {
		return paletteGroups;
	}

	private PaletteGroup parent;

	public boolean hasPaletteItems() {
		return !paletteItems.isEmpty();
	}

	public boolean hasPaletteGroups() {
		return !paletteGroups.isEmpty();
	}

	public PaletteGroup getParent() {
		return parent;
	}

	public void setParent(PaletteGroup parent) {
		this.parent = parent;
	}

	private boolean expanded;

	public boolean isExpanded() {
		return expanded;
	}

	private ImageDescriptor icon;

	public ImageDescriptor getIcon() {
		return icon;
	}

	public void setIcon(ImageDescriptor icon) {
		this.icon = icon;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	private int priority;

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	private String id;
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PaletteGroup(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}


	public void addPaletteItem(PaletteItemDefinition item) {
		paletteItemMap.put(item.getId(), item);
		if (paletteItemMap.containsKey(item.getId())){
			paletteItems.remove(paletteItemMap.get(item.getId()));
		}
		paletteItems.add(item);
	}

	public void removePaletteItem(PaletteItemDefinition def) {
		paletteItems.remove(def);
		paletteItemMap.remove(def.getId());
	}

	public void addPaletteGroup(PaletteGroup group) {
		paletteGroups.add(group);
		paletteGroupMap.put(group.getId(), group);

	}

	public PaletteItemDefinition getPaletteItem(String id) {
		return paletteItemMap.get(id);
	}

	public PaletteGroup getPaletteGroup(String id) {
		return paletteGroupMap.get(id);
	}

	public int compareTo(PaletteGroup arg0) {
		if (this.priority != arg0.getPriority()) {
			return -(this.priority - arg0.getPriority());
		}

		return this.name.compareTo(arg0.getName());
	}
}
