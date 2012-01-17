package com.teaminabox.eclipse.wiki.editors.completion;

import static com.teaminabox.eclipse.wiki.text.TextRegionBuilder.getTextRegionAtCursor;
import static com.teaminabox.eclipse.wiki.WikiPlugin.wikiPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;
import com.teaminabox.eclipse.wiki.editors.WikiEditor;
import com.teaminabox.eclipse.wiki.text.BasicTextRegion;
import com.teaminabox.eclipse.wiki.text.EclipseResourceTextRegion;
import com.teaminabox.eclipse.wiki.text.GenericTextRegionVisitor;
import com.teaminabox.eclipse.wiki.text.PluginPathFinder;
import com.teaminabox.eclipse.wiki.text.PluginResourceTextRegion;
import com.teaminabox.eclipse.wiki.text.TextRegion;
import com.teaminabox.eclipse.wiki.text.UndefinedTextRegion;
import com.teaminabox.eclipse.wiki.util.Resources;

public class ResourceCompletionProcessor {

	private final class WikiProposalsVisitor extends GenericTextRegionVisitor<List<ICompletionProposal>> {
		private final TextRegion	textRegion;
		private final int			documentOffset;

		private WikiProposalsVisitor(TextRegion textRegion, int documentOffset) {
			super(new ArrayList<ICompletionProposal>());
			this.textRegion = textRegion;
			this.documentOffset = documentOffset;
		}

		@Override
		public List<ICompletionProposal> visit(UndefinedTextRegion undefinedTextRegion) {
			return getPotentialWikiNameCompletion(textRegion.getTextToCursor(), documentOffset);
		}

		@Override
		public List<ICompletionProposal> visit(BasicTextRegion basicTextRegion) {
			return getPotentialWikiNameCompletion(textRegion.getTextToCursor(), documentOffset);
		}

		@Override
		public List<ICompletionProposal> visit(EclipseResourceTextRegion eclipseResourceTextRegion) {
			int colon = textRegion.getTextToCursor().indexOf(WikiConstants.WIKISPACE_DELIMITER) + 1;
			String location = new String(textRegion.getTextToCursor().substring(colon));
			return getResourceCompletions(textRegion.getTextToCursor(), location, documentOffset);
		}

		@Override
		public List<ICompletionProposal> visit(PluginResourceTextRegion pluginResourceTextRegion) {
			int colon = textRegion.getTextToCursor().indexOf(WikiConstants.WIKISPACE_DELIMITER) + 1;
			String location = new String(textRegion.getTextToCursor().substring(colon));
			IPath path = PluginPathFinder.getPluginPath(location);
			int slashPos = location.indexOf('/');
			if (slashPos < 0 || slashPos == location.lastIndexOf('/') && slashPos - 1 == location.length()) {
				return pluginCompletionProcessor.getPluginCompletions(textRegion.getTextToCursor(), location, documentOffset);
			}
			return pluginCompletionProcessor.getPluginCompletions(textRegion.getTextToCursor(), location, documentOffset, path);
		}
	}

	private final WikiEditor				wikiEditor;

	private ITextViewer						viewer;
	private int								documentOffset;
	private ArrayList<ICompletionProposal>	proposals;
	private PluginCompletionProcessor		pluginCompletionProcessor;

	public ResourceCompletionProcessor(WikiEditor wikiEditor) {
		this.wikiEditor = wikiEditor;
		pluginCompletionProcessor = new PluginCompletionProcessor(this);
	}

	/**
	 * @return true if attempted to find wiki word completions and no workspace completions were found
	 */
	public boolean addCompletions(ITextViewer viewer, int documentOffset, ArrayList<ICompletionProposal> proposals) throws BadLocationException {
		this.viewer = viewer;
		this.documentOffset = documentOffset;
		this.proposals = proposals;
		if (isCompletionPossible()) {
			return computeWorkspaceCompletions(getTextRegionAtCursor(wikiEditor, viewer.getDocument(), documentOffset));
		}
		return true;
	}
	
	private boolean isCompletionPossible() throws BadLocationException {
		return viewer.getDocument().getLength() > 0 && getCharacterAtDocumentOffset(viewer, documentOffset) != '.';
	}

