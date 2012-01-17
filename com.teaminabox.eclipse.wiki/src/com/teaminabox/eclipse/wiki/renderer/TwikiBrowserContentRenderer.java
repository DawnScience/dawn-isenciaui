package com.teaminabox.eclipse.wiki.renderer;

import static com.teaminabox.eclipse.wiki.WikiPlugin.wikiPlugin;

import org.eclipse.jface.text.BadLocationException;

import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;
import com.teaminabox.eclipse.wiki.text.EclipseResourceMatcher;
import com.teaminabox.eclipse.wiki.text.EmbeddedTextRegionMatcher;
import com.teaminabox.eclipse.wiki.text.EscapedWikiWordMatcher;
import com.teaminabox.eclipse.wiki.text.ForcedLinkMatcher;
import com.teaminabox.eclipse.wiki.text.IgnoredTextRegionMatcher;
import com.teaminabox.eclipse.wiki.text.JavaTypeMatcher;
import com.teaminabox.eclipse.wiki.text.LetterOrDigitMatcher;
import com.teaminabox.eclipse.wiki.text.NonLetterOrDigitMatcher;
import com.teaminabox.eclipse.wiki.text.PluginResourceMatcher;
import com.teaminabox.eclipse.wiki.text.TextRegionMatcher;
import com.teaminabox.eclipse.wiki.text.UrlMatcher;
import com.teaminabox.eclipse.wiki.text.WikiSpaceMatcher;
import com.teaminabox.eclipse.wiki.text.WikiWordMatcher;

public final class TwikiBrowserContentRenderer extends AbstractContentRenderer {

	private static final String					TOC							= "%TOC%";
	private static final String					TWIKI_WORD_PATTERN			= "[A-Z]+[a-z]+[A-Z]+\\w*";
	private static final String					ESCAPED_TWIKI_WORD_PATTERN	= "!(\\[\\[)?" + TwikiBrowserContentRenderer.TWIKI_WORD_PATTERN + "(\\]\\])?";
	private static final String[]				TEXT_TO_REMOVE_FOR_ESCAPED	= { "\\!", "\\[\\[", "\\]\\]" };

	private static final TextRegionMatcher[]	RENDERER_MATCHERS			= new TextRegionMatcher[] { new IgnoredTextRegionMatcher(), new EmbeddedTextRegionMatcher(new JavaTypeMatcher()), new JavaTypeMatcher(), new EmbeddedTextRegionMatcher(new ForcedLinkMatcher(2)), new ForcedLinkMatcher(2), new WikiSpaceMatcher(), new EscapedWikiWordMatcher(TwikiBrowserContentRenderer.ESCAPED_TWIKI_WORD_PATTERN, '!', TwikiBrowserContentRenderer.TEXT_TO_REMOVE_FOR_ESCAPED), new EmbeddedTextRegionMatcher(new WikiWordMatcher(TwikiBrowserContentRenderer.TWIKI_WORD_PATTERN)), new WikiWordMatcher(TwikiBrowserContentRenderer.TWIKI_WORD_PATTERN), new NonLetterOrDigitMatcher(), new LetterOrDigitMatcher(), new UrlMatcher(), new EmbeddedTextRegionMatcher(new EclipseResourceMatcher()), new EclipseResourceMatcher(), new PluginResourceMatcher() };

	private static final TextRegionMatcher[]	SCANNER_MATCHERS			= new TextRegionMatcher[] { new IgnoredTextRegionMatcher(), new EmbeddedTextRegionMatcher(new JavaTypeMatcher()), new JavaTypeMatcher(), new EmbeddedTextRegionMatcher(new ForcedLinkMatcher(2)), new ForcedLinkMatcher(2), new WikiSpaceMatcher(), new EscapedWikiWordMatcher(TwikiBrowserContentRenderer.ESCAPED_TWIKI_WORD_PATTERN, '!', TwikiBrowserContentRenderer.TEXT_TO_REMOVE_FOR_ESCAPED), new EmbeddedTextRegionMatcher(new WikiWordMatcher(TwikiBrowserContentRenderer.TWIKI_WORD_PATTERN)), new WikiWordMatcher(TwikiBrowserContentRenderer.TWIKI_WORD_PATTERN), new UrlMatcher(), new EmbeddedTextRegionMatcher(new EclipseResourceMatcher()), new EclipseResourceMatcher(), new PluginResourceMatcher() };

	private static final char					ORDERED_LIST_END_MARKER		= '.';

	private static final String					UNORDERED_LIST_MARKUP		= "*";
	private static final String					UNORDERED_LIST_MARKUP_REGEX	= "^\\s+\\*\\s.*";
	private static final String					ORDERED_LIST_MARKUP_REGEX	= "^\\s+[1|a|A|i|I]\\.\\s.*";
	private static final String					HEADER_MARKUP_REGEX			= "^---+(\\++|\\#+)\\s*(.+)\\s*$";

	@Override
	public TextRegionMatcher[] getRendererMatchers() {
		return TwikiBrowserContentRenderer.RENDERER_MATCHERS;
	}

	@Override
	public TextRegionMatcher[] getScannerMatchers() {
		return TwikiBrowserContentRenderer.SCANNER_MATCHERS;
	}

	@Override
	protected void appendHeader(String line) {
		String headerText = getHeaderText(line);
		appendHeaderAnchor(headerText);
		int headerSize = getHeaderSize(line);
		append("<h").append(headerSize).append(">");
		parseAndAppend(headerText);
		append("</h").append(headerSize).append(">");
		appendNewLine();
	}

	private void appendHeaderAnchor(String headerText) {
		append("<a name=\"#").append(createHeaderAnchor(headerText)).append("\"/>");
	}

