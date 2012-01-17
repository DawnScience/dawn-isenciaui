package com.teaminabox.eclipse.wiki.text;

import static com.teaminabox.eclipse.wiki.WikiPlugin.wikiPlugin;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.teaminabox.eclipse.wiki.editors.JavaContext;
import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;
import com.teaminabox.eclipse.wiki.util.JavaUtils;

public class JavaTypeMatcher extends AbstractTextRegionMatcher {

	public TextRegion createTextRegion(String text, WikiDocumentContext context) {
		IJavaProject javaProject = context.getJavaContext().getJavaProject();
		try {
			if (!isCandidateForTextRegion(text, context.getJavaContext())) {
				return null;
			}
			text = stripBadChars(text);
			// fast check
			IType type = javaProject.findType(text);
			if (type != null) {
				return new JavaTypeTextRegion(text, type);
			}

			// tedious check
			int packageNameLength = findPackageNameLength(javaProject, text);
			type = findType(javaProject, text, packageNameLength);
			if (type != null) {
				return new JavaTypeTextRegion(type.getFullyQualifiedName().replaceAll("\\$", "."), type);
			}
			return null;
		} catch (JavaModelException e) {
			wikiPlugin().log("Could not create TextRegion", e);
		}
		return null;
	}

	private boolean isCandidateForTextRegion(String text, JavaContext context) {
		if (!context.isInJavaProject() || text.length() == 0 || !accepts(text.charAt(0), true)) {
			return false;
		}
		return context.startsWithPackageName(text);
	}

	private IType findType(IJavaProject javaProject, String text, int packageEnd) throws JavaModelException {
		int index = text.indexOf('.', packageEnd + 1);
		if (index < 0) {
			index = packageEnd + 1;
		}

		IType match = null;
		while (index > 0 && index <= text.length()) {
			String candidate = new String(text.substring(0, index));
			IType javaType = javaProject.findType(candidate);
			if (javaType == null) {
				return match;
			}
			match = javaType;
			index = text.indexOf('.', index + 1);
		}
		return match;
	}

	private int findPackageNameLength(IJavaProject javaProject, String text) throws JavaModelException {
		int index = text.indexOf('.');
		if (index < 0) {
			index = text.length();
		}

		int match = 0;
		while (index > 0 && index <= text.length()) {
			String candidate = new String(text.substring(0, index));
			IPath path = path(candidate);
			IJavaElement javaElement = javaProject.findElement(path);
			if (javaElement == null) {
				return match;
			}
			match = index;
			index = text.indexOf('.', index + 1);
		}
		return match;
	}

	private String stripBadChars(String text) {
		if (text.length() == 0) {
			return text;
		}
		int maxIndex = maxLengthOfValidCharacters(text);
		maxIndex = lengthWithDotsStripped(text, maxIndex);
		return new String(text.substring(0, maxIndex + 1));
	}

	/**
	 * @param text
	 *            the text to work with
	 * @param the
	 *            maximum index in <code>text</code> to consider
	 * @return the index after dots have been removed
	 */
	private int lengthWithDotsStripped(String text, int maxIndex) {
		while (maxIndex > 0 && text.charAt(maxIndex) == '.') {
			maxIndex--;
		}
		return maxIndex;
	}

	private int maxLengthOfValidCharacters(String text) {
		int maxIndex = 0;
		while (maxIndex < text.length() && accepts(text.charAt(maxIndex), maxIndex == 0)) {
			maxIndex++;
		}
		maxIndex--;
		return maxIndex;
	}

	private IPath path(String fullyQualifiedType) {
		return new Path(fullyQualifiedType.replaceAll("\\.", "/"));
	}

	@Override
	protected boolean accepts(char c, boolean firstCharacter) {
		if (firstCharacter) {
			return Character.isJavaIdentifierPart(c);
		}
		return JavaUtils.isJavaClassNamePart(c);
	}

}