package com.teaminabox.eclipse.wiki.util;

import static com.teaminabox.eclipse.wiki.WikiPlugin.wikiPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;

public class Resources {

	public static String getContentsRelativeToPlugin(IPath path) throws IOException {
		return Resources.getContents(FileLocator.openStream(wikiPlugin().getBundle(), path, false));
	}

	public static String getContents(InputStream stream) throws IOException {
		InputStreamReader inputStreamReader = new InputStreamReader(stream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		StringBuffer buffer = new StringBuffer(1000);
		int c;
		while ((c = bufferedReader.read()) != -1) {
			buffer.append((char) c);
		}
		return buffer.toString();
	}

	public static String getContents(IFile file) throws IOException, CoreException {
		return Resources.getContents(file.getContents());
	}

	public static List<String> readLines(IFile file) throws IOException, CoreException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(file.getContents()));
		String line;
		ArrayList<String> lines = new ArrayList<String>();
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}
		return lines;
	}

	public static boolean exists(IResource resource) {
		return resource != null && resource.exists();
	}

	public static boolean existsAsFile(IResource resource) {
		return Resources.exists(resource) && resource.getType() == IResource.FILE;
	}

	private static IResource findResourceInWorkspace(String workspaceRelativePath) {
		return ResourcesPlugin.getWorkspace().getRoot().findMember(workspaceRelativePath);
	}
	
	public static boolean isWikiFile(IResource resource) {
		return Resources.exists(resource) && resource.getFileExtension() != null && WikiConstants.WIKI_FILE_EXTENSION.endsWith(resource.getFileExtension());
	}

	public static boolean isWikiFile(IFile file) {
		return file.getName().endsWith(WikiConstants.WIKI_FILE_EXTENSION);
	}

	public static IResource findResourceInProjectOrWorkspace(WikiDocumentContext context, String path) {
		IResource resource = findResourceInWorkspace(path);
		if (resource == null) resource = context.getProject().findMember(path);
		if (resource == null) return null;
		return resource;
	}

	public static IFile findFileInProjectOrWorkspace(WikiDocumentContext context, String path) {
		IResource file = findResourceInProjectOrWorkspace(context, path);
		if (file == null) return null;
		return fileIfExists(file);
	}

	private static IFile fileIfExists(IResource resource) {
		if (Resources.existsAsFile(resource)) {
			return (IFile) resource;
		}
		return null;
	}

}
