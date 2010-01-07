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
import java.net.URI;
import java.text.MessageFormat;
import java.util.Map;
import java.util.TreeMap;

import org.symbian.tools.wrttools.core.ProjectTemplate;

public class WizardContext {
	public static final String PROJECT_URI = "projectUri";
	public static final String CSS_FILE = "cssFile";
	public static final String HTML_FILE = "htmlFile";
	public static final String JS_FILE = "jsFile";
	public static final String PROJECT_NAME = "projectName";
	public static final String TEMPLATE = "template";
	public static final String WIDGET_ID = "widgetId";
	public static final String WIDGET_NAME = "widgetName";
	public static final String HOME_SCREEN = "homeScreen";

	private String cssFile;
	private String htmlFile;
	private String jsFile;
	private String projectName;
	private PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
	private ProjectTemplate template;
	private String widgetId;
	private String widgetName;
	private Map<String, String> extensions = new TreeMap<String, String>();
	private URI projectUri;
	private boolean homeScreen;

	public void addPropertyChangeListener(PropertyChangeListener arg0) {
		propertySupport.addPropertyChangeListener(arg0);
	}

	public void addPropertyChangeListener(String arg0,
			PropertyChangeListener arg1) {
		propertySupport.addPropertyChangeListener(arg0, arg1);
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
		if (template != null) {
			return MessageFormat
					.format(template.getIdFormat(), getWidgetName());
		} else {
			return null;
		}
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

	public String getProjectName() {
		return projectName;
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
		return widgetName != null ? widgetName : getProjectName();
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

	public void setProjectName(String projectName) {
		String prevName = getWidgetName();
		String prevId = getWidgetId();
		String prev = this.projectName;
		this.projectName = projectName;
		propertySupport.firePropertyChange(PROJECT_NAME, projectName, prev);
		if (widgetName == null) {
			propertySupport.firePropertyChange(WIDGET_NAME, getWidgetName(),
					prevName);
			if (widgetId == null) {
				propertySupport.firePropertyChange(WIDGET_ID, getWidgetId(),
						prevId);
			}
		}
	}

	public void setTemplate(ProjectTemplate template) {
		String id = getWidgetId();
		String html = getHtmlFile();
		String js = getJsFile();
		String css = getCssFile();
		ProjectTemplate prev = this.template;
		this.template = template;
		propertySupport.firePropertyChange(TEMPLATE, template, prev);
		if (widgetId == null) {
			propertySupport.firePropertyChange(WIDGET_ID, getWidgetId(), id);
		}
		if (htmlFile == null) {
			propertySupport.firePropertyChange(HTML_FILE, getHtmlFile(), html);
		}
		if (jsFile == null) {
			propertySupport.firePropertyChange(JS_FILE, getJsFile(), js);
		}
		if (cssFile == null) {
			propertySupport.firePropertyChange(CSS_FILE, getCssFile(), css);
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
		if (projectName.equals(widgetName)) {
			widgetName = null;
		}
		this.widgetName = widgetName;
		propertySupport.firePropertyChange(WIDGET_NAME, widgetName, prev);
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

	public void setProjectUri(URI uri) {
		URI orig = projectUri;
		this.projectUri = uri;
		propertySupport.firePropertyChange(PROJECT_URI, projectUri, orig);
	}
	
	public URI getProjectUri() {
		return projectUri;
	}
	
	public Map<String, String> getTemplateVars() {
		Map<String, String> vars = new TreeMap<String, String>();
		
		vars.put("widgetName", getWidgetName());
		vars.put("widgetId", getWidgetId());
		vars.put("mainHtml", getHtmlFile());
		vars.put("mainCss", getCssFile());
		vars.put("mainJs", getJsFile());
		vars.put("homeScreen", String.valueOf(isHomeScreen()));
		vars.putAll(extensions);
		
		return vars ;
	}

	public boolean isHomeScreen() {
		return homeScreen;
	}
	
	public void setHomeScreen(boolean homeScreen) {
		boolean old = homeScreen;
		this.homeScreen = homeScreen;
		propertySupport.firePropertyChange(HOME_SCREEN, old, homeScreen);
	}
}
