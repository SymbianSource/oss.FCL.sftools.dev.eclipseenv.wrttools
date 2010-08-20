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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
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
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.symbian.tools.wrttools.core.ProjectTemplate;
import org.symbian.tools.wrttools.core.libraries.JSLibrary;
import org.symbian.tools.wrttools.util.CompoundValidator;
import org.symbian.tools.wrttools.util.Util;

public class WizardContext {
    public static final String CSS_FILE = "cssFile";
    public static final String HTML_FILE = "htmlFile";
    public static final String JS_FILE = "jsFile";
    public static final String TEMPLATE = "template";
    public static final String WIDGET_ID = "widgetId";
    public static final String WIDGET_NAME = "widgetName";
    public static final String HOME_SCREEN = "homeScreen";
    public static final String LIBRARIES = "libraries";
    public static final String PROJECT_NAME = "projectName";

    private String cssFile;
    private String projectName = "";
    private String htmlFile;
    private String jsFile;
    private final PropertyChangeSupport propertySupport = new PropertyChangeSupport(
            this);
    private ProjectTemplate template;
    private String widgetId;
    private String widgetName;
    private Map<String, String> extensions = new TreeMap<String, String>();
    private boolean homeScreen;
    private Set<JSLibrary> libraries = new HashSet<JSLibrary>();

    public WizardContext() {
        setTemplate(getFirstTemplate(ProjectTemplate.getAllTemplates()));
    }

    private ProjectTemplate getFirstTemplate(ProjectTemplate[] allTemplates) {
        ProjectTemplate template = null;
        for (ProjectTemplate projectTemplate : allTemplates) {
            if (template == null
                    || template.getOrder() > projectTemplate.getOrder()) {
                template = projectTemplate;
            }
        }
        return template;
    }


    public void addPropertyChangeListener(PropertyChangeListener arg0) {
        propertySupport.addPropertyChangeListener(arg0);
    }

