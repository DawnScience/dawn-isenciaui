package com.teaminabox.eclipse.wiki.export;

import static com.teaminabox.eclipse.wiki.WikiPlugin.wikiPlugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.teaminabox.eclipse.wiki.WikiConstants;

public class WikiExportWizard extends Wizard implements INewWizard {
	static final QualifiedName		DIRECTORY_QUALIFIED_NAME	= new QualifiedName(WikiConstants.PLUGIN_ID, "exportDirectory");

	private WikiExportWizardPage	page;
	private ISelection				selection;

	public WikiExportWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		page = new WikiExportWizardPage(selection);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		persistExportProperties();
		return runOperationInContainer(new IRunnableWithProgress() {
			public void run(final IProgressMonitor monitor) throws InvocationTargetException {
				doExport(monitor);
			}
		});
	}

	private boolean runOperationInContainer(IRunnableWithProgress runnable) {
		try {
			getContainer().run(true, true, runnable);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			wikiPlugin().log("", e);
			MessageDialog.openError(getShell(), "Error", e.getTargetException().getMessage());
			return false;
		}

		return true;
	}

	private void persistExportProperties() {
		IProject project = page.getFolder().getProject();
		try {
			project.setPersistentProperty(WikiExportWizard.DIRECTORY_QUALIFIED_NAME, new File(page.getExportDirectoryPath()).getAbsolutePath());
		} catch (CoreException cex) {
			noteException(cex);
		}
	}

	private void noteException(CoreException cex) {
		wikiPlugin().log("Export Error", cex);
		throw new RuntimeException("An error occurred. Please see the log for details.");
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	private void doExport(final IProgressMonitor monitor) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				try {
					new WikiExporter().export(page.getFolder(), page.getExportDirectoryPath(), monitor);
				} catch (Exception e) {
					throw new RuntimeException(e);
				} finally {
					monitor.done();
				}
			}
		});
	}
}
