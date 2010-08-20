package org.symbian.tools.tmw.internal.ui.wizard;

import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhhint;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhspan;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdwhint;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.glmargins;
import static org.eclipse.wst.common.project.facet.ui.internal.util.SwtUtil.getPreferredWidth;
import static org.eclipse.wst.common.project.facet.ui.internal.util.SwtUtil.runOnDisplayThread;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.events.IProjectFacetsChangedEvent;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.common.project.facet.core.runtime.events.IRuntimeLifecycleEvent;
import org.eclipse.wst.common.project.facet.core.runtime.events.IRuntimeLifecycleListener;
import org.eclipse.wst.common.project.facet.ui.IDecorationsProvider;
import org.eclipse.wst.common.project.facet.ui.internal.FacetUiPlugin;
import org.symbian.tools.tmw.core.TMWCore;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@SuppressWarnings("restriction")
public final class FacetsSelectionPanel extends Composite implements ISelectionProvider {
    private final Composite topComposite;
    private final SashForm sform1;
    private final CheckboxTableViewer tableViewer;
    private final Table table;
    private final FixedFacetToolTip fixedFacetToolTip;
    private final TableViewer problemsView;
    private boolean showToolTips;
    private final IFacetedProjectWorkingCopy fpjwc;
    private final List<IFacetedProjectListener> registeredWorkingCopyListeners;
    private final Map<IProjectFacet, IProjectFacetVersion> selectedVersions;
    private final List<ISelectionChangedListener> selectionListeners;
    private Object selection;
    private final ImageRegistry imageRegistry;
    private final IRuntimeLifecycleListener runtimeLifecycleListener;
    private final Text description;
    private final Composite facets;

    public interface IFilter {
        boolean check(IProjectFacetVersion fv);
    }

