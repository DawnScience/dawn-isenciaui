package com.isencia.passerelle.workbench.model.editor.ui.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import ptolemy.data.type.BaseType;
import ptolemy.kernel.util.NamedObj;
import ptolemy.kernel.util.StringAttribute;

public class CommentPropertySource extends EntityPropertySource {

	public CommentPropertySource(NamedObj entity,IFigure figure) {
		super(entity,figure);
		// TODO Auto-generated constructor stub
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		Collection<PropertyDescriptor> descriptors = new ArrayList<PropertyDescriptor>();
		List<StringAttribute> parameterList = ((NamedObj) getEditableValue())
				.attributeList(StringAttribute.class);
		for (StringAttribute parameter : parameterList) {
			addPropertyDescriptor(descriptors, parameter,BaseType.STRING);
		}
		return (IPropertyDescriptor[]) descriptors
				.toArray(new IPropertyDescriptor[] {});
	}

	

}
