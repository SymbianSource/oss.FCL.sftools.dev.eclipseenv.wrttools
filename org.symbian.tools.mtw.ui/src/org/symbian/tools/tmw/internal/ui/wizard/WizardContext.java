package org.symbian.tools.tmw.internal.ui.wizard;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.value.IObservableValue;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;
import org.symbian.tools.tmw.internal.util.Util;
import org.symbian.tools.tmw.ui.TMWCoreUI;
import org.symbian.tools.tmw.ui.project.IProjectTemplate;
import org.symbian.tools.tmw.ui.project.IProjectTemplateContext;

public class WizardContext implements IProjectTemplateContext {
    //    public static final String CSS_FILE = "cssFile";
    //    public static final String HTML_FILE = "htmlFile";
    //    public static final String JS_FILE = "jsFile";
    public static final String TEMPLATE = "template";
    public static final String WIDGET_ID = "widgetId";
    public static final String WIDGET_NAME = "widgetName";
    //    public static final String HOME_SCREEN = "homeScreen";
    public static final String RUNTIME = "runtime";
    //    public static final String LIBRARIES = "libraries";
    public static final String PROJECT_NAME = "projectName";
    public static final String TEMPLATES = "templates";

    //    private String cssFile;
    private String projectName = "";
    //    private String htmlFile;
    //    private String jsFile;
    private final PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
    private IProjectTemplate template = null;
    private String widgetId;
    private String widgetName;
    private Map<String, String> extensions = new TreeMap<String, String>();
    //    private boolean homeScreen;
    private IMobileWebRuntime runtime;

    //    private Set<JSLibrary> libraries = new HashSet<JSLibrary>();

