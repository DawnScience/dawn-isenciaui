package com.teaminabox.eclipse.wiki.editors;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.teaminabox.eclipse.wiki.WikiConstants;

/**
 * Manages paths of the form a/b/c:123
 */
public class PathWithLineNumber {
	private int		line;
	private IPath	relPath;

	public PathWithLineNumber(String path) {
		int lineIndex = path.indexOf(WikiConstants.LINE_NUMBER_SEPARATOR);
		if (lineIndex > 0) {
			if (lineIndex < path.length() - 1) {
				line = Integer.parseInt(path.substring(lineIndex + 1)) - 1;
			}
			path = new String(path.substring(0, lineIndex));
		}
		relPath = new Path(path);
	}

	public int getLine() {
		return line;
	}

	public IPath getPath() {
		return relPath;
	}

	public int segmentCount() {
		return relPath.segmentCount();
	}

}