package com.teaminabox.eclipse.wiki;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class DeferredFolderDeleteThread extends Thread {
	private ArrayList<File>	folderList	= new ArrayList<File>();

	public synchronized void add(File fileOrFolder) {
		folderList.add(fileOrFolder);
	}

	public void run() {
		synchronized (this) {
			Iterator<File> iterator = folderList.iterator();
			while (iterator.hasNext()) {
				FolderUtils.deleteFileStructure(iterator.next());
				iterator.remove();
			}
		}
	}
}
