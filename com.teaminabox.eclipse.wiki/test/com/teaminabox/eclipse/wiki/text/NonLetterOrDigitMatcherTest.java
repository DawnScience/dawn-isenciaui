package com.teaminabox.eclipse.wiki.text;

public final class NonLetterOrDigitMatcherTest extends AbstractTextRegionMatcherTest {

	private static final TextRegionTestBean[]	ACCEPTABLE_CASES	= new TextRegionTestBean[] { new TextRegionTestBean(".", new BasicTextRegion(".")), new TextRegionTestBean(". abc", new BasicTextRegion(".")), new TextRegionTestBean(".abc", new BasicTextRegion(".")), };

	protected TextRegionMatcher getMatcher() {
		return new NonLetterOrDigitMatcher();
	}

	protected String[] getUnacceptableText() {
		return new String[] { "a" };
	}

	protected TextRegionTestBean[] getAcceptableCases() {
		return NonLetterOrDigitMatcherTest.ACCEPTABLE_CASES;
	}

}
