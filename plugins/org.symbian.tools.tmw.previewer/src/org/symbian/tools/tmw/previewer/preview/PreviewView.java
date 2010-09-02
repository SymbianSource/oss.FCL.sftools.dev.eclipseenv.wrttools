/*******************************************************************************
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
 *******************************************************************************/
package org.symbian.tools.tmw.previewer.preview;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.ITMWProject;
import org.symbian.tools.tmw.previewer.IEditingPreferences;
import org.symbian.tools.tmw.previewer.PreviewerPlugin;

public class PreviewView extends PageBookView {
    private final IResourceChangeListener resourceListener = new IResourceChangeListener() {
        public void resourceChanged(IResourceChangeEvent event) {
            if (event.getDelta() != null) {
                new RefreshJob(event.getDelta(), PreviewView.this).schedule();
            }
        }
    };

    private final Map<IProject, IPreviewPage> projectToPage = new HashMap<IProject, IPreviewPage>();
    private boolean preferencesLoaded = false;
    private final Map<IProject, Boolean> autorefresh = new HashMap<IProject, Boolean>();

    @Override
    protected IPage createDefaultPage(PageBook book) {
        MessagePage messagePage = new MessagePage();
        messagePage.setMessage("Open an editor to preview Mobile Web App");
        initPage(messagePage);
        messagePage.createControl(book);
        return messagePage;
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceListener);
    }

    @Override
    public void dispose() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceListener);
        super.dispose();
    }

    @Override
    protected PageRec doCreatePage(IWorkbenchPart part) {
        // All checks we need were done in isImportant method
        IResource resource = (IResource) ((IEditorPart) part).getEditorInput().getAdapter(IResource.class);

        IProject project = resource.getProject();
        IPreviewPage page = projectToPage.get(project);

        if (page == null || page.isDisposed()) {
            page = createPreviewPage(project);
            initPage(page);
            page.createControl(getPageBook());
            projectToPage.put(project, page);
        }

        return new PageRec(part, page);
    }

    private IPreviewPage createPreviewPage(IProject project) {
        if (Platform.getBundle(MozillaPreviewPage.XUL_RUNNER_BUNDLE) != null) {
            return new MozillaPreviewPage(project, this);
        } else {
            return new SwtBrowserPreviewPage(project, this);
        }
    }

    @Override
    protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
        // We do not need to delete the page
    }

    @Override
    protected IWorkbenchPart getBootstrapPart() {
        IEditorPart activeEditor = getSite().getPage().getActiveEditor();
        if (activeEditor != null) {
            if (isImportant(activeEditor)) {
                return activeEditor;
            }
        }
        return null;
    }

    private boolean getDefaultAutorefresh(IProject project) {
        IPreferenceStore preferenceStore = PreviewerPlugin.getDefault().getPreferenceStore();
        String value = preferenceStore.getString(IEditingPreferences.PREF_AUTO_REFRESH);
        boolean toggle = !MessageDialogWithToggle.NEVER.equals(value);
        if (MessageDialogWithToggle.NEVER.equals(value) || MessageDialogWithToggle.ALWAYS.equals(value)) {
            setProjectAutorefresh(project, toggle);
        }
        return toggle;
    }

    private File getPreferencesFile() {
        return PreviewerPlugin.getDefault().getStateLocation().append("autorefreshState.xml").toFile();
    }

    public boolean getProjectAutorefresh(IProject project) {
        synchronized (autorefresh) {
            loadPreferences();
            if (autorefresh.containsKey(project)) {
                return autorefresh.get(project);
            } else {
                boolean value = getDefaultAutorefresh(project);
                return value;
            }
        }
    }

    @Override
    protected boolean isImportant(IWorkbenchPart part) {
        if (part instanceof IEditorPart) {
            IResource resource = (IResource) ((IEditorPart) part).getEditorInput().getAdapter(IResource.class);
            if (resource != null) {
                final ITMWProject project = TMWCore.create(resource.getProject());
                if (project != null && project.getTargetRuntime() != null) {
                    return project.getTargetRuntime().getLayoutProvider() != null;
                }
            }
        }
        return false;
    }

    private void loadPreferences() {
        synchronized (autorefresh) {
            if (preferencesLoaded) {
                return;
            }
            preferencesLoaded = true;
            File preferencesFile = getPreferencesFile();
            Properties properties = new Properties();
            if (preferencesFile.exists()) {
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(preferencesFile);
                    properties.loadFromXML(inputStream);
                } catch (IOException e) {
                    PreviewerPlugin.log(e);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            PreviewerPlugin.log(e);
                        }
                    }
                }
                IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
                for (Entry<Object, Object> entry : properties.entrySet()) {
                    String projectName = entry.getKey().toString();
                    String value = entry.getValue().toString();
                    IProject project = root.getProject(projectName);
                    if (project.exists()) {
                        autorefresh.put(project, Boolean.valueOf(value));
                    }
                }
            }
        }
    }

    protected void refreshPages(Collection<IFile> files) {
        Collection<IPreviewPage> values = projectToPage.values();
        for (IPreviewPage page : values) {
            page.process(files);
        }
    }

    public void setProjectAutorefresh(IProject project, boolean refresh) {
        synchronized (autorefresh) {
            autorefresh.put(project, refresh);
            Properties properties = new Properties();
            for (Entry<IProject, Boolean> entry : autorefresh.entrySet()) {
                properties.setProperty(entry.getKey().getName(), entry.getValue().toString());
            }
            File path = getPreferencesFile();
            OutputStream outputStream = null;
            try {
                outputStream = new BufferedOutputStream(new FileOutputStream(path));
                properties.storeToXML(outputStream, null);
            } catch (IOException e) {
                PreviewerPlugin.log(e);
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        PreviewerPlugin.log(e);
                    }
                }
            }
        }
    }

    public boolean promptUserToToggle(IProject project, boolean toggle) {
        IPreferenceStore preferenceStore = PreviewerPlugin.getDefault().getPreferenceStore();
        String value = preferenceStore.getString(IEditingPreferences.PREF_AUTO_REFRESH);
        synchronized (autorefresh) {
            if (!autorefresh.containsKey(project)) {
                if (value == null || value.trim().length() == 0 || MessageDialogWithToggle.PROMPT.equals(value)) {
                    boolean setting = MessageDialogWithToggle.open(
                            MessageDialogWithToggle.QUESTION,
                            getSite().getShell(),
                            "Preview",
                            "The preview window can refresh (reinitialize and restart) whenever a project file is saved.\n"
                                    + "This setting for each project can be toggled from the preview toolbar.\n\n"
                                    + "Do you want to enable automatic refresh for this project?",
                            "Keep this setting for new projects", false, preferenceStore,
                            IEditingPreferences.PREF_AUTO_REFRESH, SWT.SHEET).getReturnCode() == IDialogConstants.YES_ID;
                    setProjectAutorefresh(project, setting);
                    return setting;
                }
            }
        }
        return toggle;
    }

    public synchronized void projectRenamed(IProject project, IPath newPath) {
        IPreviewPage page = projectToPage.remove(project);
        if (page != null) {
            Boolean refresh = autorefresh.remove(project);
            page.projectRenamed(newPath);
            projectToPage.put(page.getProject(), page);
            autorefresh.put(page.getProject(), refresh);
        }
    }

}
