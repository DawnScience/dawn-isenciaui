package com.teaminabox.eclipse.wiki.renderer;

import com.teaminabox.eclipse.wiki.text.EclipseResourceMatcher;
import com.teaminabox.eclipse.wiki.text.EmbeddedTextRegionMatcher;
import com.teaminabox.eclipse.wiki.text.IgnoredTextRegionMatcher;
import com.teaminabox.eclipse.wiki.text.JavaTypeMatcher;
import com.teaminabox.eclipse.wiki.text.LetterOrDigitMatcher;
import com.teaminabox.eclipse.wiki.text.NonLetterOrDigitMatcher;
import com.teaminabox.eclipse.wiki.text.PluginResourceMatcher;
import com.teaminabox.eclipse.wiki.text.TextRegionMatcher;
import com.teaminabox.eclipse.wiki.text.UrlMatcher;
import com.teaminabox.eclipse.wiki.text.WikiSpaceMatcher;
import com.teaminabox.eclipse.wiki.text.WikiWordMatcher;

public class WardsBrowserContentRenderer extends AbstractContentRenderer {

	private static final String			WIKI_WORD_PATTERN			= "([A-Z][a-z]+){2,}[0-9]*";

	private static TextRegionMatcher[]	DEFAULT_RENDERER_MATCHERS	= new TextRegionMatcher[] { new IgnoredTextRegionMatcher(), new UrlMatcher(), new EmbeddedTextRegionMatcher(new EclipseResourceMatcher()), new EclipseResourceMatcher(), new PluginResourceMatcher(), new WikiSpaceMatcher(), new EmbeddedTextRegionMatcher(new JavaTypeMatcher()), new JavaTypeMatcher(), new EmbeddedTextRegionMatcher(new WikiWordMatcher(WardsBrowserContentRenderer.WIKI_WORD_PATTERN)), new WikiWordMatcher(WardsBrowserContentRenderer.WIKI_WORD_PATTERN), new NonLetterOrDigitMatcher(), new LetterOrDigitMatcher() };
	private static TextRegionMatcher[]	DEFAULT_SCANNER_MATCHERS	= new TextRegionMatcher[] { new IgnoredTextRegionMatcher(), new UrlMatcher(), new EmbeddedTextRegionMatcher(new EclipseResourceMatcher()), new EclipseResourceMatcher(), new PluginResourceMatcher(), new WikiSpaceMatcher(), new EmbeddedTextRegionMatcher(new JavaTypeMatcher()), new JavaTypeMatcher(), new EmbeddedTextRegionMatcher(new WikiWordMatcher(WardsBrowserContentRenderer.WIKI_WORD_PATTERN)), new WikiWordMatcher(WardsBrowserContentRenderer.WIKI_WORD_PATTERN) };

	private static final String			BULLET_MARKUP				= "*";
	private static final String			QUOTE_MARKUP_REGEX			= "^\t :\t.*";
	private static final String			HEADER_MARKUP_REGEX			= "^'''.+'''$";
	private static final String			HEADER_MARKUP				= "'''";
	private static final String			BOLD_MARKUP					= "__";
	private static final String			EMPHASIS_MARKUP				= "'''''";
	private static final String			ITALIC_MARKUP				= "''";
	private static final String			LIST_MARKUP_REGEX			= "^[\\*]+.*";
	private static final String			PLURAL						= "''''''s";

	public TextRegionMatcher[] getRendererMatchers() {
		return WardsBrowserContentRenderer.DEFAULT_RENDERER_MATCHERS;
	}

	public TextRegionMatcher[] getScannerMatchers() {
		return WardsBrowserContentRenderer.DEFAULT_SCANNER_MATCHERS;
	}

	/**
	 * There aren't true headers in Ward's Wiki, just text in a <code>strong</code> element.
	 */
	protected void appendHeader(String line) {
		append("<p><strong>");
		append(encode(getHeaderText(line)));
		appendln("</strong></p>");
	}

	protected String getHeaderText(String line) {
		return line.replaceAll(WardsBrowserContentRenderer.HEADER_MARKUP, "");
	}

	protected int getListDepth(String line) {
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) != WardsBrowserContentRenderer.BULLET_MARKUP.charAt(0)) {
				return i;
			}
		}
		return line.length();
	}

	protected boolean isHeader(String line) {
		return !line.startsWith(WardsBrowserContentRenderer.EMPHASIS_MARKUP) && line.trim().matches(WardsBrowserContentRenderer.HEADER_MARKUP_REGEX);
	}

	protected boolean isList(String line) {
		return line.matches(WardsBrowserContentRenderer.LIST_MARKUP_REGEX);
	}

	protected boolean process(String line) {
		if (line.trim().matches("^-----*$")) {
			appendHR();
			return true;
		} else if (line.startsWith(" ")) {
			appendMonoSpacedLine(line);
			return true;
		} else if (line.matches(WardsBrowserContentRenderer.QUOTE_MARKUP_REGEX)) {
			appendQuote(line);
			return true;
		}
		return false;
	}

	private void appendQuote(String line) {
		append("<p class=\"").append(AbstractContentRenderer.CLASS_QUOTE).append("\">");
		parseAndAppend(processTags(encode(line.substring(4))));
		append("</p>");
	}

	private void appendMonoSpacedLine(String line) {
		append("<pre class=\"").append(AbstractContentRenderer.CLASS_MONO_SPACE).append("\">");
		appendNewLine();
		append(encode(line));
		while (hasNextLine() && peekNextLine().startsWith(" ")) {
			appendNewLine();
			append(encode(getNextLine()));
		}
		append("</pre>");
	}

	protected String processTags(String line) {
		line = line.replaceAll(WardsBrowserContentRenderer.PLURAL, "'s");
		line = replacePair(line, WardsBrowserContentRenderer.EMPHASIS_MARKUP, "<b><i>", "</i></b>");
		line = replacePair(line, WardsBrowserContentRenderer.HEADER_MARKUP, "<b>", "</b>");
		line = replacePair(line, WardsBrowserContentRenderer.BOLD_MARKUP, "<b>", "</b>");
		line = replacePair(line, WardsBrowserContentRenderer.ITALIC_MARKUP, "<i>", "</i>");
		return line;
	}

	protected String getListText(String line) {
		return new String(line.substring(getListDepth(line)).trim());
	}

	/**
	 * Ward's renderer does not support ordered lists.
	 * 
	 * @return <code>FALSE</code>
	 */
	protected boolean isOrderedList(String line) {
		return false;
	}

	/**
	 * Should never be called.
	 * 
	 * @throws UnsupportedOperationException
	 */
	protected char getListType(String line) {
		throw new UnsupportedOperationException();
	}

}