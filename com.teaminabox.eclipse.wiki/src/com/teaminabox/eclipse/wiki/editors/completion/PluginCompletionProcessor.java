package com.teaminabox.eclipse.wiki.editors.completion;

import static com.teaminabox.eclipse.wiki.WikiPlugin.wikiPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;
import com.teaminabox.eclipse.wiki.text.PluginPathFinder;
import com.teaminabox.eclipse.wiki.text.PluginProjectSupport;
import com.teaminabox.eclipse.wiki.text.PluginResourceTextRegion;

public class PluginCompletionProcessor {

	private static final String					WIKI_FOLDER	= "wiki";

	private final ResourceCompletionProcessor	resourceCompletionProcessor;

	public PluginCompletionProcessor(ResourceCompletionProcessor processor) {
		this.resourceCompletionProcessor = processor;
	}

	public List<ICompletionProposal> computePluginResourceCompletionProposals(final ITextViewer viewer, final int documentOffset) throws BadLocationException {
		IRegion region = viewer.getDocument().getLineInformationOfOffset(documentOffset);
		String line = viewer.getDocument().get(region.getOffset(), region.getLength());
		int pluginLinkIndex = line.indexOf(WikiConstants.PLUGIN_PREFIX);
		int cursorPositionInLine = documentOffset - region.getOffset();
		if (pluginLinkIndex < 0 || pluginLinkIndex > cursorPositionInLine) {
			return new ArrayList<ICompletionProposal>();
		}

		// Get the link around the cursor position
		int nextPluginLink = line.indexOf(WikiConstants.PLUGIN_PREFIX, pluginLinkIndex + 1);
		while (nextPluginLink > 0 && nextPluginLink < cursorPositionInLine) {
			pluginLinkIndex = nextPluginLink;
			nextPluginLink = line.indexOf(WikiConstants.PLUGIN_PREFIX, pluginLinkIndex + 1);
		}
		String linkText = new String(line.substring(pluginLinkIndex, cursorPositionInLine));

		PluginResourceTextRegion pluginResourceTextRegion = new PluginResourceTextRegion(linkText);
		pluginResourceTextRegion.setCursorPosition(cursorPositionInLine - pluginLinkIndex);
		return resourceCompletionProcessor.computeWikiProposals(documentOffset, pluginResourceTextRegion);
	}

	public List<ICompletionProposal> getPluginCompletions(String text, String location, int documentOffset, IPath path) {
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
			String rest = "";
			if (path == null) {
				int slashPos = location.lastIndexOf('/');
				String base = new String(location.substring(0, slashPos));
				path = PluginPathFinder.getPluginPath(base);
				rest = new String(location.substring(slashPos + 1));
			}
			String[] children = resourceCompletionProcessor.getChildren(path.toString() + rest);
			return resourceCompletionProcessor.buildResourceProposals(children, "", replacementOffset, lengthToBeReplaced);
		} catch (Exception e) {
			wikiPlugin().logAndReport("Completion Error", e.getLocalizedMessage(), e);
			return new ArrayList<ICompletionProposal>(1);
		}
	}

	ArrayList<ICompletionProposal> getPluginCompletions(String text, String location, int documentOffset) {
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

			String[] children = collectPlugIDs(location);
			return resourceCompletionProcessor.buildResourceProposals(children, "", replacementOffset, lengthToBeReplaced);
		} catch (Exception e) {
			wikiPlugin().logAndReport("Completion Error", e.getLocalizedMessage(), e);
			return new ArrayList<ICompletionProposal>(1);
		}
	}

	/*
	 * collect plugin-ID's of those plugin's, that have a folder wiki. the plugin location is taken either from the
	 * workspace or (if the plugin does not exists in the workspace) from the plugins folder of Eclipse.
	 */
	private String[] collectPlugIDs(String path) {
		if (path == null) {
			path = "";
		}
		Set<String> plugIds = gatherPluginIds(path);
		SortedMap<String, String> selectedIDs = new TreeMap<String, String>();

		for (String currPluginID : plugIds) {
			addWikiFromPlugin(path, currPluginID, selectedIDs);
		}
		return selectedIDs.values().toArray(new String[selectedIDs.size()]);
	}

	private void addWikiFromPlugin(String path, String currPluginID, SortedMap<String, String> selectedIDs) {
		if (path.length() == 0 || currPluginID.startsWith(path)) {
			IPath plugDirPath = null;
			IProject proj = PluginProjectSupport.locateProjectInWorkspace(currPluginID);
			if (proj != null) {
				plugDirPath = proj.getRawLocation();
			} else {
				plugDirPath = PluginPathFinder.getPluginPath(currPluginID);
			}
			if (plugDirPath != null) {
				File plugDir = plugDirPath.toFile();
				if (plugDir != null && plugDir.exists() && new File(plugDir, PluginCompletionProcessor.WIKI_FOLDER).exists()) {
					selectedIDs.put(currPluginID, currPluginID);
				}
			}
		}
	}

	private Set<String> gatherPluginIds(String path) {
		Set<String> plugIds = new HashSet<String>();
		if (path.length() == 0) {
			getPluginsFromWorkspace(plugIds);
		}
		IExtensionRegistry extensionRegistry = org.eclipse.core.runtime.Platform.getExtensionRegistry();
		for (int i = 0; i < extensionRegistry.getNamespaces().length; i++) {
			String id = extensionRegistry.getNamespaces()[i];
			plugIds.add(id);
		}
		return plugIds;
	}

	private void getPluginsFromWorkspace(Set<String> plugIds) {
		String[] projects = resourceCompletionProcessor.getProjectList("");
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (String projName : projects) {
			IProject proj = root.getProject(projName);
			if (proj.getFile("plugin.xml").exists() || proj.getFile("fragment.xml").exists()) {
				String id = PluginProjectSupport.extractPlugID(proj);
				if (id != null) {
					plugIds.add(id);
				}
			}
		}
	}

	public boolean isPluginWikispaceLink(String wikiSpace, WikiDocumentContext context) {
		return context.getWikiSpace().containsKey(wikiSpace) && context.getWikiSpaceLink(wikiSpace).startsWith(WikiConstants.PLUGIN_PREFIX);
	}
}
