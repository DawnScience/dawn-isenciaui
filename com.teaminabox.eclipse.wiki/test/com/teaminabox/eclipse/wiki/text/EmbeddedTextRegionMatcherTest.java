package com.teaminabox.eclipse.wiki.text;

public class EmbeddedTextRegionMatcherTest extends AbstractTextRegionMatcherTest {

	protected TextRegionMatcher getMatcher() {
		return new EmbeddedTextRegionMatcher(new WikiWordMatcher("([A-Z][a-z]+){2,}"));
	}

	protected TextRegionTestBean[] getAcceptableCases() {
		return new TextRegionTestBean[] { new TextRegionTestBean("Embed:WikiWord", new EmbeddedWikiWordTextRegion("Embed:WikiWord", new WikiWordTextRegion("WikiWord"))), new TextRegionTestBean("Embed:WikiWord stuff", new EmbeddedWikiWordTextRegion("Embed:WikiWord", new WikiWordTextRegion("WikiWord"))) };
	}

	protected String[] getUnacceptableText() {
		return new String[] { "WikiWord", "word", "123", "WikiWord followed by stuff", "Embed:" };
	}
}
