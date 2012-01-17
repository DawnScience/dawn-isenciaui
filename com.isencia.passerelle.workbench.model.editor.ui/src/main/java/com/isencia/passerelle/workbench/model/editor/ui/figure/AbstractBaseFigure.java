package com.isencia.passerelle.workbench.model.editor.ui.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.graphics.Color;

import com.isencia.passerelle.workbench.model.editor.ui.INameable;
import com.isencia.passerelle.workbench.model.editor.ui.palette.PaletteItemFactory;

public class AbstractBaseFigure extends Figure implements INameable {
	public final static int DEFAULT_WIDTH = 60;
	public final static int MIN_HEIGHT = 60;
	
	public final static Color DEFAULT_BACKGROUND_COLOR = ColorConstants.lightGray;

	public final static Color DEFAULT_FOREGROUND_COLOR = ColorConstants.gray;
	
	public final static Color LABEL_BACKGROUND_COLOR = new Color(null, 0, 0, 204);
	
	protected Class type;
	protected Label nameLabel = new Label();
	public Color getDefaultColor(){
		return DEFAULT_BACKGROUND_COLOR;
	}
	public Color getColor() {
		Color color = PaletteItemFactory.getInstance().getColor(type);
		if (color != null){
			return color;
		}
		return getDefaultColor();
	}
	public AbstractBaseFigure(String name) {
		this(name, true,null);

	}

	public AbstractBaseFigure(String name, Class type) {
		this(name, true, type);
		
	}

	public AbstractBaseFigure(String name, boolean withLabel, Class type) {
		ToolbarLayout layout = new ToolbarLayout();
		layout.setVertical(true);
		layout.setSpacing(2);
		setLayoutManager(layout);
		if (withLabel) {
			nameLabel.setText(getNotTooLongName(name));
			nameLabel.setOpaque(true);
			add(nameLabel);
		}
		setToolTip(new Label(name));
		this.type = type;
		if (type != null && PaletteItemFactory.getInstance().getType(type) != null){
			setToolTip(new Label(PaletteItemFactory.getInstance().getType(type)));
		}
		setOpaque(false);

	}
	private String getNotTooLongName(String name) {
		// TODO Add system property, for now always return full name
		if (true) return name;
		
		
		if (name.length()>16) {
			return name.substring(0,3)+"..."+name.substring(name.length()-10);
		}
		return name;
	}
	public String getName() {
		return this.nameLabel.getText();
	}

	public void setName(String name) {
		this.nameLabel.setText(name);
	}

}
