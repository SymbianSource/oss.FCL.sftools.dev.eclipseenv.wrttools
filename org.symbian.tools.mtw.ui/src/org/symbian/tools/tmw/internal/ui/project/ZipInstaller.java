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
package org.symbian.tools.tmw.internal.ui.project;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.AbstractContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.symbian.tools.tmw.internal.util.OpenFilesRunnable;
import org.symbian.tools.tmw.internal.util.Util;
import org.symbian.tools.tmw.ui.TMWCoreUI;
import org.symbian.tools.tmw.ui.project.IProjectTemplateContext;
import org.symbian.tools.tmw.ui.project.ITemplateInstaller;

/**
 * Initializes project with zip archive contents
 * 
 * @author Eugene Ostroukhov (eugeneo@symbian.org)
 */
public class ZipInstaller implements ITemplateInstaller {
    private static final String TEMPLATE_FILE_EXTENSION = ".velocitytemplate";
    private final IConfigurationElement element;
    private IProject project;
    private IProjectTemplateContext context;
    private String[] paths;
    private final Set<IFile> openFiles = new HashSet<IFile>();

    public ZipInstaller(IConfigurationElement element) {
        this.element = element;
        try {
            Velocity.init();
        } catch (Exception e) {
            TMWCoreUI.log(e);
        }
    }

    public void prepare(IProject project, IProjectTemplateContext context) {
        this.project = project;
        this.context = context;
    }

    public void cleanup() {
        project = null;
        context = null;
        openFiles.clear();
    }

    public IPath[] getFiles() throws CoreException {
        if (paths == null) {
            final Collection<String> pathCollection = new LinkedList<String>();
            ZipInputStream stream = null;
            try {
                stream = openArchive();
                ZipEntry nextEntry = stream.getNextEntry();
                while (nextEntry != null) {
                    if (!nextEntry.isDirectory()) {
                        pathCollection.add(nextEntry.getName());
                    }
                    nextEntry = stream.getNextEntry();
                }
            } catch (InvalidRegistryObjectException e) {
                TMWCoreUI.log(e);
            } catch (IOException e) {
                TMWCoreUI.log(e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        TMWCoreUI.log(e);
                    }
                }
            }
            paths = pathCollection.toArray(new String[pathCollection.size()]);
        }
        return filter(paths);
    }

    private IPath[] filter(String[] paths) {
        final IPath[] filtered = new IPath[paths.length];
        for (int i = 0; i < paths.length; i++) {
            filtered[i] = new Path(filter(paths[i])).makeRelative();
        }
        return filtered;
    }

    private String filter(String path) {
        if (path.endsWith(TEMPLATE_FILE_EXTENSION)) {
            path = path.substring(0, path.length() - TEMPLATE_FILE_EXTENSION.length());
        }
        VelocityContext ctx = new VelocityContext(new TemplateContext(context));
        final StringWriter val = new StringWriter();
        try {
            Velocity.evaluate(ctx, val, null, path);
            path = val.toString();
        } catch (Exception e) {
            TMWCoreUI.log(e);
        }
        return path;
    }

    private ZipInputStream openArchive() throws IOException {
        final InputStream stream = FileLocator.openStream(Platform.getBundle(element.getContributor().getName()),
                new Path(element.getAttribute("file")), true);
        final ZipInputStream inputStream = new ZipInputStream(stream);
        return inputStream;
    }

    public void copyFiles(IPath[] files, IProgressMonitor monitor) throws CoreException {
        final String filesToOpen = Util.neverNull(element.getAttribute("open-files"));
        HashSet<IPath> fls = new HashSet<IPath>(Arrays.asList(files));
        ZipInputStream stream = null;
        try {
            VelocityContext ctx = new VelocityContext(new TemplateContext(context));
            stream = openArchive();
            monitor.beginTask("Generating project contents", IProgressMonitor.UNKNOWN);
            ZipEntry entry;
            while ((entry = stream.getNextEntry()) != null && !monitor.isCanceled()) {
                String nm = entry.getName();
                boolean open = filesToOpen.contains(nm.endsWith(TEMPLATE_FILE_EXTENSION) ? nm.substring(0, nm.length()
                        - TEMPLATE_FILE_EXTENSION.length()) : nm);
                IPath name = new Path(filter(nm));
                if (fls.contains(name)) {
                    final InputStream contents;
                    if (nm.endsWith(TEMPLATE_FILE_EXTENSION)) {
                        contents = copyTemplate(project, name, stream, (int) entry.getSize(), ctx, monitor);
                    } else {
                        contents = stream;
                    }
                    IFile file = context.addFile(project, name, contents, new SubProgressMonitor(monitor, 10));
                    if (open) {
                        openFiles.add(file);
                    }
                }
                stream.closeEntry();
            }
            monitor.done();
        } catch (Exception e) {
            throw new CoreException(new Status(IStatus.ERROR, TMWCoreUI.PLUGIN_ID, "Project creation failed", e));
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // Ignore - something really bad happened
                }
            }
        }
    }

    private InputStream copyTemplate(IProject project, IPath name, ZipInputStream stream, int size,
            VelocityContext ctx, IProgressMonitor monitor) throws IOException, CoreException {
        // Templates will not be more then a few megs - we can afford the memory
        ByteArrayOutputStream file = new ByteArrayOutputStream();

        Reader reader = new InputStreamReader(new NonClosingStream(stream));
        Writer writer = new OutputStreamWriter(file);

        Velocity.evaluate(ctx, writer, name.toOSString(), reader);

        reader.close();
        writer.close();

        return new ByteArrayInputStream(file.toByteArray());
    }

    private static final class NonClosingStream extends FilterInputStream {
        private NonClosingStream(InputStream in) {
            super(in);
        }

        @Override
        public void close() throws IOException {
            // Avoid closing ZIP file
        }
    }

    private static final class TemplateContext extends AbstractContext {
        private final IProjectTemplateContext context;

        public TemplateContext(IProjectTemplateContext context) {
            this.context = context;
        }

        @Override
        public boolean internalContainsKey(Object key) {
            return context.getParameter((String) key) != null;
        }

        @Override
        public Object internalGet(String key) {
            return context.getParameter(key);
        }

        @Override
        public Object[] internalGetKeys() {
            return context.getParameterNames();
        }

        @Override
        public Object internalPut(String key, Object value) {
            if (key != null) {
                final Object v = context.getParameter(key);
                context.putParameter(key, value);
                return v;
            }
            return null;
        }

        @Override
        public Object internalRemove(Object key) {
            return internalPut((String) key, null);
        }

    }

    public IRunnableWithProgress getPostCreateAction() {
        if (openFiles.size() > 0) {
            return new OpenFilesRunnable(openFiles);
        }
        return null;
    }
}
