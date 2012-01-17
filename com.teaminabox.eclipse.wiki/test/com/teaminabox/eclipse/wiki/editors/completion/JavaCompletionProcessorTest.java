package com.teaminabox.eclipse.wiki.editors.completion;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.junit.Test;

import com.teaminabox.eclipse.wiki.WikiTest;
import com.teaminabox.eclipse.wiki.editors.WikiEditor;

public final class JavaCompletionProcessorTest extends WikiTest {

	/**
	 * Use a test class name that is very unlikely to be found in a project's standard library set
	 */
	private static final String	TYPE_PATH		= "foo/bar/WikiEditorTestClass.java";
	private static final String	TYPE_CONTENTS	= "package foo.bar;\npublic interface WikiEditorTestClass {\n}";

	@Test
	public void testGetProposalsNoContent() throws Exception {
		WikiEditor editor = createWikiDocumentAndOpen("").getEditor();
		JavaCompletionProcessor processor = new JavaCompletionProcessor();

		ArrayList<ICompletionProposal> proposals = processor.getProposals(getJavaProject(), editor.getTextViewerForTest(), 0);
		assertEquals(0, proposals.size());
	}

	@Test
	public void testGetClassProposalDefaultPackage() throws Exception {
		ArrayList<ICompletionProposal> proposals = prepare("public class WikiEditorTestClass {\n}", "WikiEditorTestClass.java", "WikiEditorTe");
		assertEquals(1, proposals.size());
		ICompletionProposal proposal = proposals.get(0);
		assertEquals("WikiEditorTestClass", proposal.getDisplayString());
	}

	@Test
	public void testGetPackageProposal() throws Exception {
		ArrayList<ICompletionProposal> proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, "foo");
		assertEquals(2, proposals.size());
		assertEquals("foo", proposals.get(0).getDisplayString());
		assertEquals("foo.bar", proposals.get(1).getDisplayString());
	}

	@Test
	public void testGetPackageProposalWithPartitialPackageName() throws Exception {
		ArrayList<ICompletionProposal> proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, "foo.ba");
		assertEquals(1, proposals.size());
		ICompletionProposal proposal = proposals.get(0);
		assertEquals("foo.bar", proposal.getDisplayString());
	}

	@Test
	public void testGetTypeProposalInPackage() throws Exception {
		ArrayList<ICompletionProposal> proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, "foo.bar.");
		assertEquals(1, proposals.size());
		ICompletionProposal proposal = proposals.get(0);
		assertEquals("WikiEditorTestClass - foo.bar", proposal.getDisplayString());
	}

	@Test
	public void testGetTypeProposalInSentence() throws Exception {
		ArrayList<ICompletionProposal> proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, "a test foo.bar. thing", 15);
		assertEquals(1, proposals.size());
		ICompletionProposal proposal = proposals.get(0);
		assertEquals("WikiEditorTestClass - foo.bar", proposal.getDisplayString());
	}

	@Test
	public void testGetTypePrefixedWithNonJavaCharacters() throws Exception {
		ArrayList<ICompletionProposal> proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, "'''WikiEditorTestCl");
		assertEquals(1, proposals.size());
		ICompletionProposal proposal = proposals.get(0);
		assertEquals("WikiEditorTestClass - foo.bar", proposal.getDisplayString());
	}

	@Test
	public void testGetTypePrefixedWithFullStop() throws Exception {
		ArrayList<ICompletionProposal> proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, ".WikiEditorTestCla");
		assertEquals(1, proposals.size());
		ICompletionProposal proposal = proposals.get(0);
		assertEquals("WikiEditorTestClass - foo.bar", proposal.getDisplayString());
	}

	@Test
	public void testGetTypePrefixedWithSpace() throws Exception {
		ArrayList<ICompletionProposal> proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, " WikiEditorTestClass");
		assertEquals(1, proposals.size());
		ICompletionProposal proposal = proposals.get(0);
		assertEquals("WikiEditorTestClass - foo.bar", proposal.getDisplayString());
	}

	@Test
	public void testTypeInPackage() throws Exception {
		ArrayList<ICompletionProposal> proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, "a test WikiEditorTes thing", 20);
		assertEquals(1, proposals.size());
		ICompletionProposal proposal = proposals.get(0);
		assertEquals("WikiEditorTestClass - foo.bar", proposal.getDisplayString());
	}

	@Test
	public void testCursorOnSpace() throws Exception {
		ArrayList<ICompletionProposal> proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, "a WikiEditorTestClass", 2);
		assertEquals(0, proposals.size());
	}

	@Test
	public void testIsCandidateWhenWhiteSpace() throws Exception {
		ArrayList<ICompletionProposal> proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, " ");
		assertEquals(0, proposals.size());
	}

	@Test
	public void testGetTypeProposalsWhenDotPreceededByWhiteSpace() throws Exception {
		ArrayList<ICompletionProposal> proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, " .");
		assertEquals(0, proposals.size());
	}

	private ArrayList<ICompletionProposal> prepare(String code, String path, String wikiContents) throws BadLocationException, CoreException {
		return prepare(code, path, wikiContents, wikiContents.length());
	}

	private ArrayList<ICompletionProposal> prepare(String code, String path, String wikiContents, int cursor) throws BadLocationException, CoreException {
		createAndOpen(code, path);
		WikiEditor editor = createWikiDocumentAndOpen(wikiContents).getEditor();
		return new JavaCompletionProcessor().getProposals(getJavaProject(), editor.getTextViewerForTest(), cursor);
	}
}
