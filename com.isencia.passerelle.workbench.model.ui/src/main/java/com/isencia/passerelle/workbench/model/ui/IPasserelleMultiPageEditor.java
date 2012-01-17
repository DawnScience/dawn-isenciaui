package com.isencia.passerelle.workbench.model.ui;

import ptolemy.actor.CompositeActor;

public interface IPasserelleMultiPageEditor {
	CompositeActor getSelectedContainer();
	CompositeActor getModel();
	IPasserelleEditor getSelectedPage();
	void selectPage(CompositeActor actor);
	void setPasserelleEditorActive();
	void removePage(int pageIndex);
}
