package org.symbian.tools.wrttools.editing.preview;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.symbian.tools.wrttools.editing.Activator;
import org.symbian.tools.wrttools.editing.IWrtEditingPreferences;

public class PreviewView extends PageBookView {
	private static final class ChangedResourcesCollector implements
			IResourceDeltaVisitor {
		public final Collection<IFile> files = new HashSet<IFile>();

		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			if (isRelevantResource(delta.getResource())) {
				files.add((IFile) delta.getResource());
			}
			return true;
		}
	}

	public static boolean isRelevantResource(IResource resource) {
		return resource.getType() == IResource.FILE
				&& !resource.getFullPath().segment(1).equalsIgnoreCase(
						"preview");
	}

	private final IResourceChangeListener resourceListener = new IResourceChangeListener() {
		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			ChangedResourcesCollector visitor = new ChangedResourcesCollector();
			try {
				event.getDelta().accept(visitor);
			} catch (CoreException e) {
				Activator.log(e);
			}
			refreshPages(visitor.files);
		}
	};

	private Map<IProject, PreviewPage> projectToPage = new HashMap<IProject, PreviewPage>();
	private boolean preferencesLoaded = false;
	private final Map<IProject, Boolean> autorefresh = new HashMap<IProject, Boolean>();

	@Override
	protected IPage createDefaultPage(PageBook book) {
		MessagePage messagePage = new MessagePage();
		messagePage.setMessage("Open an editor to preview WRT widget");
		initPage(messagePage);
		messagePage.createControl(book);
		return messagePage;
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				resourceListener);
	}

	@Override
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(
				resourceListener);
		super.dispose();
	}

	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		// All checks we need were done in isImportant method
		IResource resource = (IResource) ((IEditorPart) part).getEditorInput()
				.getAdapter(IResource.class);

		IProject project = resource.getProject();
		PreviewPage page = projectToPage.get(project);

		if (page == null) {
			page = new PreviewPage(project, this);
			initPage(page);
			page.createControl(getPageBook());
			projectToPage.put(project, page);
		}

		return new PageRec(part, page);
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
		IPreferenceStore preferenceStore = Activator.getDefault()
				.getPreferenceStore();
		String value = preferenceStore
				.getString(IWrtEditingPreferences.PREF_AUTO_REFRESH);
		if (value == null || value.trim().length() == 0 || MessageDialogWithToggle.PROMPT.equals(value)) {
			return MessageDialogWithToggle
					.openYesNoQuestion(
							getSite().getShell(),
							"WRT Preview",
							"WRT IDE can refresh preview whenever any changes are made to project files. Refresh will always return the widget to initial page. Do you want to enable automatic refresh for your project?\nNote: you can toggle this setting for particular project on the preview toolbar.",
							"Keep this setting for all new projects", false,
							preferenceStore,
							IWrtEditingPreferences.PREF_AUTO_REFRESH)
					.getReturnCode() == IDialogConstants.YES_ID;
		} else {
			return MessageDialogWithToggle.ALWAYS.equals(value);
		}
	}

	private File getPreferencesFile() {
		return Activator.getDefault().getStateLocation().append(
				"autorefreshState.xml").toFile();
	}

	public boolean getProjectAutorefresh(IProject project) {
		synchronized (autorefresh) {
			loadPreferences();
			if (autorefresh.containsKey(project)) {
				return autorefresh.get(project);
			} else {
				boolean value = getDefaultAutorefresh(project);
				setProjectAutorefresh(project, value);
				return value;
			}
		}
	}

	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			IResource resource = (IResource) ((IEditorPart) part)
					.getEditorInput().getAdapter(IResource.class);
			if (resource != null) {
				return isWrtProject(resource.getProject());
			}
		}
		return false;
	}

	private boolean isWrtProject(IProject project) {
		return project.getFile("wrt_preview_frame.html").exists();
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
					Activator.log(e);
				} finally {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (IOException e) {
							Activator.log(e);
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
		Collection<PreviewPage> values = projectToPage.values();
		for (PreviewPage page : values) {
			page.process(files);
		}
	}

	public void setProjectAutorefresh(IProject project, boolean refresh) {
		synchronized (autorefresh) {
			autorefresh.put(project, refresh);
			Properties properties = new Properties();
			for (Entry<IProject, Boolean> entry : autorefresh.entrySet()) {
				properties.setProperty(entry.getKey().getName(), entry
						.getValue().toString());
			}
			File path = getPreferencesFile();
			OutputStream outputStream = null;
			try {
				outputStream = new BufferedOutputStream(new FileOutputStream(
						path));
				properties.storeToXML(outputStream, null);
			} catch (IOException e) {
				Activator.log(e);
			} finally {
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
						Activator.log(e);
					}
				}
			}
		}
	}

}