	/**
	 * @return true if attempted to find wiki word completions and no workspace completions were found
	 */
	private boolean computeWorkspaceCompletions(TextRegion textRegion) throws BadLocationException {
		proposals.addAll(computeWorkspaceCompletions(viewer, documentOffset, textRegion));
		if (proposals.size() == 0) {
			proposals.addAll(computeWikiProposals(documentOffset, textRegion));
			return true;
		}
		return false;
	}

	private List<ICompletionProposal> computeWorkspaceCompletions(final ITextViewer viewer, int documentOffset, TextRegion textRegion) throws BadLocationException {
		List<ICompletionProposal> wikiProposals = new ArrayList<ICompletionProposal>();
		if (textRegion.getText().startsWith(WikiConstants.ECLIPSE_PREFIX)) {
			wikiProposals = computeEclipseResourceCompletionProposals(viewer, documentOffset);
		} else if (textRegion.getText().startsWith(WikiConstants.PLUGIN_PREFIX)) {
			wikiProposals = pluginCompletionProcessor.computePluginResourceCompletionProposals(viewer, documentOffset);
		}
		return wikiProposals;
	}

	/**
	 * Get the character position of the document offset (which is the cursor position). The documentOffset when the
	 * cursor is after the last character then it is == viewer.getDocument().getLength(). In which case documentOffset -
	 * 1 is returned.
	 *
	 * @throws BadLocationException
	 */
	private int getCharacterAtDocumentOffset(final ITextViewer viewer, int documentOffset) throws BadLocationException {
		if (documentOffset == viewer.getDocument().getLength() && viewer.getDocument().getLength() > 0) {
			return viewer.getDocument().getChar(documentOffset - 1);
		}
		return viewer.getDocument().getChar(documentOffset);
	}

	private List<ICompletionProposal> computeEclipseResourceCompletionProposals(final ITextViewer viewer, final int documentOffset) throws BadLocationException {
		IRegion region = viewer.getDocument().getLineInformationOfOffset(documentOffset);
		String line = viewer.getDocument().get(region.getOffset(), region.getLength());
		int eclipseLinkIndex = line.indexOf(WikiConstants.ECLIPSE_PREFIX);
		int cursorPositionInLine = documentOffset - region.getOffset();
		if (eclipseLinkIndex < 0 || eclipseLinkIndex > cursorPositionInLine) {
			return new ArrayList<ICompletionProposal>();
		}

		// Get the link around the cursor position
		int nextEclipseLink = line.indexOf(WikiConstants.ECLIPSE_PREFIX, eclipseLinkIndex + 1);
		while (nextEclipseLink > 0 && nextEclipseLink < cursorPositionInLine) {
			eclipseLinkIndex = nextEclipseLink;
			nextEclipseLink = line.indexOf(WikiConstants.ECLIPSE_PREFIX, eclipseLinkIndex + 1);
		}
		String linkText = new String(line.substring(eclipseLinkIndex, cursorPositionInLine));

		EclipseResourceTextRegion eclipseResourceTextRegion = new EclipseResourceTextRegion(linkText);
		eclipseResourceTextRegion.setCursorPosition(cursorPositionInLine - eclipseLinkIndex);
		return computeWikiProposals(documentOffset, eclipseResourceTextRegion);
	}

	List<ICompletionProposal> computeWikiProposals(final int documentOffset, final TextRegion textRegion) {
		List<ICompletionProposal> list = textRegion.accept(new WikiProposalsVisitor(textRegion, documentOffset));

		addWikiSpaceCompletions(textRegion.getTextToCursor(), list, documentOffset);
		return list;
	}

	private void addWikiSpaceCompletions(String text, List<ICompletionProposal> list, int documentOffset) {
		addLocalWikiSpaceCompletions(text, list, documentOffset);
		String word = text.toLowerCase().trim();
		for (String name : wikiEditor.getContext().getWikiSpace().keySet()) {
			if (name.toLowerCase().startsWith(word)) {
				String completion = name + WikiConstants.WIKISPACE_DELIMITER;
				ICompletionProposal proposal = new CompletionProposal(completion, documentOffset - word.length(), word.length(), completion.length(), wikiPlugin().getImageRegistry().get(WikiConstants.WIKI_SPACE_ICON), null, null, null);
				list.add(proposal);
			}
		}
	}

