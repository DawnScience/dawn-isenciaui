package com.isencia.passerelle.workbench.model.editor.ui.editor;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.gef.internal.ui.palette.editparts.DrawerEditPart;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isencia.passerelle.model.Flow;
import com.isencia.passerelle.workbench.model.editor.ui.palette.PaletteBuilder;
import com.isencia.passerelle.workbench.model.editor.ui.palette.PaletteItemDefinition;
import com.isencia.passerelle.workbench.model.editor.ui.palette.PaletteItemFactory;
import com.isencia.passerelle.workbench.model.editor.ui.palette.SubModelPaletteItemDefinition;

public class PaletteMouseListener implements MouseMotionListener {
	
	private static Logger logger = LoggerFactory.getLogger(PaletteMouseListener.class);
	
	private DrawerEditPart drawerFigure;
	private PaletteViewer paletteViewer;

	public PaletteMouseListener(DrawerEditPart drawerFigure,
			PaletteViewer paletteViewer) {
		super();
		this.drawerFigure = drawerFigure;
		this.paletteViewer = paletteViewer;
	}

	@Override
	public void mouseDragged(MouseEvent me) {

	}

	@Override
	public void mouseEntered(MouseEvent me) {
		try {
			addFavorite();
		} catch (Exception e) {
			logger.error("Cannot add favourite!", e);
		}

	}

	private void addFavorite() throws Exception {
		
		PaletteItemFactory paletteItemFactory = PaletteItemFactory.getInstance();
		CreationFactory config = paletteItemFactory.getSelectedItem();
		if (config != null) {
			Class  type = (Class) config.getObjectType();
			drawerFigure.getDrawer().getLabel();
			if (type.equals(Flow.class)) {
				SubModelPaletteItemDefinition item = (SubModelPaletteItemDefinition) config.getNewObject();			
				paletteItemFactory.addFavorite(item.getName(),
												(PaletteContainer) PaletteBuilder
														.getFavoriteGroup(drawerFigure.getDrawer()
																.getLabel()));
				

			} else {
				paletteItemFactory.addFavorite(type.getName(),
						(PaletteContainer) PaletteBuilder
								.getFavoriteGroup(drawerFigure.getDrawer()
										.getLabel()));
			}
			PaletteBuilder.synchFavorites(paletteViewer);
			paletteItemFactory.setSelectedItem(null);
		}
	}

	@Override
	public void mouseExited(MouseEvent me) {

	}

	@Override
	public void mouseHover(MouseEvent me) {

	}

	@Override
	public void mouseMoved(MouseEvent me) {
	}

}
