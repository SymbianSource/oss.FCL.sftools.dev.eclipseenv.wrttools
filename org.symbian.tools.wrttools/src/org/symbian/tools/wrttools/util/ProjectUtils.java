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
package org.symbian.tools.wrttools.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.filechooser.FileSystemView;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.wizards.datatransfer.TarEntry;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.wst.jsdt.core.IIncludePathEntry;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.core.LibrarySuperType;
import org.eclipse.wst.jsdt.internal.core.JavaProject;
import org.eclipse.wst.jsdt.internal.ui.wizards.buildpaths.BuildPathsBlock;
import org.eclipse.wst.validation.ValidationFramework;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.WidgetProjectNature;
import org.symbian.tools.wrttools.core.packager.WRTPackagerConstants;
import org.symbian.tools.wrttools.wizards.WrtLibraryWizardPage;

@SuppressWarnings("restriction")
public class ProjectUtils {
    public static final String EXCLUDE_MARKER_ID = "org.symbian.tools.wrttools.excluded";

    private static final class FocusOnProjectJob extends Job {

        private final Display display;
        private final IProject[] projects;

        public FocusOnProjectJob(IProject[] projects, Display display) {
            super("Preparing projects");
            this.projects = projects;
            this.display = display;
            setRule(this.projects.length == 1 ? projects[0] : projects[0].getWorkspace().getRoot());
            setUser(false);
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            try {
                final Collection<IFile> files = new HashSet<IFile>(projects.length);
                for (IProject project : projects) {
                    String file = CoreUtil.getIndexFile(project);
                    if (file != null) {
                        IFile index = project.getFile(file);
                        if (index.isAccessible()) {
                            files.add(index);
                        }
                    }
                }
                final IFile[] filesArray = files.toArray(new IFile[files.size()]);
                if (filesArray.length > 0) {
                    display.asyncExec(new Runnable() {

                        public void run() {
                            IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                                    .getActivePage();
                            IViewReference reference = activePage.findViewReference(IPageLayout.ID_PROJECT_EXPLORER);
                            IWorkbenchPart part = reference.getPart(false);
                            if (part instanceof ISetSelectionTarget) {
                                StructuredSelection selection;
                                if (filesArray.length == 1) {
                                    selection = new StructuredSelection(filesArray[0]);
                                } else {
                                    selection = new StructuredSelection(filesArray);
                                }
                                ((ISetSelectionTarget) part).selectReveal(selection);
                            }
                            try {
                                IDE.openEditors(activePage, filesArray);
                                ResourcesPlugin.getWorkspace().run(new TouchAllResources(projects),
                                        new NullProgressMonitor());
                            } catch (CoreException e) {
                                Activator.log(e);
                            }
                        }
                    });
                }
            } catch (CoreException e) {
                Activator.log(e);
            }
            return Status.OK_STATUS;
        }
    }

    private static final class TouchAllResources implements IWorkspaceRunnable {
        private final IProject[] projects;

        public TouchAllResources(IProject[] projects) {
            this.projects = projects;

        }

        public void run(IProgressMonitor monitor) throws CoreException {
            for (IProject project : projects) {
                project.accept(new IResourceVisitor() {
                    public boolean visit(IResource resource) throws CoreException {
                        if (resource.isAccessible() && resource.getType() == IResource.FILE
                                && resource.getFileExtension().equals("js")) {
                            resource.touch(new NullProgressMonitor());
                        }
                        return true;
                    }
                });
            }
        }
    }

    private static final String DEFAULT_APTANA_WORKSPACE = "Aptana Studio Workspace";
    private static final Collection<String> EXCLUDED;

    public static final String PREVIEW_FOLDER = "preview";

    public static final String PREVIEW_FRAME_FILE = "wrt_preview_frame.html";

    public static final String PREVIEW_MAIN_FILE = "wrt_preview_main.html";

    static {
        EXCLUDED = new TreeSet<String>(Arrays.asList(".project", ProjectUtils.PREVIEW_FOLDER,
                ProjectUtils.PREVIEW_FRAME_FILE, ProjectUtils.PREVIEW_MAIN_FILE));
    }

    private static boolean accepted(Object object) {
        final String name;
        if (object instanceof ZipEntry) {
            name = ((ZipEntry) object).getName();
        } else if (object instanceof TarEntry) {
            name = ((TarEntry) object).getName();
        } else if (object instanceof File) {
            name = ((File) object).getAbsolutePath();
        } else {
            throw new IllegalArgumentException("Unforeseen entry type: " + object.getClass());
        }
        IPath path = new Path(name);
        return isValidProjectFile(path.lastSegment());
    }

