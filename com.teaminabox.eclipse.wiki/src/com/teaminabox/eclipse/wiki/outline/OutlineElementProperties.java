package com.teaminabox.eclipse.wiki.outline;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class OutlineElementProperties implements IPropertySource {

	public static final String	PROPERTY_LINECOUNT	= "lineno"; //$NON-NLS-1$
	public static final String	PROPERTY_START		= "start";	//$NON-NLS-1$
	public static final String	PROPERTY_LENGTH		= "length"; //$NON-NLS-1$

	private OutlineElement		element;

	public OutlineElementProperties(OutlineElement element) {
		super();
		this.element = element;
	}

	public Object getEditableValue() {
		return this;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		IPropertyDescriptor[] propertyDescriptors = new IPropertyDescriptor[3];

		PropertyDescriptor descriptor;

		descriptor = new PropertyDescriptor(OutlineElementProperties.PROPERTY_LINECOUNT, "Line_count"); //$NON-NLS-1$
		propertyDescriptors[0] = descriptor;
		descriptor = new PropertyDescriptor(OutlineElementProperties.PROPERTY_START, "Title_start"); //$NON-NLS-1$
		propertyDescriptors[1] = descriptor;
		descriptor = new PropertyDescriptor(OutlineElementProperties.PROPERTY_LENGTH, "Title_length"); //$NON-NLS-1$
		propertyDescriptors[2] = descriptor;

		return propertyDescriptors;
	}

	public Object getPropertyValue(Object name) {
		if (name.equals(OutlineElementProperties.PROPERTY_LINECOUNT)) {
			return new Integer(element.getNumberOfLines());
		}
		if (name.equals(OutlineElementProperties.PROPERTY_START)) {
			return new Integer(element.getStart());
		}
		if (name.equals(OutlineElementProperties.PROPERTY_LENGTH)) {
			return new Integer(element.getLength());
		}
		return null;
	}

	public boolean isPropertySet(Object property) {
		return false;
	}

	public void resetPropertyValue(Object property) {
	}

	public void setPropertyValue(Object name, Object value) {
	}

}