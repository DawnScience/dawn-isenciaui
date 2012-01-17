package com.teaminabox.eclipse.wiki.renderer;

import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;
import com.teaminabox.eclipse.wiki.text.BasicTextRegion;
import com.teaminabox.eclipse.wiki.text.EclipseResourceTextRegion;
import com.teaminabox.eclipse.wiki.text.EmbeddedWikiWordTextRegion;
import com.teaminabox.eclipse.wiki.text.ForcedLinkTextRegion;
import com.teaminabox.eclipse.wiki.text.JavaTypeTextRegion;
import com.teaminabox.eclipse.wiki.text.PluginResourceTextRegion;
import com.teaminabox.eclipse.wiki.text.TextRegion;
import com.teaminabox.eclipse.wiki.text.TextRegionVisitor;
import com.teaminabox.eclipse.wiki.text.UndefinedTextRegion;
import com.teaminabox.eclipse.wiki.text.UrlTextRegion;
import com.teaminabox.eclipse.wiki.text.WikiUrlTextRegion;
import com.teaminabox.eclipse.wiki.text.WikiWordTextRegion;

public class TextRegionAppender implements TextRegionVisitor<String> {

	private final LinkMaker				linkMaker;
	private final WikiDocumentContext	context;

	public TextRegionAppender(LinkMaker linkMaker, WikiDocumentContext context) {
		this.linkMaker = linkMaker;
		this.context = context;
	}

	public String visit(UndefinedTextRegion undefinedTextRegion) {
		return undefinedTextRegion.getText();
	}

	public String visit(UrlTextRegion urlTextRegion) {
		return linkMaker.make(urlTextRegion);
	}

	public String visit(WikiWordTextRegion wikiNameTextRegion) {
		return linkMaker.make(wikiNameTextRegion);
	}

	public String visit(WikiUrlTextRegion wikiUrlTextRegion) {
		return linkMaker.make(wikiUrlTextRegion);
	}

	public String visit(BasicTextRegion basicTextRegion) {
		return basicTextRegion.getDisplayText();
	}

	public String visit(EclipseResourceTextRegion eclipseResourceTextRegion) {
		return linkMaker.make(eclipseResourceTextRegion);
	}
	
	public String visit(PluginResourceTextRegion pluginResourceTextRegion) {
		return linkMaker.make(pluginResourceTextRegion);
	}

	public String visit(JavaTypeTextRegion region) {
		return linkMaker.make(region);
	}

	public String visit(ForcedLinkTextRegion region) {
		return linkMaker.make(region);
	}

	public String visit(EmbeddedWikiWordTextRegion region) {
		TextRegion embeddedTextRegion = region.getEmbeddedTextRegion();
		return embeddedTextRegion.accept(new EmbeddedTextRegionAppender(embeddedTextRegion, context, linkMaker));
	}

}