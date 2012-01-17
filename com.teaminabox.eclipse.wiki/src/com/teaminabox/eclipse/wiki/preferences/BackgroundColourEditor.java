package com.teaminabox.eclipse.wiki.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.teaminabox.eclipse.wiki.WikiPlugin;

public class BackgroundColourEditor {

	private Button					bgDefault;
	private ColorEditor				bgColorEditor;
	private Button					bgCustom;
	private Composite				colorComposite;
	private final IPreferenceStore	preferenceStore;

	public BackgroundColourEditor(Composite parent, IPreferenceStore preferenceStore) {
		this.preferenceStore = preferenceStore;
		createControl(parent);
	}

	private IPreferenceStore getPreferenceStore() {
		return preferenceStore;
	}

	private void createControl(Composite parent) {
		colorComposite = new Composite(parent, SWT.NULL);
		colorComposite.setLayout(new GridLayout());
		GridData gd = new GridData();
		gd.horizontalSpan = 1;
		colorComposite.setLayoutData(gd);

		Group backgroundComposite = new Group(colorComposite, SWT.SHADOW_ETCHED_IN);
		backgroundComposite.setLayout(new RowLayout());
		backgroundComposite.setText(WikiPlugin.getResourceString("WikiSyntaxPreferencePage.backgroundColor")); //$NON-NLS-1$

		SelectionAdapter backgroundSelectionListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean custom = bgCustom.getSelection();
				bgColorEditor.getButton().setEnabled(custom);
			}
		};

		bgDefault = new Button(backgroundComposite, SWT.RADIO | SWT.LEFT);
		bgDefault.setText(WikiPlugin.getResourceString("WikiSyntaxPreferencePage.systemDefault")); //$NON-NLS-1$
		bgDefault.addSelectionListener(backgroundSelectionListener);

		bgCustom = new Button(backgroundComposite, SWT.RADIO | SWT.LEFT);
		bgCustom.setText(WikiPlugin.getResourceString("WikiSyntaxPreferencePage.custom")); //$NON-NLS-1$
		bgCustom.addSelectionListener(backgroundSelectionListener);

		bgColorEditor = new ColorEditor(backgroundComposite);

		setSelection(getPreferenceStore().getBoolean(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT));
	}

	private void setSelection(boolean systemDefault) {
		RGB rgb = PreferenceConverter.getColor(getPreferenceStore(), AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND);
		bgColorEditor.setColorValue(rgb);
		bgDefault.setSelection(systemDefault);
		bgCustom.setSelection(!systemDefault);
		bgColorEditor.getButton().setEnabled(!systemDefault);
	}

	public void loadDefault() {
		setSelection(getPreferenceStore().getDefaultBoolean(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT));
		bgColorEditor.setColorValue(PreferenceConverter.getDefaultColor(getPreferenceStore(), AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND));
	}

	public void store() {
		PreferenceConverter.setValue(getPreferenceStore(), AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND, bgColorEditor.getColorValue());
		getPreferenceStore().setValue(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT, bgDefault.getSelection());
	}

}
