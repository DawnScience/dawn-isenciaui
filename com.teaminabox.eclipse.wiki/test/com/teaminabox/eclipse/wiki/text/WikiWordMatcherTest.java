package com.teaminabox.eclipse.wiki.text;

public final class WikiWordMatcherTest extends AbstractTextRegionMatcherTest {

	private static final String					WIKI_WORD_PATTERN	= "([A-Z][a-z]+){2,}[0-9]*";
	private static final String[]				UNACCEPTABLE_TEXT	= new String[] { "wikiword stuff", "com.canon.cre.ciki.Ciki com.capco.estp.context.SystemContext" };
	private static final TextRegionTestBean[]	ACCEPTABLE_CASES	= new TextRegionTestBean[] { new TextRegionTestBean("WikiWord", new WikiWordTextRegion("WikiWord")), new TextRegionTestBean("WikiWord etc.", new WikiWordTextRegion("WikiWord")), new TextRegionTestBean("WikiWord.", new WikiWordTextRegion("WikiWord")), new TextRegionTestBean("WikiWord123", new WikiWordTextRegion("WikiWord123")), new TextRegionTestBean("WikiWord AnotherWikiWord", new WikiWordTextRegion("WikiWord")) };

	protected TextRegionMatcher getMatcher() {
		return new WikiWordMatcher(WIKI_WORD_PATTERN);
	}

	protected String[] getUnacceptableText() {
		return WikiWordMatcherTest.UNACCEPTABLE_TEXT;
	}

	protected TextRegionTestBean[] getAcceptableCases() {
		return WikiWordMatcherTest.ACCEPTABLE_CASES;
	}

}
