package org.symbian.tools.tmw.internal.ui.wizard;

import java.io.File;
import java.net.URI;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.viewers.IViewerObservableValue;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;
import org.eclipse.ui.internal.ide.dialogs.ProjectContentsLocationArea;
import org.eclipse.ui.internal.ide.dialogs.ProjectContentsLocationArea.IErrorMessageReporter;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;
import org.symbian.tools.tmw.ui.TMWCoreUI;

@SuppressWarnings("restriction")
public final class NewApplicationDetailsWizardPage extends WizardPage {
    public static final class ProjectNameValidator implements IValidator {

        public IStatus validate(Object value) {
            IWorkspace workspace = ResourcesPlugin.getWorkspace();

            String projectFieldContents = (String) value;
            IStatus nameStatus = workspace.validateName(projectFieldContents, IResource.PROJECT);
            if (!nameStatus.isOK()) {
                return nameStatus;
            }

            IProject handle = ResourcesPlugin.getWorkspace().getRoot().getProject(projectFieldContents);
            if (handle.exists()) {
                return new Status(IStatus.ERROR, TMWCoreUI.PLUGIN_ID,
                        IDEWorkbenchMessages.WizardNewProjectCreationPage_projectExistsMessage);
            }
            return Status.OK_STATUS;
        }

    }

    private ProjectContentsLocationArea locationArea;
    private final DataBindingContext bindingContext;
    private final WizardContext context;

    public NewApplicationDetailsWizardPage(WizardContext context, DataBindingContext bindingContext) {
        super("WRTApp");
        setImageDescriptor(null);
        setTitle("Application Details");
        setDescription("Specify application details");
        this.context = context;
        this.bindingContext = bindingContext;
    }

