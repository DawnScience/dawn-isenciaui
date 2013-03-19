package com.isencia.passerelle.workbench.model.editor.ui.figure;

import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Color;

public class PortFigure extends RectangleFigure {
	private Color fillColor;
	private String name;
	protected int width;
	protected int height;
	public PortFigure(String name) {
		this(name,ActorFigure.ANCHOR_WIDTH,ActorFigure.ANCHOR_HEIGTH);
	}
	public PortFigure(String name,int width,int height) {
		super();
		this.width = width;
		this.height = height;
		setOpaque(false);
		setName(name);
		setSize(width,height);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.draw2d.IFigure#getPreferredSize(int, int)
	 */
	public Dimension getPreferredSize(int wHint, int hHint) {
		return new Dimension(width, height);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

}