	/**
	 * Get WikiSpace completions for local resources
	 *
	 * @param word
	 *            text to the cursor which is at the documentOffset
	 * @param list
	 *            the list to add completions too
	 * @param documentOffset
	 *            position of cursor and where <code>word</code> ends
	 */
	private void addLocalWikiSpaceCompletions(String word, List<ICompletionProposal> list, int documentOffset) {
		if (word.indexOf(WikiConstants.WIKISPACE_DELIMITER) < 0) {
			return;
		}
		String wikiSpace = new String(word.substring(0, word.indexOf(WikiConstants.WIKISPACE_DELIMITER)));
		WikiDocumentContext context = wikiEditor.getContext();
		if (isEclipseWikispaceLink(wikiSpace, context)) {
			String locationPrefix = new String(context.getWikiSpaceLink(wikiSpace).substring(WikiConstants.ECLIPSE_PREFIX.length()));
			// + 1 below to get the WIKISPACE_DELIMITER (:)
			String location = new String(locationPrefix + word.substring(wikiSpace.length() + 1));
			list.addAll(getResourceCompletions(word, location, documentOffset));
		} else if (pluginCompletionProcessor.isPluginWikispaceLink(wikiSpace, context)) {
			String locationPrefix = new String(context.getWikiSpaceLink(wikiSpace).substring(WikiConstants.PLUGIN_PREFIX.length()));
			// + 1 below to get the WIKISPACE_DELIMITER (:)
			String location = new String(locationPrefix + word.substring(wikiSpace.length() + 1));
			list.addAll(getResourceCompletions(word, location, documentOffset));
		}
	}

	private boolean isEclipseWikispaceLink(String wikiSpace, WikiDocumentContext context) {
		return context.getWikiSpace().containsKey(wikiSpace) && context.getWikiSpaceLink(wikiSpace).startsWith(WikiConstants.ECLIPSE_PREFIX);
	}

	private ArrayList<ICompletionProposal> getResourceCompletions(String text, String location, int documentOffset) {
		try {
			int lengthToBeReplaced = 0;
			int replacementOffset = documentOffset;
			int colon = text.indexOf(WikiConstants.WIKISPACE_DELIMITER);
			int lastSlash = text.lastIndexOf(WikiConstants.PATH_SEPARATOR);
			int lastSegment = colon > lastSlash ? colon : lastSlash;
			if (lastSegment != -1) {
				lengthToBeReplaced = text.length() - lastSegment - 1;
				replacementOffset = documentOffset - lengthToBeReplaced;
			}

			String[] children = getChildren(location);
			return buildResourceProposals(children, "", replacementOffset, lengthToBeReplaced);
		} catch (Exception e) {
			wikiPlugin().logAndReport("Completion Error", e.getLocalizedMessage(), e);
			return new ArrayList<ICompletionProposal>();
		}
	}

	/**
	 * @param replacements
	 *            the list of completions
	 * @param prefix
	 *            text to prefix each replacement with
	 * @param replacementOffset
	 *            see CompletionProposal#CompletionProposal(String, int, int, int)
	 * @param replacementLength
	 *            see CompletionProposal#CompletionProposal(String, int, int, int)
	 * @return ICompletionProposal[]
	 * @see CompletionProposal#CompletionProposal(String, int, int, int)
	 */
	ArrayList<ICompletionProposal> buildResourceProposals(String[] replacements, String prefix, int replacementOffset, int replacementLength) {
		ArrayList<ICompletionProposal> list = new ArrayList<ICompletionProposal>();
		for (String element : replacements) {
			String child = prefix + element;
			list.add(new CompletionProposal(child, replacementOffset, replacementLength, child.length(), wikiPlugin().getImageRegistry().get(WikiConstants.WIKI_RESOURCE_ICON), null, null, null));
		}
		return list;
	}

