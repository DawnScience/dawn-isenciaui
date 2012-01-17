package com.teaminabox.eclipse.wiki.outline;

import java.util.ArrayList;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertySource;

public class OutlineElement implements IWorkbenchAdapter, IAdaptable {

	private String						headingName;
	private int							offset;
	private int							numberOfLines;
	private int							length;
	private ArrayList<OutlineElement>	children;
	private ImageDescriptor				imageDescriptor;

	public OutlineElement(IAdaptable parent, String heading, int offset, int length, ImageDescriptor imageDescriptor) {
		this.imageDescriptor = imageDescriptor;
		if (parent instanceof OutlineElement) {
			((OutlineElement) parent).addChild(this);
		}
		this.headingName = heading;
		this.offset = offset;
		this.length = length;
		children = new ArrayList<OutlineElement>();
	}

	private void addChild(OutlineElement child) {
		children.add(child);
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (adapter == IWorkbenchAdapter.class) {
			return this;
		}
		if (adapter == IPropertySource.class) {
			return new OutlineElementProperties(this);
		}

		return null;
	}

	public Object[] getChildren(Object object) {
		if (children != null) {
			return children.toArray();
		}
		return new Object[0];
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return imageDescriptor;
	}

	public String getLabel(Object o) {
		return headingName;
	}

	public int getLength() {
		return length;
	}

	public int getNumberOfLines() {
		return numberOfLines;
	}

	public Object getParent(Object o) {
		return null;
	}

	public int getStart() {
		return offset;
	}

	public void setNumberOfLines(int newNumberOfLines) {
		numberOfLines = newNumberOfLines;
	}

}