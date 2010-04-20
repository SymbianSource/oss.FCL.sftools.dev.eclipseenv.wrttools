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
package org.symbian.tools.wrttools.core.libraries;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.symbian.tools.wrttools.Activator;

public final class LibraryManager {
    public class LibraryComparator implements Comparator<JSLibrary> {
        public int compare(JSLibrary o1, JSLibrary o2) {
            if (o1 == o2) {
                return 0;
            } else if (o1 == null) {
                return -1;
            } else if (o2 == null) {
                return 1;
            } else {
                return o1.getName().compareTo(o2.getName());
            }
        }
    }

    private JSLibrary[] jsLibraries;

    public JSLibrary[] getLibraries() {
        if (jsLibraries == null) {
            final Set<JSLibrary> libs = new TreeSet<JSLibrary>(new LibraryComparator());
            IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(Activator.PLUGIN_ID,
                    "jsLibraries");
            IConfigurationElement[] elements = point.getConfigurationElements();
            //            for (IConfigurationElement element : elements) {
            //                IConfigurationElement[] children = element.getChildren();
            for (IConfigurationElement child : elements) {
                    JSLibrary lib = createLib(child);
                    if (lib != null) {
                        libs.add(lib);
                    }
                //                }
            }
            jsLibraries = libs.toArray(new JSLibrary[libs.size()]);
        }
        return jsLibraries;
    }

    private JSLibrary createLib(IConfigurationElement element) {
        String id = element.getAttribute("id");
        String name = element.getAttribute("name");

        if (id != null && name != null) {
            return new JSLibrary(id, name, element);
        } else {
            return null;
        }
    }

}
