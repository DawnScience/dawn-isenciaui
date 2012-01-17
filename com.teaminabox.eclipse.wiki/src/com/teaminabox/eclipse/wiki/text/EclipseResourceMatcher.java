package com.teaminabox.eclipse.wiki.text;

import java.io.File;

import org.eclipse.core.resources.IResource;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;
import com.teaminabox.eclipse.wiki.util.Resources;

/**
 * I match links to eclipse resources.
 * <P>
 * The resource must exist for there to be a match
 */
public class EclipseResourceMatcher extends ResourceMatcher {

	public EclipseResourceMatcher() {
		super(WikiConstants.ECLIPSE_PREFIX);
	}

	public TextRegion createTextRegion(String text, WikiDocumentContext context) {
		if (accepts(text, context)) {
			return new EclipseResourceTextRegion(new String(text.substring(0, matchLength(text, context))));
		}
		return null;
	}

	@Override
	protected File findResourceFromPath(WikiDocumentContext context, String section) {
		try {
			IResource file = Resources.findResourceInProjectOrWorkspace(context, section);
			if (file == null) return null;
			return file.getLocation().toFile();
		} catch (Exception ex) {
			return null;
		}
	}

}