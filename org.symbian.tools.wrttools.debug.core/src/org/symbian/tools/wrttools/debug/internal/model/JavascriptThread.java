// Copyright (c) 2009 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.symbian.tools.wrttools.debug.internal.model;

import java.util.List;

import org.chromium.sdk.CallFrame;
import org.eclipse.debug.core.DebugException;
import org.eclipse.osgi.util.NLS;

/**
 * This class represents the only Chromium V8 VM thread.
 * 
 * Symbian branch is based on revision 234
 */
public class JavascriptThread extends
		org.chromium.debug.core.model.JavascriptThread {

	private static final StackFrame[] EMPTY_FRAMES = new StackFrame[0];

	/**
	 * Cached stack
	 */
	private StackFrame[] stackFrames;

	/**
	 * Constructs a new thread for the given target
	 * 
	 * @param debugTarget
	 *            this thread is created for
	 */
	public JavascriptThread(DebugTargetImpl debugTarget) {
		super(debugTarget);
	}

	public org.chromium.debug.core.model.StackFrame[] getStackFrames()
			throws DebugException {
		if (isSuspended()) {
			ensureStackFrames();
			return stackFrames;
		} else {
			return EMPTY_FRAMES;
		}
	}

	public void reset() {
		this.stackFrames = null;
	}

	private void ensureStackFrames() {
		this.stackFrames = wrapStackFrames(getDebugTarget().getDebugContext()
				.getCallFrames());
	}

	private StackFrame[] wrapStackFrames(List<? extends CallFrame> jsFrames) {
		StackFrame[] frames = new StackFrame[jsFrames.size()];
		for (int i = 0, size = frames.length; i < size; ++i) {
			frames[i] = new StackFrame(getDebugTarget(), this, jsFrames.get(i));
		}
		return frames;
	}

	public String getName() throws DebugException {
		return NLS.bind("JavaScript Thread ({0})",
				(isSuspended() ? "Suspended" : "Running")); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
