package com.teaminabox.eclipse.wiki.renderer;

import org.eclipse.jface.text.BadLocationException;

import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;
import com.teaminabox.eclipse.wiki.text.TextRegionMatcher;

public interface ContentRenderer {

	String render(WikiDocumentContext context, LinkMaker linkMaker, boolean isEmbedded);

	void forEachHeader(WikiDocumentContext context, StructureClosure closure) throws BadLocationException;

	/**
	 * The ordered set of matchers for a RuleBasedScanner ({@link com.teaminabox.eclipse.wiki.editors.WikiScanner WikiScanner}).
	 * The set of Matchers used in a RuleBasedScanner does not need to include simple character matchers - ie those not
	 * used for coloring the editor. By providing a reduced set of matchers for the scanner performance is improved
	 * considerably. <p/> The matchers are <i>Ordered</i> because the first matching matcher is the one used.
	 */
	TextRegionMatcher[] getScannerMatchers();

	/**
	 * The ordered set of matches for rendering. 'Ordered' because the first matcher accepting text is the one used.
	 */
	TextRegionMatcher[] getRendererMatchers();

	String getName();

}