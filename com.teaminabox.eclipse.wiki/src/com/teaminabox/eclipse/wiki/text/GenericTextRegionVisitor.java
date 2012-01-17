package com.teaminabox.eclipse.wiki.text;

/**
 * A TextRegionVisitor that returns a default value for each TextRegionVisitor method.
 */
public class GenericTextRegionVisitor<T> implements TextRegionVisitor<T> {

	private T	defaultReturnValue;

	public GenericTextRegionVisitor(T defaultReturnValue) {
		this.defaultReturnValue = defaultReturnValue;
	}

	public T visit(UndefinedTextRegion undefinedTextRegion) {
		return defaultReturnValue;
	}

	public T visit(UrlTextRegion urlTextRegion) {
		return defaultReturnValue;
	}

	public T visit(WikiWordTextRegion wikiNameTextRegion) {
		return defaultReturnValue;
	}

	public T visit(WikiUrlTextRegion wikiUrlTextRegion) {
		return defaultReturnValue;
	}

	public T visit(BasicTextRegion basicTextRegion) {
		return defaultReturnValue;
	}

	public T visit(EclipseResourceTextRegion eclipseResourceTextRegion) {
		return defaultReturnValue;
	}

	public T visit(PluginResourceTextRegion eclipseResourceTextRegion) {
		return defaultReturnValue;
	}

	public T visit(JavaTypeTextRegion region) {
		return defaultReturnValue;
	}

	public T visit(ForcedLinkTextRegion region) {
		return defaultReturnValue;
	}

	public T visit(EmbeddedWikiWordTextRegion region) {
		return defaultReturnValue;
	}

}