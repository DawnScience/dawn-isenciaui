package com.teaminabox.eclipse.wiki.text;

public final class ForcedLinkMatcherTest extends AbstractTextRegionMatcherTest {

	private static final String[]				UNACCEPTABLE_TEXT	= new String[] { "WikiSyntax", "[[]]", "[[ ]]", "[[", "]]" };
	private static final TextRegionTestBean[]	ACCEPTABLE_CASES	= new TextRegionTestBean[] { new TextRegionTestBean("[[wiki syntax]]", new ForcedLinkTextRegion("[[wiki syntax]]", 2)), new TextRegionTestBean("[[wiki syntax]] blah", new ForcedLinkTextRegion("[[wiki syntax]]", 2)), };

	protected TextRegionMatcher getMatcher() {
		return new ForcedLinkMatcher(2);
	}

	protected String[] getUnacceptableText() {
		return ForcedLinkMatcherTest.UNACCEPTABLE_TEXT;
	}

	protected TextRegionTestBean[] getAcceptableCases() {
		return ForcedLinkMatcherTest.ACCEPTABLE_CASES;
	}

}