    public static void addWrtNature(IProject project) {
        if (!hasWrtNature(project)) {
            try {
                IProjectDescription description = project.getDescription();
                String[] natureIds = description.getNatureIds();
                String[] newNatures = new String[natureIds.length + 1];
                System.arraycopy(natureIds, 0, newNatures, 1, natureIds.length);
                newNatures[0] = WidgetProjectNature.ID;
                description.setNatureIds(newNatures);

                ICommand[] buildSpec = description.getBuildSpec();
                for (int i = 0; i < buildSpec.length; i++) {
                    ICommand command = buildSpec[i];
                    if (JavaScriptCore.BUILDER_ID.equals(command.getBuilderName())) {
                        buildSpec[i] = buildSpec[buildSpec.length - 1];
                        buildSpec[buildSpec.length - 1] = command;
                        description.setBuildSpec(buildSpec);
                        break;
                    }
                }

                project.setDescription(description, new NullProgressMonitor());
            } catch (CoreException e) {
                Activator.log(e);
            }
        }
    }

    private static void checkParent(IContainer parent) throws CoreException {
        if (parent.getType() == IResource.FOLDER && !parent.exists()) {
            checkParent(parent.getParent());
            ((IFolder) parent).create(false, true, new NullProgressMonitor());
        }
    }

    public static void copyFile(IProject project, String name, ZipInputStream stream, long size,
            IProgressMonitor monitor) throws CoreException, IOException {
        IFile file = project.getFile(name);
        file.create(new NonClosingStream(stream), true, new SubProgressMonitor(monitor, 1));
    }

    public static IProject createWrtProject(String name, URI uri, IProgressMonitor monitor) throws CoreException {
        uri = isDefaultProjectLocation(uri) ? null : uri;
        monitor.beginTask("Create project resources", 30);
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IProject project = workspace.getRoot().getProject(name);
        BuildPathsBlock.createProject(project, uri, new SubProgressMonitor(monitor, 10));
        BuildPathsBlock.addJavaNature(project, new SubProgressMonitor(monitor, 10));

        ValidationFramework.getDefault().addValidationBuilder(project);
        ValidationFramework.getDefault().applyChanges(ValidationFramework.getDefault().getProjectSettings(project),
                true);

        IJavaScriptProject jsProject = JavaScriptCore.create(project);
        jsProject.setRawIncludepath(null, new SubProgressMonitor(monitor, 5)); // See bug 3037
        final IIncludePathEntry[] includepath = jsProject.getRawIncludepath();
        final IIncludePathEntry[] newIncludePath = new IIncludePathEntry[includepath.length + 1];

        System.arraycopy(includepath, 0, newIncludePath, 0, includepath.length);
        newIncludePath[includepath.length] = JavaScriptCore.newContainerEntry(new Path(
                WrtLibraryWizardPage.CONTAINER_ID));

        jsProject.setRawIncludepath(newIncludePath, new SubProgressMonitor(monitor, 5));

        LibrarySuperType superType = new LibrarySuperType(new Path(WrtLibraryWizardPage.CONTAINER_ID), jsProject,
                "Window");
        ((JavaProject) jsProject).setCommonSuperType(superType);

        addWrtNature(project);

        excludeResources(project);

        monitor.done();
        return project;
    }

    private static void excludeResources(IProject project) {
        IFile file = project.getFile(IProjectDescription.DESCRIPTION_FILE_NAME);
        if (file.exists()) {
            exclude(file);
        }
        IFolder settings = project.getFolder(".settings");
        if (settings.exists()) {
            excludeFolder(settings);
            exclude(settings);
        }
    }

    private static void excludeFolder(IFolder folder) {
        exclude(folder);
        try {
            IResource[] members = folder.members();
            for (IResource resource : members) {
                if (resource.getType() == IResource.FOLDER) {
                    excludeFolder((IFolder) resource);
                } else {
                    exclude(resource);
                }
            }
        } catch (CoreException e) {
            Activator.log(e);
        }
    }

    public static List<Object> filterExternalProjectEntries(List<Object> fileSystemObjects) {
        List<Object> result = new LinkedList<Object>();
        for (Object object : fileSystemObjects) {
            if (accepted(object)) {
                result.add(object);
            }
        }
        return result;
    }

    public static void focusOn(IProject... projects) {
        new FocusOnProjectJob(projects, Display.getCurrent()).schedule(50);
    }

