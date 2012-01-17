package com.teaminabox.eclipse.wiki.text;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.text.rules.IToken;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.editors.ColourManager;

public class JavaTypeTextRegion extends TextRegion {

	private IType	javaType;

	public JavaTypeTextRegion(String text, IType javaType) {
		super(text);
		this.javaType = javaType;
	}

	public <T> T accept(TextRegionVisitor<T> textRegionVisitor) {
		return textRegionVisitor.visit(this);
	}

	public boolean isLink() {
		return true;
	}

	public IType getType() {
		return javaType;
	}

	public IToken getToken(ColourManager colourManager) {
		return getToken(WikiConstants.JAVA_TYPE, colourManager);
	}

}