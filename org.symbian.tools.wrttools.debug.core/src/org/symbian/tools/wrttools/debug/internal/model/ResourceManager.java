// Copyright (c) 2009 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.symbian.tools.wrttools.debug.internal.model;

import java.util.HashMap;
import java.util.Map;

import org.chromium.debug.core.model.IResourceManager;
import org.chromium.sdk.Script;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.symbian.tools.wrttools.previewer.PreviewerPlugin;

/**
 * This object handles the mapping between {@link Script}s and their
 * corresponding resources inside Eclipse.
 * 
 * Symbian branch is currently based on Revision 138
 */
public class ResourceManager implements IResourceManager {
	/**
	 * Script identifier for a breakpoint location.
	 */
	public static class ScriptIdentifier {
		public static ScriptIdentifier forScript(Script script) {
			String name = script.getName();
			return new ScriptIdentifier(name, name != null ? -1 : script
					.getId(), script.getStartLine(), script.getEndLine());
		}

		private final int endLine;
		private final long id;
		private final String name;
		private final int startLine;

		private ScriptIdentifier(String name, long id, int startLine,
				int endLine) {
			this.name = name;
			this.id = id;
			this.startLine = startLine;
			this.endLine = endLine;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ScriptIdentifier)) {
				return false;
			}
			ScriptIdentifier that = (ScriptIdentifier) obj;
			if (this.startLine != that.startLine
					|| this.endLine != that.endLine) {
				return false;
			}
			if (name == null) {
				// an unnamed script, only id is known
				return that.name == null && this.id == that.id;
			}
			// a named script
			return this.name.equals(that.name);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (id ^ (id >>> 32));
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + 17 * startLine + 19 * endLine;
			return result;
		}
	}

	private Object fileBeingAdded;
	private final Map<IFile, Script> resourceToScript = new HashMap<IFile, Script>();
	private final Map<ScriptIdentifier, IFile> scriptIdToResource = new HashMap<ScriptIdentifier, IFile>();

	public synchronized void addScript(Script script) {
		IFile scriptFile = getResource(script);
		if (scriptFile == null) {
			scriptFile = getFile(script.getName());
			if (scriptFile != null) {
				fileBeingAdded = scriptFile;
				try {
					putScript(script, scriptFile);
				} finally {
					fileBeingAdded = null;
				}
			}
		}
	}

	public synchronized void clear() {
		resourceToScript.clear();
		scriptIdToResource.clear();
	}

	private IFile getFile(String name) {
		if (name == null) {
			return null;
		}
		IFile file = PreviewerPlugin.getDefault().getHttpPreviewer().getFileFromUrl(name);
		if (file != null && !file.isAccessible()) {
			file = null;
		}
		return file;
	}

	public synchronized IFile getResource(Script script) {
		return scriptIdToResource.get(ScriptIdentifier.forScript(script));
	}

	public synchronized Script getScript(IFile resource) {
		return resourceToScript.get(resource);
	}

	/**
	 * @return whether the given file is being added to the target project
	 */
	public boolean isAddingFile(IFile file) {
		return file.equals(fileBeingAdded);
	}

	public synchronized void putScript(Script script, IFile resource) {
		ScriptIdentifier scriptId = ScriptIdentifier.forScript(script);
		resourceToScript.put(resource, script);
		scriptIdToResource.put(scriptId, resource);
	}

	public synchronized boolean scriptHasResource(Script script) {
		return getResource(script) != null;
	}

	public String translateResourceToScript(IResource resource) {
		return PreviewerPlugin.getDefault().getHttpPreviewer().getHttpUrl(resource);
	}
}
