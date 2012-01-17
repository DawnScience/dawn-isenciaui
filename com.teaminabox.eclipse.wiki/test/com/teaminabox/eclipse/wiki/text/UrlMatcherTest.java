package com.teaminabox.eclipse.wiki.text;

public final class UrlMatcherTest extends AbstractTextRegionMatcherTest {

	private static final TextRegionTestBean[]	ACCEPTABLE_CASES	= new TextRegionTestBean[] {
		new TextRegionTestBean("http://abc.def.se/WEBLINK/ViewDocs?DocumentName=EAB%2FOEP-07%3A0518&Latest=true", new UrlTextRegion("http://abc.def.se/WEBLINK/ViewDocs?DocumentName=EAB%2FOEP-07%3A0518&Latest=true")),
		new TextRegionTestBean("http://www.foo.com/~bar/", new UrlTextRegion("http://www.foo.com/~bar/")), new TextRegionTestBean("http://www.google.com", new UrlTextRegion("http://www.google.com")), new TextRegionTestBean("http://www.google.com blah", new UrlTextRegion("http://www.google.com")), new TextRegionTestBean("http://www.google.com)", new UrlTextRegion("http://www.google.com")), new TextRegionTestBean("ftp://a.b.c", new UrlTextRegion("ftp://a.b.c")), new TextRegionTestBean("ftp://a.b.c blah", new UrlTextRegion("ftp://a.b.c")), new TextRegionTestBean("mailto:steve@apple.com", new UrlTextRegion("mailto:steve@apple.com")), new TextRegionTestBean("mailto:steve@apple.com blah", new UrlTextRegion("mailto:steve@apple.com")), };

	protected TextRegionMatcher getMatcher() {
		return new UrlMatcher();
	}

	protected TextRegionTestBean[] getAcceptableCases() {
		return UrlMatcherTest.ACCEPTABLE_CASES;
	}

	protected String[] getUnacceptableText() {
		return new String[] { "blah" };
	}

}
