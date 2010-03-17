/**
 * Copyright (c) 2009-2010 Symbian Foundation and/or its subsidiary(-ies).
 * All rights reserved.
 * This component and the accompanying materials are made available
 * under the terms of the License "Eclipse Public License v1.0"
 * which accompanies this distribution, and is available
 * at the URL "http://www.eclipse.org/legal/epl-v10.html".
 *
 * Initial Contributors:
 * Nokia Corporation - initial contribution.
 * 
 * Contributors:
 * 
 * Description:
 * 
 */

///////////////////////////////////////////////////////////////////////////////
// The UIInit script is included before the rest of the UI scripts to setup
// any resources needed by the UI toolkit.

// Create UI logger.
var uiLogger = new Logger();
uiLogger.level = uiLogger.LOG_LEVEL_OFF;
uiLogger.filter = ["QECR"];
