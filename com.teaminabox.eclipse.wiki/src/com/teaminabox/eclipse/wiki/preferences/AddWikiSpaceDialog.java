/*
 * Shamelessly stolen from org.eclipse.ant.internal.ui.preferences
 * 
 * Copyright (c) 2000, 2003 IBM Corporation and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 */
package com.teaminabox.eclipse.wiki.preferences;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;

public class AddWikiSpaceDialog extends Dialog {

	private String		name;
	private String		value;

	private String		title;

	private Label		nameLabel;
	private Text		nameText;
	private Label		valueLabel;
	private Text		valueText;
	private Label		noteLabel;

	private String[]	initialValues;

	public AddWikiSpaceDialog(Shell shell, String title, String[] initialValues) {
		super(shell);
		this.title = title;
		this.initialValues = initialValues;
	}

	protected Control createDialogArea(Composite parent) {
		Composite comp = (Composite) super.createDialogArea(parent);
		((GridLayout) comp.getLayout()).numColumns = 3;
		addFields(comp);
		addResourceButton(comp);
		addNote(parent);

		return comp;
	}

	private void addFields(Composite comp) {
		nameLabel = new Label(comp, SWT.NONE);
		nameText = new Text(comp, SWT.BORDER | SWT.SINGLE);
		nameText.setText(initialValues[0]);
		layoutFields(comp, WikiPlugin.getResourceString("WikiSpacePreferencePage.prefix"), nameLabel, nameText); //$NON-NLS-1$
		new Label(comp, SWT.NONE); // spacer

		valueLabel = new Label(comp, SWT.NONE);
		valueText = new Text(comp, SWT.BORDER | SWT.SINGLE);
		valueText.setText(initialValues[1]);
		layoutFields(comp, WikiPlugin.getResourceString("WikiSpacePreferencePage.url"), valueLabel, valueText); //$NON-NLS-1$
	}

	private void layoutFields(Composite comp, String labelText, Label label, Text text) {
		label.setText(labelText);
		label.setFont(comp.getFont());

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 300;
		text.setLayoutData(gd);
		text.setFont(comp.getFont());
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateButtons();
			}
		});
	}

	private void addNote(Composite parent) {
		Composite noteComposite = new Composite(parent, SWT.NONE);
		GridLayout basicLayout = new GridLayout();
		basicLayout.numColumns = 1;
		noteComposite.setLayout(basicLayout);

		noteLabel = new Label(noteComposite, SWT.NONE);
		noteLabel.setText(WikiPlugin.getResourceString("WikiSpacePreferencePage.addNote")); //$NON-NLS-1$
		noteLabel.setFont(noteComposite.getFont());
	}

	private void addResourceButton(Composite comp) {
		Button button = new Button(comp, SWT.PUSH);
		button.setText(WikiPlugin.getResourceString("WikiSpacePreferencePage.resource")); //$NON-NLS-1$
		button.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				openResourceDialog();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	protected void openResourceDialog() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot(), false, WikiPlugin.getResourceString("WikiSpacePreferencePage.selectResource")); //$NON-NLS-1$
		dialog.open();
		if (dialog.getReturnCode() == Window.OK && dialog.getResult().length != 0) {
			IPath path = (IPath) dialog.getResult()[0];
			valueText.setText(WikiConstants.ECLIPSE_PREFIX + path.toString());
		}
	}

	/**
	 * Return the name/value pair entered in this dialog. If the cancel button was hit, both will be <code>null</code>.
	 */
	public String[] getNameValuePair() {
		return new String[] { name, value };
	}

	/**
	 * @see Dialog#buttonPressed(int)
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			name = nameText.getText();
			value = valueText.getText();
		} else {
			name = null;
			value = null;
		}
		super.buttonPressed(buttonId);
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (title != null) {
			shell.setText(title);
		}
	}

	/**
	 * Enable the OK button if valid input
	 */
	protected void updateButtons() {
		String name = nameText.getText().trim();
		String value = valueText.getText().trim();
		getButton(IDialogConstants.OK_ID).setEnabled(isValid(name, value));
	}

	private boolean isValid(String name, String value) {
		return name.length() > 0 && value.length() > 0 && name.indexOf(WikiConstants.WIKISPACE_SEPARATOR) < 0 && value.indexOf(WikiConstants.WIKISPACE_SEPARATOR) < 0;
	}

	/**
	 * Enable the buttons on creation.
	 */
	public void create() {
		super.create();
		updateButtons();
	}
}