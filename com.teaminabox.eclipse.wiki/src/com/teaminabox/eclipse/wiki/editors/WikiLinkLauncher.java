package com.teaminabox.eclipse.wiki.editors;

import static com.teaminabox.eclipse.wiki.WikiPlugin.wikiPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;
import com.teaminabox.eclipse.wiki.text.EclipseResourceTextRegion;
import com.teaminabox.eclipse.wiki.text.ForcedLinkTextRegion;
import com.teaminabox.eclipse.wiki.text.GenericTextRegionVisitor;
import com.teaminabox.eclipse.wiki.text.JavaTypeTextRegion;
import com.teaminabox.eclipse.wiki.text.PluginPathFinder;
import com.teaminabox.eclipse.wiki.text.PluginResourceTextRegion;
import com.teaminabox.eclipse.wiki.text.UrlTextRegion;
import com.teaminabox.eclipse.wiki.text.WikiUrlTextRegion;
import com.teaminabox.eclipse.wiki.text.WikiWordTextRegion;
import com.teaminabox.eclipse.wiki.util.Resources;

/**
 * Each visitor method will return {@link Boolean#TRUE true} or {@link Boolean#FALSE false} depending on whether the
 * link was opened or not.
 */
final class WikiLinkLauncher extends GenericTextRegionVisitor<Boolean> {

	private final WikiEditor	editor;

	public WikiLinkLauncher(WikiEditor editor) {
		super(false);
		this.editor = editor;
	}

	@Override
	public Boolean visit(UrlTextRegion urlTextRegion) {
		Program.launch(urlTextRegion.getText());
		return true;
	}

