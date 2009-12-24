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
package org.symbian.tools.wrttools.debug.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

public class Images {
	private static final String WRT16 = "main16.gif";
	
	public static void initImageRegistry(ImageRegistry registry) {
		setImage(registry, WRT16);
	}

	private static void setImage(ImageRegistry registry, String image) {
		ImageDescriptor img = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/" + image);
		registry.put(image, img);
	}
	
	public static ImageDescriptor getWrtIcon() {
		return getImageDescriptor(WRT16);
	}

	private static ImageDescriptor getImageDescriptor(String image) {
		return Activator.getDefault().getImageRegistry().getDescriptor(image);
	}

	public static Image getWrtIconImage() {
		return getImage(WRT16);
	}

	private static Image getImage(String image) {
		return Activator.getDefault().getImageRegistry().get(image);
	}
}
