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
package org.symbian.tools.wrttools.wizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.core.ProjectTemplate;
import org.symbian.tools.wrttools.core.WRTImages;
import org.symbian.tools.wrttools.core.libraries.JSLibrary;
import org.symbian.tools.wrttools.util.NonClosingStream;
import org.symbian.tools.wrttools.util.ProjectUtils;
import org.symbian.tools.wrttools.wizards.libraries.WRTProjectLibraryWizardPage;

public class WrtWidgetWizard extends Wizard implements INewWizard,
        IExecutableExtension {
    private WizardContext context;
    private DataBindingContext bindingContext;
    private final Map<ProjectTemplate, WRTProjectFilesWizardPage> templateDetails = new HashMap<ProjectTemplate, WRTProjectFilesWizardPage>();
    private WRTProjectTemplateWizardPage templatesPage;
    private WRTProjectDetailsWizardPage detailsPage;
    private IConfigurationElement config;
    private WRTProjectLibraryWizardPage librariesPage;

    public WrtWidgetWizard() {
        setDefaultPageImageDescriptor(WRTImages.newWizardBanner());
        setWindowTitle("New Mobile Web Application");
        setNeedsProgressMonitor(true);
    }

    public boolean performFinish() {
        final IProject[] holder = new IProject[1];
        final URI locationURI = detailsPage.getLocationURI();
        try {
            getContainer().run(true, true, new IRunnableWithProgress() {
                public void run(IProgressMonitor monitor)
                        throws InvocationTargetException, InterruptedException {
                    holder[0] = action(locationURI, monitor);
                }
            });
        } catch (InvocationTargetException e) {
            Activator.log(e);
        } catch (InterruptedException e) {
            Activator.log(e);
        }
        BasicNewProjectResourceWizard.updatePerspective(config);
        if (holder[0] != null) {
            ProjectUtils.focusOn(holder[0]);
        }
        return true;
    }

    protected IProject action(final URI locationURI, IProgressMonitor monitor) {
        final IProject[] holder = new IProject[1];
        try {
            ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
                public void run(IProgressMonitor monitor) throws CoreException {
                    holder[0] = createAndInitProject(locationURI, monitor);
                }
            }, monitor);
        } catch (CoreException e) {
            Activator.log(e);
        }
        return holder[0];
    }

    protected IProject createAndInitProject(URI locationURI,
            IProgressMonitor monitor) throws CoreException {
        monitor.beginTask("Creating project", 100);
        final IProject project = ProjectUtils.createWrtProject(context
                .getProjectName(), locationURI, new SubProgressMonitor(monitor,
                30));
        populateProject(project, new SubProgressMonitor(monitor, 30));
        try {
            initLibraries(project, context.getLibraries(),
                    new SubProgressMonitor(monitor, 40));
        } catch (IOException e) {
            throw new CoreException(new Status(IStatus.ERROR,
                    Activator.PLUGIN_ID, "Failed to setup libraries", e));
        }
        monitor.done();
        return project;
    }

    private void initLibraries(IProject project, Set<JSLibrary> set,
            IProgressMonitor progressMonitor) throws IOException, CoreException {
        if (set.isEmpty()) {
            progressMonitor.done();
            return;
        }
        progressMonitor.beginTask("Installing JS libraries", 100);
        int perContainer = 90 / set.size();
        for (JSLibrary library : set) {
            library.install(project, context.getLibraryParameters(library),
                    new SubProgressMonitor(progressMonitor, perContainer));
        }
        progressMonitor.done();
    }

    private void populateProject(IProject project, IProgressMonitor monitor)
            throws CoreException {
        URL projectContents = context.getTemplate().getProjectContents();
        Map<String, String> vars = context.getTemplateVars();

        ZipInputStream stream = null;
        try {
            Velocity.init();
            VelocityContext ctx = new VelocityContext(vars);
            stream = new ZipInputStream(projectContents.openStream());
            monitor.beginTask("Generating project contents",
                    IProgressMonitor.UNKNOWN);
            ZipEntry entry;
            while ((entry = stream.getNextEntry()) != null
                    && !monitor.isCanceled()) {
                String name = entry.getName();
                boolean isVelocity = name.endsWith(".velocitytemplate");
                if (isVelocity) {
                    name = name.substring(0, name.length()
                            - ".velocitytemplate".length());
                }
                if (name.startsWith("$")) {
                    int dotLocation = name.indexOf(".");
                    String template = name.substring(1,
                            dotLocation > 1 ? dotLocation : name.length());
                    if (vars.containsKey(template)) {
                        name = vars.get(template)
                                + name.substring(dotLocation > 1 ? dotLocation
                                        : name.length());
                    }
                }
                if (entry.isDirectory()) {
                    IFolder folder = project.getFolder(entry.getName());
                    folder.create(false, true, new SubProgressMonitor(monitor,
                            1));
                } else if (isVelocity) {
                    copyTemplate(project, name, stream, (int) entry.getSize(),
                            ctx, monitor);
                } else {
                    ProjectUtils.copyFile(project, name, stream,
                            entry.getSize(), monitor);
                }
                stream.closeEntry();
            }
            monitor.done();
        } catch (Exception e) {
            throw new CoreException(new Status(IStatus.ERROR,
                    Activator.PLUGIN_ID, "Project creation failed", e));
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // Ignore - something really bad happened
                }
            }
        }
    }

    private void copyTemplate(IProject project, String name,
            ZipInputStream stream, int size, VelocityContext ctx,
            IProgressMonitor monitor) throws IOException, CoreException {
        // Templates will not be more then a few megs - we can afford the memory
        ByteArrayOutputStream file = new ByteArrayOutputStream();

        Reader reader = new InputStreamReader(new NonClosingStream(stream));
        Writer writer = new OutputStreamWriter(file);

        Velocity.evaluate(ctx, writer, name, reader);

        reader.close();
        writer.close();

        ByteArrayInputStream contents = new ByteArrayInputStream(
                file.toByteArray());
        IFile f = project.getFile(name);
        f.create(contents, true, new SubProgressMonitor(monitor, 1));
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        context = new WizardContext();
        bindingContext = new DataBindingContext();
    }

    @Override
    public void addPages() {
        detailsPage = new WRTProjectDetailsWizardPage(context, bindingContext);
        addPage(detailsPage);

        templatesPage = new WRTProjectTemplateWizardPage(context,
                bindingContext);
        addPage(templatesPage);

        ProjectTemplate[] templates = ProjectTemplate.getAllTemplates();
        for (ProjectTemplate projectTemplate : templates) {
            final WRTProjectFilesWizardPage page = projectTemplate
                    .createWizardPage(context, bindingContext);
            addPage(page);
            templateDetails.put(projectTemplate, page);
        }

        librariesPage = new WRTProjectLibraryWizardPage(context, bindingContext);
        addPage(librariesPage);
    }

    @Override
    public boolean canFinish() {
        return super.canFinish()
                && getContainer().getCurrentPage() == getPages()[getPageCount() - 1];
    }

    @Override
    public IWizardPage getNextPage(IWizardPage page) {
        if (page == templatesPage) {
            ProjectTemplate template = context.getTemplate();
            if (template != null) {
                WRTProjectFilesWizardPage activePage = templateDetails
                        .get(template);
                for (WRTProjectFilesWizardPage wizardPage : templateDetails
                        .values()) {
                    wizardPage.setActive(wizardPage == activePage);
                }
                bindingContext.updateModels();
                return activePage;
            }
        }
        if (page instanceof WRTProjectFilesWizardPage) {
            return librariesPage;
        }
        return super.getNextPage(page);
    }

    public void setInitializationData(IConfigurationElement config,
            String propertyName, Object data) throws CoreException {
        this.config = config;
    }
}
