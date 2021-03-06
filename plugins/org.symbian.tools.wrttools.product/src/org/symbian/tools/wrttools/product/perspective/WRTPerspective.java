/*******************************************************************************
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
 *******************************************************************************/
package org.symbian.tools.wrttools.product.perspective;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.progress.IProgressConstants;
import org.eclipse.wst.jsdt.ui.JavaScriptUI;
import org.symbian.tools.tmw.previewer.PreviewerPlugin;
import org.symbian.tools.tmw.ui.project.NewApplicationWizard;

public class WRTPerspective implements IPerspectiveFactory {

    public void createInitialLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();

        IFolderLayout folder = layout.createFolder("left", IPageLayout.LEFT, (float) 0.15, editorArea); //$NON-NLS-1$
        folder.addView(IPageLayout.ID_PROJECT_EXPLORER);

        IFolderLayout snippetsFolder = layout.createFolder("snippets", IPageLayout.BOTTOM, (float) 0.5, "left"); //$NON-NLS-1$
        snippetsFolder.addView("org.eclipse.wst.common.snippets.internal.ui.SnippetsView");

        IFolderLayout outputfolder = layout.createFolder("bottom", IPageLayout.BOTTOM, (float) 0.75, editorArea); //$NON-NLS-1$
        outputfolder.addView(IPageLayout.ID_PROBLEM_VIEW);
        outputfolder.addView(JavaScriptUI.ID_JAVADOC_VIEW);
        outputfolder.addView(JavaScriptUI.ID_SOURCE_VIEW);
        outputfolder.addPlaceholder(NewSearchUI.SEARCH_VIEW_ID);
        outputfolder.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW);
        outputfolder.addPlaceholder(IPageLayout.ID_BOOKMARKS);
        outputfolder.addPlaceholder(IProgressConstants.PROGRESS_VIEW_ID);

        IFolderLayout helpersFolder = layout.createFolder("right", IPageLayout.RIGHT, (float) 0.60, editorArea);
        helpersFolder.addView(PreviewerPlugin.PREVIEW_VIEW);
        helpersFolder.addView(IPageLayout.ID_OUTLINE);

        layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
        layout.addActionSet(JavaScriptUI.ID_ACTION_SET);
        layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
        layout.addActionSet("org.symbian.tools.wrttools.product.creationActionSet");

        // views - java
        layout.addShowViewShortcut(JavaScriptUI.ID_TYPE_HIERARCHY);
        layout.addShowViewShortcut(IPageLayout.ID_PROJECT_EXPLORER);
        layout.addShowViewShortcut(JavaScriptUI.ID_SOURCE_VIEW);
        layout.addShowViewShortcut(JavaScriptUI.ID_JAVADOC_VIEW);

        layout.addPerspectiveShortcut(IDebugUIConstants.ID_DEBUG_PERSPECTIVE);

        // views - search
        layout.addShowViewShortcut(NewSearchUI.SEARCH_VIEW_ID);

        // views - standard workbench
        layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
        layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
        layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
        layout.addShowViewShortcut(PreviewerPlugin.PREVIEW_VIEW);
        layout.addShowViewShortcut("org.eclipse.wst.common.snippets.internal.ui.SnippetsView");

        // new actions - Java project creation wizard
        layout.addNewWizardShortcut(NewApplicationWizard.ID);
        layout.addNewWizardShortcut("org.eclipse.wst.jsdt.ui.NewJSWizard"); //$NON-NLS-1$
        layout.addNewWizardShortcut("org.eclipse.wst.html.ui.internal.wizard.NewHTMLWizard"); //$NON-NLS-1$
        layout.addNewWizardShortcut("org.eclipse.wst.css.ui.internal.wizard.NewCSSWizard"); //$NON-NLS-1$
        layout.addNewWizardShortcut("org.eclipse.wst.xml.ui.internal.wizards.NewXMLWizard"); //$NON-NLS-1$
        layout.addNewWizardShortcut("org.eclipse.wst.jsdt.ui.wizards.NewSnippetFileCreationWizard"); //$NON-NLS-1$
        layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder"); //$NON-NLS-1$
        layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file"); //$NON-NLS-1$
        layout.addNewWizardShortcut("org.eclipse.ui.editors.wizards.UntitledTextFileWizard"); //$NON-NLS-1$
    }

}
