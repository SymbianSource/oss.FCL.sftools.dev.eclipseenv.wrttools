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

import org.eclipse.wst.jsdt.core.ast.IASTNode;
import org.eclipse.wst.jsdt.core.ast.IExpression;
import org.eclipse.wst.jsdt.core.ast.ISingleNameReference;
import org.eclipse.wst.jsdt.core.infer.InferEngine;
import org.eclipse.wst.jsdt.core.infer.InferredType;
import org.eclipse.wst.jsdt.internal.compiler.ast.CompilationUnitDeclaration;

public class PhoneGapInferEngine extends InferEngine {
    private static final Map<String, String> TYPE_TO_PROPERTY = new TreeMap<String, String>();
    private static final char[] NAVIGATOR_TYPE = "Navigator".toCharArray();
    
    private CompilationUnitDeclaration compUnit;
    
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
    
    @Override
    public void setCompilationUnit(
    		CompilationUnitDeclaration scriptFileDeclaration) {
    	this.compUnit = scriptFileDeclaration;
    	super.setCompilationUnit(scriptFileDeclaration);
    }
    
	@Override
	protected InferredType getInferredType2(IExpression fieldReceiver) {
		if (fieldReceiver.getASTType() == IASTNode.SINGLE_NAME_REFERENCE) {
			ISingleNameReference nameReference = (ISingleNameReference) fieldReceiver;
			if ("navigator".equals(String.valueOf(nameReference.getToken()))) {
                return addType(NAVIGATOR_TYPE, true);
			}
		}
		return super.getInferredType2(fieldReceiver);
	}
	

    //    @Override
    //    public boolean visit(IAssignment assignment) {
    //        if (assignment.getLeftHandSide().getASTType() == IASTNode.FIELD_REFERENCE) {
    //            FieldReference reference = (FieldReference) assignment.getLeftHandSide();
    //            if (reference.receiver.getASTType() == IASTNode.SINGLE_NAME_REFERENCE) {
    //                ISingleNameReference nameReference = (ISingleNameReference) reference.receiver;
    //                if ("navigator".equals(String.valueOf(nameReference.getToken()))) {
    //                    return addNavigatorField(reference, assignment);
    //                }
    //            }
    //        }
    //        // TODO Auto-generated method stub
    //        return super.visit(assignment);
    //    }
    //	
    //	private boolean addNavigatorField(FieldReference fieldReference, IAssignment assignment) {
    //        pushContext();
    //        char[] possibleTypeName = NAVIGATOR_TYPE;
    //        InferredType newType = compUnit.findInferredType(possibleTypeName);
    //
    //        //create the new type if not found
    //        if (newType == null) {
    //            newType = addType(possibleTypeName);
    //        }
    //        newType.isDefinition = true;
    //
    //        newType.updatePositions(assignment.sourceStart(), assignment.sourceEnd());
    //
    //        //prevent Object literal based anonymous types from being created more than once
    //        if (passNumber == 1 && assignment.getExpression() instanceof IObjectLiteral) {
    //            return false;
    //        }
    //
    //        char[] memberName = fieldReference.token;
    //        int nameStart = (int) (fieldReference.nameSourcePosition >>> 32);
    //
    //        InferredType typeOf = getTypeOf(assignment.getExpression());
    //        IFunctionDeclaration methodDecl = null;
    //
    //        if (typeOf == null || typeOf == FunctionType) {
    //            methodDecl = getDefinedFunction(assignment.getExpression());
    //        }
    //
    //        if (methodDecl != null) {
    //            InferredMember method = newType.addMethod(memberName, methodDecl, nameStart);
    //        }
    //        // http://bugs.eclipse.org/269053 - constructor property not supported in JSDT
    //        else /*if (!CharOperation.equals(CONSTRUCTOR_ID, memberName))*/
    //        {
    //            InferredAttribute attribute = newType.addAttribute(memberName, assignment, nameStart);
    //            handleAttributeDeclaration(attribute, assignment.getExpression());
    //            attribute.initializationStart = assignment.getExpression().sourceStart();
    //            if (attribute.type == null) {
    //                attribute.type = typeOf;
    //            }
    //        }
    //        return true;
    //
    //    }
}
