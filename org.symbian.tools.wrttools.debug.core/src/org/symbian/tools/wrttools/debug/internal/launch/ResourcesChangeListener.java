/**
 * Copyright (c) 2010 Symbian Foundation and/or its subsidiary(-ies).
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
package org.symbian.tools.wrttools.debug.internal.launch;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.symbian.tools.wrttools.debug.internal.Activator;
import org.symbian.tools.wrttools.debug.internal.IConstants;
import org.symbian.tools.wrttools.previewer.PreviewerUtil;

public class ResourcesChangeListener implements IResourceChangeListener {
    public void resourceChanged(IResourceChangeEvent event) {
        if (event.getDelta() != null
                && !MessageDialogWithToggle.ALWAYS.equals(Activator.getDefault().getPreferenceStore().getString(
                        IConstants.PREF_SHOW_RESOURCE_CHANGE_ERROR))) {
            IFile[] changes = PreviewerUtil.getWebChanges(event.getDelta());
            if (changes.length > 0 && isWrtResourceChanges(changes)) {
                Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                        String message = "Debug browser is not updated when the files are updated. You may notice discrepancies between your workspace contents and debug session. You should either refresh the browser or restart debugging session to see the latest changes made to the workspace.";
                        MessageDialogWithToggle.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                                .getShell(), "WebRuntime Debugger", message,
                                "Do not show this warning on code changes", false, Activator.getDefault()
                                        .getPreferenceStore(), IConstants.PREF_SHOW_RESOURCE_CHANGE_ERROR);
                        ResourcesPlugin.getWorkspace().removeResourceChangeListener(ResourcesChangeListener.this);
                    }
                });
            }
        }
    }

    private boolean isWrtResourceChanges(IFile[] changes) {
        Collection<IProject> projects = new HashSet<IProject>();
        ILaunch[] launches = DebugPlugin.getDefault().getLaunchManager().getLaunches();
        for (ILaunch launch : launches) {
            if (!launch.isTerminated()) {
                final ILaunchConfiguration config = launch.getLaunchConfiguration();
                try {
                    if (WidgetLaunchDelegate.ID.equals(config.getType().getIdentifier())) {
                        String projectName = config.getAttribute(IConstants.PROP_PROJECT_NAME, (String) null);
                        if (projectName != null && projectName.trim().length() > 0) {
                            IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
                            projects.add(project);
                        }
                    }
                } catch (CoreException e) {
                    Activator.log(e);
                }
            }
        }
        for (IFile file : changes) {
            if (projects.contains(file.getProject())) {
                return true;
            }
        }
        return false;
    }

    //    // TODO Progress indicator - ... files updated of ...
    //
    //    public void resourceChanged(IResourceChangeEvent event) {
    //        try {
    //            if (event.getDelta() != null) {
    //
    //                processDelta(event.getDelta());
    //            } else {
    //                // TODO Can't refresh
    //            }
    //        } catch (CoreException e) {
    //            Activator.log(e);
    //        }
    //
    //    }
    //
    //    private static final class DeltaProcessor implements IResourceDeltaVisitor {
    //        private final Map<IProject, DebugTargetImpl> targets;
    //        private final Map<IFile, Script> updateList = new HashMap<IFile, Script>();
    //        private boolean hasUnupdatables = false;
    //
    //        public DeltaProcessor(Map<IProject, DebugTargetImpl> targets) {
    //            this.targets = targets;
    //        }
    //
    //        public boolean visit(IResourceDelta delta) throws CoreException {
    //            final IResource resource = delta.getResource();
    //            switch (resource.getType()) {
    //            case IResource.PROJECT:
    //                return targets.containsKey(resource);
    //            case IResource.FILE:
    //                processFile((IFile) resource, targets.get(resource.getProject()));
    //                return false;
    //            default:
    //                return true;
    //            }
    //        }
    //
    //        private void processFile(final IFile resource, final DebugTargetImpl debugTargetImpl) throws CoreException {
    //            if (!ProjectUtils.isExcluded(resource)) {
    //                final VmResource vmResource = debugTargetImpl.getVmResource(resource);
    //                if (vmResource != null) {
    //                    updateList.put(resource, vmResource.getScript());
    //                } else {
    //                    if (!JavaScriptCore.isJavaScriptLikeFileName(resource.getName())) {
    //                        hasUnupdatables = true;
    //                    }
    //                }
    //            }
    //        }
    //
    //        public Map<IFile, Script> getUpdateList() {
    //            return updateList;
    //        }
    //
    //        public boolean hasUnupdatables() {
    //            return hasUnupdatables;
    //        }
    //    }
    //
    //    private void processDelta(final IResourceDelta delta) throws CoreException {
    //        final ILaunch[] launches = DebugPlugin.getDefault().getLaunchManager().getLaunches();
    //        final Map<IProject, DebugTargetImpl> targets = new HashMap<IProject, DebugTargetImpl>();
    //        for (ILaunch launch : launches) {
    //            final IDebugTarget target = launch.getDebugTarget();
    //            if (!launch.isTerminated() && target instanceof DebugTargetImpl) {
    //                IProject project = DebugUtil.getProject(launch);
    //                if (project != null) {
    //                    targets.put(project, (DebugTargetImpl) target);
    //                }
    //            }
    //        }
    //
    //        DeltaProcessor processor = new DeltaProcessor(targets);
    //        delta.accept(processor);
    //
    //        if (processor.getUpdateList().size() > 0) {
    //            final Collection<IStatus> statuses = new LinkedList<IStatus>();
    //            for (Entry<IFile, Script> entry : processor.getUpdateList().entrySet()) {
    //                statuses.add(updateScript(entry.getValue(), entry.getKey()));
    //            }
    //        } else if (processor.hasUnupdatables()) {
    //
    //        }
    //    }
    //
    //    private IStatus updateScript(Script script, final IFile file) {
    //        UpdatableScript updatableScript = LiveEditExtension.castToUpdatableScript(script);
    //
    //        if (updatableScript == null) {
    //            throw new RuntimeException();
    //        }
    //
    //        byte[] fileData;
    //        try {
    //            fileData = readFileContents(file);
    //        } catch (IOException e) {
    //            throw new RuntimeException(e);
    //        } catch (CoreException e) {
    //            throw new RuntimeException(e);
    //        }
    //        final IStatus[] res = new IStatus[1];
    //        // We are using default charset here like usually.
    //        String newSource = new String(fileData);
    //
    //        UpdatableScript.UpdateCallback callback = new UpdatableScript.UpdateCallback() {
    //            public void success(Object report) {
    //                res[0] = new Status(
    //                        IStatus.OK,
    //                        ChromiumDebugPlugin.PLUGIN_ID,
    //                        String.format(
    //                                "Script %s was successfully updated on remote: %s", file.getProjectRelativePath().toString(), report)); //$NON-NLS-1$
    //            }
    //
    //            public void failure(String message) {
    //                res[0] = new Status(IStatus.ERROR, ChromiumDebugPlugin.PLUGIN_ID, String.format(
    //                        "Script %s cannot be updated: %s", file.getProjectRelativePath().toString(), message)); //$NON-NLS-1$
    //            }
    //        };
    //
    //        updatableScript.setSourceOnRemote(newSource, callback, null);
    //        return res[0];
    //    }
    //
    //    private static byte[] readFileContents(IFile file) throws IOException, CoreException {
    //        InputStream inputStream = file.getContents();
    //        try {
    //            return readBytes(inputStream);
    //        } finally {
    //            inputStream.close();
    //        }
    //    }
    //
    //    private static byte[] readBytes(InputStream inputStream) throws IOException {
    //        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    //        byte[] array = new byte[1024];
    //        while (true) {
    //            int len = inputStream.read(array);
    //            if (len == -1) {
    //                break;
    //            }
    //            buffer.write(array, 0, len);
    //        }
    //        return buffer.toByteArray();
    //    }

}
