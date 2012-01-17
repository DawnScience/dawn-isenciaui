/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package com.isencia.passerelle.workbench.model.editor.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

import com.isencia.passerelle.workbench.model.editor.ui.Activator;
import com.isencia.passerelle.workbench.model.editor.ui.editor.actions.EditSubmodelAction;
import com.isencia.passerelle.workbench.model.editor.ui.palette.PaletteGroup;
import com.isencia.passerelle.workbench.model.editor.ui.palette.PaletteItemDefinition;

/**
 * The provider class used by views
 */

public class ActorTreeProvider implements ITreeContentProvider, ILabelProvider {


	@Override
	public Object[] getChildren(Object parentElement) {

		if (parentElement instanceof PaletteGroup) {
			List<PaletteItemDefinition> paletteItems = ((PaletteGroup) parentElement).getPaletteItems();
			List<PaletteGroup> groups = ((PaletteGroup) parentElement).getPaletteGroups();
			List allItems = new ArrayList();
			allItems.addAll(paletteItems);
			allItems.addAll(groups);
			return allItems.toArray();
		}
		return new Object[] {};
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof PaletteItemDefinition) {
			((PaletteItemDefinition) element).getGroup();
		}
		if (element instanceof PaletteGroup) {
			((PaletteGroup) element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		// TODO Auto-generated method stub
		return element instanceof PaletteGroup
				&& (((PaletteGroup) element).hasPaletteItems() || ((PaletteGroup) element)
						.hasPaletteGroups());
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Object[]) {
			return (Object[]) inputElement;

		}
		return getChildren(inputElement);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof PaletteItemDefinition) {
			return ((PaletteItemDefinition) element).getIcon().createImage();
		}
		if (element instanceof PaletteGroup) {
			
			if (((PaletteGroup)element).getIcon()!=null) {
			    return ((PaletteGroup)element).getIcon().createImage();
			} else {
			    Activator.getImageDescriptor("icons/folder.gif").createImage();
			}
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof PaletteItemDefinition) {
			return ((PaletteItemDefinition) element).getName();
		}
		if (element instanceof PaletteGroup) {
			return ((PaletteGroup) element).getName();
		}
		return null;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

}