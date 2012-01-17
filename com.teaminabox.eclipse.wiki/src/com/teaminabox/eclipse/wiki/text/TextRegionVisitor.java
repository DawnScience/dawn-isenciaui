package com.teaminabox.eclipse.wiki.text;

public interface TextRegionVisitor<T> {
	T visit(UndefinedTextRegion undefinedTextRegion);

	T visit(UrlTextRegion urlTextRegion);

	T visit(WikiWordTextRegion wikiNameTextRegion);

	T visit(WikiUrlTextRegion wikiUrlTextRegion);

	T visit(BasicTextRegion basicTextRegion);

	T visit(EclipseResourceTextRegion eclipseResourceTextRegion);

	T visit(PluginResourceTextRegion pluginResourceTextRegion);

	T visit(JavaTypeTextRegion region);

	T visit(ForcedLinkTextRegion region);

	T visit(EmbeddedWikiWordTextRegion region);
}