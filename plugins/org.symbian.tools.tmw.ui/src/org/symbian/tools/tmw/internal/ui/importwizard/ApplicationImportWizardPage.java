package org.symbian.tools.tmw.internal.ui.importwizard;

import java.io.File;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.dialogs.ProjectContentsLocationArea;
import org.eclipse.ui.internal.ide.dialogs.ProjectContentsLocationArea.IErrorMessageReporter;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;
import org.symbian.tools.tmw.ui.TMWCoreUI;
import org.symbian.tools.tmw.ui.project.IApplicationImporter;

@SuppressWarnings("restriction")
public class ApplicationImportWizardPage extends WizardPage {
    public class MapContentProvider implements IStructuredContentProvider {

        public void dispose() {
            // Do nothing
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            // Do nothing
        }

        @SuppressWarnings("unchecked")
        public Object[] getElements(Object inputElement) {
            return ((Map<String, IApplicationImporter>) inputElement).entrySet().toArray();
        }

    }

    // constants
    private static final int SIZING_TEXT_FIELD_WIDTH = 250;
    private final IFile file;
    // initial value stores
    private String initialProjectFieldValue;
    private ProjectContentsLocationArea locationArea;
    private final Listener nameModifyListener = new Listener() {
        public void handleEvent(Event e) {
            setLocationForSelection();
            boolean valid = validatePage();
            setPageComplete(valid);

        }
    };
    // widgets
    private Text projectNameField;
    private Text fileName;
    private ComboViewer runtimes;

    protected ApplicationImportWizardPage(IFile file) {
        super("ImportApplication");
        this.file = file;
        setTitle("Import Mobile Web Application");
        setDescription("Create mobile web application project from application package file");
        setPageComplete(false);
    }

