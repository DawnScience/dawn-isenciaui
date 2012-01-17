package com.teaminabox.eclipse.wiki.editors.completion;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.CompletionRequestor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.eval.IEvaluationContext;
import org.eclipse.jdt.ui.text.java.CompletionProposalComparator;
import org.eclipse.jdt.ui.text.java.CompletionProposalLabelProvider;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;

import com.teaminabox.eclipse.wiki.util.JavaUtils;

/**
 * This is a replacement for the JavaCompletionProcessor. It will replace the JavaCompletionProcessor when I've figured
 * out how to make it search in source too.
 */
public class JavaCompletionProcessor extends CompletionRequestor {

	private String						textToComplete;
	@SuppressWarnings("unchecked")
	private Set<ICompletionProposal>	proposals	= new TreeSet<ICompletionProposal>(new CompletionProposalComparator());
	private int							documentOffset;

	public ArrayList<ICompletionProposal> getProposals(IJavaProject project, ITextViewer viewer, final int documentOffset) throws BadLocationException, JavaModelException {
		initialise(viewer, documentOffset);
		if (textToComplete != null) {
			performCodeComplete(project, textToComplete);
			ArrayList<ICompletionProposal> results = new ArrayList<ICompletionProposal>(proposals);
			proposals.clear();
			return results;
		}
		return new ArrayList<ICompletionProposal>();
	}

	private void initialise(ITextViewer viewer, final int documentOffset) throws BadLocationException {
		proposals.clear();
		this.documentOffset = documentOffset;
		textToComplete = getTextToComplete(viewer, documentOffset);
	}

	@Override
	public void accept(CompletionProposal proposal) {
		if (proposal.getKind() != CompletionProposal.TYPE_REF && proposal.getKind() != CompletionProposal.PACKAGE_REF) {
			return;
		}
		CompletionProposalLabelProvider labelProvider = new CompletionProposalLabelProvider();
		String matchName = getReplacementText(proposal);
		String displayText = labelProvider.createLabel(proposal);
		Image image = labelProvider.createImageDescriptor(proposal).createImage();
		ICompletionProposal myProposal = new org.eclipse.jface.text.contentassist.CompletionProposal(matchName, documentOffset - textToComplete.length(), textToComplete.length(), matchName.length(), image, displayText, null, null);
		proposals.add(myProposal);
	}

	private void performCodeComplete(IJavaProject project, String text) throws JavaModelException {
		IEvaluationContext context = project.newEvaluationContext();
		context.codeComplete(text, text.length(), this);
	}

	/**
	 * Get the (start of) the fully qualified type at <code>documentOffset</code> that the user is trying to complete.
	 *
	 * @return the beginnings of the fully qualified type or <code>null</code> if there is nothing.
	 */
	private String getTextToComplete(ITextViewer viewer, int documentOffset) throws BadLocationException {
		IDocument document = viewer.getDocument();
		int characterIndex = documentOffset - 1;
		if (characterIndex < 0 || !JavaUtils.isJavaClassNamePart(document.getChar(characterIndex))) {
			return null;
		}
		int start = characterIndex;
		while (start > 0 && JavaUtils.isJavaClassNamePart(document.getChar(start))) {
			start--;
		}
		while (start < characterIndex && !Character.isJavaIdentifierPart(document.getChar(start))) {
			start++;
		}
		String prefix = document.get(start, characterIndex - start + 1);
		if (".".equals(prefix)) {
			return null;
		}
		return prefix;
	}

	private String getReplacementText(CompletionProposal proposal) {
		String matchName = new String(proposal.getCompletion());
		String pack = new String(proposal.getDeclarationSignature());
		if (!matchName.startsWith(pack)) {
			// java.lang Classes don't have their package so ...
			matchName = pack + "." + matchName;
		}
		return matchName;
	}
}
