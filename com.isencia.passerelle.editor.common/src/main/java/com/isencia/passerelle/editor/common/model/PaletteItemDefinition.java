package com.isencia.passerelle.editor.common.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.isencia.passerelle.editor.common.utils.EditorUtils;

public class PaletteItemDefinition implements Serializable, Comparable<PaletteItemDefinition> {
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof PaletteItemDefinition)) {
      return false;
    }
    PaletteItemDefinition def = (PaletteItemDefinition) obj;
    return def.getClazz().equals(getClazz()) && def.getId().equals(getId());
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(getClazz()).append(id).hashCode();
  }

  
  public PaletteItemDefinition(Object icon, PaletteGroup group, String id, String name, String color, Class clazz,String bundleId) {
    this.group = group;
    this.id = id;
    this.icon = icon;
    this.name = name;
    this.bundleId = bundleId;
    if (clazz != null)
      this.clazz = clazz.getName();
    if (group != null)
      group.addPaletteItem(this);
    if (color != null && !color.contains("rgb")) {
      StringBuffer sb = new StringBuffer("rgb(");
      sb.append(color);
      sb.append(")");
      this.color = sb.toString();
    } else {
      this.color = color;
    }

  }

  private String bundleId;
  
  public String getBundleId() {
    return bundleId;
  }

  private Object icon;

  public Object getIcon() {
    return icon;
  }

  public void setIcon(Object icon) {
    this.icon = icon;
  }

  private PaletteGroup group;

  public PaletteGroup getGroup() {
    return group;
  }

  public PaletteGroup getTopGroup() {
    return getTopGroup(group);
  }

  private PaletteGroup getTopGroup(PaletteGroup group) {
    if (group == null) {
      return null;
    }
    if (group.getParent() == null)
      return group;
    return getTopGroup(group.getParent());
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

    return EditorUtils.loadClass(clazz);
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  private String id;
  private String name;
  private String color;
  private String clazz;
  private String width = "100";

  public String getWidth() {
    return width;
  }

  public void setWidth(String width) {
    this.width = width;
  }

  public int compareTo(PaletteItemDefinition arg0) {
    if (this.name == null) {
      return 0;
    }
    return this.name.compareTo(arg0.getName());
  }
}
