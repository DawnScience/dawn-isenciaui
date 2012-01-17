package com.teaminabox.eclipse.wiki.text;

import junit.framework.Assert;

import org.junit.*;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiTest;

public final class EclipseResourceMatcherTest extends AbstractTextRegionMatcherTest {

	private String	acceptableText;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		acceptableText = "Eclipse:/" + project.getName() + "/" + WikiTest.WIKI_FILE;
	}

	protected TextRegionMatcher getMatcher() {
		return new EclipseResourceMatcher();
	}

	protected String[] getUnacceptableText() {
		return new String[] { "foo" };
	}

	@Test
	public void testMatchLengthWithNoPath() {
		String text = "Eclipse:";
		Assert.assertNotNull("accepts", getMatcher().createTextRegion(text, getContext()));
		EclipseResourceTextRegion region = (EclipseResourceTextRegion) getMatcher().createTextRegion(text, getContext());
		Assert.assertEquals("text", text, region.getText());
	}

	@Test
	public void testLineNumber() {
		String text = acceptableText + WikiConstants.LINE_NUMBER_SEPARATOR + "123";
		Assert.assertNotNull("accepts", getMatcher().createTextRegion(text, getContext()));
		EclipseResourceTextRegion region = (EclipseResourceTextRegion) getMatcher().createTextRegion(text, getContext());
		Assert.assertEquals("text", text, region.getText());
	}

	@Test
	public void testNoLineNumberWithSeparator() {
		String text = acceptableText + WikiConstants.LINE_NUMBER_SEPARATOR;
		Assert.assertNotNull("accepts", getMatcher().createTextRegion(text, getContext()));
		EclipseResourceTextRegion region = (EclipseResourceTextRegion) getMatcher().createTextRegion(text, getContext());
		Assert.assertEquals("text", text, region.getText());
	}

	protected TextRegionTestBean[] getAcceptableCases() {
		return new TextRegionTestBean[] { new TextRegionTestBean(acceptableText, new EclipseResourceTextRegion(acceptableText)), new TextRegionTestBean(acceptableText + ". more text", new EclipseResourceTextRegion(acceptableText)), };
	}
}