    public void addPropertyChangeListener(String arg0,
            PropertyChangeListener arg1) {
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

    public String getCssFile() {
        if (cssFile == null) {
            if (template != null) {
                return template.getDefaultCssFile();
            }
        }
        return cssFile;
    }

    private String getDefaultWidgetId() {
        String name = Util.removeSpaces(getWidgetName());
        return MessageFormat.format("com.company.{0}", name.length() > 0 ? name
                : "ApplicationName");
    }

    public String getHtmlFile() {
        if (htmlFile == null) {
            if (template != null) {
                return template.getDefaultHtmlFile();
            }
        }
        return htmlFile;
    }

    public String getJsFile() {
        if (jsFile == null) {
            if (template != null) {
                return template.getDefaultJsFile();
            }
        }
        return jsFile;
    }

    public ProjectTemplate getTemplate() {
        return template;
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

    public void removePropertyChangeListener(String arg0,
            PropertyChangeListener arg1) {
        propertySupport.removePropertyChangeListener(arg0, arg1);
    }

    public void setCssFile(String cssFile) {
        if (template != null && template.getDefaultCssFile().equals(cssFile)) {
            cssFile = null;
        }
        String prev = this.cssFile;
        this.cssFile = cssFile;
        propertySupport.firePropertyChange(CSS_FILE, cssFile, prev);
    }

    public void setHtmlFile(String htmlFile) {
        if (template != null && template.getDefaultHtmlFile().equals(htmlFile)) {
            htmlFile = null;
        }
        String prev = this.htmlFile;
        this.htmlFile = htmlFile;
        propertySupport.firePropertyChange(HTML_FILE, htmlFile, prev);
    }

    public void setJsFile(String jsFile) {
        if (template != null && template.getDefaultJsFile().equals(jsFile)) {
            jsFile = null;
        }
        String prev = this.jsFile;
        this.jsFile = jsFile;
        propertySupport.firePropertyChange(JS_FILE, jsFile, prev);
    }

    public void setTemplate(ProjectTemplate template) {
        String html = getHtmlFile();
        String js = getJsFile();
        String css = getCssFile();
        ProjectTemplate prev = this.template;
        this.template = template;
        propertySupport.firePropertyChange(TEMPLATE, template, prev);
        if (htmlFile == null) {
            propertySupport.firePropertyChange(HTML_FILE, getHtmlFile(), html);
        }
        if (jsFile == null) {
            propertySupport.firePropertyChange(JS_FILE, getJsFile(), js);
        }
        if (cssFile == null) {
            propertySupport.firePropertyChange(CSS_FILE, getCssFile(), css);
        }
        if (cssFile == null) {
            propertySupport.firePropertyChange(LIBRARIES, getLibraries(),
                    libraries);
        }
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
            propertySupport
                    .firePropertyChange(WIDGET_ID, getWidgetId(), prevId);
        }
    }

    public void setExtensions(Map<String, String> extensions) {
        this.extensions = extensions;
    }

    public Map<String, String> getExtensions() {
        return extensions;
    }

    public Map<String, String> getTemplateVars() {
        Map<String, String> vars = new TreeMap<String, String>();

        vars.put("widgetName", getWidgetName());
        vars.put("widgetId", getWidgetId());
        vars.put("mainHtml", getHtmlFileName());
        vars.put("mainCss", getCssFileName());
        vars.put("mainJs", getJsFileName());
        vars.put("homeScreen", String.valueOf(isHomeScreen()));
        vars.putAll(extensions);

        return vars;
    }

    public boolean isHomeScreen() {
        return homeScreen;
    }

    public void setHomeScreen(boolean homeScreen) {
        boolean old = homeScreen;
        this.homeScreen = homeScreen;
        propertySupport.firePropertyChange(HOME_SCREEN, old, homeScreen);
    }

    public String getHtmlFileName() {
        return stripExtension(getHtmlFile(), "htm", "html");
    }

    public String getJsFileName() {
        return stripExtension(getJsFile(), "js");
    }

    public String getCssFileName() {
        return stripExtension(getCssFile(), "css");
    }

    private String stripExtension(String fileName, String... extensions) {
        for (String extension : extensions) {
            String extensionAndDot = "." + extension;
            if (fileName.endsWith(extensionAndDot)) {
                return fileName.substring(0, fileName.length()
                        - extensionAndDot.length());
            }
        }
        return fileName;
    }

    public boolean isRequiredLibrary(JSLibrary element) {
        return template != null && template.requires(element);
    }

    public Set<JSLibrary> getLibraries() {
        final Set<JSLibrary> set = new HashSet<JSLibrary>(libraries);
        if (template != null) {
            set.addAll(Arrays.asList(template.getRequiredLibraries()));
        }
        return set;
    }

    public void setLibraries(Set<JSLibrary> libraries) {
        Set<JSLibrary> prev = this.libraries;
        this.libraries = libraries;
        propertySupport.firePropertyChange(LIBRARIES, prev, libraries);
    }

    public Map<String, String> getLibraryParameters(JSLibrary library) {
        return Collections.emptyMap();
    }

    protected Text createText(Composite root, String property,
            String propertyName, DataBindingContext bindingContext,
            AbstractDataBindingPage page, IValidator... validators) {
        return createText(root, BeansObservables.observeValue(this, property),
                propertyName, bindingContext, page, validators);
    }

    protected Text createTextForExt(Composite root, String property,
            String propertyName, DataBindingContext bindingContext,
            AbstractDataBindingPage page) {
        IObservableMap map = BeansObservables.observeMap(this, "extensions");
        IObservableValue entry = Observables.observeMapEntry(map, property,
                String.class);
        return createText(root, entry, propertyName, bindingContext, page);
    }

    private Text createText(Composite root, IObservableValue model,
            String propertyName, DataBindingContext bindingContext,
            AbstractDataBindingPage page, IValidator... validators) {
        Text text = new Text(root, SWT.BORDER);
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ISWTObservableValue view = SWTObservables.observeText(text, SWT.Modify);
        UpdateValueStrategy strategy = new UpdateValueStrategy(
                UpdateValueStrategy.POLICY_UPDATE);
        NonEmptyStringValidator validator = new NonEmptyStringValidator(
                propertyName, page);
        strategy.setBeforeSetValidator(validators.length == 0 ? validator
                : new CompoundValidator(validator, validators));
        bindingContext.bindValue(view, model, strategy, null);
        return text;
    }

    protected void createLabel(Composite root, String text) {
        Label label = new Label(root, SWT.NONE);
        label.setText(text);
    }

}
