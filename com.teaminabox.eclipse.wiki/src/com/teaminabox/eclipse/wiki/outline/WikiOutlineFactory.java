package com.teaminabox.eclipse.wiki.outline;

import static com.teaminabox.eclipse.wiki.WikiPlugin.wikiPlugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.model.AdaptableList;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.editors.WikiEditor;
import com.teaminabox.eclipse.wiki.renderer.StructureClosure;

public final class WikiOutlineFactory {

	private static final WikiOutlineFactory	INSTANCE	= new WikiOutlineFactory();

	private WikiOutlineFactory() {
	}

	/**
	 * Returns the content outline for the given manifest file.
	 *
	 * @return the content outline for the argument
	 */
	public AdaptableList getContentOutline(WikiEditor editor) {
		IFile file = ((IFileEditorInput) editor.getEditorInput()).getFile();
		return new AdaptableList(getContents(file, editor));
	}

	private OutlineElement[] getContents(final IFile file, final WikiEditor editor) {
		try {
			final OutlineElement root = new OutlineElement(file, editor.getContext().getWikiNameBeingEdited(), 0, 0, wikiPlugin().getImageRegistry().getDescriptor(WikiConstants.WIKI_ICON));
			editor.getContext().getContentRenderer().forEachHeader(editor.getContext(), new StructureClosure() {
				public void acceptHeader(String header, int line) throws BadLocationException {
					new OutlineElement(root, header, editor.getOffset(line), 0, wikiPlugin().getImageRegistry().getDescriptor(WikiConstants.WIKI_ICON));
				}
				public void applyListOrderedListTag(int level) {
				}
				
			});
			return new OutlineElement[] { root };
		} catch (Exception e) {
			wikiPlugin().log(e.getLocalizedMessage(), e);
		}
		return new OutlineElement[0];
	}

	public static WikiOutlineFactory getInstance() {
		return WikiOutlineFactory.INSTANCE;
	}

}