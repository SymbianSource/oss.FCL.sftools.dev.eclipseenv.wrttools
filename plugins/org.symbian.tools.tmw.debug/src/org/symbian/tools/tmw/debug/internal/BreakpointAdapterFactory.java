/*******************************************************************************
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
 *******************************************************************************/
package org.symbian.tools.tmw.debug.internal;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.ui.IEditorPart;

@SuppressWarnings("rawtypes")
public class BreakpointAdapterFactory implements IAdapterFactory {

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof IEditorPart) {
			IResource resource = (IResource) ((IEditorPart) adaptableObject)
					.getEditorInput().getAdapter(IResource.class);
			if (resource != null) {
				return new WorkspaceLineBreakpointAdapter();
			}
		}
		return null;
	}

    public Class[] getAdapterList() {
		return new Class[] { IToggleBreakpointsTarget.class };
	}

}
