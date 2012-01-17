package com.teaminabox.eclipse.wiki.text;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class PluginPathFinder {
	public static IPath getPluginPath(String text) {
		if (text == null || text.length() == 0) {
			return null;
		}
		IPath relPath = new Path(text);
		IProject wsProj = PluginProjectSupport.locateProjectInWorkspace(relPath.segment(0));
		if (wsProj == null) {
			Bundle bundle = null;
			try {
				bundle = Platform.getBundle(relPath.segment(0));
			} catch (Exception ex) {
			} finally {
				relPath = relPath.removeFirstSegments(1);
			}
			if (bundle != null) {
				try {
					URL entry = bundle.getEntry(relPath.toString());
					if (entry != null) {
						URL url = FileLocator.toFileURL(entry);
						if (url != null) {
							return new Path(new String(url.getFile().substring(1)));
						}
					}
				} catch (IOException e) {
				}
			}
			return null;
		}
		relPath = relPath.removeFirstSegments(1);
		return PluginPathFinder.getPath(relPath, wsProj);
	}

	private static IPath getPath(IPath relPath, IProject wsProj) {
		IResource res = relPath.segmentCount() > 0 ? wsProj.findMember(relPath) : wsProj;
		if (res instanceof IProject) {
			return res.getLocation().addTrailingSeparator();
		} else if (res instanceof IFolder) {
			return res.getLocation().addTrailingSeparator();
		} else if (res != null) {
			return res.getLocation();
		} else {
			return null;
		}
	}
}
