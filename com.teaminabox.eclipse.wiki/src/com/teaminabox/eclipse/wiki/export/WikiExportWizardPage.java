package com.teaminabox.eclipse.wiki.export;

import static com.teaminabox.eclipse.wiki.WikiPlugin.wikiPlugin;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import com.teaminabox.eclipse.wiki.WikiPlugin;

public class WikiExportWizardPage extends WizardPage implements IPropertyChangeListener, SelectionListener {
	private StringFieldEditor	folderText;
	private StringFieldEditor	exportDirectoryText;
	private ISelection			selection;

	public WikiExportWizardPage(ISelection selection) {
		super(WikiPlugin.getResourceString("Export.wizardTitle"));
		setTitle(WikiPlugin.getResourceString("Export.wizardTitle"));
		setDescription(WikiPlugin.getResourceString("Export.wizardDescription"));
		this.selection = selection;
	}

	public void createControl(Composite parent) {
		Composite rootComposite = createControlsContainer(parent);

		try {
			initialize();
		} catch (RuntimeException rex) {
			throw rex;
		} catch (CoreException cex) {
			wikiPlugin().log("", cex);
			throw new RuntimeException("Caught CoreException. See log for details.");
		}
		dialogChanged();
		setControl(rootComposite);
	}

	private Composite createControlsContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 20;
		container.setLayout(layout);
		container.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

		createCommonControls(container);
		return container;
	}

	private void createCommonControls(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		container.setLayout(layout);
		container.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

		createFolderControls(container);
		createExportDirectoryControls(container);
	}

	private void createExportDirectoryControls(Composite container) {
		exportDirectoryText = addStringFieldEditor(container, WikiPlugin.getResourceString("Export.wizardExportDirectory"));

		Button button = new Button(container, SWT.PUSH);
		button.setText(WikiPlugin.getResourceString("Export.wizardBrowse"));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleBrowseHtmlExportLocation();
			}
		});
	}

	private void createFolderControls(Composite container) {
		folderText = addStringFieldEditor(container, WikiPlugin.getResourceString("Export.wizardFolder"));

		Button button = new Button(container, SWT.PUSH);
		button.setText(WikiPlugin.getResourceString("Export.wizardBrowse"));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					handleBrowseFolders();
				} catch (CoreException cex) {
					wikiPlugin().log("", cex);
					throw new RuntimeException("Caught CoreException. See log for details.");
				}
			}
		});
	}

	private StringFieldEditor addStringFieldEditor(Composite container, String labelText) {
		Label label = new Label(container, SWT.NULL);
		label.setText(labelText);

		Composite editorComposite = new Composite(container, SWT.NULL);
		editorComposite.setLayout(new GridLayout());
		editorComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		StringFieldEditor editor = new StringFieldEditor("", "", editorComposite);

		editor.setPropertyChangeListener(this);

		return editor;
	}

	private void initialize() throws CoreException {
		if (selection == null || selection.isEmpty() || !(selection instanceof IStructuredSelection)) {
			return;
		}

		IStructuredSelection ssel = (IStructuredSelection) selection;
		if (ssel.size() == 1) {
			initialiseFromSelectedObject(ssel.getFirstElement());
		}
	}

	private void initialiseFromSelectedObject(Object obj) throws CoreException {
		if (obj instanceof IFolder || obj instanceof IProject) {
			initialiseFolder((IResource) obj);
		}
	}

	private void initialiseFolder(IResource resource) throws CoreException {
		folderText.setStringValue(resource.getFullPath().toString());
		initialiseExportDirectoryText(resource);
	}

	private void initialiseExportDirectoryText(IResource resource) throws CoreException {
		String exportDir = resource.getProject().getPersistentProperty(WikiExportWizard.DIRECTORY_QUALIFIED_NAME);
		if (exportDir != null) {
			exportDirectoryText.setStringValue(exportDir);
		} else {
			exportDirectoryText.setStringValue("");
		}
	}

	private void handleBrowseHtmlExportLocation() {
		DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.SINGLE | SWT.OPEN);
		String path = dialog.open();
		if (path != null) {
			exportDirectoryText.setStringValue(path);
		}
	}

	private void handleBrowseFolders() throws CoreException {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot(), false, WikiPlugin.getResourceString("Export.wizardSelectFolder"));
		if (dialog.open() == Window.OK) {
			Object[] result = dialog.getResult();
			if (result != null && result.length == 1) {
				IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember((IPath) result[0]);
				if (resource.getType() == IResource.FILE) {
					return;
				}
				initialiseFolder(resource);
			}
		}
	}

	private void dialogChanged() {
		if (getFolderText().length() == 0) {
			updateStatus("Folder must be specified");
		} else if (getExportDirectoryPath().length() == 0) {
			updateStatus("Directory must be specified");
		} else {
			updateStatus(null);
		}
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getExportDirectoryPath() {
		return exportDirectoryText.getStringValue();
	}

	public void propertyChange(PropertyChangeEvent event) {
		dialogChanged();
	}

	public void widgetSelected(SelectionEvent e) {
		dialogChanged();
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		dialogChanged();
	}

	String getFolderText() {
		return folderText.getStringValue();
	}

	public IContainer getFolder() {
		return (IContainer) ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(getFolderText()));
	}
}
