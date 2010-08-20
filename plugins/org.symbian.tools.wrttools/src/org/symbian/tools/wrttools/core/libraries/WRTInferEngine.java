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

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.wst.jsdt.core.ast.IASTNode;
import org.eclipse.wst.jsdt.core.ast.IExpression;
import org.eclipse.wst.jsdt.core.ast.IFunctionCall;
import org.eclipse.wst.jsdt.core.ast.IFunctionDeclaration;
import org.eclipse.wst.jsdt.core.ast.ISingleNameReference;
import org.eclipse.wst.jsdt.core.infer.InferEngine;
import org.eclipse.wst.jsdt.core.infer.InferredType;
import org.eclipse.wst.jsdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.wst.jsdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.wst.jsdt.internal.compiler.ast.StringLiteral;

@SuppressWarnings("restriction")
public class WRTInferEngine extends InferEngine {
    private final Map<String, char[]> serviceIdToType = new TreeMap<String, char[]>();
    private static final char[] NAVIGATOR_TYPE = "Navigator".toCharArray();

    public WRTInferEngine() {
        serviceIdToType.put("Service.AppManager:IAppManager", "AppManager".toCharArray());
        serviceIdToType.put("Service.Calendar:IDataSource", "DataSource".toCharArray());
        serviceIdToType.put("Service.Contact:IDataSource", "DataSource".toCharArray());
        serviceIdToType.put("Service.Landmarks:IDataSource", "DataSource".toCharArray());
        serviceIdToType.put("Service.Location:ILocation", "Location".toCharArray());
        serviceIdToType.put("Service.Logging:IDataSource", "DataSource".toCharArray());
        serviceIdToType.put("Service.MediaManagement:IDataSource", "DataSource".toCharArray());
        serviceIdToType.put("Service.Messaging:IMessaging", "Messaging".toCharArray());
        serviceIdToType.put("Service.Sensor:ISensor", "Sensor".toCharArray());
        serviceIdToType.put("Service.SysInfo:ISysInfo", "SysInfo".toCharArray());
    }

    @Override
    protected boolean handleFunctionCall(IFunctionCall messageSend, LocalDeclaration assignmentExpression) {
        if (assignmentExpression != null) {
            assignmentExpression.setInferredType(getTypeOf(messageSend));
        }
        return super.handleFunctionCall(messageSend, assignmentExpression);
    }

    @Override
    protected InferredType getTypeOf(IExpression expression) {
        if (expression.getASTType() == IASTNode.FUNCTION_CALL) {
            IFunctionCall call = (IFunctionCall) expression;
            char[] selector = call.getSelector();
            if (selector != null && "getServiceObject".equals(String.valueOf(selector))) {
                IExpression[] arguments = call.getArguments();
                if (arguments.length == 2) {
                    if (arguments[0].getASTType() == IASTNode.STRING_LITERAL
                            && arguments[1].getASTType() == IASTNode.STRING_LITERAL) {
                        String key = String.valueOf(((StringLiteral) arguments[0]).source()) + ":"
                                + String.valueOf(((StringLiteral) arguments[1]).source());
                        char[] type = serviceIdToType.get(key);
                        if (type != null) {
                            return new InferredType(type);
                        } else {
                            return null;
                        }
                    }
                }
            }
        } else if (expression instanceof SingleNameReference) {
            IFunctionDeclaration fun = getDefinedFunction(expression);
            if (fun != null) {
                return FunctionType;
            }
        }
        return super.getTypeOf(expression);
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
}
