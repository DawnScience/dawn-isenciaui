package com.teaminabox.eclipse.wiki.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.*;

public final class JavaTypeMatcherTest extends AbstractTextRegionMatcherTest {

	private static final String					CLASS_SOURCE		= "package com.teaminabox.foo;\npublic class BigClass { class InnerClass {} }";
	private static final String					CLASS_NAME			= "com.teaminabox.foo.BigClass";
	private static final String[]				UNACCEPTABLE_TEXT	= new String[] { "wikiword stuff" };

	private static final TextRegionTestBean[]	ACCEPTABLE_CASES	= new TextRegionTestBean[] { new TextRegionTestBean(CLASS_NAME, new JavaTypeTextRegion(CLASS_NAME, null)), new TextRegionTestBean(CLASS_NAME + ".InnerClass", new JavaTypeTextRegion(CLASS_NAME + ".InnerClass", null)), new TextRegionTestBean(CLASS_NAME + ". etc.", new JavaTypeTextRegion(CLASS_NAME, null)), };

	@Before
	public void setUp() throws Exception {
		super.setUp();
		create(JavaTypeMatcherTest.CLASS_SOURCE, JavaTypeMatcherTest.CLASS_NAME.replaceAll("\\.", "/") + ".java");
	}

	protected TextRegionMatcher getMatcher() {
		return new JavaTypeMatcher();
	}

	protected String[] getUnacceptableText() {
		return JavaTypeMatcherTest.UNACCEPTABLE_TEXT;
	}

	protected TextRegionTestBean[] getAcceptableCases() {
		return JavaTypeMatcherTest.ACCEPTABLE_CASES;
	}

	@Test
	public void testTwoTypesOnSameLine() {
		JavaTypeMatcher matcher = new JavaTypeMatcher();
		create("package foo.bar;\\npublic class Test { }", "foo/bar/Test.java");
		String text = JavaTypeMatcherTest.CLASS_NAME + ".foo.bar.Test blah";
		TextRegion region = matcher.createTextRegion(text, getContext());
		assertNotNull("region should not be null", region);
		assertEquals("matches", JavaTypeMatcherTest.CLASS_NAME, region.getText());
	}
}
