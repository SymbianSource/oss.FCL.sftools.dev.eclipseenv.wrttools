package org.symbian.tools.wrttools.debug.internal.launch;

import org.chromium.debug.core.model.DebugTargetImpl;
import org.chromium.debug.core.model.JavascriptThread;
import org.chromium.debug.core.model.StackFrame;
import org.chromium.debug.core.model.WorkspaceBridge.JsLabelProvider;
import org.chromium.sdk.CallFrame;
import org.chromium.sdk.Script;
import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.DebugException;
import org.eclipse.osgi.util.NLS;

public class WrtLabelProvider implements JsLabelProvider {

	public String getStackFrameLabel(StackFrame stackFrame)
			throws DebugException {
		CallFrame callFrame = stackFrame.getCallFrame();
		String name = callFrame.getFunctionName();
		Script script = callFrame.getScript();
		if (script == null) {
			return "<unknown>";
		}
		IFile resource = stackFrame.getDebugTarget().getScriptResource(script);
		int line = script.getStartLine() + stackFrame.getLineNumber();
		if (line != -1) {
			String resourcePath = resource != null ? resource
					.getProjectRelativePath().toString() : script.getName();
			name = NLS.bind("{0} [{1}:{2}]", new Object[] { name, resourcePath,
					line });
		}
		return name;
	}

	public String getTargetLabel(DebugTargetImpl debugTarget)
			throws DebugException {
		return "WRT Runtime";
	}

	public String getThreadLabel(JavascriptThread thread) throws DebugException {
		return NLS.bind("JavaScript Thread ({0})",
				(thread.isSuspended() ? "Suspended" : "Running")); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
