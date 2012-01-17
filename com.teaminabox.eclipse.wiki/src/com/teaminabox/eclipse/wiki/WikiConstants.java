package com.teaminabox.eclipse.wiki;

import java.util.Arrays;
import java.util.List;

import com.teaminabox.eclipse.wiki.renderer.SnipSnapContentRenderer;
import com.teaminabox.eclipse.wiki.renderer.TwikiBrowserContentRenderer;
import com.teaminabox.eclipse.wiki.renderer.WardsBrowserContentRenderer;

public interface WikiConstants {

	String			PLUGIN_ID													= "com.teaminabox.eclipse.wiki";
	String			KEYBINDING_CONTEXT											= "com.teaminabox.eclipse.wiki.context";

	String			REUSE_EDITOR												= "reuseEditor";
	String			DEFAULT_REUSE_EDITOR										= "true";

	String			WORD_WRAP													= "WordWrap";
	String			SHOW_BROWSER_IN_EDITOR_WHEN_OPENING							= "ShowBrowserInEditorWhenOpening";
	String			DEFAULT_SHOW_BROWSER_IN_EDITOR_WHEN_OPENING					= "false";
	String			DEFAULT_WORD_WRAP											= "false";

	String			WIKI_FILE_EXTENSION											= ".wiki";

	String			WIKISPACE_NAMES												= "wikispace.names";
	String			WIKISPACE_URLS												= "wikispace.urls";
	String			WIKISPACE_SEPARATOR											= "|";
	String			WIKISPACE_FILE												= "/wikispace.properties";
	String			WIKISPACE_DELIMITER											= ":";

	String			EXCLUDES_FILE												= "wiki.exclude";

	String[]		URL_PREFIXES												= new String[] { "http", "https", "ftp", "sftp", "mailto", "file", "news", "gopher", "telnet", "afp", "nfs", "smb" };
	String			ECLIPSE_PREFIX												= "Eclipse:";
	String			PROJECT_PREFIX												= "Project:";
	String			PLUGIN_PREFIX												= "Plugin:";
	String			EMBEDDED_PREFIX												= "Embed:";
	String			JAVA_LINK_PREFIX											= "Java:";
	char			LINE_NUMBER_SEPARATOR										= ':';

	String			HOVER_PREVIEW_LENGTH										= "hoverPreviewLength";
	int				DEFAULT_HOVER_PREVIEW_LENGTH								= 256;
	int				MAX_HOVER_PREVIEW_LENGTH									= 1000;

	String			WIKI_URL													= "wikiURL";
	String			NEW_WIKI_NAME												= "newWikiName";
	String			WIKI_NAME													= "wikiName";
	String			URL															= "url";
	String			ECLIPSE_RESOURCE											= "eclipseResource";
	String			PLUGIN_RESOURCE												= "pluginResource";
	String			JAVA_TYPE													= "javaType";
	String			OTHER														= "other";

	String			SUFFIX_FOREGROUND											= "_foreground";
	String			SUFFIX_BACKGROUND											= "_background";
	String			SUFFIX_STYLE												= "_style";

	String			OTHER_DEFAULT_COLOUR										= "0,0,0";
	String			ECLIPSE_RESOURCE_DEFAULT_COLOUR								= "192,128,32";
	String			JAVA_TYPE_DEFAULT_COLOUR									= "128,0,128";
	String			URL_DEFAULT_COLOUR											= "0,0,255";
	String			WIKI_URL_DEFAULT_COLOUR										= "98,0,175";
	String			NEW_WIKI_NAME_DEFAULT_COLOUR								= "255,140,255";
	String			WIKI_NAME_DEFAULT_COLOUR									= "0,140,255";

	String			STYLE_NORMAL												= "normal";
	String			STYLE_BOLD													= "bold";

	String			CONTENT_ASSIST												= "WikiContentAssist";
	String			QUICK_ASSIST												= "WikiQuickAssist";

	String			NAVIGATE_TO_NEXT_LINK_ACTION_ID								= "com.teaminabox.eclipse.wiki.NavigateToNextLinkActionID";
	String			NAVIGATE_TO_PREVIOUS_LINK_ACTION_ID							= "com.teaminabox.eclipse.wiki.NavigateToPreviousLinkActionID";

	String			RESOURCE_NEW_PAGE_HEADER									= "NewPageHeader";

	String			RESOURCE_WIKI_ERROR_DIALOGUE								= "WikiErrorDialogue.";

	String			RESOURCE_WIKI_ERROR_DIALOGUE_OPEN_WIKI_FILE_TITLE			= WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE + "OpenWikiFileTitle";
	String			RESOURCE_WIKI_ERROR_DIALOGUE_OPEN_WIKI_FILE_TEXT			= WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE + "OpenWikiFileText";
	String			RESOURCE_WIKI_ERROR_DIALOGUE_OPEN_ECLIPSE_RESOURCE_TITLE	= WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE + "OpenEclipseResourceTitle";
	String			RESOURCE_WIKI_ERROR_DIALOGUE_OPEN_PLUGIN_RESOURCE_TITLE		= WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE + "OpenPluginResourceTitle";

	String			RESOURCE_WIKI_ERROR_DIALOGUE_OPEN_ECLIPSE_RESOURCE_TEXT		= WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE + "OpenEclipseResourceText";
	String			RESOURCE_WIKI_ERROR_DIALOGUE_OPEN_PLUGIN_RESOURCE_TEXT		= WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE + "OpenPluginResourceText";
	String			RESOURCE_WIKI_ERROR_DIALOGUE_PROGRAMMATIC_ERROR_TITLE		= WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE + "ProgrammaticErrorTitle";
	String			RESOURCE_WIKI_ERROR_DIALOGUE_PROGRAMMATIC_ERROR_TEXT		= WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE + "ProgrammaticErrorText";

	String			PATH_SEPARATOR												= "/";

	String			WIKI_ICON													= "icons/wiki.png";
	String			WIKI_RESOURCE_ICON											= "icons/resource_persp.gif";
	String			WIKI_SPACE_ICON												= "icons/links_view.gif";
	String			WIKI_HR_ICON												= "icons/hr.jpg";
	String			PACKAGE_ICON												= "icons/package_obj.gif";
	String			CLASS_ICON													= "icons/class_obj.gif";
	String			INTERFACE_ICON												= "icons/int_obj.gif";
	String[]		ICONS														= { WikiConstants.WIKI_HR_ICON, WikiConstants.WIKI_ICON, WikiConstants.WIKI_RESOURCE_ICON, WikiConstants.WIKI_SPACE_ICON, WikiConstants.PACKAGE_ICON, WikiConstants.CLASS_ICON, WikiConstants.INTERFACE_ICON };

	String[]		BROWSER_RENDERERS											= { WardsBrowserContentRenderer.class.getName(), TwikiBrowserContentRenderer.class.getName(), SnipSnapContentRenderer.class.getName() };
	String			DEFAULT_BROWSER_RENDERER									= WardsBrowserContentRenderer.class.getName();
	String			BROWSER_RENDERER											= "BrowserRenderer";

	String			BROWSER_CSS_URL												= "BrowserCssUrl";
	String			RENDER_FULLY_QUALIFIED_TYPE_NAMES							= "renderFullyQualifiedTypeNames";
	boolean			DEFAULT_RENDER_FULLY_QUALIFIED_TYPE_NAMES					= true;

	String			WIKI_HREF													= "http://--wiki/";
	String			HELP_PATH													= "/help";
	List<String>	IMAGE_SUFFIXES												= Arrays.asList(new String[] { ".jpg", ".jpeg", ".jpe", ".gif", ".png" });

}
