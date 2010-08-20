package org.symbian.tools.tmw.internal.ui.wizard;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.ValueDiff;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;
import org.symbian.tools.tmw.internal.util.Util;
import org.symbian.tools.tmw.ui.TMWCoreUI;
import org.symbian.tools.tmw.ui.project.IProjectTemplate;
import org.symbian.tools.tmw.ui.project.IProjectTemplateContext;

public class WizardContext implements IProjectTemplateContext {
    public static final String PROJECT_NAME = "projectName";
    public static final String RUNTIME = "runtime";
    public static final String TEMPLATE = "template";
    public static final String TEMPLATES = "templates";
    public static final String WIDGET_ID = "widgetId";
    public static final String WIDGET_NAME = "widgetName";

    private final Map<String, Object> extensions = new TreeMap<String, Object>();
    private final Map<String, IObservableValue> observables = new TreeMap<String, IObservableValue>();
    private String projectName = "";
    private final PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
    private IMobileWebRuntime runtime;
    private IProjectTemplate template = null;

    private String widgetId;

    private String widgetName;
    private final Collection<String> jsIncludes = new TreeSet<String>();

    public WizardContext() {
        IMobileWebRuntime[] runtimes = TMWCore.getRuntimesManager().getAllRuntimes();
        if (runtimes.length > 0) {
            runtime = runtimes[0];
        }
    }

    public IFile addFile(IProject project, IPath name, InputStream contents, IProgressMonitor monitor)
            throws CoreException {
        monitor.beginTask(name.toOSString(), 100);
        final IFile file = project.getFile(name);
        if (!file.exists()) {
            create(file.getParent());
        }
        file.create(new NonClosingStream(contents), false, new SubProgressMonitor(monitor, 100));
        monitor.done();
        return file;
    }

    public void addPropertyChangeListener(PropertyChangeListener arg0) {
        propertySupport.addPropertyChangeListener(arg0);
    }

    public void addPropertyChangeListener(String arg0, PropertyChangeListener arg1) {
        propertySupport.addPropertyChangeListener(arg0, arg1);
    }

    private void create(IContainer parent) throws CoreException {
        if (!parent.exists() && parent instanceof IFolder) {
            create(parent.getParent());
            ((IFolder) parent).create(false, true, new NullProgressMonitor());
        }
    }

    private Text createText(Composite root, IObservableValue model, String propertyName,
            DataBindingContext bindingContext, AbstractDataBindingPage page, IValidator... validators) {
        Text text = new Text(root, SWT.BORDER);
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ISWTObservableValue view = SWTObservables.observeText(text, SWT.Modify);
        UpdateValueStrategy strategy = new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
        NonEmptyStringValidator validator = new NonEmptyStringValidator(propertyName, page);
        strategy.setBeforeSetValidator(validators.length == 0 ? validator
                : new CompoundValidator(validator, validators));
        bindingContext.bindValue(view, model, strategy, null);
        return text;
    }

    protected Text createText(Composite root, String property, String propertyName, DataBindingContext bindingContext,
            AbstractDataBindingPage page, IValidator... validators) {
        return createText(root, BeansObservables.observeValue(this, property), propertyName, bindingContext, page,
                validators);
    }

    private IProjectTemplate getDefaultTemplate(IMobileWebRuntime runtime) {
        return TMWCoreUI.getProjectTemplateManager().getDefaultTemplate(runtime);
    }

    private String getDefaultWidgetId() {
        String name = Util.removeSpaces(getWidgetName());
        return MessageFormat.format("com.company.{0}", name.length() > 0 ? name : "ApplicationName");
    }

    public Map<String, Object> getExtensions() {
        return extensions;
    }

    public Object getParameter(String parameter) {
        return getTemplateVars().get(parameter);
    }

    public String[] getParameterNames() {
        Set<String> keys = getTemplateVars().keySet();
        return keys.toArray(new String[keys.size()]);
    }

    public IObservableValue getParameterObservable(String name) {
        IObservableValue value = observables.get(name);
        if (value == null) {
            value = new ObservableParameter(name);
            observables.put(name, value);
        }
        return value;
    }

    public String getProjectName() {
        return projectName;
    }

    public IMobileWebRuntime getRuntime() {
        return runtime;
    }

    public IProjectTemplate getTemplate() {
        if (template == null) {
            return getDefaultTemplate(getRuntime());
        }
        return template;
    }

    public IProjectTemplate[] getTemplates() {
        return TMWCoreUI.getProjectTemplateManager().getProjectTemplates(getRuntime());
    }