    public WizardContext() {
        IMobileWebRuntime[] runtimes = TMWCore.getRuntimesManager().getAllRuntimes();
        if (runtimes.length > 0) {
            runtime = runtimes[0];
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener arg0) {
        propertySupport.addPropertyChangeListener(arg0);
    }

    public void addPropertyChangeListener(String arg0, PropertyChangeListener arg1) {
        propertySupport.addPropertyChangeListener(arg0, arg1);
    }

    public String getProjectName() {
        return projectName;
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

    //    public String getCssFile() {
    //        if (cssFile == null) {
    //            if (template != null) {
    //                return template.getDefaultCssFile();
    //            }
    //        }
    //        return cssFile;
    //    }
    //
    private String getDefaultWidgetId() {
        String name = Util.removeSpaces(getWidgetName());
        return MessageFormat.format("com.company.{0}", name.length() > 0 ? name : "ApplicationName");
    }

    //
    //    public String getHtmlFile() {
    //        if (htmlFile == null) {
    //            if (template != null) {
    //                return template.getDefaultHtmlFile();
    //            }
    //        }
    //        return htmlFile;
    //    }
    //
    //    public String getJsFile() {
    //        if (jsFile == null) {
    //            if (template != null) {
    //                return template.getDefaultJsFile();
    //            }
    //        }
    //        return jsFile;
    //    }

    public IProjectTemplate getTemplate() {
        if (template == null) {
            return getDefaultTemplate(getRuntime());
        }
        return template;
    }

    private IProjectTemplate getDefaultTemplate(IMobileWebRuntime runtime) {
        return TMWCoreUI.getProjectTemplateManager().getDefaultTemplate(runtime);
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

    public void removePropertyChangeListener(PropertyChangeListener arg0) {
        propertySupport.removePropertyChangeListener(arg0);
    }

    public void removePropertyChangeListener(String arg0, PropertyChangeListener arg1) {
        propertySupport.removePropertyChangeListener(arg0, arg1);
    }

    //    public void setCssFile(String cssFile) {
    //        if (template != null && template.getDefaultCssFile().equals(cssFile)) {
    //            cssFile = null;
    //        }
    //        String prev = this.cssFile;
    //        this.cssFile = cssFile;
    //        propertySupport.firePropertyChange(CSS_FILE, cssFile, prev);
    //    }
    //
    //    public void setHtmlFile(String htmlFile) {
    //        if (template != null && template.getDefaultHtmlFile().equals(htmlFile)) {
    //            htmlFile = null;
    //        }
    //        String prev = this.htmlFile;
    //        this.htmlFile = htmlFile;
    //        propertySupport.firePropertyChange(HTML_FILE, htmlFile, prev);
    //    }
    //
    //    public void setJsFile(String jsFile) {
    //        if (template != null && template.getDefaultJsFile().equals(jsFile)) {
    //            jsFile = null;
    //        }
    //        String prev = this.jsFile;
    //        this.jsFile = jsFile;
    //        propertySupport.firePropertyChange(JS_FILE, jsFile, prev);
    //    }
    //
    public void setTemplate(IProjectTemplate template) {
        //        String html = getHtmlFile();
        //        String js = getJsFile();
        //        String css = getCssFile();
        IProjectTemplate prev = this.template;
        this.template = template;
        propertySupport.firePropertyChange(TEMPLATE, template, prev);
        //        if (htmlFile == null) {
        //            propertySupport.firePropertyChange(HTML_FILE, getHtmlFile(), html);
        //        }
        //        if (jsFile == null) {
        //            propertySupport.firePropertyChange(JS_FILE, getJsFile(), js);
        //        }
        //        if (cssFile == null) {
        //            propertySupport.firePropertyChange(CSS_FILE, getCssFile(), css);
        //        }
        //        if (cssFile == null) {
        //            propertySupport.firePropertyChange(LIBRARIES, getLibraries(), libraries);
        //        }
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

    public void setExtensions(Map<String, String> extensions) {
        this.extensions = extensions;
    }

    public Map<String, String> getExtensions() {
        return extensions;
    }

    private Map<String, String> getTemplateVars() {
        Map<String, String> vars = new TreeMap<String, String>();

        vars.put("widgetName", getWidgetName());
        vars.put("widgetId", getWidgetId());
        //        vars.put("mainHtml", getHtmlFileName());
        //        vars.put("mainCss", getCssFileName());
        //        vars.put("mainJs", getJsFileName());
        //        vars.put("homeScreen", String.valueOf(isHomeScreen()));
        vars.putAll(extensions);

        return vars;
    }

    //
    //    public boolean isHomeScreen() {
    //        return homeScreen;
    //    }

    //    public void setHomeScreen(boolean homeScreen) {
    //        boolean old = homeScreen;
    //        this.homeScreen = homeScreen;
    //        propertySupport.firePropertyChange(HOME_SCREEN, old, homeScreen);
    //    }

    //    public String getHtmlFileName() {
    //        return stripExtension(getHtmlFile(), "htm", "html");
    //    }
    //
    //    public String getJsFileName() {
    //        return stripExtension(getJsFile(), "js");
    //    }
    //
    //    public String getCssFileName() {
    //        return stripExtension(getCssFile(), "css");
    //    }

    //    private String stripExtension(String fileName, String... extensions) {
    //        for (String extension : extensions) {
    //            String extensionAndDot = "." + extension;
    //            if (fileName.endsWith(extensionAndDot)) {
    //                return fileName.substring(0, fileName.length() - extensionAndDot.length());
    //            }
    //        }
    //        return fileName;
    //    }

    //    public boolean isRequiredLibrary(JSLibrary element) {
    //        return template != null && template.requires(element);
    //    }
    //
    //    public Set<JSLibrary> getLibraries() {
    //        final Set<JSLibrary> set = new HashSet<JSLibrary>(libraries);
    //        if (template != null) {
    //            set.addAll(Arrays.asList(template.getRequiredLibraries()));
    //        }
    //        return set;
    //    }

    //    public void setLibraries(Set<JSLibrary> libraries) {
    //        Set<JSLibrary> prev = this.libraries;
    //        this.libraries = libraries;
    //        propertySupport.firePropertyChange(LIBRARIES, prev, libraries);
    //    }

    //    public Map<String, String> getLibraryParameters(JSLibrary library) {
    //        return Collections.emptyMap();
    //    }

    protected Text createText(Composite root, String property, String propertyName, DataBindingContext bindingContext,
            AbstractDataBindingPage page, IValidator... validators) {
        return createText(root, BeansObservables.observeValue(this, property), propertyName, bindingContext, page,
                validators);
    }

    protected Text createTextForExt(Composite root, String property, String propertyName,
            DataBindingContext bindingContext, AbstractDataBindingPage page) {
        IObservableMap map = BeansObservables.observeMap(this, "extensions");
        IObservableValue entry = Observables.observeMapEntry(map, property, String.class);
        return createText(root, entry, propertyName, bindingContext, page);
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

    protected void createLabel(Composite root, String text) {
        Label label = new Label(root, SWT.NONE);
        label.setText(text);
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

    public IMobileWebRuntime getRuntime() {
        return runtime;
    }

    public IProjectTemplate[] getTemplates() {
        return TMWCoreUI.getProjectTemplateManager().getProjectTemplates(getRuntime());
    }

    public void initialize(IProject project, IProgressMonitor monitor) {
        final IProjectTemplate template = getTemplate();
        if (template != null) {
            template.init(project, this, monitor);
        }
    }

    public Object getParameter(String parameter) {
        return getTemplateVars().get(parameter);
    }

    public String[] getParameterNames() {
        Set<String> keys = getTemplateVars().keySet();
        return keys.toArray(new String[keys.size()]);
    }

    public void putParameter(String key, Object value) {
        if (value instanceof String) {
            extensions.put(key, (String) value);
        }
    }

    public void addFile(IProject project, IPath name, InputStream contents, IProgressMonitor monitor)
            throws CoreException {
        monitor.beginTask(name.toOSString(), 100);
        final IFile file = project.getFile(name);
        if (!file.exists()) {
            create(file.getParent());
        }
        file.create(contents, false, new SubProgressMonitor(monitor, 100));
        monitor.done();
    }

    private void create(IContainer parent) throws CoreException {
        if (!parent.exists() && parent instanceof IFolder) {
            create(parent.getParent());
            ((IFolder) parent).create(false, true, new NullProgressMonitor());
        }
    }
}