	private String createHeaderAnchor(String headerText) {
		return headerText.replaceAll(" ", "_");
	}

	@Override
	protected String getHeaderText(String line) {
		return new String(line.substring(getHeaderStart(line)));
	}

	private int getHeaderStart(String line) {
		return line.indexOf(' ') + 1;
	}

	private int getHeaderSize(String line) {
		int size = 1;
		int i = line.indexOf('+') + 1;
		while (i < line.length() && line.charAt(i) == '+' && size < 9) {
			i++;
			size++;
		}
		return size;
	}

	@Override
	protected boolean isOrderedList(String line) {
		return line.matches(TwikiBrowserContentRenderer.ORDERED_LIST_MARKUP_REGEX);
	}

	@Override
	protected char getListType(String line) {
		return line.trim().charAt(0);
	}

	@Override
	protected int getListDepth(String line) {
		if (isOrderedList(line)) {
			return line.indexOf(TwikiBrowserContentRenderer.ORDERED_LIST_END_MARKER) / 3;
		}
		return line.indexOf(TwikiBrowserContentRenderer.UNORDERED_LIST_MARKUP) / 3;
	}

	@Override
	protected boolean isHeader(String line) {
		return line.matches(TwikiBrowserContentRenderer.HEADER_MARKUP_REGEX);
	}

	@Override
	protected boolean isList(String line) {
		return isUnorderedList(line) || isOrderedList(line);
	}

	private boolean isUnorderedList(String line) {
		return line.matches(TwikiBrowserContentRenderer.UNORDERED_LIST_MARKUP_REGEX);
	}

	@Override
	protected String getListText(String line) {
		if (isOrderedList(line)) {
			return new String(line.substring(line.indexOf(TwikiBrowserContentRenderer.ORDERED_LIST_END_MARKER) + 1).trim());
		}
		return new String(line.substring(line.indexOf(TwikiBrowserContentRenderer.UNORDERED_LIST_MARKUP) + 1).trim());
	}

	@Override
	protected boolean process(String line) {
		if (TOC.equals(line)) {
			appendToc();
			return true;
		}
		if (isVerbatim(line)) {
			processVerbatim();
			return true;
		}
		if (line.trim().matches("^----*$")) {
			appendHR();
			return true;
		}
		return false;
	}

	public void forEachHeader(WikiDocumentContext context, StructureClosure closure) throws BadLocationException {
		String[] document = context.getDocument();
		int level=0;
		int diff=0;
		
		for (int i = 0; i < document.length; i++) {
			String line = document[i];
			if (isHeader(line)) {
				diff=level-(getHeaderSize(line));    			// MARK THE CHANGE IN HEADING LEVEL 
				level=getHeaderSize(line);
				String header = getHeaderText(line);
				closure.applyListOrderedListTag(diff);
				closure.acceptHeader(header, i);
			}				
				if((i+1 == document.length)  )
					closure.applyListOrderedListTag(level);
		}
	}

	private void appendToc() {
		appendln("<div class=\"twikiToc\">");
		try {
			forEachHeader(getContext(), new StructureClosure() {
				public void acceptHeader(String header, int line) throws BadLocationException {
					append("<li><a href=\"#").append(createHeaderAnchor(header)).append("\">").append(header).append("</a>");
					appendln("</li>");					
					}
				public void applyListOrderedListTag(int level) {
					while(level>0) // LABEL FOR TOC
					{ appendln("</ol>"); level--;}
					while(level<0) // LABEL FOR TOC
					{ appendln("<ol>"); level++;}
				}
			});
		} catch (BadLocationException e) {
			wikiPlugin().log("Unable to build Table contents", e);
			append("<p>Sorry, there was an error building the table of contents. Please report the error in your logs. Thanks.</p>");
		}
		appendln("</div>");
	}

	private void processVerbatim() {
		append("<pre>");
		while (hasNextLine()) {
			String line = getNextLine();
			if (isEndVerbatim(line)) {
				break;
			}
			append(encode(line));
			if (!isEndVerbatim(peekNextLine())) {
				appendNewLine();
			}
		}
		append("</pre>");
	}

	private boolean isEndVerbatim(String line) {
		return line.toLowerCase().startsWith("</verbatim>");
	}

	private boolean isVerbatim(String line) {
		return line.toLowerCase().startsWith("<verbatim>");
	}

	@Override
	protected String processTags(String line) {
		// enclose in white space for the regex that follow
		line = '\n' + line + '\n';

		// the following madness from TWiki's Render.pm
		line = line.replaceAll("([\\s\\(])==([^\\s]+?|[^\\s].*?[^\\s])==([\\s\\,\\.\\;\\:\\!\\?\\)])", "$1<code><b>$2</b></code>$3");
		line = line.replaceAll("([\\s\\(])__([^\\s]+?|[^\\s].*?[^\\s])__([\\s\\,\\.\\;\\:\\!\\?\\)])", "$1<strong><em>$2</em></strong>$3");
		line = line.replaceAll("([\\s\\(])\\*([^\\s]+?|[^\\s].*?[^\\s])\\*([\\s\\,\\.\\;\\:\\!\\?\\)])", "$1<strong>$2</strong>$3");
		line = line.replaceAll("([\\s\\(])_([^\\s]+?|[^\\s].*?[^\\s])_([\\s\\,\\.\\;\\:\\!\\?\\)])", "$1<em>$2</em>$3");
		line = line.replaceAll("([\\s\\(])=([^\\s]+?|[^\\s].*?[^\\s])=([\\s\\,\\.\\;\\:\\!\\?\\)])", "$1<code>$2</code>$3");

		// get rid of the enclosing white space we added above
		return new String(line.substring(1, line.length() - 1));
	}

}