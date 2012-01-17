package com.teaminabox.eclipse.wiki.editors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.junit.*;

import com.teaminabox.eclipse.wiki.WikiTest;

public class JavaContextTest extends WikiTest {

	private static final String	CLASS_SOURCE			= "package com.teaminabox.foo;\npublic class BigClass { class InnerClass {} }";
	private static final String	CLASS_NAME				= "com.teaminabox.foo.BigClass";

	private static final String	ANOTHER_CLASS_SOURCE	= "package net.sourceforge;\npublic class Project { class InnerClass {} }";
	private static final String	ANOTHER_CLASS_NAME		= "net.sourceforge.Project";

	@Test
	public void testGetJavaProjectInNonJavaProject() throws Exception {
		IProject project = createNonJavaProject("simpleProject");
		IFile file = createFile(project, "test.txt", "Hi");
		WikiDocumentContext wikiDocumentContext = new WikiDocumentContext(file);
		JavaContext javaContext = new JavaContext(wikiDocumentContext);
		assertFalse(javaContext.isInJavaProject());
		project.delete(true, true, null);
	}

	@Test
	public void testIsInJavaProject() throws Exception {
		IFile file = createFile(getJavaProject().getProject(), "test.txt", "Hi");
		WikiDocumentContext wikiDocumentContext = new WikiDocumentContext(file);
		JavaContext javaContext = wikiDocumentContext.getJavaContext();
		assertTrue(javaContext.isInJavaProject());
	}

	@Test
	public void testStartsWithPackageNameForPartialMatch() throws Exception {
		create(JavaContextTest.CLASS_SOURCE, JavaContextTest.CLASS_NAME.replaceAll("\\.", "/") + ".java");
		IFile file = createFile(getJavaProject().getProject(), "test.txt", "Hi");
		WikiDocumentContext wikiDocumentContext = new WikiDocumentContext(file);
		assertTrue(wikiDocumentContext.getJavaContext().startsWithPackageName("com...blah"));
	}

	@Test
	public void testStartsWithPackageNameForFullyQualifiedName() throws Exception {
		create(JavaContextTest.CLASS_SOURCE, JavaContextTest.CLASS_NAME.replaceAll("\\.", "/") + ".java");
		IFile file = createFile(getJavaProject().getProject(), "test.txt", "Hi");
		WikiDocumentContext wikiDocumentContext = new WikiDocumentContext(file);
		assertTrue(wikiDocumentContext.getJavaContext().startsWithPackageName(JavaContextTest.CLASS_NAME));
	}

	@Test
	public void testStartsWithPackageNameWhenFalse() throws Exception {
		create(JavaContextTest.CLASS_SOURCE, JavaContextTest.CLASS_NAME.replaceAll("\\.", "/") + ".java");
		IFile file = createFile(getJavaProject().getProject(), "test.txt", "Hi");
		WikiDocumentContext wikiDocumentContext = new WikiDocumentContext(file);
		assertFalse(wikiDocumentContext.getJavaContext().startsWithPackageName("something else"));
	}

	@Test
	public void testStartsWithPackageNameWhenNewPackageAdded() throws Exception {
		create(JavaContextTest.CLASS_SOURCE, JavaContextTest.CLASS_NAME.replaceAll("\\.", "/") + ".java");
		IFile file = createFile(getJavaProject().getProject(), "test.txt", "Hi");
		WikiDocumentContext wikiDocumentContext = new WikiDocumentContext(file);

		create(JavaContextTest.ANOTHER_CLASS_SOURCE, JavaContextTest.ANOTHER_CLASS_NAME.replaceAll("\\.", "/") + ".java");
		assertTrue(wikiDocumentContext.getJavaContext().startsWithPackageName(JavaContextTest.ANOTHER_CLASS_NAME));
	}

}
