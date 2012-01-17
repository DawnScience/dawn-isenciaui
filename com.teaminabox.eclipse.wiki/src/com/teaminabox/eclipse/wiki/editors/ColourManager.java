package com.teaminabox.eclipse.wiki.editors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ColourManager {

	private Map<RGB, Color>	fColorTable	= new HashMap<RGB, Color>();
	private WikiEditor		editor;

	public ColourManager(WikiEditor editor) {
		this.editor = editor;
	}

	public WikiEditor getWikiEditor() {
		return editor;
	}

	public void dispose() {
		for (Color color : fColorTable.values()) {
			color.dispose();
		}
	}

	public Color getColor(RGB rgb) {
		Color color = fColorTable.get(rgb);
		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}
}