package com.isencia.passerelle.workbench.model.editor.ui.palette;

import java.io.Serializable;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;

public class PaletteItemDefinition implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1589063207120521216L;

	public PaletteItemDefinition(ImageDescriptor icon, PaletteGroup group,
			String id, String name, String color, Class clazz) {
		this(icon, group, id, name, clazz);
		if (color != null) {
			String[] colors = color.split(",");
			if (colors.length == 3) {
				try {
					this.color = new Color(null, Integer.parseInt(colors[0]),
							Integer.parseInt(colors[1]), Integer
									.parseInt(colors[2]));
				} catch (Exception e) {

				}
			}
		}

	}

	public PaletteItemDefinition(ImageDescriptor icon, PaletteGroup group,
			String id, String name, Class clazz) {
		this(group, id, name, clazz);
		this.icon = icon;
	}

	public PaletteItemDefinition(PaletteGroup group, String id, String name,
			Class clazz) {
		super();
		this.group = group;
		this.id = id;
		this.name = name;
		this.clazz = clazz;
		group.addPaletteItem(this);
	}

	private ImageDescriptor icon;

	public ImageDescriptor getIcon() {
		return icon;
	}

	public void setIcon(ImageDescriptor icon) {
		this.icon = icon;
	}

	private PaletteGroup group;

	public PaletteGroup getGroup() {
		return group;
	}

	public void setGroup(String groupId) {
		this.group = group;
	}

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

	public Class getClazz() {
		return clazz;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}

	private String id;
	private String name;
	private Color color;
	private Class clazz;
	private boolean refresh;

	public boolean isRefresh() {
		return refresh;
	}

	public void setRefresh(boolean refresh) {
		this.refresh = refresh;
	}

}
