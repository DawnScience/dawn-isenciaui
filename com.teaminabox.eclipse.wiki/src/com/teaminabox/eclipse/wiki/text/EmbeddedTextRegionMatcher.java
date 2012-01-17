package com.teaminabox.eclipse.wiki.text;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;

public class EmbeddedTextRegionMatcher extends AbstractTextRegionMatcher {

	private final AbstractTextRegionMatcher	wikiWordMatcher;

	public EmbeddedTextRegionMatcher(AbstractTextRegionMatcher wikiWordMatcher) {
		this.wikiWordMatcher = wikiWordMatcher;
	}

	protected boolean accepts(char c, boolean firstCharacter) {
		return firstCharacter ? WikiConstants.EMBEDDED_PREFIX.charAt(0) == c : true;
	}

	public TextRegion createTextRegion(String text, WikiDocumentContext context) {
		if (isEmbedded(text)) {
			String embeddedText = text.substring(WikiConstants.EMBEDDED_PREFIX.length());
			TextRegion embedded = wikiWordMatcher.createTextRegion(embeddedText, context);
			if (embedded != null) {
				return new EmbeddedWikiWordTextRegion(WikiConstants.EMBEDDED_PREFIX + embedded.getText(), embedded);
			}
		}
		return null;
	}

	private boolean isEmbedded(String text) {
		return text.startsWith(WikiConstants.EMBEDDED_PREFIX) && text.length() > WikiConstants.EMBEDDED_PREFIX.length() + 1;
	}

}
