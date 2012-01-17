package com.teaminabox.eclipse.wiki;

import java.io.File;
import java.io.IOException;

public class FolderUtils {
	private static DeferredFolderDeleteThread	folderDeletionThread;

	static {
		folderDeletionThread = new DeferredFolderDeleteThread();
		Runtime.getRuntime().addShutdownHook(folderDeletionThread);
	}

	/**
	 * Generates a unique, random file folder name and creates a corresponding temporary file folder out on disk.
	 *
	 * @param prefix
	 *            Name for the folder to create (will be suffixed with a random number)
	 * @param baseFolder
	 *            Where to create the new folder
	 * @return A File representation of the newly created folder
	 * @throws IOException
	 */
	public static File createTempFolder(String prefix, File baseFolder) throws IOException {
		File tempFile = File.createTempFile(prefix, "", baseFolder);
		if (!tempFile.delete() || !tempFile.mkdir())
			throw new IOException("Temporary folder " + tempFile.getName() + " could not be created.  Already exists?");
		return tempFile;
	}

	/**
	 * Recursively deletes a file or an entire directory structure.
	 *
	 * @param fileOrFolder
	 *            the location of the old file or directory
	 * @return true if the operation succeeds without errors
	 */
	public static boolean deleteFileStructure(File fileOrFolder) {
		if (fileOrFolder == null || !fileOrFolder.exists())
			return false;
		if (fileOrFolder.isDirectory()) {
			/*
			 * Delete a directory
			 */
			File[] subFiles = fileOrFolder.listFiles();
			if (subFiles != null) {
				for (File oldSubFile : subFiles) {
					if (!deleteFileStructure(oldSubFile)) {
						return false;
					}
				}
			}
		}
		return fileOrFolder.delete();
	}

	/**
	 * Queues the given file (or folder structure) to be deleted when the JVM shuts down, if possible.
	 *
	 * @param fileOrFolder
	 */
	public static void deleteFileStructureOnExit(File fileOrFolder) {
		folderDeletionThread.add(fileOrFolder);
	}

	/**
	 * For stubborn Windows files, attempts to delete the specified file or folder structure. Will keep trying up to
	 * maxTries times, pausing longer with each attempt.
	 *
	 * @param name
	 * @param fileOrFolder
	 * @param maxTries
	 * @return The number of tries required: -1 = could not delete (gave up), 0 = already deleted, 1 = deleted on first
	 *         try, 2 = deleted on second try, etc.
	 * @throws Exception
	 * @usage <code>
	 * try {
	 * 		int tries = FolderUtils.deleteStubbornFiles(name, myFileFolder, 30);
	 * 		if (tries > 1)
	 * 			System.out.println("It took "+tries+" tries to delete the folder.");
	 * } catch (Exception e) {
	 * 		e.printStackTrace();
	 * }
	 * </code>
	 */
	public static int deleteStubbornFiles(String name, File fileOrFolder, int maxTries) throws Exception {
		int deleteAttempt;
		for (deleteAttempt = 0; deleteAttempt < maxTries; deleteAttempt++) {
			if (!fileOrFolder.exists()) {
				break;
			}
			sleepIgnoreInterrupt(deleteAttempt);
			try {
				FolderUtils.deleteFileStructure(fileOrFolder);
			} catch (Exception e) {
				if (deleteAttempt >= maxTries) {
					throw e;
				}
			}
		}
		if (fileOrFolder.exists()) {
			return -1;
		}
		return deleteAttempt;
	}

	private static void sleepIgnoreInterrupt(int deleteAttempt) {
		try {
			// wait a bit longer every time
			Thread.sleep(100 * deleteAttempt);
		} catch (InterruptedException e) {
			// do nothing
		}
	}

}
