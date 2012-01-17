package com.teaminabox.eclipse.wiki.editors;

import static com.teaminabox.eclipse.wiki.WikiPlugin.wikiPlugin;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.outline.WikiContentOutlinePage;
import com.teaminabox.eclipse.wiki.util.Resources;

public class WikiBrowserEditor extends MultiPageEditorPart implements IReusableEditor, IResourceChangeListener, PropertyListener {

	private final class ResourceChangedEventHandler implements Runnable {
		private final IResourceChangeEvent	event;

		private ResourceChangedEventHandler(IResourceChangeEvent event) {
			this.event = event;
		}

		public void run() {
			IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
			for (IWorkbenchPage element : pages) {
				if (((FileEditorInput) editor.getEditorInput()).getFile().getProject().equals(event.getResource())) {
					IEditorPart editorPart = element.findEditor(editor.getEditorInput());
					element.closeEditor(editorPart, true);
				}
			}
		}
	}

	private WikiEditor		editor;
	private WikiBrowser		wikiBrowser;
	private int				browserIndex;
	private int				sourceIndex;
	private Browser			syntaxBrowser;
	private int				syntaxIndex;
	private PropertyChangeAdapter	propertyListener;

	public WikiBrowserEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		propertyListener = new PropertyChangeAdapter(this);
	}

	public void propertyChanged() {
		initialiseSyntaxBrowser();
	}

	@Override
	public void dispose() {
		propertyListener.dispose();
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		wikiBrowser.dispose();
		editor.dispose();
		super.dispose();
	}

	private void createSourcePage() {
		try {
			editor = new WikiEditor();
			editor.setReusableEditor(this);
			sourceIndex = addPage(editor, getEditorInput());
			setPageText(sourceIndex, "Source");
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(), "Error creating nested text editor", null, e.getStatus());
		}
	}

	private void createBrowserPage() {
		wikiBrowser = new WikiBrowser(editor);
		Composite composite = new Composite(getContainer(), SWT.NULL);
		composite.setLayout(new FillLayout());
		wikiBrowser.createPartControl(composite);
		browserIndex = addPage(composite);
		setPageText(browserIndex, "Browser");
	}

	@Override
	protected void createPages() {
		createSourcePage();
		createBrowserPage();
		createSyntaxPage();
		initialiseActivePage();
	}

	private void initialiseActivePage() {
		if (wikiPlugin().getPluginPreferences().getBoolean(WikiConstants.SHOW_BROWSER_IN_EDITOR_WHEN_OPENING)) {
			setActivePage(browserIndex);
		} else {
			setActivePage(sourceIndex);
		}
	}

	private void createSyntaxPage() {
		Composite composite = new Composite(getContainer(), SWT.NULL);
		composite.setLayout(new FillLayout());
		syntaxBrowser = new Browser(composite, SWT.NONE);
		initialiseSyntaxBrowser();
		syntaxIndex = addPage(composite);
		setPageText(syntaxIndex, "Syntax");
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		getEditor(0).doSave(monitor);
	}

	@Override
	public void doSaveAs() {
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(0, editor.getTitle());
		setInput(editor.getEditorInput());
	}

	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	@Override
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput)) {
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		}
		super.init(site, editorInput);
		setEditorTitle();
	}

	private void setEditorTitle() {
		if (getEditorInput() != null) {
			setPartName(getEditorInput().getName());
		}
	}

	@Override
	public void setInput(IEditorInput newInput) {
		super.setInputWithNotify(newInput);
		if (editor != null) {
			editor.setInput(newInput);
			wikiBrowser.redrawWebView();
			setEditorTitle();
		}
	}

	@Override
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		if (newPageIndex == browserIndex) {
			wikiBrowser.redrawWebView();
		}
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			Display.getDefault().asyncExec(new ResourceChangedEventHandler(event));
		}
	}

	public WikiEditor getEditor() {
		return editor;
	}

	public boolean isEditingSource() {
		return getActivePage() == sourceIndex;
	}

	private void initialiseSyntaxBrowser() {
		if (syntaxBrowser == null) {
			return;
		}
		String renderer = editor.getContext().getContentRenderer().getName();
		IPath path = new Path(WikiConstants.HELP_PATH).append(renderer + ".html");
		try {
			syntaxBrowser.setText(Resources.getContentsRelativeToPlugin(path));
		} catch (Exception e) {
			wikiPlugin().log("Unable to load syntax", e);
			syntaxBrowser.setText(e.getLocalizedMessage());
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class key) {
		if (key.equals(IContentOutlinePage.class)) {
			return new WikiContentOutlinePage(editor);
		}
		return super.getAdapter(key);
	}

}