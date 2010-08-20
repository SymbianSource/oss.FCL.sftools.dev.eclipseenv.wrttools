package org.symbian.tools.tmw.internal.ui.wizard;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.viewers.IViewerObservableValue;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.symbian.tools.tmw.ui.project.IProjectTemplate;

public class NewApplicationTemplateWizardPage extends WizardPage {
    public class ProjectTemplateLabelProvider extends LabelProvider {

        @Override
        public Image getImage(Object element) {
            return ((IProjectTemplate) element).getIcon();
        }

        @Override
        public String getText(Object element) {
            return ((IProjectTemplate) element).getName();
        }
    }

    private TableViewer templates;
    private Text description;
    private final WizardContext context;
    private final DataBindingContext bindingContext;

    public NewApplicationTemplateWizardPage(WizardContext context, DataBindingContext bindingContext) {
        super("Create a New Mobile Web Application");
        setTitle("Create a New Mobile Web Application");
        this.context = context;
        this.bindingContext = bindingContext;
        setDescription("Select project name and template that will be used to populate");
    }

    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        FormLayout layout = new FormLayout();
        layout.marginWidth = 5;
        composite.setLayout(layout);

        templates = new TableViewer(composite, SWT.BORDER | SWT.SINGLE);
        FormData templatesData = new FormData();
        templatesData.top = new FormAttachment(0, 0);
        templatesData.left = new FormAttachment(0, 0);
        templatesData.right = new FormAttachment(40, -2);
        templatesData.bottom = new FormAttachment(100, -8);
        templates.getControl().setLayoutData(templatesData);
        templates.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                final IProjectTemplate template = (IProjectTemplate) selection.getFirstElement();
                refreshSelection(template);
            }
        });
        templates.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent arg0) {
                switchWizardPage();
            }
        });

        description = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
        FormData descriptionData = new FormData();
        descriptionData.top = new FormAttachment(0, 0);
        descriptionData.bottom = new FormAttachment(100, -8);
        descriptionData.left = new FormAttachment(templates.getControl(), 5);
        descriptionData.right = new FormAttachment(100, 0);
        descriptionData.width = 50;
        description.setLayoutData(descriptionData);

        templates.setContentProvider(new ArrayContentProvider());
        templates.setLabelProvider(new ProjectTemplateLabelProvider());
        templates.setSorter(new ViewerSorter() {
            @Override
            public int category(Object element) {
                return Integer.valueOf(((IProjectTemplate) element).getWeight());
            }
        });
        setPageComplete(false);

        IObservableValue input = ViewersObservables.observeInput(templates);
        IObservableValue templ = BeansObservables.observeValue(context, WizardContext.TEMPLATES);
        bindingContext.bindValue(input, templ, new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER),
                new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));

        IViewerObservableValue selection = ViewersObservables.observeSingleSelection(templates);
        IObservableValue property = BeansObservables.observeValue(context, WizardContext.TEMPLATE);

        bindingContext.bindValue(selection, property);
        if (context.getTemplate() != null) {
            refreshSelection(context.getTemplate());
        }
        setErrorMessage(null);
        setControl(composite);
    }

    protected void switchWizardPage() {
        Display display = getShell().getDisplay();
        display.asyncExec(new Runnable() {
            public void run() {
                if (isPageComplete()) {
                    IWizardPage nextPage = getWizard().getNextPage(NewApplicationTemplateWizardPage.this);
                    getContainer().showPage(nextPage);
                }
            }
        });
    }

    protected void refreshSelection(IProjectTemplate template) {
        if (template != null) {
            description.setText(template.getDescription());
        } else {
            description.setText("");
        }
        validatePage();
    }

    protected boolean validatePage() {
        if (templates.getSelection().isEmpty()) {
            setErrorMessage("Project template is not selected");
            setPageComplete(false);
            return false;
        } else {
            setErrorMessage(null);
            setPageComplete(true);
            return true;
        }
    }

    public IProjectTemplate getSelectedProjectTemplate() {
        IStructuredSelection selection = (IStructuredSelection) templates.getSelection();
        return (IProjectTemplate) selection.getFirstElement();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        templates.getControl().setFocus();
    }
}
