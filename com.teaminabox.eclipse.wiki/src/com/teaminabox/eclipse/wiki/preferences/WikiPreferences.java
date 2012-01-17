package com.teaminabox.eclipse.wiki.preferences;

import static com.teaminabox.eclipse.wiki.WikiPlugin.wikiPlugin;

import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;

public class WikiPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static Map<String, String>	wikispace;
	private WikiSpacePreferencePage		wikiSpacePreferencePage;
	private BackgroundColourEditor		backgroundColourEditor;
	private EditorColours				editorColours;

	public WikiPreferences() {
		super(FieldEditorPreferencePage.GRID);
		setDescription(WikiPlugin.getResourceString("WikiSyntaxPreferencePage.description")); //$NON-NLS-1$
		setPreferenceStore(wikiPlugin().getPreferenceStore());
	}

	@Override
	public boolean performOk() {
		wikiSpacePreferencePage.store();
		backgroundColourEditor.store();
		editorColours.store();
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		backgroundColourEditor.loadDefault();
		wikiSpacePreferencePage.loadDefault();
		editorColours.loadDefault();
		super.performDefaults();
	}

	@Override
	protected void addField(FieldEditor editor) {
		super.addField(editor);
	}

	@Override
	public void createFieldEditors() {
		Composite composite = new Composite(getFieldEditorParent(), SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		TabFolder folder = new TabFolder(composite, SWT.NONE);

		createEditorPreferences(createTab(folder, "WikiPreferences.editorPreferencesTitle")); //$NON-NLS-1$
		wikiSpacePreferencePage = new WikiSpacePreferencePage(createTab(folder, "WikiPreferences.WikiSpacePreferencesTitle"), getPreferenceStore()); //$NON-NLS-1$
		createRendererFieldEditors(createTab(folder, "WikiPreferences.rendererPreferencesTitle")); //$NON-NLS-1$

		Dialog.applyDialogFont(composite);
	}

	private void createEditorPreferences(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout basicLayout = new GridLayout();
		basicLayout.numColumns = 2;
		composite.setLayout(basicLayout);

		backgroundColourEditor = new BackgroundColourEditor(composite, getPreferenceStore());
		editorColours = new EditorColours(composite, getPreferenceStore());
		addField(new StringFieldEditor(WikiConstants.HOVER_PREVIEW_LENGTH, WikiPlugin.getResourceString("WikiSyntaxPreferencePage.HoverLength"), composite)); //$NON-NLS-1$
		addField(new BooleanFieldEditor(WikiConstants.SHOW_BROWSER_IN_EDITOR_WHEN_OPENING, WikiPlugin.getResourceString("WikiSyntaxPreferencePage.showBrowserInEditorWhenOpening"), composite));//$NON-NLS-1$
		addField(new BooleanFieldEditor(WikiConstants.REUSE_EDITOR, WikiPlugin.getResourceString("WikiSyntaxPreferencePage.reuseEditor"), composite)); //$NON-NLS-1$
		addField(new BooleanFieldEditor(WikiConstants.WORD_WRAP, WikiPlugin.getResourceString("WikiSyntaxPreferencePage.wordWrap"), composite));//$NON-NLS-1$
	}

	private void createRendererFieldEditors(Composite parent) {
		addField(new StringFieldEditor(WikiConstants.BROWSER_CSS_URL, WikiPlugin.getResourceString(WikiConstants.BROWSER_CSS_URL), parent));
		addField(new ComboListEditor(WikiConstants.BROWSER_RENDERER, WikiPlugin.getResourceString(WikiConstants.BROWSER_RENDERER), getBrowserRendererLabels(), WikiConstants.BROWSER_RENDERERS, parent));
		addField(new BooleanFieldEditor(WikiConstants.RENDER_FULLY_QUALIFIED_TYPE_NAMES, WikiPlugin.getResourceString(WikiConstants.RENDER_FULLY_QUALIFIED_TYPE_NAMES), parent));
	}

	private String[] getBrowserRendererLabels() {
		String[] labels = new String[WikiConstants.BROWSER_RENDERERS.length];
		for (int i = 0; i < labels.length; i++) {
			labels[i] = WikiPlugin.getResourceString(WikiConstants.BROWSER_RENDERERS[i]);
		}
		return labels;
	}

	private Composite createTab(TabFolder folder, String titleKey) {
		TabItem item = new TabItem(folder, SWT.NONE);
		item.setText(WikiPlugin.getResourceString(titleKey));
		Composite c = new Composite(folder, SWT.NONE);
		c.setLayout(new TabFolderLayout());
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		item.setControl(c);
		return c;
	}

	public void init(IWorkbench workbench) {
	}

	public static void init(IPreferenceStore store) {
		WikiPreferences.wikispace = WikiPreferences.reloadWikiSpaceMap(store);
	}

	public static Map<String, String> reloadWikiSpaceMap(IPreferenceStore store) {
		TreeMap<String, String> wikiSpace = new TreeMap<String, String>();
		String names = store.getDefaultString(WikiConstants.WIKISPACE_NAMES);
		String urls = store.getDefaultString(WikiConstants.WIKISPACE_URLS);
		StringTokenizer nameTokenizer = new StringTokenizer(names, WikiConstants.WIKISPACE_SEPARATOR);
		StringTokenizer urlTokenizer = new StringTokenizer(urls, WikiConstants.WIKISPACE_SEPARATOR);
		while (nameTokenizer.hasMoreTokens() && urlTokenizer.hasMoreTokens()) {
			String wiki = nameTokenizer.nextToken();
			String urlPrefix = urlTokenizer.nextToken();
			wikiSpace.put(wiki, urlPrefix);
		}
		return wikiSpace;
	}

	public static Map<String, String> getWikiSpace() {
		return WikiPreferences.wikispace;
	}

	static void setWikiSpace(Map<String, String> wikis) {
		WikiPreferences.wikispace = wikis;
	}
}