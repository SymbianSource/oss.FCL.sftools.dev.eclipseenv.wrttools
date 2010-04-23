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
package org.symbian.tools.wrttools.core.libraries.jsdt;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.wst.jsdt.core.ast.IIfStatement;
import org.eclipse.wst.jsdt.core.infer.InferEngine;
import org.eclipse.wst.jsdt.core.infer.InferredAttribute;
import org.eclipse.wst.jsdt.core.infer.InferredType;
import org.eclipse.wst.jsdt.internal.compiler.ast.CompilationUnitDeclaration;

public class PhoneGapInferEngine extends InferEngine {
    private static final Map<String, String> TYPE_TO_PROPERTY = new TreeMap<String, String>();
    private CompilationUnitDeclaration compilationUnit;
    static {
        TYPE_TO_PROPERTY.put("Notification", "notification");
        TYPE_TO_PROPERTY.put("Accelerometer", "accelerometer");
        TYPE_TO_PROPERTY.put("Camera", "camera");
        TYPE_TO_PROPERTY.put("Contacts", "contacts");
        TYPE_TO_PROPERTY.put("Geolocation", "geolocation");
        TYPE_TO_PROPERTY.put("Media", "media");
        TYPE_TO_PROPERTY.put("Notification", "notification");
        TYPE_TO_PROPERTY.put("Orientation", "orientation");
        TYPE_TO_PROPERTY.put("Sms", "sms");
        TYPE_TO_PROPERTY.put("Storage", "storage");
    }

    @SuppressWarnings("restriction")
    @Override
    public void setCompilationUnit(CompilationUnitDeclaration compilationUnit) {
        this.compilationUnit = compilationUnit;
        super.setCompilationUnit(compilationUnit);
    }

    @Override
    public boolean visit(IIfStatement ifStatement) {
        // TODO Auto-generated method stub
        return super.visit(ifStatement);
    }

    @SuppressWarnings("restriction")
    @Override
    protected InferredType addType(char[] className, boolean isDefinition) {
        InferredType type = super.addType(className, isDefinition);
        if (TYPE_TO_PROPERTY.containsKey(String.valueOf(type.getName()))) {
            InferredType inferredType = compilationUnit.findInferredType("Navigator".toCharArray());
            System.out.println(inferredType);
            if (inferredGlobal != null) {
                InferredAttribute[] attributes = inferredGlobal.attributes;
                for (InferredAttribute attr : attributes) {
                    System.out.println(String.valueOf(attr.name));
                }
            }
            final InferredType definedType = findDefinedType("Navigator".toCharArray());
            System.out.println(definedType);
        }
        return type;
    }

}
