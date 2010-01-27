/**
 * Copyright (c) 2009 Symbian Foundation and/or its subsidiary(-ies).
 * All rights reserved.
 * This component and the accompanying materials are made available
 * under the terms of the License "Eclipse Public License v1.0"
 * which accompanies this distribution, and is available
 * at the URL "http://www.eclipse.org/legal/epl-v10.html".
 *
 * Initial Contributors:
 * Symbian Foundation - initial contribution.
 * Contributors:
 * Description:
 * Overview:
 * Details:
 * Platforms/Drives/Compatibility:
 * Assumptions/Requirement/Pre-requisites:
 * Failures and causes:
 */
package org.symbian.tools.wrttools.sdt.utils;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author eswartz
 *
 */
public class FileUtils {
	
	// special IStatus code for file modified during validateEdit
	public static final int MODIFIED_FILES_STATUS = 1000;
	
    /** Copy files or directories from 'from' to 'to'.  
     * 
     * Recursively descends if 'from' is a directory.  
     * Creates 'from' tail directory in 'to', e.g.:
     * from = c:/foo/bar
     * to = c:/temp
     * 
     * -->
     * 
     * c:/temp/bar
     * 
     * @param from
     * @param to
     * @param filter the filename filter, or null
     */
    public static void copyTree(File from, File to, FileFilter filter) throws IOException {
        File[] children;
        
    	if (!from.exists()) {
    		throw new IOException ("Source of copy \'" + from.getName() + "\' doesn't exist");
    	}
    	
    	if (to.getCanonicalPath().equals(from.getCanonicalPath())) {
    		throw new IOException ("\'" + from.getName() + "\' cannot copy to itself");
    	}

        if (from.isDirectory()) {
            children = from.listFiles(filter);
            // just like cp command, if target exist, make a new folder into
            // the target, copy content into the target folder
            if (to.exists()) {
            	if (to.isFile()) {
            		throw new IOException("Cannot overwrite non-directory \'" + to.getName() + "\' with directory " + from.getName());
            	}
            	// copy into new directory name grafted after the existing one
            	to = new File(to, from.getName());
            	copyTree(from, to, filter);
            	return;
            } else {
            	// copy into new directory name
            	to.mkdir();            	
                for (int i = 0; i < children.length; i++) {
                    File curto = new File(to, children[i].getName());
         
                    copyTree(children[i], curto, filter);
                }
                return;
            }
        }
        else {
        	if (to.isDirectory()) {
        		to = new File (to, from.getName());
        	}
            copyFile(from, to);
            return;
        }
        
    }

