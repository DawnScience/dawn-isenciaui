package com.isencia.passerelle.workbench.model.editor.ui.views;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ptolemy.data.BooleanToken;
import ptolemy.data.expr.Variable;
import ptolemy.data.type.BaseType;
import ptolemy.kernel.util.Attribute;
import ptolemy.kernel.util.StringAttribute;

import com.isencia.passerelle.workbench.model.editor.ui.Constants;
import com.isencia.passerelle.workbench.model.editor.ui.HelpUtils;
import com.isencia.passerelle.workbench.model.editor.ui.properties.CellEditorAttribute;
import com.isencia.passerelle.workbench.model.editor.ui.properties.EntityPropertySource;
import com.isencia.passerelle.workbench.model.ui.GeneralAttribute;

/**
 * Editing support for parameter column.
 * 
 * @author gerring
 * 
 */
public class VariableEditingSupport extends EditingSupport {

	private static Logger logger = LoggerFactory
			.getLogger(VariableEditingSupport.class);

	private ActorAttributesView actorAttributesView;

	public VariableEditingSupport(ActorAttributesView part, ColumnViewer viewer) {
		super(viewer);
		this.actorAttributesView = part;
	}

	// private Object previousSelection;

	@Override
	protected CellEditor getCellEditor(Object element) {

		PropertyDescriptor desc = null;

		if (element instanceof CellEditorAttribute) {
			return ((CellEditorAttribute) element).createCellEditor(getViewer()
					.getControl());
		} else if (element instanceof GeneralAttribute) {

			if (((GeneralAttribute) element).getType().equals(
					GeneralAttribute.ATTRIBUTE_TYPE.NAME)) {
				desc = new TextPropertyDescriptor(VariableEditingSupport.class
						.getName()
						+ ".nameText", "Name");
			}

		} else if (element instanceof Variable) {
			desc = EntityPropertySource
					.getPropertyDescriptor((Variable) element);
		} else if (element instanceof StringAttribute) {
			desc = EntityPropertySource.getPropertyDescriptor(
					(StringAttribute) element, BaseType.STRING);
		}
		if (desc != null) {
			CellEditor createPropertyEditor = desc
					.createPropertyEditor((Composite) getViewer().getControl());
//			String contextId = HelpUtils.getContextId(element);
//			if (contextId != null) {
//				try {
//					PlatformUI.getWorkbench().getHelpSystem().setHelp(
//							createPropertyEditor.getControl(), contextId);
//					PlatformUI.getWorkbench().getHelpSystem()
//							.displayDynamicHelp();
//				} catch (Exception e) {
//
//				}
//			}
			return createPropertyEditor;
		}
		return null;

	}

	@Override
	protected boolean canEdit(Object element) {
		if (element instanceof GeneralAttribute)

			return ((GeneralAttribute) element).getType().equals(
					GeneralAttribute.ATTRIBUTE_TYPE.NAME);

		return true;
	}

	@Override
	protected Object getValue(Object element) {

		if (element instanceof GeneralAttribute)
			return ((GeneralAttribute) element).getValue();
		if (element instanceof StringAttribute)
			return ((StringAttribute) element).getExpression();
		final Variable param = (Variable) element;
		try {
			if (!param.isStringMode() && param.getToken() != null
					&& param.getToken() instanceof BooleanToken) {
				return ((BooleanToken) param.getToken()).booleanValue();
			}
		} catch (Exception ne) {
			logger.error("Cannot set read token from " + param.getName(), ne);
		}
		return param.getExpression();
	}

	public String showHelpSelectedParameter(Variable param) {

		Attribute attr = (Attribute) param;
		if (param.getContainer() != null) {
			String helpBundle = Constants.HELP_BUNDLE_ID;
			String actorName = param.getContainer().getClass().getName()
					.replace(".", "_");
			return helpBundle + "." + actorName + "_" + attr.getName();
		}
		return "";
	}

	@Override
	protected void setValue(Object element, Object value) {

		try {
			if (element instanceof GeneralAttribute) {
				actorAttributesView.setActorName((GeneralAttribute)element,(String)value);
			} else {
				actorAttributesView.setAttributeValue(element, value);
			}

		} catch (Exception ne) {
			logger.error("Cannot set variable value " + value, ne);
		}
	}

}
