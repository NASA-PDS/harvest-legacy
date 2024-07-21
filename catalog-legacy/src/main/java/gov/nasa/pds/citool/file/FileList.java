// $Id: FileList.java 5853 2010-02-06 22:54:00Z shardman $

package gov.nasa.pds.citool.file;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class that can hold a list of files and directories. Used to store files
 * and directories found when traversing a directory.
 * 
 * @author mcayanan
 *
 */
public class FileList {
	
	private final List files;
	private final List dirs;
	
	public FileList() {
		files = new ArrayList();
		dirs = new ArrayList();
	}
	
	/**
	 * Adds a single object to the end of the file list
	 * @param o a single file to add
	 */
	public void addToFileList(Object o) {
		files.add(o);
	}
	
	/**
	 * Adds a list of objects to the end of the file list
	 * @param c a list of files to add
	 */
	public void addToFileList(Collection c) {
		files.addAll(c);
	}
	
	/**
	 * Adds a single object to the end of the directory list
	 * @param o a single directory to add
	 */
	public void addToDirList(Object o) {
		dirs.add(o);
	}
	
	/**
	 * Adds a list of objects to the end of the directory list
	 * @param c a list of directories to add
	 */
	public void addToDirList(Collection c) {
		dirs.addAll(c);
	}
	
	/**
	 * Gets files that were added to the list
	 * @return a list of files
	 */
	public List getFiles() {
		return files;
	}
	
	/**
	 * Gets directories that were added to the list
	 * @return a list of directories
	 */
	public List getDirs() {
		return dirs;
	}
}