    /** Copy files or directories from 'from' to 'to'.  
     * 
     * Recursively descends if 'from' is a directory.  
     * Does not create 'from' tail directory in 'to', e.g.:
     * from = c:/foo/bar contains a,b,c
     * to = c:/temp
     * 
     * -->
     * 
     * c:/temp/a
     * c:/temp/b
     * c:/temp/c
     * 
     * @param from
     * @param to
     * @param filter the filename filter, or null
     */
    public static void copyTreeNoParent(File from, File to, FileFilter filter) throws IOException {
        File[] files;
        
        Check.checkArg(from.isDirectory());
        
        files = from.listFiles(filter);
        to.mkdirs();
        
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                //File curto = new File(to, files[i].getName());
                //curto.mkdirs();
                copyTree(files[i], to, filter);
            }
            else {
                File curto = new File(to, files[i].getName());
                copyFile(files[i], curto);
            }
        }
        
    }
    
    /**
     * Copy from an input stream to a file
     * 
     * @param in
     * @param to
     * @throws IOException
     */
    public static void copyFile(InputStream in, File to) throws IOException {
        FileOutputStream out = new FileOutputStream(to);
        int len;
        byte[] buffer = new byte[4096];
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        out.close();
        in.close();
    }

    /** Copy a single file from 'from' to 'to'
     * 
     * @param from
     * @param to
     * @throws IOException
     */
    public static void copyFile(File from, File to) throws IOException {
        FileInputStream in = new FileInputStream(from);
        copyFile(in, to);
    }

    /** Delete a directory tree recursively.
     * <p> 
     * Does not delete file.
     * @param file start point for deletion -- not itself deleted
     */
    public static void delTree(File file) {
        File[] files = file.listFiles();
        if (files != null) { 
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    delTree(files[i]);
                }
            }
        }
        files = file.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        }
    }

	public static File pluginRelativeFile(Plugin plugin, String file) throws IOException {
        Check.checkArg(plugin);
        Bundle bundle = plugin.getBundle();
        if (bundle == null)
            return null;
        URL url = FileLocator.find(bundle, new Path("."), null); //$NON-NLS-1$
        if (url == null)
            return null;
        url = FileLocator.resolve(url);
        return new File(url.getPath(), file);
    }

    /** 
     * Convert the path to a path relative to the base, if possible.
     * @param rootPath full path to base
     * @param cpath full path to resource
     * @return new path (base-relative) or null if not resolvable to base
     */
    static public IPath removePrefixFromPath(IPath rootPath, IPath cpath) {
        if (matchingFirstSegments(rootPath, cpath) == rootPath.segmentCount()) {
        	IPath suffix = cpath.removeFirstSegments(rootPath.segmentCount());
        	return suffix.setDevice(null);
        }
        return null;
    }

    /** 
     * Convert the path to a path in the workspace, if possible.
     * Either the path comes in as a path pointing inside the
     * workspace, or we can find a linked resource which aliases
     * to the same location.
     * @param cpath full filesystem path to resource
     * @return new path (workspace-relative) or null if not resolvable to workspace
     */
    static public IPath convertToWorkspacePath(IPath cpath) {
    	return convertToWorkspacePath(cpath, false);
    }
    
    /** 
     * Convert the path to a path in the workspace, if possible.
     * Either the path comes in as a path pointing inside the
     * workspace, or we can find a linked resource which aliases
     * to the same location.
     * @param cpath full filesystem path to resource
     * @param makeCanonical if true, work from the canonical path if possible
     * (recommended)
     * @return new path (workspace-relative) or null if not resolvable to workspace
     */
    static public IPath convertToWorkspacePath(IPath cpath, boolean makeCanonical) {
    	return convertToWorkspacePath(cpath, makeCanonical, true);
    }
    /** 
     * Convert the path to a path in the workspace, if possible.
     * Either the path comes in as a path pointing inside the
     * workspace, or we can find a linked resource which aliases
     * to the same location.
     * @param cpath full filesystem path to resource
     * @param makeCanonical if true, work from the canonical path if possible
     * (recommended)
     * @param resolveLinks if true, look for project paths which reference files outside
     * the workspace via links (very slow)
     * @return new path (workspace-relative) or null if not resolvable to workspace
     */
    static public IPath convertToWorkspacePath(IPath cpath, boolean makeCanonical, boolean resolveLinks) {
        if (!Platform.isRunning() || cpath == null)
            return null;
        
        if (makeCanonical) {
			try {
				// Since we have a filesystem path, try using the filesystem
				// and Java's smarter APIs to fix the caps early. 
				// Watch out for the device being set incorrectly, though.
				String device = cpath.getDevice();
				cpath = new Path(cpath.toFile().getCanonicalPath()).setDevice(device);
			} catch (IOException e) {
				// Something's wrong with the path; just use it as-is
			}
        }
        
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot(); 
        IFile file = root.getFileForLocation(cpath);
        if (file != null) {
        	return file.getFullPath().setDevice(null);
        }
        
        if (resolveLinks) {
	        // try to see if a linked resource points to this
	        IFile[] files = root.findFilesForLocation(cpath);
	        IPath newPath = null;
	        if (files.length > 0)
	            newPath = files[0].getFullPath().setDevice(null);
	        else {
	            IContainer[] folders = root.findContainersForLocation(cpath);
	            if (folders.length > 0)
	                newPath = folders[0].getFullPath().setDevice(null);
	        }
	        return newPath;
        }
        
        return null;
    }

    /** 
     * Convert the full path to a full path inside the workspace, if possible.
     * (E.g. change a linked resource to the real full path.)
     * <p>
     * Either the path comes in as a path pointing inside the
     * workspace, or we can find a linked resource which aliases
     * to the same location.
     * @param cpath full filesystem path
     * @return same cpath, new path, or null if not resolvable to workspace
     */
    static public IPath convertToWorkspaceLocation(IPath cpath) {
        if (!Platform.isRunning())
            return null;
        
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot(); 
        IPath rootPath = root.getLocation();
        IPath newPath = convertToWorkspacePath(cpath);
        if (newPath == null)
            return null;
        
        // convert to full path
        return rootPath.append(newPath);
    }

    /** 
     * Variant of routine that works with case-insensitive matching.
     * @see IPath#matchingFirstSegments(IPath) 
     */
    public static int matchingFirstSegments(IPath my, IPath anotherPath) {
    	if (my == null || anotherPath == null)
    		return 0;
        int anotherPathLen = anotherPath.segmentCount();
        int max = Math.min(my.segmentCount(), anotherPathLen);
        int count = 0;
        for (int i = 0; i < max; i++) {
            if (!my.segment(i).equalsIgnoreCase(anotherPath.segment(i))) {
                return count;
            }
            count++;
        }
        return count;
    }

    /**
     * Map the given file to a path in the current workspace.
     * @param file
     * @return the IFile for the file
     * @deprecated this function is broken since it doesn't resolve linked resources.
     */
    public static IFile convertFileToIFile(IContainer container, File file) {
        IPath path = new Path(file.getAbsolutePath());
        IPath rootPath = container.getLocation();
        
        // this is BUGGY!  Case sensitive for some dumb reason
        //int match = path.matchingFirstSegments(rootPath);
        int match = matchingFirstSegments(path, rootPath);
        Check.checkState(match >= rootPath.segmentCount()); 
            
        path = path.removeFirstSegments(match);
        IFile wsFile = container.getFile(path);
        return wsFile;
    }
    
    /**
     * Map the given file to a path in the current workspace.
     * @param file
     * @return the IFile for the file, or null if the file is not in the workspace
     */
    public static IFile convertFileToIFile(File file) {
       IPath path = new Path(file.getAbsolutePath());
        
        IPath wsPath = convertToWorkspacePath(path, true);
        if (wsPath != null)
        	return ResourcesPlugin.getWorkspace().getRoot().getFile(wsPath);
        
        return null;
        
        /*
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IPath rootPath = root.getRawLocation();
        
        // this is BUGGY!  Case sensitive for some dumb reason
        //int match = path.matchingFirstSegments(rootPath);
        int match = matchingFirstSegments(path, rootPath);
        if (match < rootPath.segmentCount())
        	return null;
            
        path = path.removeFirstSegments(match);
        IFile wsFile = root.getFile(path);
        
        return wsFile;
        */
    }
    
    /**
     * Map the given file to a resource in the current workspace.  This only handles
     * files that map to known workspace resources.
     * @param file
     * @return the IResource for the file, or null if the file is not in the workspace or doesn't
     * exist.
     */
    public static IResource convertFileToExistingResource(File file) {
       IPath path = new Path(file.getAbsolutePath());
        
       IPath wsPath = convertToWorkspacePath(path, true);
       if (wsPath != null)
    	   return ResourcesPlugin.getWorkspace().getRoot().findMember(wsPath);
        
       return null;
    }
    
    /**
     * Return the project for the given workspace-relative path.
     */
    public static IProject projectForPath(IPath path) {
    	IProject result = null;
    	if (path != null) {
    		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    		result = root.getProject(path.segment(0));
    	}
    	return result;
    }

	/**
	 * Read contents of a file into an array
	 * @param file
	 * @param encoding (null = system default)
	 * @return array of text
	 * @throws CoreException wrapping java.io.FileException
	 */
	public static char[] readFileContents(File file, String encoding) throws CoreException {
		Check.checkArg(file);
		FileInputStream fis;
        try {
        	fis = new FileInputStream(file);
        	return readInputStreamContents(fis, encoding);
        } catch (IOException e) {
            throw new CoreException(Logging.newStatus(UtilsPlugin.getDefault(), e));
        }
	}

	/**
	 * Read contents of a file into an array
	 * @param is input stream
	 * @param encoding (null is system default)
	 * @return array of text
	 * @throws CoreException wrapping java.io.FileException
	 */
	public static char[] readInputStreamContents(InputStream is, String encoding) throws CoreException {
		Check.checkArg(is);

		Reader reader;
		try {
	    	if (encoding != null)
	    		reader = new InputStreamReader(is, encoding);
	    	else
	    		reader = new InputStreamReader(is);
	
	        return readReaderContents(reader);
		} catch (UnsupportedEncodingException e) {
            throw new CoreException(Logging.newStatus(UtilsPlugin.getDefault(), e));
        }
	}
	
	/**
	 * Read contents of a file into an array
	 * @param file
	 * @param encoding (null = system default)
	 * @return array of text
	 * @throws CoreException wrapping java.io.FileException
	 */
	public static char[] readReaderContents(Reader reader) throws CoreException {
		Check.checkArg(reader);
        try {
            char[] buf = new char[1024];
            StringBuffer sb = new StringBuffer();
            int len;
            while ((len = reader.read(buf)) > 0) {
                sb.append(buf, 0, len);
            }
            reader.close();
            return sb.toString().toCharArray();
        } catch (UnsupportedEncodingException e) {
            throw new CoreException(Logging.newStatus(UtilsPlugin.getDefault(), e));
        } catch (IOException e) {
            throw new CoreException(Logging.newStatus(UtilsPlugin.getDefault(), e));
        }
	}

	/**
	 * Write contents of a file from an array
	 * @param file
	 * @param encoding (null = system default)
	 * @throws CoreException wrapping java.io.FileException
	 */
	public static void writeFileContents(File file, char[] text, String encoding) throws CoreException {
		Check.checkArg(file);
		FileOutputStream fos;
        try {
        	fos = new FileOutputStream(file);
        	writeOutputStreamContents(fos, text, encoding);
        } catch (IOException e) {
            throw new CoreException(Logging.newStatus(UtilsPlugin.getDefault(), e));
        }
	}

	/**
	 * Write contents on array to an output stream
	 * @param os output stream
	 * @param encoding (null is system default)
	 * @return array of text
	 * @throws CoreException wrapping java.io.FileException
	 */
	public static void writeOutputStreamContents(OutputStream os, char[] text, String encoding) throws CoreException {
		Check.checkArg(os);

		Writer writer;
		try {
	    	if (encoding != null)
	    		writer = new OutputStreamWriter(os, encoding);
	    	else
	    		writer = new OutputStreamWriter(os);
	
	        writeWriterContents(writer, text);
		} catch (UnsupportedEncodingException e) {
            throw new CoreException(Logging.newStatus(UtilsPlugin.getDefault(), e));
        }
	}

	/**
	 * Write contents of an array to a file
	 * @param file
	 * @param encoding (null = system default)
	 * @throws CoreException wrapping java.io.FileException
	 */
	public static void writeWriterContents(Writer writer, char[] text) throws CoreException {
		Check.checkArg(writer);
        try {
        	try {
        		writer.write(text, 0, text.length);
        	} finally {
        		writer.close();
        	}
        } catch (UnsupportedEncodingException e) {
            throw new CoreException(Logging.newStatus(UtilsPlugin.getDefault(), e));
        } catch (IOException e) {
            throw new CoreException(Logging.newStatus(UtilsPlugin.getDefault(), e));
        }
	}


	/**
	 * Returns whether pathSegment is a valid part of a path in a Carbide project.
	 * This checks for file name validity and 
	 * name only contains alpha-numeric -or- hyphen -or- underscrore -or- dot characters
	 * 
	 * @param pathSegment the segment (file or folder name)
	 * @return true if valid
	 */
	public static boolean isValidCarbideProjectPathSegment(String pathSegment) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		int typeMask = IResource.FILE | IResource.FOLDER;
		boolean valid = pathSegment.length() == 0 || 
			workspace.validateName(pathSegment, typeMask).isOK();
		if (valid) {
			for (int i = 0; i < pathSegment.length(); i++) {
				char c = pathSegment.charAt(i);
				valid = Character.isLetterOrDigit(c) || (c == '-') || (c == '_');
				if (!valid)
					break;
			}
		}
		
		return valid;
	}
	
	/**
	 * Resolve a workspace-relative path to correct any problems in
	 * capitalization.
	 * @param projectName
	 * @param wsPath workspace-relative path
	 * @return resolved path, as far as actual resources exist (the suffix
	 * may be unchanged if an intervening folder is missing) 
	 */
	public static IPath getCanonicalWorkspacePath(IPath wsPath) {
		IPath resolvedPath = new Path(""); //$NON-NLS-1$
		IContainer container = ResourcesPlugin.getWorkspace().getRoot();
		// flag to consume the rest of the path without lookup
		boolean failed = false;
		for (String segment : wsPath.segments()) {
			if (failed || container == null) {
				resolvedPath = resolvedPath.append(segment);
				continue;
			}
			
			IResource child = container.findMember(segment);
			if (child != null) {
				// found, so correct caps
				resolvedPath = resolvedPath.append(segment);
				if (child instanceof IContainer) {
					container = (IContainer) child;
				} else {
					container = null;
				}
			} else {
				// not found: manually search
				boolean found = false;
				try {
					for (IResource member : container.members()) {
						if (member.getName().equalsIgnoreCase(segment)) {
							resolvedPath = resolvedPath.append(member.getName());
							found = true;
							if (member instanceof IContainer) {
								container = (IContainer) member;
							} else {
								container = null;
							}
							break;
						}
					}
				} catch (CoreException e) {
					// just fail
				}
				if (!found) {
					failed = true;
					resolvedPath = resolvedPath.append(segment);
				}
			}
		}
		return resolvedPath;
	}
	
	/**
	 * This is an analogue to new Path(String) but avoids some bugs in
	 * the implementation when pathString is a relative path.
	 * @param pathString
	 * @return new Path
	 */
	public static IPath createPossiblyRelativePath(String pathString) {
		// canonicalize slashes
		char[] pathChars = pathString.toCharArray();
		int offset = 0;
		for (int i = 0; i < pathChars.length; i++) {
			if (pathChars[i] == '\\')
				pathChars[i] = '/';
		}
		
		// The bug is, if "./" appears, then "../"'s after it are dropped,
		// so get rid of "./" early.  The result will still be relative.
		while (offset + 2 <= pathChars.length) { 
			if (pathChars[offset] == '.' && pathChars[offset+1] == '/') {
				offset += 2;
			} else {
				break;
			}
		}
		
		return new Path(new String(pathChars, offset, pathChars.length - offset));
	}

	/**
	 * Tell if the given path refers to a parent directory (e.g.
	 * contains ".." entries)
	 * @param path
	 * @return true if resolved path contains ".."
	 */
	public static boolean isPathInParent(IPath path) {
		// first see if any segments are ".." at all
		for (int i = 0; i < path.segmentCount(); i++) {
			if (path.segment(i).equals("..")) { //$NON-NLS-1$
				// comprehensive check, canonicalizing
				path = new Path(path.toString());
				if (path.segmentCount() > 0 && path.segment(0).equals("..")) { //$NON-NLS-1$
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * Recurse the directory, returning files that match the given filter.
	 * The directories "." and ".." are filtered out before the filter sees them.
	 * The filter is passed files and directories, and a directory must
	 * pass the filter to be recursed.
	 * @param dir
	 * @param filter
	 * @param recordDirectories if true, accepted directories are added to the returned list;
	 * otherwise, directories are not added to the file list.
	 * @return array of matching files , never null
	 */
	public static File[] listFilesInTree(File dir, FileFilter filter, boolean recordDirectories) {
		List<File> files = new ArrayList<File>();
		listFilesInTreeHelper(files, dir, filter, recordDirectories);
		return (File[]) files.toArray(new File[files.size()]);
	}

	private static void listFilesInTreeHelper(List<File> files, File dir, FileFilter filter, boolean recordDirectories) {
		if (recordDirectories)
			files.add(dir);
		
		File[] allEntries = dir.listFiles();
		if (allEntries == null)
			return;
		
		for (File fileOrDir : allEntries) {
			if (fileOrDir.getName().equals(".") || fileOrDir.getName().equals("..")) { //$NON-NLS-1$ //$NON-NLS-2$
				// ignore
			} else if (fileOrDir.isDirectory()) {
				if (filter.accept(fileOrDir)) {
					listFilesInTreeHelper(files, fileOrDir, filter, recordDirectories);
				}
			} else if (filter.accept(fileOrDir)) {
				files.add(fileOrDir);
			}
		}
	}
	
	public static URL getParentPathURL(URL url) throws CoreException {
		URL result;
		try {
			String string = url.toString();
			int loc = string.lastIndexOf('/');
			result = new URL(string.substring(0, loc + 1));
		} catch (Exception e) {
            throw new CoreException(Logging.newStatus(UtilsPlugin.getDefault(), e));
		}
		
		return result;
	}
	
	/**
	 * Wrapper around {@link FileUtils#validateEdit(IFile[], Shell)} 
	 * taking a single file. If a modified files multistatus is generated
	 * the single entry for the single file is retrieved and returned.
	 */
	public static IStatus validateEdit(IFile file, Shell context) {
		IStatus status = validateEdit(new IFile[] {file}, context);
		if (!status.isOK()) {
			if (status.isMultiStatus()) {
				IStatus[] children = status.getChildren();
				if (children != null && children.length == 1 &&
					children[0].getSeverity() == IStatus.WARNING &&
					children[0].getCode() == MODIFIED_FILES_STATUS) {
					status = children[0];
				}
			}
		}
		return status;
	}
	
	/**
	 * Wrapper around {@link IWorkspace#validateEdit(IFile[], Object)}
	 * Checks if any of the files have been modified by the validate
	 * operations. If so a MultiStatus is returned with WARNING severity. 
	 * The code for the multistatus and each child is MODIFIED_FILES_STATUS
	 * and for each child the message fields is the full workspace
	 * path to the modified file.
	 * @param files
	 * @param context a Shell if UI interaction is allowed
	 * @return OK status, non-OK status from validateEdit, or a
	 * MultiStatus with one child for each modified file.
	 */
	public static IStatus validateEdit(IFile[] files, Shell context) {
		List<IFile> readOnly = new ArrayList<IFile>();
		Map<IFile, Long> timeStamps = new HashMap<IFile, Long>();
		for (IFile file : files) {
			if (file.isReadOnly()) {
				readOnly.add(file);
				timeStamps.put(file, file.getModificationStamp());
			}
		}
	
		if (readOnly.isEmpty()) {
			return Status.OK_STATUS;
		}
			
		IStatus status = ResourcesPlugin.getWorkspace().validateEdit(
				readOnly.toArray(new IFile[readOnly.size()]), context);
		if (!status.isOK())
			return status;
			
		List<IFile> modified = new ArrayList<IFile>();
		for (Map.Entry<IFile, Long> entry : timeStamps.entrySet()) {
			Long newTimeStamp = entry.getKey().getModificationStamp();
			if (!newTimeStamp.equals(entry.getValue())) {
				modified.add(entry.getKey());
			}
		}
		
		if (modified.size() > 0) {
			String pluginID = UtilsPlugin.getDefault().getBundle().getSymbolicName();
			MultiStatus ms = new MultiStatus(pluginID, MODIFIED_FILES_STATUS, "modified files", null);
			for (IFile file : modified) {
				String path = file.getFullPath().toString();
				ms.add(new Status(IStatus.WARNING, pluginID, MODIFIED_FILES_STATUS, path, null));
			}
			status = ms;
		}
		
		return status;
	}
	
	/**
	 * Get the directory used for temporary files.
	 * @return absolute directory for temporary files, which may be the current directory
	 */
	public static File getTemporaryDirectory() {
		File dir = new File(System.getProperty("java.io.tmpdir", //$NON-NLS-1$
				System.getProperty("user.dir", "."))); //$NON-NLS-1$ //$NON-NLS-2$
		try {
			return dir.getCanonicalFile();
		} catch (IOException e) {
			return dir;
		}
	}
	
	/**
	 * Get the path in a form amenable to comparing with another path.
	 * On Win32 hosts, this lowercases the path.  On other hosts, the path is unmodified.
	 * @param path incoming path or <code>null</code>
	 * @return path converted to comparison-friendly format, or <code>null</code>
	 */
	public static IPath getComparablePath(IPath path) {
		if (path == null)
			return null;
		
		if (Platform.getOS().equals(Platform.OS_WIN32)) {
			return new Path(path.toOSString().toLowerCase());
		}
		
		return path;
	}

	/**
	 * Get the file extension from path, guaranteed not to return null
	 * @param path IPath
	 * @return String non-null
	 */
	public static String getSafeFileExtension(IPath path) {
		String fileExtension = ""; //$NON-NLS-1$
		if (path != null) {
			String temp = path.getFileExtension();
			if (temp != null)
				fileExtension = temp;
		}
		
		return fileExtension;
	}
	
	/**
	 * Uses java.io semantics to test existence, because eclipse resources are always case-sensitive, 
	 * regardless of the file system.
	 * @param resource {@link IResource}
	 * @return <code>boolean</code>
	 */
	public static boolean exists(IResource resource) {
		return resource.getLocation().toFile().exists();
	}
}
