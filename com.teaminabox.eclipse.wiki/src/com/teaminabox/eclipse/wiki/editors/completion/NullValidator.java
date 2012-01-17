package com.teaminabox.eclipse.wiki.editors.completion;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public class NullValidator implements IContextInformationValidator, IContextInformationPresenter {
	public static final IContextInformationValidator	INSTANCE	= new NullValidator();

	public boolean isContextInformationValid(int offset) {
		return false;
	}

	public void install(IContextInformation info, ITextViewer viewer, int offset) {
	}

	public boolean updatePresentation(int documentPosition, TextPresentation presentation) {
		return false;
	}
}