    protected void browse() {
        FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
        String path = fileName.getText();
        path = path.trim().length() > 0 ? path.trim() : TMWCoreUI.getDefault().getPreferenceStore()
                .getString(ApplicationImportWizard.RECENT_IMPORT_PATH);
        fileDialog.setFilterPath(path);

        final Map<String, String> filters = new TreeMap<String, String>();
        filters.put("*", "All files (*.*)");
        final IApplicationImporter[] importers = TMWCoreUI.getApplicationImporters();
        for (IApplicationImporter importer : importers) {
            filters.putAll(importer.getFileFilters());
        }

        final String[] extensions = filters.keySet().toArray(new String[filters.size()]);
        final String[] names = filters.values().toArray(new String[filters.size()]);

        fileDialog.setFilterExtensions(extensions);
        fileDialog.setFilterNames(names);
        String res = fileDialog.open();
        if (res != null) {
            updateWgzName(path.trim(), res);
            fileName.setText(res);
            setPageComplete(validatePage());
        }
    }

    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);

        initializeDialogUnits(parent);

        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        createProjectNameGroup(composite);
        locationArea = new ProjectContentsLocationArea(getErrorReporter(), composite);
        if (initialProjectFieldValue != null) {
            locationArea.updateProjectName(initialProjectFieldValue);
        }

        // Scale the button based on the rest of the dialog
        setButtonLayoutData(locationArea.getBrowseButton());
        if (file != null) {
            fileName.setText(file.getLocation().toOSString());
        }
        setPageComplete(validatePage());
        // Show description on opening
        setErrorMessage(null);
        setMessage(null);
        setControl(composite);
        Dialog.applyDialogFont(composite);

        setControl(composite);
    }

    /**
     * Creates the project name specification controls.
     * 
     * @param parent
     *            the parent composite
     */
    private final void createProjectNameGroup(Composite parent) {
        // project specification group
        Composite projectGroup = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        projectGroup.setLayout(layout);
        projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label label = new Label(projectGroup, SWT.NONE);
        label.setText("WGZ archive:");

        Composite buttonText = new Composite(projectGroup, SWT.NONE);

        buttonText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        buttonText.setLayout(gridLayout);

        fileName = new Text(buttonText, SWT.BORDER);
        fileName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        fileName.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                String val = fileName.getData() != null ? fileName.getData().toString() : "";
                String name = fileName.getText().trim();
                updateWgzName(val, name);
                fileName.setData(name);
                setPageComplete(validatePage());
            }
        });
        Button browse = new Button(buttonText, SWT.NONE);
        browse.setText("Browse...");
        browse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                browse();
            }
        });

        new Label(projectGroup, SWT.NONE).setText("Targeted runtime:");
        runtimes = new ComboViewer(projectGroup, SWT.BORDER | SWT.READ_ONLY);
        runtimes.getCombo().setEnabled(false);
        runtimes.setContentProvider(new MapContentProvider());
        runtimes.setLabelProvider(new LabelProvider() {
            @SuppressWarnings("unchecked")
            @Override
            public String getText(Object element) {
                return ((Map.Entry<String, IApplicationImporter>) element).getKey().toString();
            }
        });
        runtimes.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        runtimes.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                setPageComplete(validatePage());
            }
        });

        Label separator = new Label(projectGroup, SWT.NONE);
        GridDataFactory.generate(separator, 2, 2);

        // new project label
        Label projectLabel = new Label(projectGroup, SWT.NONE);
        projectLabel.setText(IDEWorkbenchMessages.WizardNewProjectCreationPage_nameLabel);
        projectLabel.setFont(parent.getFont());

        // new project name entry field
        projectNameField = new Text(projectGroup, SWT.BORDER);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        projectNameField.setLayoutData(data);
        projectNameField.setFont(parent.getFont());

        // Set the initial value first before listener
        // to avoid handling an event during the creation.
        if (initialProjectFieldValue != null) {
            projectNameField.setText(initialProjectFieldValue);
        }
        projectNameField.addListener(SWT.Modify, nameModifyListener);
    }

    public String getArchiveFile() {
        return fileName.getText().trim();
    }

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
     * Returns the current project location path as entered by the user, or its
     * anticipated initial value. Note that if the default has been returned the
     * path in a project description used to create a project should not be set.
     * 
     * @return the project location path or its anticipated initial value.
     */
    public IPath getLocationPath() {
        return new Path(locationArea.getProjectLocation());
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

    /**
     * Creates a project resource handle for the current project name field
     * value. The project handle is created relative to the workspace root.
     * <p>
     * This method does not create the project resource; this is the
     * responsibility of <code>IProject::create</code> invoked by the new
     * project resource wizard.
     * </p>
     * 
     * @return the new project resource handle
     */
    public IProject getProjectHandle() {
        return ResourcesPlugin.getWorkspace().getRoot().getProject(getProjectName());
    }

    /**
     * Returns the current project name as entered by the user, or its
     * anticipated initial value.
     * 
     * @return the project name, its anticipated initial value, or
     *         <code>null</code> if no project name is known
     */
    public String getProjectName() {
        if (projectNameField == null) {
            return initialProjectFieldValue;
        }

        return getProjectNameFieldValue();
    }

    /**
     * Returns the value of the project name field with leading and trailing
     * spaces removed.
     * 
     * @return the project name in the field
     */
    private String getProjectNameFieldValue() {
        if (projectNameField == null) {
            return ""; //$NON-NLS-1$
        }

        return projectNameField.getText().trim();
    }

    /**
     * Sets the initial project name that this page will use when created. The
     * name is ignored if the createControl(Composite) method has already been
     * called. Leading and trailing spaces in the name are ignored. Providing
     * the name of an existing project will not necessarily cause the wizard to
     * warn the user. Callers of this method should first check if the project
     * name passed already exists in the workspace.
     * 
     * @param name
     *            initial project name for this page
     * 
     * @see IWorkspace#validateName(String, int)
     * 
     */
    public void setInitialProjectName(String name) {
        if (name == null) {
            initialProjectFieldValue = null;
        } else {
            initialProjectFieldValue = name.trim();
            if (locationArea != null) {
                locationArea.updateProjectName(name.trim());
            }
        }
    }

    /**
     * Set the location to the default location if we are set to useDefaults.
     */
    void setLocationForSelection() {
        locationArea.updateProjectName(getProjectNameFieldValue());
    }

    private void updateWgzName(String oldValue, String newValue) {
        String project = projectNameField.getText().trim();
        if (project.length() == 0 || project.equals(new Path(oldValue).removeFileExtension().lastSegment())) {
            String projectName = new Path(newValue).removeFileExtension().lastSegment();
            projectNameField.setText(projectName);
            locationArea.updateProjectName(projectName);
        }
    }

    /**
     * Returns the useDefaults.
     * 
     * @return boolean
     */
    public boolean useDefaults() {
        return locationArea.isDefault();
    }

    private boolean validating = false;

    /**
     * Returns whether this page's controls currently all contain valid values.
     * 
     * @return <code>true</code> if all controls are valid, and
     *         <code>false</code> if at least one is invalid
     */
    protected synchronized boolean validatePage() {
        if (validating) {
            return true;
        }
        validating = true;
        try {
            final IWorkspace workspace = IDEWorkbenchPlugin.getPluginWorkspace();
            final String archive = fileName.getText().trim();
            if (archive.equals("")) {
                setErrorMessage(null);
                setMessage("Archive name must be specified");
                return false;
            }

            final File f = new File(archive);
            if (!isValidArchive(f)) {
                setErrorMessage(MessageFormat.format("{0} is not a valid application archive", archive));
                return false;
            }

            final String projectFieldContents = getProjectNameFieldValue();
            if (projectFieldContents.equals("")) { //$NON-NLS-1$
                setErrorMessage(null);
                setMessage(IDEWorkbenchMessages.WizardNewProjectCreationPage_projectNameEmpty);
                return false;
            }

            final IStatus nameStatus = workspace.validateName(projectFieldContents, IResource.PROJECT);
            if (!nameStatus.isOK()) {
                setErrorMessage(nameStatus.getMessage());
                return false;
            }

            final IProject handle = getProjectHandle();
            if (handle.exists()) {
                setErrorMessage(IDEWorkbenchMessages.WizardNewProjectCreationPage_projectExistsMessage);
                return false;
            }

            final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(getProjectNameFieldValue());
            locationArea.setExistingProject(project);

            final String validLocationMessage = locationArea.checkValidLocation();
            if (validLocationMessage != null) { // there is no destination location given
                setErrorMessage(validLocationMessage);
                return false;
            }
            if (runtimes.getSelection().isEmpty()) {
                setErrorMessage("Select target runtime");
                return false;
            }

            setErrorMessage(null);
            setMessage(null);
            return true;
        } finally {
            validating = false;
        }
    }

    private boolean isValidArchive(File f) {
        if (f.isFile()) {
            final Map<String, IApplicationImporter> importers = new TreeMap<String, IApplicationImporter>();
            final IApplicationImporter[] applicationImporters = TMWCoreUI.getApplicationImporters();
            for (IApplicationImporter importer : applicationImporters) {
                final IMobileWebRuntime runtime = importer.getApplicationRuntime(f);
                if (runtime != null) {
                    importers.put(runtime.getName(), importer);
                }
            }
            final ISelection selection = runtimes.getSelection();
            Object sel = null;
            if (!selection.isEmpty()) {
                @SuppressWarnings("unchecked")
                Map.Entry<String, IApplicationImporter> entry = (Entry<String, IApplicationImporter>) ((IStructuredSelection) selection)
                        .getFirstElement();
                for (Entry<String, IApplicationImporter> ent : importers.entrySet()) {
                    if (ent.getKey().equals(entry.getKey())) {
                        sel = ent;
                    }
                }
            }
            runtimes.setInput(importers);
            if (sel != null) {
                runtimes.setSelection(new StructuredSelection(sel));
            } else if (importers.size() == 1) {
                runtimes.setSelection(new StructuredSelection(importers.entrySet().iterator().next()));
            }
            runtimes.getControl().setEnabled(importers.size() > 1);
            return !runtimes.getSelection().isEmpty();
        }
        return false;
    }

    public IApplicationImporter getImporter() {
        if (runtimes == null || runtimes.getControl().isDisposed()) {
            return null;
        }
        @SuppressWarnings("unchecked")
        final Map.Entry<String, IApplicationImporter> element = (Entry<String, IApplicationImporter>) ((IStructuredSelection) runtimes
                .getSelection()).getFirstElement();
        return element.getValue();
    }

}
