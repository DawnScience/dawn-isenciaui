package com.teaminabox.eclipse.wiki.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.*;

import com.teaminabox.eclipse.wiki.WikiTest;

public class WikiExporterTest extends WikiTest {

	// This is just a sanity test
	@Test
	public void testExportsWithoutError() throws Exception {
		create("This is a link to AnotherPage", "HomePage.wiki");
		create("This is a link to the HomePage", "AnotherPage.wiki");

		WikiExporter exporter = new WikiExporter();
		File file = File.createTempFile("Wiki", "Test");
		file.deleteOnExit();
		File exportDirectory = new File(file.getParent(), file.getName() + ".dir");
		assertTrue("create export directory", exportDirectory.mkdir());
		exporter.export(getJavaProject().getProject(), exportDirectory.getPath(), new NullProgressMonitor());

		Set<String> actual = new HashSet<String>();
		actual.addAll(Arrays.asList(exportDirectory.list()));

		Set<String> expected = new HashSet<String>();
		expected.addAll(Arrays.asList(new String[] { "index.html", "AnotherPage.html", "HomePage.html" }));

		assertEquals(expected, actual);
	}
}
