package com.teaminabox.eclipse.wiki.editors;

import static com.teaminabox.eclipse.wiki.WikiPlugin.wikiPlugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;
import com.teaminabox.eclipse.wiki.renderer.ContentRenderer;
import com.teaminabox.eclipse.wiki.renderer.IdeLinkMaker;

public class WikiBrowser extends ViewPart implements PropertyListener {

	private static final String	HTML_ESCAPED_SPACE	= "%20";

	private final class LaunchAction extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			Program.launch(browser.getUrl());
		}
	}

	private final class StopAction extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			WikiBrowser.this.browser.stop();
			WikiBrowser.this.progressBar.setSelection(0);
		}
	}

	private final class RefreshAction extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			WikiBrowser.this.refresh();
		}
	}

	private final class ForwardAction extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			WikiBrowser.this.goForward();
		}
	}

	private final class BackAction extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			WikiBrowser.this.goBack();
		}
	}

	private final class HomeAction extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			history.add(WikiConstants.WIKI_HREF + editor.getContext().getWikiNameBeingEdited());
			enableButtons(false);
			redrawWebView();
		}
	}

	private final class BrowserProgressListener implements ProgressListener {
		public void changed(ProgressEvent event) {
			if (event.total == 0) {
				return;
			}
			browser.setCursor(waiter);
			int ratio = event.current * 100 / event.total;
			progressBar.setSelection(ratio);
		}

		public void completed(ProgressEvent event) {
			progressBar.setSelection(0);
			locationListener.listen(true);
			stopButton.setEnabled(false);
			browser.setCursor(null);
		}
	}

	/**
	 * A helper that can ignore events. It is needed because the browser fires events for all the resources a URL
	 * requires, not just the URL itself. This is problematic because I need to manage the history manually, and don't
	 * want lots of stuff appearing in the history that shouldn't. The history is populated in part by location events
	 * because the events are the only way to know that a link has been clicked in the browser.
	 * <P>
	 * The pattern to set LocationListener.listen to false prior to loading external URLs and then setting back to true
	 * with the browser has finished loading the page. This is determined by the progress monitor...nasty!
	 */
	private class LocationListener extends LocationAdapter {
		private boolean	listen	= true;

		@Override
		public void changing(LocationEvent event) {
			if (listen) {
				WikiBrowser.this.followLink(event);
			}
		}

		public void listen(boolean listen) {
			this.listen = listen;
		}
	}

	private Cursor				waiter;
	private WikiEditor			editor;
	private FormToolkit			toolkit;
	private ContentRenderer		browserContentRenderer;
	private Form				browserForm;
	private Browser				browser;
	private Button				launchButton;
	private History<String>		history;
	private Button				forwardButton;
	private Button				backButton;
	private LocationListener	locationListener;
	private Button				refreshButton;
	private Button				stopButton;
	private ProgressBar			progressBar;
	private PropertyChangeAdapter		propertyListener;

	public WikiBrowser(WikiEditor editor) {
		this.editor = editor;
		history = new History<String>();
		history.add(WikiConstants.WIKI_HREF + editor.getContext().getWikiNameBeingEdited());
		propertyListener = new PropertyChangeAdapter(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		browserForm = toolkit.createForm(parent);
		browserForm.getBody().setLayout(new FillLayout());

		Composite contents = toolkit.createComposite(browserForm.getBody());
		toolkit.paintBordersFor(contents);
		contents.setLayout(new GridLayout(1, true));

		createButtons(contents);

		browser = new Browser(contents, SWT.NONE);
		browser.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		toolkit.adapt(browser, true, true);
		browser.setLayoutData(new GridData(GridData.FILL_BOTH));
		locationListener = new LocationListener();
		browser.addLocationListener(locationListener);

		addProgressBar(contents);
		createBrowserRenderer();
		waiter = new Cursor(Display.getDefault(), SWT.CURSOR_WAIT);
	}

	private void addProgressBar(Composite parent) {
		progressBar = new ProgressBar(parent, SWT.NONE);
		progressBar.setLayoutData(new GridData(GridData.BEGINNING));
		browser.addProgressListener(new BrowserProgressListener());
	}

	private void createButtons(Composite contents) {
		Composite buttonComposite = toolkit.createComposite(contents);
		buttonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonComposite.setLayout(new GridLayout(6, false));

		createButton(buttonComposite, "wikiHome", null, new HomeAction()).setEnabled(true);

		backButton = createButton(buttonComposite, "back", ISharedImages.IMG_TOOL_BACK, new BackAction());
		forwardButton = createButton(buttonComposite, "forward", ISharedImages.IMG_TOOL_FORWARD, new ForwardAction());
		refreshButton = createButton(buttonComposite, "refresh", ISharedImages.IMG_TOOL_REDO, new RefreshAction());
		stopButton = createButton(buttonComposite, "stop", ISharedImages.IMG_TOOL_DELETE, new StopAction());
		launchButton = createButton(buttonComposite, "launch", ISharedImages.IMG_TOOL_UP, new LaunchAction());
	}

	private Button createButton(Composite buttonComposite, String label, String sharedImagesConstant, SelectionListener selectionListener) {
		Button button = toolkit.createButton(buttonComposite, WikiPlugin.getResourceString("WikiBrowser." + label), SWT.PUSH);
		button.setLayoutData(new GridData(GridData.BEGINNING));
		button.setToolTipText(WikiPlugin.getResourceString("WikiBrowser." + label + "Tooltip"));
		button.setEnabled(false);
		button.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(sharedImagesConstant));
		button.addSelectionListener(selectionListener);
		return button;
	}

	private void followLink(LocationEvent event) {
		locationListener.listen(false);
		backButton.setEnabled(true);
		if (WikiBrowser.isWikiLocation(event.location)) {
			openWikiLocation(event.location);
			event.doit = false;
			if (wikiPlugin().getPreferenceStore().getBoolean(WikiConstants.REUSE_EDITOR)) {
				history.add(event.location);
			}
		} else if (!locationIsBlank(event.location)) {
			history.add(event.location);
			enableButtons(true);
		}
	}

	private boolean locationIsBlank(String location) {
		return "about:blank".equals(location);
	}

	private void openWikiLocation(String location) {
		String wikiDoc = new String(location.substring(WikiConstants.WIKI_HREF.length()));
		if (wikiDoc.endsWith("/")) { //$NON-NLS-1$
			wikiDoc = new String(wikiDoc.substring(0, wikiDoc.length() - 1));
		}

		if (wikiDoc.startsWith(WikiConstants.JAVA_LINK_PREFIX)) {
			new WikiLinkLauncher(editor).openJavaType(wikiDoc.substring(WikiConstants.JAVA_LINK_PREFIX.length()));
		} else if (wikiDoc.startsWith(WikiConstants.ECLIPSE_PREFIX)) {
			new WikiLinkLauncher(editor).openEclipseLocation(unescapeHtmlSpaces(wikiDoc));
		} else if (wikiDoc.startsWith(WikiConstants.PLUGIN_PREFIX)) {
			new WikiLinkLauncher(editor).openPluginLocation(unescapeHtmlSpaces(wikiDoc));
		} else {
			launchWikiLocation(wikiDoc);
		}
		enableButtons(false);
		browser.setCursor(null);
		locationListener.listen(true);
	}

	private void launchWikiLocation(String wikiDoc) {
		try {
			new WikiLinkLauncher(editor).openWikiDocument(wikiDoc);
		} catch (Exception e) {
			wikiPlugin().logAndReport(WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_OPEN_WIKI_FILE_TITLE), WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_OPEN_WIKI_FILE_TEXT), e);
		}
	}

	private String unescapeHtmlSpaces(String wikiDoc) {
		return wikiDoc.replaceAll(HTML_ESCAPED_SPACE, " ");
	}

	private void enableButtons(boolean state) {
		launchButton.setEnabled(state);
		refreshButton.setEnabled(state);
		stopButton.setEnabled(state);
	}

	private void goForward() {
		locationListener.listen(false);
		if (history.hasNext()) {
			String location = history.next();
			openLocation(location);
			backButton.setEnabled(true);
			forwardButton.setEnabled(history.hasNext());
		}
	}

	private void goBack() {
		locationListener.listen(false);
		if (history.hasPrevious()) {
			String location = history.back();
			openLocation(location);
			forwardButton.setEnabled(true);
			backButton.setEnabled(history.hasPrevious());
		} else {
			redrawWebView();
		}
	}

	private void refresh() {
		locationListener.listen(false);
		browser.refresh();
	}

	private void openLocation(String location) {
		if (WikiBrowser.isWikiLocation(location)) {
			openWikiLocation(location);
		} else {
			browser.setUrl(location);
			enableButtons(true);
		}
	}

	private static boolean isWikiLocation(String location) {
		return location.startsWith(WikiConstants.WIKI_HREF);
	}

	public void redrawWebView() {
		browser.setCursor(waiter);
		String text = browserContentRenderer.render(editor.getContext(), new IdeLinkMaker(editor.getContext()), false);
		browser.setText(text);
		enableButtons(false);
		browser.setCursor(null);
	}

	public void propertyChanged() {
		createBrowserRenderer();
		redrawWebView();
	}

	private void createBrowserRenderer() {
		browserContentRenderer = editor.getContext().getContentRenderer();
	}

	@Override
	public void setFocus() {
		browser.setFocus();
	}

	@Override
	public void dispose() {
		waiter.dispose();
		propertyListener.dispose();
		browser.dispose();
		toolkit.dispose();
	}

}