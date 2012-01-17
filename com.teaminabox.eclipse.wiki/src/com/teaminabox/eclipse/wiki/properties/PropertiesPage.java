package com.teaminabox.eclipse.wiki.properties;

import static com.teaminabox.eclipse.wiki.properties.ProjectProperties.projectProperties;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;

public class PropertiesPage extends PropertyPage {

	private Combo	combo;
	private Button	enabled;

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		composite.setLayout(layout);

		enabled = new Button(composite, SWT.CHECK);
		enabled.setText(WikiPlugin.getResourceString("WikiProjectProperties.enableProjectSpecificSettings")); //$NON-NLS-1$

		enabled.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setEnablement();
			}

		});
		enabled.setSelection(projectProperties().isProjectPropertiesEnabled(getProject()));

		Label label = new Label(composite, SWT.LEFT);
		label.setText(WikiPlugin.getResourceString("BrowserRenderer")); //$NON-NLS-1$

		combo = new Combo(composite, SWT.DROP_DOWN);
		setRendererLabels();
		setEnablement();

		Dialog.applyDialogFont(composite);

		return composite;
	}

	private void setEnablement() {
		combo.setEnabled(enabled.getSelection());
	}

	private void setRendererLabels() {
		String preferred = ProjectProperties.projectProperties().getRenderer(getProject());
		int selected = -1;
		String[] items = new String[WikiConstants.BROWSER_RENDERERS.length];
		for (int i = 0; i < WikiConstants.BROWSER_RENDERERS.length; i++) {
			String renderer = WikiConstants.BROWSER_RENDERERS[i];
			items[i] = WikiPlugin.getResourceString(renderer);
			if (preferred != null && preferred.equals(renderer)) {
				selected = i;
			}
		}
		combo.setItems(items);
		if (selected >= 0) {
			combo.select(selected);
		} else {
			combo.select(0);
		}
	}

	@Override
	protected void performDefaults() {
		projectProperties().setDefaults(getProject());
	}

	@Override
	public boolean performOk() {
		if (combo.getSelectionIndex() >= 0) {
			projectProperties().setRenderer(getProject(), WikiConstants.BROWSER_RENDERERS[combo.getSelectionIndex()]);
		}
		projectProperties().setProjectPropertiesEnabled(getProject(), enabled.getSelection());
		return true;
	}

	private IProject getProject() {
		if (getElement() instanceof IJavaProject) {
			return ((IJavaProject) getElement()).getProject();
		}
		return (IProject) getElement();
	}

}
