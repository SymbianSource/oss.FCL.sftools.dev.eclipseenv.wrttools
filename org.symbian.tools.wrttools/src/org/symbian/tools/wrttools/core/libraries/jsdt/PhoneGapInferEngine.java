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

import org.eclipse.wst.jsdt.core.ast.IASTNode;
import org.eclipse.wst.jsdt.core.ast.IExpression;
import org.eclipse.wst.jsdt.core.ast.ISingleNameReference;
import org.eclipse.wst.jsdt.core.infer.InferEngine;
import org.eclipse.wst.jsdt.core.infer.InferredType;

public class PhoneGapInferEngine extends InferEngine {
    private static final char[] NAVIGATOR_TYPE = "Navigator".toCharArray();

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