    private Map<String, Object> getTemplateVars() {
        Map<String, Object> vars = new TreeMap<String, Object>();

        if (runtime != null) {
            vars.putAll(TMWCoreUI.getProjectTemplateManager().getDefaultTemplateParameterValues(runtime));
        }
        final IProjectTemplate t = getTemplate();
        if (t != null) {
            vars.putAll(t.getDefaultParameterValues());
        }

        vars.put("widgetName", getWidgetName());
        vars.put("widgetId", getWidgetId());
        vars.put("jsIncludes", jsIncludes);
        vars.putAll(extensions);

        return vars;
    }

    public String getWidgetId() {
        if (widgetId == null) {
            return getDefaultWidgetId();
        }
        return widgetId;
    }

    public String getWidgetName() {
        return widgetName == null ? getProjectName() : widgetName;
    }

    public void initialize(IProject project, IProgressMonitor monitor) {
        final IProjectTemplate template = getTemplate();
        if (template != null) {
            template.init(project, this, monitor);
        }
    }

    public void putParameter(String key, Object value) {
        if (value != null) {
            extensions.put(key, value);
        } else {
            extensions.remove(key);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener arg0) {
        propertySupport.removePropertyChangeListener(arg0);
    }

    public void removePropertyChangeListener(String arg0, PropertyChangeListener arg1) {
        propertySupport.removePropertyChangeListener(arg0, arg1);
    }

    public void setProjectName(String projectName) {
        String prev = getProjectName();
        String prevId = getWidgetId();
        String prevName = getWidgetName();
        this.projectName = projectName;
        propertySupport.firePropertyChange(PROJECT_NAME, getProjectName(), prev);
        if (widgetName == null) {
            propertySupport.firePropertyChange(WIDGET_NAME, getWidgetName(), prevName);
            if (widgetId == null) {
                propertySupport.firePropertyChange(WIDGET_ID, getWidgetId(), prevId);
            }
        }
    }

    public void setRuntime(IMobileWebRuntime runtime) {
        final IProjectTemplate[] prevTemplates = getTemplates();
        final IProjectTemplate prevTemplate;
        if (template == null) {
            prevTemplate = getTemplate();
        } else {
            prevTemplate = null;
        }
        final IMobileWebRuntime prev = this.runtime;
        this.runtime = runtime;
        propertySupport.firePropertyChange(RUNTIME, prev, runtime);
        propertySupport.firePropertyChange(TEMPLATES, prevTemplates, getTemplates());
        if (prevTemplate == null) {
            propertySupport.firePropertyChange(TEMPLATE, prevTemplate, getTemplate());
        }
    }

    public void setTemplate(IProjectTemplate template) {
        IProjectTemplate prev = this.template;
        this.template = template;
        propertySupport.firePropertyChange(TEMPLATE, template, prev);
    }

    public void setWidgetId(String widgetId) {
        String prev = getWidgetId();
        if (getDefaultWidgetId().equals(widgetId)) {
            widgetId = null;
        }
        this.widgetId = widgetId;
        propertySupport.firePropertyChange(WIDGET_ID, getWidgetId(), prev);
    }

    public void setWidgetName(String widgetName) {
        String prevId = getWidgetId();
        String prev = getWidgetName();
        if (widgetName == getProjectName()) {
            this.widgetName = null;
        } else {
            this.widgetName = widgetName;
        }
        propertySupport.firePropertyChange(WIDGET_NAME, getWidgetName(), prev);
        if (widgetId == null) {
            propertySupport.firePropertyChange(WIDGET_ID, getWidgetId(), prevId);
        }
    }

    private final class ObservableParameter extends AbstractObservableValue {
        private final String name;

        public ObservableParameter(String name) {
            this.name = name;
        }

        public Object getValueType() {
            return Object.class;
        }

        @Override
        protected Object doGetValue() {
            return getParameter(name);
        }

        @Override
        protected void doSetValue(final Object value) {
            final Object prev = getParameter(name);
            putParameter(name, value);
            fireValueChange(new ValueDiff() {

                @Override
                public Object getOldValue() {
                    return prev;
                }

                @Override
                public Object getNewValue() {
                    return value;
                }
            });
        }
    }

    private static final class NonClosingStream extends FilterInputStream {
        private NonClosingStream(InputStream in) {
            super(in);
        }

        @Override
        public void close() throws IOException {
            // Avoid closing ZIP file
        }
    }


    public void addIncludedJsFile(IFile file) {
        jsIncludes.add(file.getProjectRelativePath().makeRelative().toString());
    }
}
