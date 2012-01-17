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

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;

/**
 * Represents the data view page.
 * 
 * 
 */
public abstract class ActorPalettePage extends Page implements ISelectionProvider {

	private TreeViewer treeViewer;

	private ListenerList selectionChangedListeners = new ListenerList(
			ListenerList.IDENTITY);

	/**
	 * Creates the SWT control for this page under the given parent control.
	 * 
	 * @param parent
	 *            the parent control
	 */
	public void createControl(Composite parent) {
		treeViewer = createTreeViewer(parent);
		getTreeViewer().addSelectionChangedListener(
				new ISelectionChangedListener() {

					public void selectionChanged(SelectionChangedEvent event) {
						treeSelect(event);
					}

				});

		treeViewer.getTree().addListener(SWT.PaintItem, new Listener() {

			public void handleEvent(Event event) {
				// Fix bug 192094
				TreeItem item = (TreeItem) event.item;
				if (item == null)
					return;
			}
		});

		configTreeViewer();
		hookTreeViewer();
		initPage();
	}

	protected void initPage() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.IPageBookViewPage#init(org.eclipse.ui.part.IPageSite)
	 */
	public void init(IPageSite pageSite) {
		super.init(pageSite);
		pageSite.setSelectionProvider(this);
	}

	/**
	 * The <code>Page</code> implementation of this <code>IPage</code> method
	 * returns <code>null</code> if the tree viewer is null. Returns the tree
	 * viewer's control if tree viewer is null
	 */
	public Control getControl() {
		if (treeViewer == null)
			return null;
		return treeViewer.getControl();
	}

	/**
	 * Sets the focus to the tree viewer's control
	 */
	public void setFocus() {
		treeViewer.getControl().setFocus();
	}

	protected void configTreeViewer() {
	}

	protected abstract TreeViewer createTreeViewer(Composite parent);

	protected void hookTreeViewer() {

	}

	/**
	 * Returns the tree viewer
	 * 
	 * @return the tree viewer
	 */
	protected TreeViewer getTreeViewer() {
		return treeViewer;
	}

	/**
	 * Selects the node
	 * 
	 * @param event
	 *            the selection changed event
	 */
	protected void treeSelect(SelectionChangedEvent event) {
		fireSelectionChanged(event.getSelection());
	}

	/**
	 * Fires a selection changed event.
	 * 
	 * @param selection
	 *            the new selection
	 */
	protected void fireSelectionChanged(ISelection selection) {
		final SelectionChangedEvent event = new SelectionChangedEvent(this,
				selection);
	}

	/**
	 * Notifies that the selection has changed.
	 * 
	 * @param event
	 *            event object describing the change
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		setSelection(event.getSelection());
	}

	/**
	 * Adds a listener for selection changes in this selection provider. Has no
	 * effect if an identical listener is already registered.
	 * 
	 * @param listener
	 *            a selection changed listener
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

	/**
	 * Returns the current selection for this provider.
	 * 
	 * @return the current selection
	 */
	public ISelection getSelection() {
		if (getTreeViewer() == null) {
			return StructuredSelection.EMPTY;
		}
		return getTreeViewer().getSelection();
	}

	/**
	 * Removes the given selection change listener from this selection provider.
	 * Has no affect if an identical listener is not registered.
	 * 
	 * @param listener
	 *            a selection changed listener
	 */
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	/**
	 * Sets the current selection for this selection provider.
	 * 
	 * @param selection
	 *            the new selection
	 */
	public void setSelection(ISelection selection) {
		if (getTreeViewer() != null) {
			getTreeViewer().setSelection(selection);
		}

	}

	/**
	 * The <code>Page</code> implementation of this <code>IPage</code> method
	 * disposes of this page's control (if it has one and it has not already
	 * been disposed).
	 */
	public void dispose() {
		selectionChangedListeners.clear();
		treeViewer = null;

		super.dispose();
	}

	private boolean canSetSelection(List list) {
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			Object element = iter.next();
//			if (UIUtil.containElement(getTreeViewer(), element)) {
				return true;
			// }
		}
		return false;
	}

}