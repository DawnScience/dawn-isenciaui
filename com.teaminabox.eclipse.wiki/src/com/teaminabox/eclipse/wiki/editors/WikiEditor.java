/*
 * Contributors: Channing Walton. Torsten Juergeleit helped with making the content assist and open wiki link use the
 * same accelerators as the Java editor. Ronald Steinhau added the Plugin protocol support
 */
package com.teaminabox.eclipse.wiki.editors;

import static com.teaminabox.eclipse.wiki.WikiPlugin.wikiPlugin;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.TextOperationAction;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.text.TextRegion;
import com.teaminabox.eclipse.wiki.text.TextRegionBuilder;

public class WikiEditor extends TextEditor implements PropertyListener {

	public static final String	PART_ID				= WikiEditor.class.getName();
	public static final String	CONTEXT_MENU_ID		= WikiEditor.PART_ID + ".ContextMenu";
	public static final String	WIKI_TEMP_FOLDER	= "wiki";
	public static final String	WIKI_TEMP_PROJECT	= "wiki_temp";

	private ColourManager		colourManager;
	private Color				backgroundColor;
	private IReusableEditor		reusableEditor;
	private PropertyChangeAdapter		propertyListener	= new PropertyChangeAdapter(this);
	private WikiDocumentContext	context;

	public WikiEditor() {
		super();
		colourManager = new ColourManager(this);
		setSourceViewerConfiguration(new WikiConfiguration(this));
		setDocumentProvider(new FileDocumentProvider());
		setEditorContextMenuId(WikiEditor.CONTEXT_MENU_ID);
		Editors.registerEditor(this);
		reusableEditor = this;
	}

	public void propertyChanged() {
		if (getSourceViewer() != null) {
			setWordWrap();
			setBackgroundColor();
			redrawTextAsync();
		}
	}

	@Override
	protected void initializeKeyBindingScopes() {
		setKeyBindingScopes(new String[] { "org.eclipse.ui.textEditorScope", WikiConstants.KEYBINDING_CONTEXT });
	}

	@Override
	public void dispose() {
		Editors.unregisterEditor(this);
		propertyListener.dispose();
		colourManager.dispose();
		context.dispose();
		super.dispose();
	}

	void setWordWrap() {
		if (getSourceViewer() != null) {
			getSourceViewer().getTextWidget().setWordWrap(wikiPlugin().getPreferenceStore().getBoolean(WikiConstants.WORD_WRAP));
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		setBackgroundColor();
		setWordWrap();
	}

	void setBackgroundColor() {
		Control widget = getSourceViewer().getTextWidget();
		backgroundColor = getBackgroundColor();
		widget.setBackground(backgroundColor);
	}

	private Color getBackgroundColor() {
		if (wikiPlugin().getPreferenceStore().getBoolean(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT)) {
			return null;
		}
		RGB rgb = PreferenceConverter.getColor(wikiPlugin().getPreferenceStore(), AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND);
		return colourManager.getColor(rgb);
	}

	public void openWikiLinkOnSelection() {
		IDocument doc = getDocument();
		ITextSelection selection = (ITextSelection) getSelectionProvider().getSelection();
		int pos = selection.getOffset();
		TextRegion textRegion = TextRegionBuilder.getTextRegionAtCursor(this, doc, pos);
		boolean opened = openWikiLink(textRegion);
		// if the cursor is between text regions then we need to try the next
		// region up
		if (!opened && pos < doc.getLength()) {
			textRegion = TextRegionBuilder.getTextRegionAtCursor(this, doc, pos + 1);
			openWikiLink(textRegion);
		}
	}

	public IDocument getDocument() {
		return getDocumentProvider().getDocument(getEditorInput());
	}

	/**
	 * @return boolean true if the word has been dealt with
	 */
	private boolean openWikiLink(TextRegion textRegion) {
		if (textRegion == null || textRegion.getLength() == 0) {
			return false;
		}
		return textRegion.accept(new WikiLinkLauncher(this));
	}

	/**
	 * This is unfortunate but I cannot see how else to get the {@link AbstractTextEditor#getSourceViewer() TextViewer}
	 * to support unit tests.
	 */
	public ITextViewer getTextViewerForTest() {
		return getSourceViewer();
	}

	void saveIfNeeded() {
		if (isDirty()) {
			doSave(getProgressMonitor());
		}
	}

	public int getOffset(int line) throws BadLocationException {
		return getDocumentProvider().getDocument(getEditorInput()).getLineOffset(line);
	}

	/**
	 * Is the file being edited local?
	 *
	 * @return whether the file is local or not
	 */
	public boolean isLocal() {
		return getEditorInput() instanceof FileEditorInput;
	}

	private IWorkbenchPage getActivePage() {
		IWorkbenchWindow workbenchWindow = getEditorSite().getWorkbenchWindow();
		return workbenchWindow.getActivePage();
	}

	public ColourManager getColourManager() {
		return colourManager;
	}

	@Override
	protected void createActions() {
		super.createActions();

		IAction action = new TextOperationAction(wikiPlugin().getResourceBundle(), "ContentAssistProposal.", this, ISourceViewer.CONTENTASSIST_PROPOSALS);

		action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		setAction(WikiConstants.CONTENT_ASSIST, action);
	}

	public void navigateToNextLink() {
		new NextLinkNavigator(this).next();
	}

	public void navigateToPreviousLink() {
		new PreviousLinkNavigator(this).previous();
	}

	public String getDocumentText() {
		if (getSourceViewer() != null && getSourceViewer().getDocument() != null) {
			return getSourceViewer().getDocument().get();
		}
		return "";
	}

	public void openPreview() {
		new Preview(this, (ITextSelection) getSelectionProvider().getSelection(), getSourceViewer()).show();
	}

	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		try {
			context = new WikiDocumentContext(((IFileEditorInput) input).getFile());
		} catch (IOException e) {
			wikiPlugin().log("Error whilst setting editor input", e);
		}
	}

	public void redrawTextAsync() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (WikiEditor.this.getSourceViewer() != null && !WikiEditor.this.getSourceViewer().getTextWidget().isDisposed()) {
					WikiEditor.this.getSourceViewer().invalidateTextPresentation();
				}
			}
		});
	}

	public void setReusableEditor(IReusableEditor reusableEditor) {
		this.reusableEditor = reusableEditor;
	}

	public WikiDocumentContext getContext() {
		return context;
	}

	@Override
	public boolean isEditable() {
		return !isTempWiki();
	}

	private boolean isTempWiki() {
		IEditorInput editorInput = getEditorInput();
		if (editorInput instanceof FileEditorInput) {
			return isTempWiki(((FileEditorInput) editorInput).getFile());
		}
		return false;
	}

	public boolean isTempWiki(IFile file) {
		if (file.getProject() == null || !file.getProject().isOpen()) {
			return true;
		}
		return file.getProject().getName().equals(WikiEditor.WIKI_TEMP_PROJECT);
	}

	public void openWith(IFile file) {
		saveIfNeeded();
		getActivePage().reuseEditor(reusableEditor, new FileEditorInput(file));
	}

}