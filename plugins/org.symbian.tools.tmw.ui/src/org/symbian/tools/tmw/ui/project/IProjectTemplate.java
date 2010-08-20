/**
 * Copyright (c) 2010 Symbian Foundation and/or its subsidiary(-ies).
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
package org.symbian.tools.tmw.ui.project;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;

/**
 * This interface will allow extending new mobile web application wizard
 * with "smart" templates that have complex logic.
 * 
 * @author Eugene Ostroukhov (eugeneo@symbian.org)
 */
public interface IProjectTemplate {
    /**
     * Attributes that are common for different templates for different 
     * runtimes. They are included here to improve compatibility and reuse.
     */
    public interface CommonKeys {
        String application_id = "widgetName";
        String application_name = "widgetId";
        String project_name = "projectName";
        String main_html = "mainHtml";
        String main_css = "mainCss";
        String main_js = "mainJs";
    }

    /**
     * @return template "weight" that determines how high the template will 
     * be in a templates list. This is purely for visualization as IDE vendors
     * might want more basic templates higher in the list to improve the 
     * learning curve.
     */
    int getWeight();

    /**
     * @return icon that will be used in the UI to represent the template
     */
    Image getIcon();

    /**
     * @return template name. It is only used in the UI and is not stored in
     * project metadata.
     */
    String getName();

    /**
     * @return template description that will be used in UI
     */
    String getDescription();

    /**
     * @return array of the supported runtimes
     */
    IMobileWebRuntime[] getSupportedRuntimes();

    /**
     * @return facets that should be enable so this project template works
     */
    IProjectFacetVersion[] getRequiredFacets();

    /**
     * Initializes project with template contents
     */
    void init(IProject project, IProjectTemplateContext context, IProgressMonitor monitor);

    /**
     * Returns default template parameter values
     */
    Map<String, String> getDefaultParameterValues();

    /**
     * @return template ID
     */
    String getId();
}
