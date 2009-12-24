// Copyright (c) 2009 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.symbian.tools.wrttools.debug.internal.model;

import org.chromium.debug.core.model.IChromiumDebugTarget;
import org.chromium.sdk.CallFrame;
import org.chromium.sdk.Script;
import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.DebugException;
import org.eclipse.osgi.util.NLS;

/**
 * An IStackFrame implementation over a JsStackFrame instance.
 * 
 * Symbian branch is based on revision 261
 */
public class StackFrame extends org.chromium.debug.core.model.StackFrame {
	private final CallFrame stackFrame;

	/**
	 * Constructs a stack frame for the given handler using the FrameMirror data
	 * from the remote V8 VM.
	 * 
	 * @param debugTarget
	 *            the global parent
	 * @param thread
	 *            for which the stack frame is created
	 * @param stackFrame
	 *            an underlying SDK stack frame
	 */
	public StackFrame(IChromiumDebugTarget debugTarget,
			JavascriptThread thread, CallFrame stackFrame) {
		super(debugTarget, thread, stackFrame);
		this.stackFrame = stackFrame;
	}

	public String getName() throws DebugException {
		String name = stackFrame.getFunctionName();
		Script script = stackFrame.getScript();
		if (script == null) {
			return "<unknown>";
		}
		IFile resource = getDebugTarget().getResourceManager().getResource(
				script);
		int line = script.getStartLine() + getLineNumber();
		if (line != -1) {
			name = NLS.bind("{0} [{1}:{2}]", new Object[] { name,
					resource.getProjectRelativePath().toString(), line });
		}
		return name;
	}
}