    public void createControl(Composite parent) {
        Composite root = new Composite(parent, SWT.NONE);

        initializeDialogUnits(parent);

        PlatformUI.getWorkbench().getHelpSystem().setHelp(root, IIDEHelpContextIds.NEW_PROJECT_WIZARD_PAGE);

        WizardPageSupport.create(this, bindingContext);
        root.setLayout(new GridLayout(2, false));
        createProjectNameGroup(root);

        createLabel(root, "");
        createLabel(root, "");
        createLabel(root, "Mobile Runtime:");

        ComboViewer viewer = new ComboViewer(root);
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                return ((IMobileWebRuntime) element).getName();
            }
        });
        viewer.setInput(TMWCore.getRuntimesManager().getAllRuntimes());

        final IViewerObservableValue observeSelection = ViewersObservables.observeSingleSelection(viewer);
        final IObservableValue observableValue = BeansObservables.observeValue(context, WizardContext.RUNTIME);
        bindingContext.bindValue(observeSelection, observableValue);

        createLabel(root, "");
        createLabel(root, "");
        createLabel(root, "Application identifier:");

        context.createText(root, WizardContext.WIDGET_ID, "applicatoin identifier", bindingContext, null,
                new RegexpValidator("[\\w]*(\\.\\w[\\w]*)*", "{0} is not a valid applicatoin ID", true));
        createLabel(root, "");
        createLabel(root, "This id should be unique for successful installation of application on the device");

        createLabel(root, "");
        createLabel(root, "");

        createLabel(root, "Application name:");

        context.createText(root, WizardContext.WIDGET_NAME, "application name", bindingContext, null,
                new RegexpValidator("[^\\w\\. ]", "Application name cannot contain {0} character", false));

        createLabel(root, "");
        createLabel(root, "This will be the application display name on the device");

        Composite composite = new Composite(root, SWT.NONE);
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginWidth = 0;
        composite.setLayout(gridLayout);
        composite.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, true, 2, 1));

        locationArea = new ProjectContentsLocationArea(getErrorReporter(), composite);
        if (context.getProjectName() != null && context.getProjectName().trim().length() > 0) {
            locationArea.updateProjectName(context.getProjectName());
        }

        // Scale the button based on the rest of the dialog
        setButtonLayoutData(locationArea.getBrowseButton());

        setPageComplete(validatePage());
        // Show description on opening
        setErrorMessage(null);
        setMessage(null);
        setControl(root);
        Dialog.applyDialogFont(root);
    }

    private void createLabel(Composite root, String string) {
        new Label(root, SWT.NONE).setText(string);
    }

    /**
     * Creates the project name specification controls.
     *
     * @param parent
     *            the parent composite
     */
    private void createProjectNameGroup(Composite parent) {
        // new project label
        Label projectLabel = new Label(parent, SWT.NONE);
        projectLabel.setText(IDEWorkbenchMessages.WizardNewProjectCreationPage_nameLabel);
        projectLabel.setFont(parent.getFont());

        Text projectNameField = context.createText(parent, WizardContext.PROJECT_NAME, "project name", bindingContext,
                null, new ProjectNameValidator());

        projectNameField.setFont(parent.getFont());

        projectNameField.addListener(SWT.Modify, nameModifyListener);
    }

    /**
     * Returns the current project location path as entered by the user, or its
     * anticipated initial value. Note that if the default has been returned the
     * path in a project description used to create a project should not be set.
     *
     * @return the project location path or its anticipated initial value.
     */
    public IPath getLocationPath() {
        return locationArea.isDefault() ? null : new Path(locationArea.getProjectLocation());
    }

    /**
     * /** Returns the current project location URI as entered by the user, or
     * <code>null</code> if a valid project location has not been entered.
     *
     * @return the project location URI, or <code>null</code>
     * @since 3.2
     */
    public URI getLocationURI() {
        return locationArea.getProjectLocationURI();
    }

    private final Listener nameModifyListener = new Listener() {
        public void handleEvent(Event e) {
            if (isPageComplete()) {
                setLocationForSelection();
                boolean valid = validatePage();
                setPageComplete(valid);
            }
        }
    };

    /**
     * Get an error reporter for the receiver.
     *
     * @return IErrorMessageReporter
     */
    private IErrorMessageReporter getErrorReporter() {
        return new IErrorMessageReporter() {
            /*
             * (non-Javadoc)
             *
             * @see
             * org.eclipse.ui.internal.ide.dialogs.ProjectContentsLocationArea
             * .IErrorMessageReporter#reportError(java.lang.String)
             */
            public void reportError(String errorMessage, boolean infoOnly) {
                if (infoOnly) {
                    setMessage(errorMessage, IStatus.INFO);
                    setErrorMessage(null);
                } else {
                    setErrorMessage(errorMessage);
                }
                boolean valid = errorMessage == null;
                if (valid) {
                    valid = validatePage();
                }

                setPageComplete(valid);
            }
        };
    }

    /**
     * Set the location to the default location if we are set to useDefaults.
     */
    void setLocationForSelection() {
        locationArea.updateProjectName(context.getProjectName());
    }

    /**
     * Returns whether this page's controls currently all contain valid values.
     *
     * @return <code>true</code> if all controls are valid, and
     *         <code>false</code> if at least one is invalid
     */
    protected boolean validatePage() {
        if (isPageComplete() || context.getProjectName().trim().length() == 0) {
            return false;
        }
        String projectName = context.getProjectName();
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        locationArea.setExistingProject(project);

        String validLocationMessage = locationArea.checkValidLocation();
        if (validLocationMessage != null) { // there is no destination location
                                            // given
            setErrorMessage(validLocationMessage);
            return false;
        }
        File file = new File(locationArea.getProjectLocationURI());
        if (file.isFile()) {
            setErrorMessage(String.format("%s is an existing file", file));
            return false;
        } else if (file.isDirectory() && file.listFiles().length > 0) {
            setErrorMessage(String.format("%s is a non-empty folder", file));
            return false;
        }

        setErrorMessage(null);
        setMessage(null);
        return true;
    }

    /**
     * Returns the useDefaults.
     *
     * @return boolean
     */
    public boolean useDefaults() {
        return locationArea.isDefault();
    }

    @Override
    public void setVisible(boolean visible) {
        // TODO Auto-generated method stub
        super.setVisible(visible);
        setErrorMessage(null);
    }
}
