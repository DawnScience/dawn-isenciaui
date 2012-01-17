package com.teaminabox.eclipse.wiki.text;

public final class LetterOrDigitMatcherTest extends AbstractTextRegionMatcherTest {

	private static final TextRegionTestBean[]	ACCEPTABLE_CASES	= new TextRegionTestBean[] { new TextRegionTestBean("abc", new BasicTextRegion("abc")), new TextRegionTestBean("abc123", new BasicTextRegion("abc123")), new TextRegionTestBean("abc 123", new BasicTextRegion("abc")), new TextRegionTestBean("123 abc", new BasicTextRegion("123")), };

	protected TextRegionMatcher getMatcher() {
		return new LetterOrDigitMatcher();
	}

	protected String[] getUnacceptableText() {
		return new String[] { "!@�$%" };
	}

	protected TextRegionTestBean[] getAcceptableCases() {
		return LetterOrDigitMatcherTest.ACCEPTABLE_CASES;
	}

}
