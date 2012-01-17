package com.teaminabox.eclipse.wiki.preferences;

import java.util.HashMap;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;

public class EditorColours {

	private static final String[][]		COLOUR_LIST		= new String[][] { { WikiPlugin.getResourceString("WikiSyntaxPreferencePage.WikiName"), WikiConstants.WIKI_NAME }, //$NON-NLS-1$
			{ WikiPlugin.getResourceString("WikiSyntaxPreferencePage.NewWikiName"), WikiConstants.NEW_WIKI_NAME }, //$NON-NLS-1$
			{ WikiPlugin.getResourceString("WikiSyntaxPreferencePage.WikiSpaceURL"), WikiConstants.WIKI_URL }, //$NON-NLS-1$
			{ WikiPlugin.getResourceString("WikiSyntaxPreferencePage.URL"), WikiConstants.URL }, //$NON-NLS-1$
			{ WikiPlugin.getResourceString("WikiSyntaxPreferencePage.EclipseResource"), WikiConstants.ECLIPSE_RESOURCE }, //$NON-NLS-1$
			{ WikiPlugin.getResourceString("WikiSyntaxPreferencePage.PluginResource"), WikiConstants.PLUGIN_RESOURCE }, //$NON-NLS-1$
			{ WikiPlugin.getResourceString("WikiSyntaxPreferencePage.JavaType"), WikiConstants.JAVA_TYPE }, //$NON-NLS-1$
			{ WikiPlugin.getResourceString("WikiSyntaxPreferencePage.Other"), WikiConstants.OTHER }, //$NON-NLS-1$
													};

	private List					colors;
	private ColorEditor				fgColorEditor;
	private Button					fgBold;
	private RGB[]					currentColours	= new RGB[COLOUR_LIST.length];
	private HashMap<String, String>	currentBold		= new HashMap<String, String>();

	private final IPreferenceStore	preferenceStore;

	public EditorColours(Composite parent, IPreferenceStore preferenceStore) {
		this.preferenceStore = preferenceStore;
		createControl(parent);
		load();
	}

	private IPreferenceStore getPreferenceStore() {
		return preferenceStore;
	}

	private void createControl(Composite parent) {
		Composite colorComposite = new Composite(parent, SWT.NULL);
		colorComposite.setLayout(new GridLayout());
		colorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label label = new Label(colorComposite, SWT.LEFT);
		label.setText(WikiPlugin.getResourceString("WikiSyntaxPreferencePage.foreground")); //$NON-NLS-1$
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite editorComposite = new Composite(colorComposite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		editorComposite.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		editorComposite.setLayoutData(gd);

		colors = new List(editorComposite, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
		gd = new GridData(GridData.FILL_BOTH);
		colors.setLayoutData(gd);

		Composite stylesComposite = new Composite(editorComposite, SWT.NONE);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 2;
		stylesComposite.setLayout(layout);
		stylesComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		label = new Label(stylesComposite, SWT.LEFT);
		label.setText(WikiPlugin.getResourceString("WikiSyntaxPreferencePage.color")); //$NON-NLS-1$
		gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;
		label.setLayoutData(gd);

		fgColorEditor = new ColorEditor(stylesComposite);

		Button fgColorButton = fgColorEditor.getButton();
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = GridData.BEGINNING;
		fgColorButton.setLayoutData(gd);
		fgColorButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int i = colors.getSelectionIndex();
				currentColours[i] = fgColorEditor.getColorValue();
			}
		});

		label = new Label(stylesComposite, SWT.LEFT);
		label.setText(WikiPlugin.getResourceString("WikiSyntaxPreferencePage.bold")); //$NON-NLS-1$
		gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;
		label.setLayoutData(gd);

		fgBold = new Button(stylesComposite, SWT.CHECK);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = GridData.BEGINNING;
		fgBold.setLayoutData(gd);

		fgBold.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int i = colors.getSelectionIndex();
				String key = COLOUR_LIST[i][1] + WikiConstants.SUFFIX_STYLE;
				String value = (fgBold.getSelection()) ? WikiConstants.STYLE_BOLD : WikiConstants.STYLE_NORMAL;
				currentBold.put(key, value);
			}
		});

		colors.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleSyntaxColorListSelection();
			}
		});

	}

	private void handleSyntaxColorListSelection() {
		int i = colors.getSelectionIndex();
		fgColorEditor.setColorValue(currentColours[i]);
		fgBold.setSelection(getPreferenceStore().getString(COLOUR_LIST[i][1] + WikiConstants.SUFFIX_STYLE).indexOf(WikiConstants.STYLE_BOLD) >= 0);
	}

	private void load() {
		colors.removeAll();
		for (int i = 0; i < COLOUR_LIST.length; i++) {
			colors.add(COLOUR_LIST[i][0]);
			currentColours[i] = PreferenceConverter.getColor(getPreferenceStore(), COLOUR_LIST[i][1] + WikiConstants.SUFFIX_FOREGROUND);
		}
		colors.select(0);
		handleSyntaxColorListSelection();
	}

	protected void loadDefault() {
		colors.removeAll();
		for (int i = 0; i < COLOUR_LIST.length; i++) {
			colors.add(COLOUR_LIST[i][0]);
			currentColours[i] = PreferenceConverter.getDefaultColor(getPreferenceStore(), COLOUR_LIST[i][1] + WikiConstants.SUFFIX_FOREGROUND);
		}
		colors.select(0);
		handleSyntaxColorListSelection();
	}

	public void store() {
		for (int i = 0; i < currentColours.length; i++) {
			String key = COLOUR_LIST[i][1] + WikiConstants.SUFFIX_FOREGROUND;
			PreferenceConverter.setValue(getPreferenceStore(), key, currentColours[i]);
		}
		for (String key : currentBold.keySet()) {
			getPreferenceStore().setValue(key, currentBold.get(key));
		}
	}

}
