package org.symbian.tools.wrttools.editing.preview;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.eclipse.wst.css.core.internal.provisional.contenttype.ContentTypeIdForCSS;
import org.eclipse.wst.html.core.internal.provisional.contenttype.ContentTypeIdForHTML;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.symbian.tools.wrttools.editing.Activator;

public class PreviewView extends PageBookView {
	private static final Set<String> CONTENT_TYPES = new TreeSet<String>(Arrays.asList(ContentTypeIdForCSS.ContentTypeID_CSS, ContentTypeIdForHTML.ContentTypeID_HTML, JavaScriptCore.JAVA_SOURCE_CONTENT_TYPE));
	
	private static final class ChangedResourcesCollector implements IResourceDeltaVisitor {
		public final Collection<IFile> files = new HashSet<IFile>();
		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			if (isRelevantResource(delta.getResource())) {
				files.add((IFile) delta.getResource());
			}
			return true;
		}
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
	
	@Override
	protected IPage createDefaultPage(PageBook book) {
		MessagePage messagePage = new MessagePage();
		messagePage.setMessage("Open editor to preview WRT widget");
		return messagePage;
	}

	protected void refreshPages(Collection<IFile> files) {
		Collection<PreviewPage> values = projectToPage.values();
		for (PreviewPage page : values) {
			page.process(files);
		}
	}

	public static boolean isRelevantResource(IResource resource) {
		if (resource.getType() == IResource.FILE) {
			IContentType contentType;
			try {
				contentType = ((IFile) resource).getContentDescription().getContentType();
				if (contentType != null) {
					return CONTENT_TYPES.contains(contentType.getId());
				}
			} catch (CoreException e) {
				Activator.log(e);
			}
		}
		return false;
	}

	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		// All checks we need were done in isImportant method
		IResource resource = (IResource) ((IEditorPart) part).getEditorInput().getAdapter(IResource.class);
		
		IProject project = resource.getProject();
		PreviewPage page = projectToPage.get(project);
		
		if (page == null) {
			page = new PreviewPage(project);
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

	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			IResource resource = (IResource) ((IEditorPart) part).getEditorInput().getAdapter(IResource.class);
			if (resource != null) {
				return isWrtProject(resource.getProject());
			}
		}
		return false;
	}

	private boolean isWrtProject(IProject project) {
		return project.getFile("wrt_preview_frame.html").exists();
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
}
