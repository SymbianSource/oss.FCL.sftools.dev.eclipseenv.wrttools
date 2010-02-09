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
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
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
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.core.JsGlobalScopeContainerInitializer;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.core.ProjectTemplate;
import org.symbian.tools.wrttools.core.libraries.IWrtIdeContainer;
import org.symbian.tools.wrttools.util.NonClosingStream;
import org.symbian.tools.wrttools.util.ProjectUtils;

public class WrtWidgetWizard extends Wizard implements INewWizard, IExecutableExtension {
	private WizardNewProjectCreationPage resourcePage;
	private WizardContext context;
	private DataBindingContext bindingContext;
	private Map<ProjectTemplate, WRTProjectDetailsWizardPage> templateDetails = new HashMap<ProjectTemplate, WRTProjectDetailsWizardPage>();
	private WRTProjectTemplateWizardPage templatesPage;
	private WRTProjectFilesWizardPage filesPage;
	private IConfigurationElement config;

	public WrtWidgetWizard() {
		setWindowTitle("New WRT Widget");
		setNeedsProgressMonitor(true);
	}

	public boolean performFinish() {
		try {
			getContainer().run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					action(monitor);
				}
			});
		} catch (InvocationTargetException e) {
			Activator.log(e);
		} catch (InterruptedException e) {
			Activator.log(e);
		}
		BasicNewProjectResourceWizard.updatePerspective(config);
		return true;
	}

	protected void action(IProgressMonitor monitor) {
		try {
			ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					createAndInitProject(monitor);
				}
			}, monitor);
		} catch (CoreException e) {
			Activator.log(e);
		}
	}

	protected void createAndInitProject(IProgressMonitor monitor)
			throws CoreException {
		monitor.beginTask("Creating project", 100);
		IProject project = ProjectUtils.createWrtProject(context.getProjectName(), context.getProjectUri(), new SubProgressMonitor(monitor, 30));
		populateProject(project, new SubProgressMonitor(monitor, 30));
		try {
			initLibraries(project, context.getTemplate().getLibraryIds(), new SubProgressMonitor(monitor, 40));
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Failed to setup libraries", e));
		}
		monitor.done();
	}

	private void initLibraries(IProject project, String[] libraryIds, IProgressMonitor progressMonitor) throws IOException, CoreException {
		if (libraryIds.length == 0) {
			progressMonitor.done();
			return;
		}
		progressMonitor.beginTask("Installing JS libraries", 100);
		int perContainer = 90 / libraryIds.length;
		for (String string : libraryIds) {
			JsGlobalScopeContainerInitializer containerInitializer = JavaScriptCore.getJsGlobalScopeContainerInitializer(string);
			if (containerInitializer instanceof IWrtIdeContainer) {
				((IWrtIdeContainer) containerInitializer).populateProject(project, new SubProgressMonitor(progressMonitor, perContainer));
			}
		}
//		IJavaScriptProject js = JavaScriptCore.create(project);
//		IIncludePathEntry[] rawIncludepath = js.getRawIncludepath();
//		int preconfigured = rawIncludepath.length;
//		IIncludePathEntry[] newIncludepath = new IIncludePathEntry[preconfigured + libraryIds.length];
//		System.arraycopy(rawIncludepath, 0, newIncludepath, 0, preconfigured);
//		for (int i = 0; i < libraryIds.length; i++) {
//			String string = libraryIds[i];
//			IIncludePathEntry entry = JavaScriptCore.newContainerEntry(new Path(string));
//			newIncludepath[preconfigured + i] = entry;
//		}
//		js.setRawIncludepath(newIncludepath, new SubProgressMonitor(progressMonitor, 10));
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
					folder.create(false, true, new SubProgressMonitor(monitor, 1));
				} else if (isVelocity) {
					copyTemplate(project, name, stream, (int) entry.getSize(),
							ctx, monitor);
				} else {
					ProjectUtils.copyFile(project, name, stream, entry.getSize(), monitor);
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

		ByteArrayInputStream contents = new ByteArrayInputStream(file
				.toByteArray());
		IFile f = project.getFile(name);
		f.create(contents, true, new SubProgressMonitor(monitor, 1));
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		context = new WizardContext();
		bindingContext = new DataBindingContext();
	}

	@Override
	public void addPages() {
		resourcePage = new WizardNewProjectCreationPage("Resource");
		resourcePage
				.setDescription("Create a WRT widget project in the workspace or other location");
		resourcePage.setTitle("Create a New WRT Widget Project");
		addPage(resourcePage);

		templatesPage = new WRTProjectTemplateWizardPage(context,
				bindingContext);
		addPage(templatesPage);

		ProjectTemplate[] templates = ProjectTemplate.getAllTemplates();
		for (ProjectTemplate projectTemplate : templates) {
			WRTProjectDetailsWizardPage page = projectTemplate
					.createWizardPage(context, bindingContext);
			addPage(page);
			templateDetails.put(projectTemplate, page);
		}

		filesPage = new WRTProjectFilesWizardPage(context, bindingContext);
		addPage(filesPage);
	}

	@Override
	public boolean canFinish() {
		return super.canFinish()
				&& getContainer().getCurrentPage() == filesPage;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == resourcePage) {
			context.setProjectName(resourcePage.getProjectName());
			context.setProjectUri(resourcePage.getLocationURI());
		}
		if (page == templatesPage) {
			ProjectTemplate template = context.getTemplate();
			if (template != null) {
				WRTProjectDetailsWizardPage activePage = templateDetails
						.get(template);
				for (WRTProjectDetailsWizardPage wizardPage : templateDetails
						.values()) {
					wizardPage.setActive(wizardPage == activePage);
				}
				bindingContext.updateModels();
				return activePage;
			}
		}
		if (page instanceof WRTProjectDetailsWizardPage) {
			return filesPage;
		}
		return super.getNextPage(page);
	}

	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		this.config = config;
	}
}
