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
package org.symbian.tools.tmw.core.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.runtimes.IPackagerDelegate;

/**
 * Use this visitor to zip application if the web runtime uses zip archive as application
 * distribution format.
 *
 * @author Eugene Ostroukhov (eugeneo@symbian.org)
 */
public class ZipApplicationVisitor implements IResourceVisitor {
    private static final int DEFAULT_BUFFER_SIZE = 65536;
    private final ZipOutputStream zipStream;
    private final IPackagerDelegate packager;

    public ZipApplicationVisitor(ZipOutputStream zip, IPackagerDelegate delegate) {
        this.zipStream = zip;
        this.packager = delegate;
    }

    public boolean visit(IResource resource) throws CoreException {
        IPath path = packager.getPathInPackage(resource);
        if (path != null && resource.getType() == IResource.FILE) {
            zip(path, resource);
        }
        return true;
    }

    private void zip(IPath path, IResource file) throws CoreException {
        ZipEntry zipEntry = new ZipEntry(path.toString());
        try {
            zipStream.putNextEntry(zipEntry);
            if (file.getType() == IResource.FILE) {
                InputStream contents = ((IFile) file).getContents();
                try {
                    copy(contents, zipStream);
                } finally {
                    contents.close();
                }
            }
            zipStream.closeEntry();
        } catch (IOException e) {
            throw new CoreException(new Status(IStatus.ERROR, TMWCore.PLUGIN_ID, String.format("Can't package %s", file
                    .getFullPath().toString()), e));
        }
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    public static long copyLarge(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

}