	String[] getChildren(String path) throws CoreException {
		if (path.length() == 0 || path.equals(WikiConstants.PATH_SEPARATOR) || path.indexOf(WikiConstants.PATH_SEPARATOR, 1) == -1) {
			return getProjectListWithLocalChildren(path);
		}
		// path must be at least one segment at this point (project name)
		IPath relPath = new Path(path);
		if (relPath.hasTrailingSeparator()) {
			return getChildren(relPath, "");
		}

		String lastBit = relPath.lastSegment();
		relPath = relPath.removeLastSegments(1);
		return getChildren(relPath, lastBit);
	}

	private String[] getProjectListWithLocalChildren(String path) throws CoreException {
		ArrayList<String> list = new ArrayList<String>(Arrays.asList(getProjectList(path)));
		IResource[] members = wikiEditor.getContext().getProject().members();
		for (IResource iResource : members) {
			list.add(iResource.getName());
		}
		return list.toArray(new String[list.size()]);
	}

	private String[] getChildren(IPath parent, String resourcePrefix) throws CoreException {
		IResource resource = Resources.findResourceInProjectOrWorkspace(wikiEditor.getContext(), parent.toString());
		if (resource == null) {
			File xfile = parent.toFile();
			if (xfile.exists()) {
				return getChildren(resourcePrefix, xfile);
			}
		} else if (resource.exists() && (resource.getType() == IResource.FOLDER || resource.getType() == IResource.PROJECT)) {
			return getChildren(resourcePrefix, resource);
		}
		return new String[0];
	}

	private String[] getChildren(String resourcePrefix, File parent) {
		SortedMap<String, String> sort = new TreeMap<String, String>();
		File[] files = parent.listFiles();
		for (File file : files) {
			if (file.getName().startsWith(resourcePrefix)) {
				sort.put(file.getName(), file.getName());
			}
		}
		Collection<String> values = sort.values();
		return values.toArray(new String[values.size()]);
	}

	private String[] getChildren(String resourcePrefix, IResource resource) throws CoreException {
		IContainer container = (IContainer) resource;
		IResource[] children = container.members();
		ArrayList<String> childNames = new ArrayList<String>();
		for (IResource element : children) {
			if (element.getName().startsWith(resourcePrefix)) {
				childNames.add(element.getName());
			}
		}
		return childNames.toArray(new String[childNames.size()]);
	}

	String[] getProjectList(String text) {
		String prefix = text;
		if (prefix != null && prefix.startsWith(WikiConstants.PATH_SEPARATOR)) {
			prefix = new String(prefix.substring(1));
		}
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		ArrayList<String> names = new ArrayList<String>();
		for (IProject proj : projects) {
			if (prefix == null || prefix.length() == 0 || proj.getName().startsWith(prefix)) {
				names.add(proj.getName());
			}
		}
		return names.toArray(new String[names.size()]);
	}

	private ArrayList<ICompletionProposal> getPotentialWikiNameCompletion(String text, int documentOffset) {
		String word = text.trim();
		try {
			if (word.length() > 0 && !Character.isUpperCase(word.charAt(0))) {
				return new ArrayList<ICompletionProposal>();
			}
			IResource[] resources = wikiEditor.getContext().getWorkingLocation().members(IResource.FILE);
			ArrayList<ICompletionProposal> list = new ArrayList<ICompletionProposal>();
			for (IResource element : resources) {
				String name = element.getName();
				if (isWikiFile(name) && name.startsWith(word)) {
					String wikiName = getWikiWord(name);
					ICompletionProposal proposal = new CompletionProposal(wikiName, documentOffset - word.length(), word.length(), wikiName.length(), wikiPlugin().getImageRegistry().get(WikiConstants.WIKI_ICON), null, null, null);
					list.add(proposal);
				}
			}
			return list;
		} catch (CoreException e) {
			wikiPlugin().logAndReport("Completion Error", e.getLocalizedMessage(), e);
			return new ArrayList<ICompletionProposal>();
		}
	}

	private String getWikiWord(String fileName) {
		return new String(fileName.substring(0, fileName.indexOf(WikiConstants.WIKI_FILE_EXTENSION)));
	}

	private boolean isWikiFile(String name) {
		return name.endsWith(WikiConstants.WIKI_FILE_EXTENSION);
	}

}
