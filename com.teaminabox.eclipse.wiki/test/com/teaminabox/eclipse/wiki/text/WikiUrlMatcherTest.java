package com.teaminabox.eclipse.wiki.text;

public final class WikiUrlMatcherTest extends AbstractTextRegionMatcherTest {

	private static final TextRegionTestBean[]	ACCEPTABLE_CASES	= new TextRegionTestBean[] { new TextRegionTestBean("Apache:", new WikiUrlTextRegion("Apache:", "Apache:")), new TextRegionTestBean("Apache: stuff", new WikiUrlTextRegion("Apache:", "Apache:")), new TextRegionTestBean("Apache:foo stuff", new WikiUrlTextRegion("Apache:foo", "Apache:foo")), };

	protected TextRegionMatcher getMatcher() {
		return new WikiSpaceMatcher();
	}

	protected String[] getUnacceptableText() {
		return new String[] { "not acceptable" };
	}

	protected TextRegionTestBean[] getAcceptableCases() {
		return WikiUrlMatcherTest.ACCEPTABLE_CASES;
	}

}
