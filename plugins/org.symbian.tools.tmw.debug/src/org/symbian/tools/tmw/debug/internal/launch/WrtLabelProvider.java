package org.symbian.tools.tmw.debug.internal.launch;

import org.chromium.debug.core.model.DebugTargetImpl;
import org.chromium.debug.core.model.JavascriptThread;
import org.chromium.debug.core.model.Messages;
import org.chromium.debug.core.model.StackFrame;
import org.chromium.debug.core.model.WorkspaceBridge.JsLabelProvider;
import org.chromium.sdk.CallFrame;
import org.chromium.sdk.DebugContext;
import org.chromium.sdk.ExceptionData;
import org.chromium.sdk.Script;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.DebugException;
import org.eclipse.osgi.util.NLS;

public class WrtLabelProvider implements JsLabelProvider {

    public String getStackFrameLabel(StackFrame stackFrame) throws DebugException {
        CallFrame callFrame = stackFrame.getCallFrame();
        String name = callFrame.getFunctionName();
        Script script = callFrame.getScript();
        if (script == null) {
            return "<unknown>";
        }
        Object element = stackFrame.getLaunch().getSourceLocator().getSourceElement(stackFrame);
        if (element instanceof IFile) {
            IFile resource = (IFile) element;
            int line = script.getStartLine() + stackFrame.getLineNumber();
            if (line != -1) {
                String resourcePath = resource != null ? resource.getProjectRelativePath().toString() : script
                        .getName();
                name = NLS.bind("{0} [{1}:{2}]", new Object[] { name, resourcePath, line });
            }
        } else if (element instanceof IFileStore) {
            return "(WRT System Library)";
        }
        return name;
    }

    public String getTargetLabel(DebugTargetImpl debugTarget) throws DebugException {
        return "WRT Runtime";
    }

    public String getThreadLabel(JavascriptThread thread) throws DebugException {
        return NLS.bind("JavaScript Thread ({0})", getThreadStateLabel(thread));
    }

    private String getThreadStateLabel(JavascriptThread thread) {
        if (thread.isSuspended()) {
            DebugContext context = thread.getDebugTarget().getDebugContext();
            ExceptionData exceptionData = context.getExceptionData();
            if (exceptionData != null) {
                return NLS.bind(Messages.JsThread_ThreadLabelSuspendedExceptionFormat,
                        exceptionData.getExceptionMessage());
            } else {
                return Messages.JsThread_ThreadLabelSuspended;
            }
        } else {
            return Messages.JsThread_ThreadLabelRunning;
        }
    }

}