    public static String getDefaultAptanaLocation() {
        File myDocuments = FileSystemView.getFileSystemView().getDefaultDirectory();
        File file = new File(myDocuments, DEFAULT_APTANA_WORKSPACE); // Windows
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        file = new File(myDocuments, "Documents" + File.separator + DEFAULT_APTANA_WORKSPACE); // Mac OS X
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        return "";
    }

    public static boolean hasWrtNature(IProject project) {
        try {
            return project.hasNature(WidgetProjectNature.ID);
        } catch (CoreException e) {
            Activator.log(e);
            return false;
        }
    }

    public static File isWrtProject(File[] contents) {
        for (int i = 0; i < contents.length; i++) {
            File file = contents[i];
            if (file.isFile() && file.getName().equalsIgnoreCase(CoreUtil.METADATA_FILE)) {
                return file.getParentFile();
            }
        }
        return null;
    }

    private static boolean isDefaultProjectLocation(URI uri) {
        if (uri == null) {
            return true;
        }
        File file = new File(uri);
        IPath project = new Path(file.getAbsolutePath());
        IPath workspace = ResourcesPlugin.getWorkspace().getRoot().getLocation();
        return workspace.isPrefixOf(project);
    }

    public static boolean isPlist(IResource resource) {
        return resource.getType() == IResource.FILE && resource.getName().equalsIgnoreCase(CoreUtil.METADATA_FILE);
    }

    private static boolean isValidProjectFile(String fileName) {
        return !EXCLUDED.contains(fileName);
    }

    public static void unzip(String archiveFile, IContainer location, int trimSegments, IProgressMonitor progressMonitor)
            throws IOException, CoreException {
        unzip(new FileInputStream(archiveFile), location, trimSegments, archiveFile, progressMonitor);
    }

    public static void unzip(InputStream in, IContainer location, int trimSegments, String label,
            IProgressMonitor progressMonitor) throws IOException, CoreException {
        progressMonitor.beginTask(MessageFormat.format("Unpacking {0}", label), IProgressMonitor.UNKNOWN);
        ZipInputStream stream = new ZipInputStream(in);
        try {
            ZipEntry nextEntry;
            int count = 0;
            while ((nextEntry = stream.getNextEntry()) != null) {
                count++;
                IPath p = new Path(nextEntry.getName()).removeFirstSegments(trimSegments);
                if (!isIgnored(p) && !nextEntry.isDirectory()) {
                    IFile file = location.getFile(p);
                    checkParent(file.getParent());
                    if (file.exists()) {
                        file.setContents(new NonClosingStream(stream), false, true, new SubProgressMonitor(
                                progressMonitor, 1));
                    } else {
                        file.create(new NonClosingStream(stream), true, new SubProgressMonitor(progressMonitor, 1));
                    }
                }
            }
            if (count == 0) {
                throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                        "Selected archive file does not contain application files"));
            }
        } finally {
            stream.close();
        }
        progressMonitor.done();
    }

    private static boolean isIgnored(IPath p) {
        if (p.segmentCount() == 1) {
            return IProjectDescription.DESCRIPTION_FILE_NAME.equals(p.lastSegment());
        }
        return false;
    }

    public static boolean isExcluded(IResource resource) {
        if (!resource.exists()) {
            return false;
        }
        try {
            IMarker[] markers = resource
                    .findMarkers(EXCLUDE_MARKER_ID, false, IResource.DEPTH_ZERO);
            IPath path = resource.getProjectRelativePath();
            return markers.length != 0 || (path.segmentCount() > 1 && ".settings".equals(path.segment(0)));
        } catch (CoreException e) {
            Activator.log(e);
            return false;
        }

    }

    public static void exclude(IResource resource) {
        try {
            resource.createMarker(EXCLUDE_MARKER_ID);
            resource.setPersistentProperty(WRTPackagerConstants.EXCLUDE_PROPERTY, Boolean.TRUE.toString());
        } catch (CoreException e) {
            Activator.log(e);
        }
    }

    public static void include(IResource resource) {
        try {
            IMarker[] markers = resource
                    .findMarkers(EXCLUDE_MARKER_ID, false, IResource.DEPTH_ZERO);
            resource.setPersistentProperty(WRTPackagerConstants.EXCLUDE_PROPERTY, null);
            for (IMarker marker : markers) {
                marker.delete();
            }
        } catch (CoreException e) {
            Activator.log(e);
        }
    }
}