    public FacetsSelectionPanel(final Composite parent, final IFacetedProjectWorkingCopy fpjwc) {
        super(parent, SWT.NONE);

        this.fpjwc = fpjwc;
        this.registeredWorkingCopyListeners = new ArrayList<IFacetedProjectListener>();
        this.selectedVersions = new HashMap<IProjectFacet, IProjectFacetVersion>();
        this.selection = null;
        this.selectionListeners = new ArrayList<ISelectionChangedListener>();
        this.showToolTips = false;

        // Initialize the image registry.

        this.imageRegistry = new ImageRegistry();
        // Layout the panel.

        setLayout(glmargins(new GridLayout(1, false), 0, 0));

        this.topComposite = new Composite(this, SWT.NONE);
        this.topComposite.setLayout(glmargins(new GridLayout(4, false), 0, 0));

        this.sform1 = new SashForm(this.topComposite, SWT.VERTICAL | SWT.SMOOTH);
        this.sform1.setLayoutData(gdhspan(gdfill(), 4));

        facets = new Composite(sform1, SWT.NONE);
        facets.setLayout(new GridLayout(2, false));
        this.table = new Table(facets, SWT.BORDER | SWT.CHECK);
        GridData gd = new GridData(GridData.FILL_VERTICAL);
        gd.widthHint = 250;
        table.setLayoutData(gd);
        description = new Text(facets, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.BORDER);
        gd = new GridData(GridData.FILL_BOTH);
        description.setLayoutData(gd);
        this.tableViewer = new CheckboxTableViewer(this.table);

        this.tableViewer.setLabelProvider(new FacetColumnLabelProvider());
        this.tableViewer.setContentProvider(new ContentProvider());
        this.tableViewer.setSorter(new Sorter());

        this.fixedFacetToolTip = new FixedFacetToolTip(this.table);

        this.tableViewer.setInput(new Object());

        this.tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent e) {
                FacetsSelectionPanel.this.handleSelectionChangedEvent();
            }
        });

        this.tableViewer.addCheckStateListener(new ICheckStateListener() {
            public void checkStateChanged(final CheckStateChangedEvent e) {
                FacetsSelectionPanel.this.handleCheckStateChanged(e);
            }
        });

        this.table.addListener(SWT.MouseDown, new Listener() {
            public void handleEvent(final Event event) {
                handleMouseDownEvent(event);
            }
        });

        this.problemsView = new TableViewer(this.sform1, SWT.BORDER);
        this.problemsView.setContentProvider(new ProblemsContentProvider());
        this.problemsView.setLabelProvider(new ProblemsLabelProvider());
        this.problemsView.setInput(new Object());

        addDisposeListener(new DisposeListener() {
            public void widgetDisposed(final DisposeEvent e) {
                handleDisposeEvent();
            }
        });

        Dialog.applyDialogFont(parent);

        // Setup runtime lifecycle listener.

        this.runtimeLifecycleListener = new IRuntimeLifecycleListener() {
            public void handleEvent(final IRuntimeLifecycleEvent event) {
                handleValidationProblemsChangedEvent();
            }
        };

        RuntimeManager
                .addListener(this.runtimeLifecycleListener, IRuntimeLifecycleEvent.Type.VALIDATION_STATUS_CHANGED);

        // Bind to the model.

        addWorkingCopyListener(new IFacetedProjectListener() {
            public void handleEvent(final IFacetedProjectEvent event) {
                handleProjectFacetsChangedEvent(event);
            }
        }, IFacetedProjectEvent.Type.PROJECT_FACETS_CHANGED);

        handleProjectFacetsChangedEvent(null);

        addWorkingCopyListener(new IFacetedProjectListener() {
            public void handleEvent(final IFacetedProjectEvent event) {
                handleValidationProblemsChangedEvent();
            }
        }, IFacetedProjectEvent.Type.VALIDATION_PROBLEMS_CHANGED, IFacetedProjectEvent.Type.PROJECT_MODIFIED);

        handleValidationProblemsChangedEvent();

        addWorkingCopyListener(new IFacetedProjectListener() {
            public void handleEvent(final IFacetedProjectEvent event) {
                handleModelChangedEvent(event);
            }
        }, IFacetedProjectEvent.Type.FIXED_FACETS_CHANGED, IFacetedProjectEvent.Type.SELECTED_PRESET_CHANGED,
                IFacetedProjectEvent.Type.TARGETED_RUNTIMES_CHANGED);

        // Set the preferred dimensions of the panel.

        final int prefWidthTree = getPreferredWidth(this.table);
        final int prefWidth = prefWidthTree + 80;

        this.topComposite.setLayoutData(gdwhint(gdhhint(gdfill(), 200), prefWidth));

        this.sform1.setWeights(new int[] { 70, 30 });

        // Select the first item in the table.

        if (this.table.getItemCount() > 0) {
            final TableItem firstItem = this.table.getItem(0);
            this.tableViewer.setSelection(new StructuredSelection(firstItem.getData()));
        }

        handleSelectionChangedEvent();
    }

    public IFacetedProjectWorkingCopy getFacetedProjectWorkingCopy() {
        return this.fpjwc;
    }

    public boolean isSelectionValid() {
        return (this.fpjwc.validate().getSeverity() != IStatus.ERROR);
    }

    public boolean setFocus() {
        return this.table.setFocus();
    }

    public void addSelectionChangedListener(final ISelectionChangedListener listener) {
        this.selectionListeners.add(listener);
    }

    public void removeSelectionChangedListener(final ISelectionChangedListener listener) {
        this.selectionListeners.remove(listener);
    }

    public ISelection getSelection() {
        if (this.selection != null) {
            return new StructuredSelection(this.selection);
        } else {
            return new StructuredSelection(new Object[0]);
        }
    }

    public void setSelection(final ISelection selection) {
        throw new UnsupportedOperationException();
    }

    private void notifySelectionChangedListeners() {
        final SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());

        for (ISelectionChangedListener listener : this.selectionListeners) {
            listener.selectionChanged(event);
        }
    }

    private ImageRegistry getImageRegistry() {
        return this.imageRegistry;
    }

    public Image getImage(final IProjectFacet facet, final boolean showDecorations) {
        final boolean isFixed = getFacetedProjectWorkingCopy().isFixedProjectFacet(facet);
        final String id = (isFixed && showDecorations ? "F:" : "f:") + facet.getId(); //$NON-NLS-1$ //$NON-NLS-2$

        Image image = getImageRegistry().get(id);

        if (image == null) {
            final IDecorationsProvider decprov = (IDecorationsProvider) facet.getAdapter(IDecorationsProvider.class);

            ImageDescriptor imgdesc = decprov.getIcon();

            if (isFixed && showDecorations) {
                imgdesc = new FixedFacetImageDescriptor(imgdesc);
            }

            getImageRegistry().put(id, imgdesc);
            image = getImageRegistry().get(id);
        }

        return image;
    }

    private void refresh() {
        // Somehow the checked state of nested items gets lost when a refresh
        // is performed, so we have to do this workaround.

        final Object[] checked = this.tableViewer.getCheckedElements();
        this.tableViewer.refresh();
        this.tableViewer.setCheckedElements(checked);
    }

    public boolean getShowToolTips() {
        return this.showToolTips;
    }

    public void setShowToolTips(final boolean showToolTips) {
        this.showToolTips = showToolTips;
    }

    private void addWorkingCopyListener(final IFacetedProjectListener listener,
            final IFacetedProjectEvent.Type... types) {
        this.fpjwc.addListener(listener, types);
        this.registeredWorkingCopyListeners.add(listener);
    }

    public IProjectFacet getSelectedProjectFacet() {
        final IProjectFacetVersion fv = getSelectedProjectFacetVersion();

        if (fv != null) {
            return fv.getProjectFacet();
        }

        return null;
    }

    public IProjectFacetVersion getSelectedProjectFacetVersion() {
        if (this.selection != null && this.selection instanceof IProjectFacetVersion) {
            return (IProjectFacetVersion) this.selection;
        }

        return null;
    }

    private IProjectFacetVersion getSelectedVersion(final IProjectFacet f) {
        final Set<IProjectFacetVersion> availableVersions = this.fpjwc.getAvailableVersions(f);

        if (availableVersions.isEmpty()) {
            throw new IllegalStateException();
        }

        IProjectFacetVersion selectedVersion = this.fpjwc.getProjectFacetVersion(f);

        if (selectedVersion == null) {
            selectedVersion = this.selectedVersions.get(f);

            if (selectedVersion == null) {
                selectedVersion = f.getDefaultVersion();
            }

            if (!availableVersions.contains(selectedVersion)) {
                selectedVersion = this.fpjwc.getHighestAvailableVersion(f);
            }
        }

        this.selectedVersions.put(f, selectedVersion);

        return selectedVersion;
    }

    private void handleSelectionChangedEvent() {
        Object selection = ((IStructuredSelection) this.tableViewer.getSelection()).getFirstElement();

        if (selection != null && selection instanceof IProjectFacet) {
            selection = getSelectedVersion((IProjectFacet) selection);
            description.setText(((IProjectFacetVersion) selection).getProjectFacet().getDescription());
        }

        if (selection != this.selection) {
            this.selection = selection;

            notifySelectionChangedListeners();
        }
    }

    private void handleCheckStateChanged(final CheckStateChangedEvent event) {
        final Object el = event.getElement();
        final boolean checked = event.getChecked();

        final IProjectFacet f = (IProjectFacet) el;

        if (this.fpjwc.getFixedProjectFacets().contains(f)) {
            if (!checked) {
                this.tableViewer.setChecked(el, true);

                final String msg = MessageFormat
                        .format("Project facet {0} cannot be deselected. It is critical to the proper function of this project.",
                                f.getLabel());

                this.fixedFacetToolTip.setMessage(msg);

                final Point cursorLocation = getDisplay().getCursorLocation();
                this.fixedFacetToolTip.show(this.table.toControl(cursorLocation));
            }

            return;
        }

        if (checked) {
            this.fpjwc.addProjectFacet(getSelectedVersion(f));
        } else {
            this.fpjwc.removeProjectFacet(f);
        }

        this.fpjwc.setSelectedPreset(null);
    }

    private void handleMouseDownEvent(final Event event) {
        handleMouseDownEventHelper(event, this.table.getItems());
    }

    private boolean handleMouseDownEventHelper(final Event event, final TableItem[] items) {
        for (TableItem item : items) {
            if (item.getBounds(1).contains(event.x, event.y)) {
                final TableItem[] newSelection = new TableItem[] { item };

                if (!Arrays.equals(this.table.getSelection(), newSelection)) {
                    this.table.setSelection(new TableItem[] { item });
                    this.tableViewer.editElement(item.getData(), 1);
                }

                return true;
            }
        }

        return false;
    }

    private void handleDisposeEvent() {
        this.imageRegistry.dispose();

        for (IFacetedProjectListener listener : this.registeredWorkingCopyListeners) {
            this.fpjwc.removeListener(listener);
        }

        RuntimeManager.removeListener(this.runtimeLifecycleListener);
    }

    private void handleProjectFacetsChangedEvent(final IFacetedProjectEvent event) {
        if (!Thread.currentThread().equals(getDisplay().getThread())) {
            final Runnable uiRunnable = new Runnable() {
                public void run() {
                    handleProjectFacetsChangedEvent(event);
                }
            };

            getDisplay().asyncExec(uiRunnable);

            return;
        }

        if (event != null) {
            final IFacetedProjectWorkingCopy fpjwc = event.getWorkingCopy();

            final IProjectFacetsChangedEvent evt = (IProjectFacetsChangedEvent) event;

            for (IProjectFacetVersion fv : evt.getAllAffectedFacets()) {
                final IProjectFacet f = fv.getProjectFacet();
                final boolean checked = fpjwc.hasProjectFacet(fv);

                this.tableViewer.setChecked(f, checked);
                this.tableViewer.update(f, null);
            }
        } else {
            final List<IProjectFacet> facets = new ArrayList<IProjectFacet>();

            for (IProjectFacetVersion fv : this.fpjwc.getProjectFacets()) {
                facets.add(fv.getProjectFacet());
            }

            this.tableViewer.setCheckedElements(facets.toArray());
            this.tableViewer.update(this.fpjwc.getAvailableFacets().keySet().toArray(), null);
        }
    }

    private void handleValidationProblemsChangedEvent() {
        if (!Thread.currentThread().equals(getDisplay().getThread())) {
            final Runnable uiRunnable = new Runnable() {
                public void run() {
                    handleValidationProblemsChangedEvent();
                }
            };

            getDisplay().asyncExec(uiRunnable);

            return;
        }

        this.problemsView.refresh();

        if (getFilteredProblems().length == 0) {
            if (this.sform1.getMaximizedControl() == null) {
                this.sform1.setMaximizedControl(facets);
            }
        } else {
            if (this.sform1.getMaximizedControl() != null) {
                this.sform1.setMaximizedControl(null);
            }
        }
    }

    private void handleModelChangedEvent(final IFacetedProjectEvent event) {
        switch (event.getType()) {
        case FIXED_FACETS_CHANGED:
        case TARGETED_RUNTIMES_CHANGED: {
            final Runnable runnable = new Runnable() {
                public void run() {
                    refresh();
                }
            };

            runOnDisplayThread(getDisplay(), runnable);

            break;
        }
        }
    }

    private IStatus[] getFilteredProblems() {
        final IStatus[] unfiltered = this.fpjwc.validate().getChildren();
        boolean somethingToRemove = false;

        for (IStatus st : unfiltered) {
            if (st.getCode() == IFacetedProjectWorkingCopy.PROBLEM_PROJECT_NAME) {
                somethingToRemove = true;
                break;
            }
        }

        if (!somethingToRemove) {
            return unfiltered;
        }

        final List<IStatus> filtered = new ArrayList<IStatus>();

        for (IStatus st : unfiltered) {
            if (st.getCode() != IFacetedProjectWorkingCopy.PROBLEM_PROJECT_NAME) {
                filtered.add(st);
            }
        }

        return filtered.toArray(new IStatus[filtered.size()]);
    }

    private final class ContentProvider

    implements ITreeContentProvider

    {
        public Object[] getElements(final Object element) {
            final IFacetedProjectWorkingCopy fpjwc = getFacetedProjectWorkingCopy();
            final List<Object> list = new ArrayList<Object>();
            for (Map.Entry<IProjectFacet, SortedSet<IProjectFacetVersion>> entry : fpjwc.getAvailableFacets()
                    .entrySet()) {
                final IProjectFacet f = entry.getKey();
                final SortedSet<IProjectFacetVersion> availableVersions = entry.getValue();

                if (f.getCategory() == null && !availableVersions.isEmpty()) {
                    list.add(f);
                }
            }

            return list.toArray();
        }

        public Object[] getChildren(final Object parent) {
            return new Object[0];
        }

        public Object getParent(final Object element) {
            if (element instanceof IProjectFacet) {
                final IProjectFacet f = (IProjectFacet) element;
                return f.getCategory();
            } else {
                return null;
            }
        }

        public boolean hasChildren(final Object element) {
            return false;
        }

        public void dispose() {
        }

        public void inputChanged(final Viewer viewer, final Object oldObject, final Object newObject) {
        }
    }

    private final class FacetColumnLabelProvider extends LabelProvider {
        @Override
        public String getText(final Object element) {
            return ((IProjectFacet) element).getLabel();
        }

        @Override
        public Image getImage(final Object element) {
            return FacetsSelectionPanel.this.getImage((IProjectFacet) element, true);
        }
    }

    private static final class FixedFacetImageDescriptor

    extends CompositeImageDescriptor

    {
        private static final String OVERLAY_IMG_LOCATION = "images/lock.gif"; //$NON-NLS-1$

        private static final ImageData OVERLAY = FacetUiPlugin.getImageDescriptor(OVERLAY_IMG_LOCATION).getImageData();

        private final ImageData base;
        private final Point size;

        public FixedFacetImageDescriptor(final ImageDescriptor base) {
            this.base = base.getImageData();
            this.size = new Point(this.base.width, this.base.height);
        }

        protected void drawCompositeImage(final int width, final int height) {
            drawImage(this.base, 0, 0);
            drawImage(OVERLAY, 0, height - OVERLAY.height);
        }

        protected Point getSize() {
            return this.size;
        }
    }

    private static final class Sorter extends ViewerSorter {
        public int compare(final Viewer viewer, final Object a, final Object b) {
            return getLabel(a).compareToIgnoreCase(getLabel(b));
        }

        private static String getLabel(final Object obj) {
            return ((IProjectFacet) obj).getLabel();
        }
    }

    private final class FixedFacetToolTip extends ToolTip {
        private static final int FAKE_EVENT_TYPE = -9999;

        private String message = ""; //$NON-NLS-1$

        @Override
        protected Composite createToolTipContentArea(final Event event, final Composite parent) {
            final Display display = parent.getDisplay();

            final Composite composite = new Composite(parent, SWT.NONE);
            composite.setLayout(gl(1));
            composite.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));

            final Label label = new Label(composite, SWT.WRAP);
            label.setLayoutData(gdwhint(gdfill(), 300));
            label.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
            label.setText(this.message);

            return composite;
        }

        public void setMessage(final String message) {
            this.message = message;
        }

        public FixedFacetToolTip(final Control control) {
            super(control);
            setPopupDelay(0);
            setShift(new Point(10, 3));
        }

        public void show(final Point location) {
            // The JFace ToolTip class does not support alternative methods of tool tip activation.
            // An enhancement request https://bugs.eclipse.org/bugs/show_bug.cgi?id=174844 tracks
            // this issue. When that enhancement request has been resolved, this hacky 
            // implementation should be replaced with something more sensible.

            final Event fakeEvent = new Event();
            fakeEvent.type = FAKE_EVENT_TYPE;
            fakeEvent.x = location.x;
            fakeEvent.y = location.y;

            try {
                final Method method = ToolTip.class.getDeclaredMethod("toolTipCreate", Event.class); //$NON-NLS-1$

                method.setAccessible(true);
                method.invoke(this, fakeEvent);
            } catch (Exception e) {
                TMWCore.log(null, e);
            }
        }

        @Override
        protected final boolean shouldCreateToolTip(final Event event) {
            return (event.type == FAKE_EVENT_TYPE);
        }
    }

    private final class ProblemsContentProvider implements IStructuredContentProvider {
        public Object[] getElements(final Object element) {
            return getFilteredProblems();
        }

        public void inputChanged(final Viewer viewer, final Object oldObject, final Object newObject) {
        }

        public void dispose() {
        }
    }

    private final class ProblemsLabelProvider implements ITableLabelProvider {
        public String getColumnText(final Object element, final int column) {
            return ((IStatus) element).getMessage();
        }

        public Image getColumnImage(final Object element, final int column) {
            final ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
            final String imageType;

            if (((IStatus) element).getSeverity() == IStatus.ERROR) {
                imageType = ISharedImages.IMG_OBJS_ERROR_TSK;
            } else {
                imageType = ISharedImages.IMG_OBJS_WARN_TSK;
            }

            return sharedImages.getImage(imageType);
        }

        public boolean isLabelProperty(final Object obj, final String s) {
            return false;
        }

        public void dispose() {
        }

        public void addListener(final ILabelProviderListener listener) {
        }

        public void removeListener(ILabelProviderListener listener) {
        }
    }
}
