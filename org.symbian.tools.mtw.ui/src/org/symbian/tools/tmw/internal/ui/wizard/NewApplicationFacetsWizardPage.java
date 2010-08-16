package org.symbian.tools.tmw.internal.ui.wizard;

import java.util.Set;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.common.project.facet.core.runtime.events.IRuntimeLifecycleEvent;
import org.eclipse.wst.common.project.facet.core.runtime.events.IRuntimeLifecycleListener;
import org.eclipse.wst.common.project.facet.ui.FacetUiHelpContextIds;
import org.eclipse.wst.common.project.facet.ui.internal.FacetedProjectFrameworkImages;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@SuppressWarnings("restriction")
public final class NewApplicationFacetsWizardPage extends WizardPage {
    public FacetsSelectionPanel panel;
    private final IFacetedProjectWorkingCopy fpjwc;

    public NewApplicationFacetsWizardPage(final Set<IProjectFacetVersion> base, final IFacetedProjectWorkingCopy fpjwc) {
        super("facets.selection.page"); //$NON-NLS-1$

        setTitle("Project Facets");
        setDescription("Select the facets that should be enabled for this project.");
        setImageDescriptor(FacetedProjectFrameworkImages.BANNER_IMAGE.getImageDescriptor());

        this.fpjwc = fpjwc;
    }

    public void createControl(final Composite parent) {
        this.panel = new FacetsSelectionPanel(parent, this.fpjwc);

        updatePageState();

        this.fpjwc.addListener(new IFacetedProjectListener() {
            public void handleEvent(final IFacetedProjectEvent event) {
                updatePageState();
            }
        }, IFacetedProjectEvent.Type.PROJECT_MODIFIED);

        final IRuntimeLifecycleListener runtimeLifecycleListener = new IRuntimeLifecycleListener() {
            public void handleEvent(final IRuntimeLifecycleEvent event) {
                updatePageState();
            }
        };

        RuntimeManager.addListener(runtimeLifecycleListener, IRuntimeLifecycleEvent.Type.VALIDATION_STATUS_CHANGED);

        this.panel.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(final DisposeEvent e) {
                RuntimeManager.removeListener(runtimeLifecycleListener);
            }
        });

        final IWorkbenchHelpSystem h = PlatformUI.getWorkbench().getHelpSystem();
        h.setHelp(this.panel, FacetUiHelpContextIds.FACETS_SELECTION_PAGE);

        setControl(this.panel);
    }

    private void updatePageState() {
        if (!Thread.currentThread().equals(this.panel.getDisplay().getThread())) {
            final Runnable uiRunnable = new Runnable() {
                public void run() {
                    updatePageState();
                }
            };

            this.panel.getDisplay().asyncExec(uiRunnable);
            return;
        }

        setPageComplete(this.panel.isSelectionValid());

        if (getContainer().getCurrentPage() != null) {
            getContainer().updateButtons();
        }
    }

    public void setVisible(final boolean visible) {
        super.setVisible(visible);

        if (visible) {
            this.panel.setFocus();
        }
    }
}