	@Override
	public Boolean visit(WikiWordTextRegion wikiNameTextRegion) {
		try {
			openWikiDocument(wikiNameTextRegion.getText());
		} catch (Exception e) {
			wikiPlugin().logAndReport(WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_OPEN_WIKI_FILE_TITLE), WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_OPEN_WIKI_FILE_TEXT), e);
		}
		return true;
	}

	@Override
	public Boolean visit(ForcedLinkTextRegion region) {
		try {
			openWikiDocument(region.getWikiDocumentName());
		} catch (Exception e) {
			wikiPlugin().logAndReport(WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_OPEN_WIKI_FILE_TITLE), WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_OPEN_WIKI_FILE_TEXT), e);
		}
		return true;
	}

	@Override
	public Boolean visit(WikiUrlTextRegion wikiUrlTextRegion) {
		openWikiUrl(wikiUrlTextRegion);
		return true;
	}

	@Override
	public Boolean visit(EclipseResourceTextRegion eclipseResourceTextRegion) {
		String location = eclipseResourceTextRegion.getText();
		openEclipseLocation(location);
		return true;
	}
	
	@Override
	public Boolean visit(PluginResourceTextRegion pluginResourceTextRegion) {
		String location = pluginResourceTextRegion.getText();
		openPluginLocation(location);
		return true;
	}

	@Override
	public Boolean visit(JavaTypeTextRegion javaTypeTextRegion) {
		openType(javaTypeTextRegion);
		return true;
	}

	void openWikiDocument(String wikiWord) throws CoreException {
		IFile file = editor.getContext().getWikiFile(wikiWord);
		editor.getContext().createNewFileIfNeeded(file);
		openFile(file);
	}

	void openWikiUrl(WikiUrlTextRegion wikiUrl) {
		if (wikiUrl.getLink().startsWith(WikiConstants.ECLIPSE_PREFIX)) {
			openEclipseLocation(wikiUrl.getLink());
		} else if (wikiUrl.getLink().startsWith(WikiConstants.PLUGIN_PREFIX)) {
			openPluginLocation(wikiUrl.getLink());
		} else {
			Program.launch(wikiUrl.getLink());
		}
	}

	void openEclipseLocation(String location) {
		String resource = new String(location.substring(WikiConstants.ECLIPSE_PREFIX.length()));
		if (resource.length() > 0) {
			openEclipseResource(resource);
		}
	}

	void openPluginLocation(String location) {
		String resource = new String(location.substring(WikiConstants.PLUGIN_PREFIX.length()));
		if (resource.length() > 0) {
			openPluginResource(resource);
		}
	}

	private void openPluginResource(String path) {
		try {
			PathWithLineNumber pathWithLineNumber = new PathWithLineNumber(path);
			if (pathWithLineNumber.segmentCount() < 2) {
				wikiPlugin().reportError(WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_OPEN_PLUGIN_RESOURCE_TITLE), WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_OPEN_PLUGIN_RESOURCE_TEXT));
				return;
			}
			IResource resource = findPluginResource(path);
			if (Resources.existsAsFile(resource)) {
				if (Resources.isWikiFile(resource)) {
					IEditorPart part = openFile((IFile) resource);
					if (pathWithLineNumber.getLine() > 0 && part instanceof AbstractTextEditor) {
						gotoLine(pathWithLineNumber.getLine(), part);
					}
					return;
				}
				Program.launch(resource.getLocation().toString());
			}
			wikiPlugin().reportError(WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_OPEN_PLUGIN_RESOURCE_TITLE), WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_OPEN_PLUGIN_RESOURCE_TEXT) + " " + path);
		} catch (Exception e) {
			wikiPlugin().logAndReport(WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_OPEN_WIKI_FILE_TITLE), "Unable to open " + path, e);
		}
	}

	private IEditorPart openFile(IFile file) throws CoreException {
		if (Resources.isWikiFile(file) && wikiPlugin().getPreferenceStore().getBoolean(WikiConstants.REUSE_EDITOR) && !editor.isTempWiki(file)) {
			editor.openWith(file);
			return editor;
		}
		IEditorPart part = IDE.openEditor(getActivePage(), file, true);
		editor.redrawTextAsync(); // so that links are redrawn if necessary
		return part;
	}

	private IResource findPluginResource(String path) throws CoreException, FileNotFoundException {
		IResource resource = PluginResourceTextRegion.findResource(path);
		if (resource == null) {
			resource = createTemporaryPluginResource(path, resource);
		}
		return resource;
	}

	private IResource createTemporaryPluginResource(String path, IResource resource) throws CoreException, FileNotFoundException {
		IPath relPath = PluginPathFinder.getPluginPath(path);
		File xfile = relPath.toFile();
		if (xfile.exists()) {
			IProject wikiTemp = ResourcesPlugin.getWorkspace().getRoot().getProject(WikiEditor.WIKI_TEMP_PROJECT);
			if (!wikiTemp.exists()) {
				wikiTemp.create(null);
			}
			if (!wikiTemp.isOpen()) {
				wikiTemp.open(null);
			}
			IFolder wikiDir = wikiTemp.getFolder(WikiEditor.WIKI_TEMP_FOLDER);
			if (!wikiDir.exists()) {
				wikiDir.create(true, true, null);
			}
			FileInputStream fis = new FileInputStream(xfile);
			IFile wikiFile = wikiDir.getFile(xfile.getName());
			if (wikiFile.exists()) {
				wikiFile.delete(true, false, null);
			}
			wikiFile.create(fis, true, null);
			resource = wikiFile;
		}
		return resource;
	}

	private void openEclipseResource(String path) {
		try {
			PathWithLineNumber pathWithLineNumber = new PathWithLineNumber(path);
//			if (pathWithLineNumber.segmentCount() < 2) {
//				wikiPlugin().reportError(WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_OPEN_ECLIPSE_RESOURCE_TITLE), WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_OPEN_ECLIPSE_RESOURCE_TEXT));
//				return;
//			}
			IResource resource = Resources.findFileInProjectOrWorkspace(editor.getContext(), pathWithLineNumber.getPath().toString());
			if (resource != null) {
				IEditorPart part = openFile((IFile) resource);
				if (pathWithLineNumber.getLine() > 0 && part instanceof AbstractTextEditor) {
					gotoLine(pathWithLineNumber.getLine(), part);
				}
			} else {
				wikiPlugin().reportError(WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_OPEN_ECLIPSE_RESOURCE_TITLE), WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_OPEN_ECLIPSE_RESOURCE_TEXT) + " " + path);
			}
		} catch (Exception e) {
			wikiPlugin().logAndReport(WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_OPEN_WIKI_FILE_TITLE), "Unable to open " + path, e);
		}
	}

	private void gotoLine(int line, IEditorPart part) throws BadLocationException {
		AbstractTextEditor editor = (AbstractTextEditor) part;
		IDocumentProvider provider = editor.getDocumentProvider();
		IDocument document = provider.getDocument(editor.getEditorInput());
		if (line < 0) {
			line = 0;
		} else if (line > document.getNumberOfLines() - 1) {
			line = document.getNumberOfLines() - 1;
		}
		editor.selectAndReveal(document.getLineOffset(line), 0);
	}

	private void openType(JavaTypeTextRegion javaTypeTextRegion) {
		try {
			JavaUI.openInEditor(javaTypeTextRegion.getType());
		} catch (Exception e) {
			wikiPlugin().logAndReport(WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_PROGRAMMATIC_ERROR_TITLE), WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_PROGRAMMATIC_ERROR_TEXT), e);
		}
	}

	private IWorkbenchPage getActivePage() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}

	public void openJavaType(String typeName) {
		try {
			JavaUI.openInEditor(editor.getContext().getJavaContext().getJavaProject().findType(typeName));
		} catch (Exception e) {
			wikiPlugin().logAndReport(WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_PROGRAMMATIC_ERROR_TITLE), WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_PROGRAMMATIC_ERROR_TEXT), e);
		}
	}
}