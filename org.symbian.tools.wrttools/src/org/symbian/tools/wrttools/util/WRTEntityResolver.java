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

package org.symbian.tools.wrttools.util;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This entity resolver class can be used when DOM Parser is used for local files
 * @author ranoliya
 *
 */
public class WRTEntityResolver {

	 
	/**
	 * 
	 * @param dtdSystemID -- DTD System ID
	 * @param dtdFilename -- Local DTD File name
	 * @return
	 */
	public EntityResolver inputResolveEntity(final String dtdSystemID, final String dtdName) {
		EntityResolver er = new EntityResolver() {

			/**
			 * This method resolves publicID and systemID for local files
			 */
  			public InputSource resolveEntity(String publicId, String systemId)
  					throws SAXException, IOException {
  					InputSource result = null;
  					InputStream resourceAsStream = this.getClass().getResourceAsStream(dtdName);
  					result = new InputSource(resourceAsStream);
  					result.setPublicId(publicId);
  					result.setSystemId(systemId);
  				return result;
  			}
  			
  		};
		return er;
	}
	
}
