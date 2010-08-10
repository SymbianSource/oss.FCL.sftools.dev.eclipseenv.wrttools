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
package org.symbian.tools.wrttools.core.deploy.emulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.symbian.tools.tmw.core.projects.IMTWProject;
import org.symbian.tools.tmw.ui.deployment.IDeploymentTarget;
import org.symbian.tools.tmw.ui.deployment.IDeploymentTargetType;
import org.symbian.tools.wrttools.core.IWRTConstants;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class EmulatorTargetType implements IDeploymentTargetType {
    /**
     * Map containing of the SDKS with the corresponding paths.
     */
    private Map<String, Emulator> listofEmulators;

    public EmulatorTargetType() {
        discoverTargets(new NullProgressMonitor());
    }

    public IDeploymentTarget[] getTargets(IMTWProject project) {
        Collection<Emulator> values = listofEmulators.values();
        return values.toArray(new Emulator[values.size()]);
    }

    public void discoverTargets(IProgressMonitor monitor) {
        if (listofEmulators == null) {
            listofEmulators = new HashMap<String, Emulator>();
        }
        listofEmulators.clear();
        initialize();
    }

    public IDeploymentTarget findTarget(IMTWProject project, String id) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean targetsDiscovered() {
        return true;
    }

    public ISchedulingRule getSchedulingRule(IDeploymentTarget target) {
        return null;
    }

    // helper methods

    /**
     * This will parse the xml and create the nodes to be displayed on the table
     * viewer. The information will be used by content & label provider to get
     * the contents and display accordingly in the list of the projects in the view.
     */
    public Map<String, Emulator> initialize() {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // Parse the devices.xml and retrieve the list of the emulators from it
        // and build the list to be displayed in the view.

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            File file = new File(IWRTConstants.DEVICES_XML_PATH);
            if (!file.exists()) {
                IPath otherPath = new Path(System.getProperty("user.home"))
                        .append(IWRTConstants.DEVICES_VISTA_XML_PATH);
                file = otherPath.toFile();
            }
            if (file.exists()) {
                FileInputStream fin = new FileInputStream(file);
                Document document = builder.parse(fin);
                NodeList childNodes = document.getChildNodes();

                for (int i = 0; i < childNodes.getLength(); i++) {

                    Node node = childNodes.item(i);
                    String nodeName = node.getNodeName();
                    // If the node name is "devices" it is the root node of the xml.
                    if (nodeName.equals(IWRTConstants.DEVICES_TAG)) {
                        // once we have the root node get the child information to
                        // build the devices list.
                        createDevicesList(node);

                    }
                }
            }
        } catch (ParserConfigurationException e) {
            //          WidgetUtils.getView().getLogger().severe(e.getMessage());
        } catch (SAXException e) {
            //          WidgetUtils.getView().getLogger().severe(e.getMessage());
        } catch (IOException e) {
            //          WidgetUtils.getView().getLogger().severe(e.getMessage());
        }
        return listofEmulators;
    }

    /**
     * Creates the devices nodes in the table.
     * @param parentNode
     */
    private void createDevicesList(Node parentNode) {
        NodeList list = getChildNodes(parentNode);
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            String nodeName = node.getNodeName();
            if (nodeName.equals(IWRTConstants.DEVICE_TAG)) {
                createDeviceChildNode(node);
            }
        }
    }

    /**
     * Gets the EPOC ROOT node and than finally the list of devices.
     * @param parentNode
     */
    private void createDeviceChildNode(Node parentNode) {
        NodeList list = getChildNodes(parentNode);
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            String nodeName = node.getNodeName();
            if (nodeName.equals(IWRTConstants.EPOC_ROOT_TAG)) {
                addProject(node, parentNode);
            }
        }
    }

    /**
     * Adds the devices to the list to be displayed.
     * @param deviceNode the device node 
     * @param epocNode the epoc root node.
     */
    private void addProject(Node epocNode, Node deviceNode) {
        NodeList list = getChildNodes(epocNode);
        NamedNodeMap attributes = deviceNode.getAttributes();
        String sdkId = "";
        String sdkName = "";
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            if (item.getNodeName().equals(IWRTConstants.NAME_ATTR)) {
                sdkName = item.getNodeValue();
            }
            if (item.getNodeName().equals(IWRTConstants.ID_ATTR)) {
                sdkId = item.getNodeValue();
            }
        }
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (sdkName.equals(IWRTConstants.EMULATOR_NAME)) {
                listofEmulators.put(sdkId, new Emulator(sdkId, node.getNodeValue()));
            }
        }
    }

    /**
     * Returns the child list of the particular Node.
     * @param parentNode
     */
    private static NodeList getChildNodes(Node parentNode) {
        return parentNode.getChildNodes();
    }

}
