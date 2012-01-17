/**
 * Contributions: Channing Walton Quality Eclipse Plugins, chapter 2.8.3 test example
 */
package com.teaminabox.eclipse.wiki;

import static com.teaminabox.eclipse.wiki.WikiPlugin.wikiPlugin;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.junit.After;
import org.junit.Before;

import com.teaminabox.eclipse.wiki.editors.WikiBrowserEditor;
import com.teaminabox.eclipse.wiki.editors.WikiEditor;

public abstract class WikiTest {

	public static final String	TEST_PROJECT	= "wikitest";
	public static final String	WIKI_FILE		= "HomePage.wiki";

	protected IProject			project;

	@Before
	public void setUp() throws Exception {
		project = createProject();
	}

	@After
	public void tearDown() throws Exception {
		closeAllEditors();
		try {
			project.delete(true, true, null);
		} catch (CoreException e) {
			// do nothing (Windows hack)
		}
		waitForJobs();
	}

	/**
	 * Process UI input but do not return for the specified time interval.
	 *
	 * @param waitTimeMillis
	 *            the number of milliseconds
	 */
	private void delay(long waitTimeMillis) {
		Display display = Display.getCurrent();

		// If this is the UI thread,
		// then process input.
		if (display != null) {
			long endTimeMillis = System.currentTimeMillis() + waitTimeMillis;
			while (System.currentTimeMillis() < endTimeMillis) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
			display.update();
		}

		// Otherwise, perform a simple sleep.
		else {
			try {
				Thread.sleep(waitTimeMillis);
			} catch (InterruptedException e) {
				// Ignored.
			}
		}
	}

	/**
	 * Wait until all background tasks are complete.
	 */
	public void waitForJobs() {
		while (Job.getJobManager().currentJob() != null) {
			delay(1000);
		}
	}

	public WikiBrowserEditor createWikiDocumentAndOpen(String content) {
		return createWikiDocumentAndOpen(content, WIKI_FILE);
	}

	public WikiBrowserEditor createWikiDocumentAndOpen(String content, String fileName) {
		try {
			IEditorPart editor = createAndOpen(content, fileName);
			if (editor instanceof WikiBrowserEditor) {
				return (WikiBrowserEditor) editor;
			}
			fail("Expected a WikiBrowserEditor but received a " + editor.getClass());
			return null; // keep the compiler cheery
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private IEditorPart open(IFile file) throws PartInitException {
		return IDE.openEditor(wikiPlugin().getWorkbench().getActiveWorkbenchWindow().getActivePage(), file, true);
	}

	public IFile create(String content, String fileName) {
		try {
			Path path = new Path(fileName);
			createFolders(path.removeLastSegments(1));

			IFile file = project.getFile(path);
			byte[] buffer = content.getBytes();
			ByteArrayInputStream source = new ByteArrayInputStream(buffer);
			file.create(source, true, new NullProgressMonitor());
			waitForJobs();
			return file;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public IEditorPart createAndOpen(String content, String fileName) throws PartInitException {
		IFile file = create(content, fileName);
		return open(file);
	}

	private void createFolders(IPath path) throws CoreException {
		for (int i = path.segmentCount() - 1; i >= 0; i--) {
			IFolder folder = project.getFolder(path.removeLastSegments(i));
			if (!folder.exists()) {
				folder.create(true, true, null);
			}
		}
	}

	public int getCursorPosition(WikiEditor editor) {
		ITextSelection selection = (ITextSelection) editor.getSelectionProvider().getSelection();
		return selection.getOffset();
	}

	private IProject createProject() throws CoreException {
		IProject project = createNonJavaProject(WikiTest.TEST_PROJECT);
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = JavaCore.NATURE_ID;
		description.setNatureIds(newNatures);
		project.setDescription(description, null);
		IJavaProject javaProject = JavaCore.create(project);
		Set<IClasspathEntry> entries = new HashSet<IClasspathEntry>();
		entries.addAll(Arrays.asList(javaProject.getRawClasspath()));
		entries.add(JavaRuntime.getDefaultJREContainerEntry());
		javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);
		waitForJobs();
		return project;
	}

	public IProject createNonJavaProject(final String namePrefix) throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		File projRoot = new File(root.getLocation().toOSString());
		String name = namePrefix;
		try {
			File projFile = FolderUtils.createTempFolder(namePrefix, projRoot);
			FolderUtils.deleteFileStructureOnExit(projFile);
			name = projFile.getName();
		} catch (IOException e) {
			e.printStackTrace();
		}
		IProject project = root.getProject(name);
		project.create(null);
		project.open(null);
		waitForJobs();
		return project;
	}

	public IJavaProject getJavaProject() {
		return JavaCore.create(project);
	}

	private void closeAllEditors() {
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() == null) {
			return;
		}
		IWorkbenchPage[] pages = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages();
		for (IWorkbenchPage element : pages) {
			element.closeAllEditors(false);
		}
		waitForJobs();
	}

	public boolean exists(String fileName) {
		IResource resource = project.findMember(fileName);
		return resource != null && !resource.isPhantom() && resource.exists();
	}

	public void delete(String file) {
		try {
			IResource resource = project.findMember(file);
			resource.delete(true, null);
			waitForJobs();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String load(String resource) throws IOException {
		InputStream systemResourceAsStream = getClass().getResourceAsStream(resource);
		assertNotNull(resource + " wasn't found.", systemResourceAsStream);
		BufferedReader reader = new BufferedReader(new InputStreamReader(systemResourceAsStream));
		int c;
		StringBuffer buffer = new StringBuffer();
		while ((c = reader.read()) != -1) {
			buffer.append((char) c);
		}
		String content = buffer.toString();
		return content;
	}

	public IFile createFile(IProject project, String fileName, String content) throws CoreException {
		IFile file = project.getFile(fileName);
		byte[] buffer = content.getBytes();
		ByteArrayInputStream source = new ByteArrayInputStream(buffer);
		file.create(source, true, new NullProgressMonitor());
		file.setCharset("UTF8", null);
		return file;
	}
}