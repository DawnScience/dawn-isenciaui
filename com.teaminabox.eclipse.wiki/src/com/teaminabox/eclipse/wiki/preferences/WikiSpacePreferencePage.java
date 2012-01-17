package com.teaminabox.eclipse.wiki.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;

public class WikiSpacePreferencePage {

	private TableColumn				prefixColumn;
	private TableColumn				urlColumn;
	private Button					addButton;
	private Button					editButton;
	private Button					removeButton;
	private Table					table;
	private Map<String, String>		wikis;
	private StringBuffer			names;
	private StringBuffer			urls;
	private final IPreferenceStore	preferenceStore;

	public WikiSpacePreferencePage(Composite parent, IPreferenceStore store) {
		this.preferenceStore = store;
		createControl(parent);
	}

	private void load() {
		wikis = new HashMap<String, String>(WikiPreferences.getWikiSpace());
		populateTable();
	}

	public void loadDefault() {
		wikis = new HashMap<String, String>(WikiPreferences.reloadWikiSpaceMap(preferenceStore));
		populateTable();
	}

	public void store() {
		updatePreferences();
		preferenceStore.putValue(WikiConstants.WIKISPACE_NAMES, names.toString());
		preferenceStore.putValue(WikiConstants.WIKISPACE_URLS, urls.toString());
		WikiPreferences.setWikiSpace(wikis);
	}

	private void updatePreferences() {
		names = new StringBuffer();
		urls = new StringBuffer();
		for (String wikiName : wikis.keySet()) {
			names.append(wikiName).append(WikiConstants.WIKISPACE_SEPARATOR);
			urls.append(wikis.get(wikiName).toString()).append(WikiConstants.WIKISPACE_SEPARATOR);
		}
	}

	private void createControl(final Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		container.setLayout(layout);

		Composite basicComposite = new Composite(container, SWT.NONE);
		layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		basicComposite.setLayout(layout);

		Label label = new Label(basicComposite, SWT.WRAP);
		label.setText(WikiPlugin.getResourceString("WikiSpacePreferencePage.description")); //$NON-NLS-1$
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		label.setLayoutData(gd);

		table = new Table(basicComposite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableLayout tableLayout = new TableLayout();
		table.setLayout(tableLayout);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		prefixColumn = new TableColumn(table, SWT.NONE);
		prefixColumn.setText(WikiPlugin.getResourceString("WikiSpacePreferencePage.prefix")); //$NON-NLS-1$

		urlColumn = new TableColumn(table, SWT.NONE);
		urlColumn.setText(WikiPlugin.getResourceString("WikiSpacePreferencePage.url")); //$NON-NLS-1$

		createButtons(basicComposite);

		load();
	}

	private void createButtons(Composite parent) {
		GridLayout layout;
		final Composite buttons = new Composite(parent, SWT.NONE);
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttons.setLayout(layout);

		addButton = createPushButton(buttons, WikiPlugin.getResourceString("WikiSpacePreferencePage.new")); //$NON-NLS-1$
		addButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				WikiSpacePreferencePage.this.add(buttons.getShell(), "", "");
			}
		});

		editButton = createPushButton(buttons, WikiPlugin.getResourceString("WikiSpacePreferencePage.edit")); //$NON-NLS-1$
		editButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				WikiSpacePreferencePage.this.edit(buttons.getShell());
			}
		});

		removeButton = createPushButton(buttons, WikiPlugin.getResourceString("WikiSpacePreferencePage.remove")); //$NON-NLS-1$
		removeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				WikiSpacePreferencePage.this.remove();
			}
		});
	}

	private Button createPushButton(Composite parent, String key) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText(JFaceResources.getString(key));
		button.setFont(parent.getFont());
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		int widthHint = convertHorizontalDLUsToPixels(button, IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		button.setLayoutData(data);
		return button;
	}

	private int convertHorizontalDLUsToPixels(Control control, int dlus) {
		GC gc = new GC(control);
		gc.setFont(control.getFont());
		int averageWidth = gc.getFontMetrics().getAverageCharWidth();
		gc.dispose();

		double horizontalDialogUnitSize = averageWidth * 0.25;

		return (int) Math.round(dlus * horizontalDialogUnitSize);
	}

	private void populateTable() {
		table.removeAll();
		for (String wiki : wikis.keySet()) {
			String urlPrefix = wikis.get(wiki).toString();
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] { wiki, urlPrefix });
		}
		prefixColumn.pack();
		urlColumn.pack();
	}

	private void remove() {
		TableItem[] items = table.getSelection();
		if (items.length != 0) {
			String name = items[0].getText(0);
			wikis.remove(name);
			populateTable();
		}
	}

	private void edit(Shell shell) {
		TableItem[] items = table.getSelection();
		if (items.length != 0) {
			String name = items[0].getText(0);
			String url = items[0].getText(1);
			String newName = add(shell, name, url);
			if (newName.length() > 0 && !name.equals(newName)) {
				wikis.remove(name);
				populateTable();
			}
		}
	}

	/**
	 * Add a new WikiSpace entry.
	 *
	 * @param shell
	 *            the shell to open the dialog with
	 * @param wiki
	 * @param url
	 * @return the name of the WikiSpace entry or empty string if nothing was added
	 */
	private String add(Shell shell, String wiki, String url) {
		AddWikiSpaceDialog dialog = new AddWikiSpaceDialog(shell, WikiPlugin.getResourceString("WikiSpacePreferencePage.addEntry"), new String[] { wiki, url }); //$NON-NLS-1$
		if (dialog.open() == Window.OK) {
			String[] newEntry = dialog.getNameValuePair();
			boolean eclipseWikiLink = newEntry[1].startsWith(WikiConstants.ECLIPSE_PREFIX) || newEntry[1].startsWith(WikiConstants.PLUGIN_PREFIX);
			if (eclipseWikiLink && !newEntry[1].endsWith(WikiConstants.PATH_SEPARATOR)) {
				newEntry[1] += WikiConstants.PATH_SEPARATOR;
			}
			wikis.put(newEntry[0], newEntry[1]);
			populateTable();
			return newEntry[0];
		}
		return "";
	}

}
