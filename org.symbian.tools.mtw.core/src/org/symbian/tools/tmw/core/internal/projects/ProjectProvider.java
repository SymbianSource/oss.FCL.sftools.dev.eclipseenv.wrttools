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
package org.symbian.tools.tmw.core.internal.projects;

import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.IMTWProject;
import org.symbian.tools.tmw.core.projects.IMTWProjectProvider;

public class ProjectProvider implements IMTWProjectProvider {
    private final IConfigurationElement element;
    private final Expression expression;
    private IMTWProjectProvider provider;

    public ProjectProvider(IConfigurationElement element) throws CoreException {
        this.element = element;
        IConfigurationElement[] children = element.getChildren("enablement");
        if (children.length == 1) {
            expression = ExpressionConverter.getDefault().perform(children[0]);
        } else {
            expression = null;
            if (children.length > 1) {
                TMWCore.log(
                        "Project extension in plugin %s has several <enablement> elements. All expressions will be ignored.",
                        element.getNamespaceIdentifier());
            }
        }
    }

    public IMTWProject create(IProject project) {
        return getProvider().create(project);
    }

    private IMTWProjectProvider getProvider() {
        if (provider == null) {
            try {
                provider = (IMTWProjectProvider) element.createExecutableExtension("class");
            } catch (CoreException e) {
                throw new RuntimeException(e);
            }
        }
        return provider;
    }

    public boolean isSupportedProject(final IProject project) {
        try {
            final EvaluationResult result = expression.evaluate(new EvaluationContext(null, project));
            if (!EvaluationResult.FALSE.equals(result)) {
                return getProvider().isSupportedProject(project);
            }
        } catch (CoreException e) {
            TMWCore.log(null, e);
        }
        return false;
    }
}
