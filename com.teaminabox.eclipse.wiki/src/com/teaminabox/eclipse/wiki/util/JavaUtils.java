package com.teaminabox.eclipse.wiki.util;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;

import de.java2html.converter.JavaSource2HTMLConverter;
import de.java2html.javasource.JavaSource;
import de.java2html.javasource.JavaSourceParser;
import de.java2html.options.Java2HtmlConversionOptions;

public class JavaUtils {

	public static boolean isJavaClassNamePart(char c) {
		return Character.isJavaIdentifierPart(c) || c == '.';
	}

	public static boolean isJavaProject(IProject project) throws CoreException {
		return project.hasNature(JavaCore.NATURE_ID);
	}

	public static void writeJava(Reader from, Writer to) throws IOException {
		JavaSource java = new JavaSourceParser().parse(from);
		JavaSource2HTMLConverter converter = new JavaSource2HTMLConverter(java);
		Java2HtmlConversionOptions options = Java2HtmlConversionOptions.getDefault();
		options.setShowLineNumbers(true);
		options.setShowFileName(true);
		options.setShowJava2HtmlLink(true);
		converter.setConversionOptions(options);
		converter.convert(to);
		to.flush();
	}
}