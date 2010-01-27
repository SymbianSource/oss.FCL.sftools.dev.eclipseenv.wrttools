/**
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
 */

package org.symbian.tools.wrttools.core.packager;

import org.eclipse.core.runtime.QualifiedName;

public class WRTPackagerConstants {
	
	public static QualifiedName EXCLUDE_PROPERTY = new QualifiedName("com.nokia.wrt.packager", "exclude");
	

	public static String STA_PKG_PASSED = PackagerMessages.getString("WRTPackagerConstants.success"); //$NON-NLS-1$
	public static String STA_PKG_FAILED = PackagerMessages.getString("WRTPackagerConstants.failed"); //$NON-NLS-1$

	public static String ERR_PKG_MAN_PLIST_FILE_MISSING = PackagerMessages.getString("WRTPackagerConstants.package.missing.mandatory.files.plist"); //$NON-NLS-1$
	public static String ERR_PKG_MAN_HTML_FILE_MISSING = PackagerMessages.getString("WRTPackagerConstants.package.missing.mandatory.files.html"); //$NON-NLS-1$

	
	
	public static String STA_PKG_PROGRESS = PackagerMessages.getString("WRTPackagerConstants.inProgress"); //$NON-NLS-1$
	public static String STA_PKG_START = PackagerMessages.getString("WRTPackagerConstants.started"); //$NON-NLS-1$
	
	public static String LOG_NULL_INPUT= PackagerMessages.getString("WRTPackagerConstants.nullpera"); //$NON-NLS-1$
	public static String ERR_NULL= PackagerMessages.getString("WRTPackagerConstants.nullInput"); //$NON-NLS-1$
	public static String LOG_DIR_CREATE_FAIL= PackagerMessages.getString("WRTPackagerConstants.directoryCreateErr"); //$NON-NLS-1$
	public static String ERR_DIR_CREATE_FAIL= PackagerMessages.getString("WRTPackagerConstants.checkPermission"); //$NON-NLS-1$
	public static String LOG_UNSUPPORTED_INPUT= PackagerMessages.getString("WRTPackagerConstants.unSupportedInput"); //$NON-NLS-1$
	public static String ERR_UNSUPPORTED_INPUT= PackagerMessages.getString("WRTPackagerConstants.InputNotSupported"); //$NON-NLS-1$
	public static String LOG_RENAME_FAIL= PackagerMessages.getString("WRTPackagerConstants.renameFailed"); //$NON-NLS-1$
	public static String ERR_RENAME_FAIL= PackagerMessages.getString("WRTPackagerConstants.failureGenerate"); //$NON-NLS-1$
	public static String LOG_SOURCE_NOT_FOUND= PackagerMessages.getString("WRTPackagerConstants.srcNotFound"); //$NON-NLS-1$
	public static String ERR_SOURCE_NOT_FOUND= PackagerMessages.getString("WRTPackagerConstants.inputSourceNotFound"); //$NON-NLS-1$
	public static String LOG_UNREADABLE= PackagerMessages.getString("WRTPackagerConstants.srcNotReadable"); //$NON-NLS-1$
	public static String ERR_UNREADABLE= PackagerMessages.getString("WRTPackagerConstants.unReadable"); //$NON-NLS-1$
	public static String LOG_FILE_COPY_FAIL= PackagerMessages.getString("WRTPackagerConstants.canNotcopy"); //$NON-NLS-1$
	public static String ERR_FILE_COPY_FAIL= PackagerMessages.getString("WRTPackagerConstants.copyErr"); //$NON-NLS-1$
	public static String LOG_DEST_NOT_FOUND= PackagerMessages.getString("WRTPackagerConstants.NoDestination"); //$NON-NLS-1$
	public static String ERR_DEST_NOT_FOUND= PackagerMessages.getString("WRTPackagerConstants.noLocateDest"); //$NON-NLS-1$
	public static String LOG_NOT_WRITABLE= PackagerMessages.getString("WRTPackagerConstants.destNotWritable"); //$NON-NLS-1$
	public static String ERR_NOT_WRITABLE= PackagerMessages.getString("WRTPackagerConstants.destNotwritableErr"); //$NON-NLS-1$
	public static String LOG_READ_WRITE_FAIL= PackagerMessages.getString("WRTPackagerConstants.readWriteFail"); //$NON-NLS-1$
	public static String ERR_READ_WRITE_FAIL= PackagerMessages.getString("WRTPackagerConstants.readWritePermission"); //$NON-NLS-1$
	public static String LOG_FILE_CLOSE_FAIL= PackagerMessages.getString("WRTPackagerConstants.closeFile"); //$NON-NLS-1$
	public static String ERR_FILE_CLOSE_FAIL= PackagerMessages.getString("WRTPackagerConstants.closeFileerr"); //$NON-NLS-1$
	public static String LOG_ZIP_FAILED= PackagerMessages.getString("WRTPackagerConstants.ZipFailed"); //$NON-NLS-1$
	public static String ERR_ZIP_FAILED= PackagerMessages.getString("WRTPackagerConstants.packFailed"); //$NON-NLS-1$
	public static String LOG_EMPTY_SRC= PackagerMessages.getString("WRTPackagerConstants.emptyInputFolder"); //$NON-NLS-1$
	public static String ERR_EMPTY_SRC= PackagerMessages.getString("WRTPackagerConstants.emptyFolderErr"); //$NON-NLS-1$
}
