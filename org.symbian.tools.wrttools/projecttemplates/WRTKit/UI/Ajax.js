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
// Ajax utility calss to create XmlHttpRequest object
function Ajax() 
{
	//	xmlHttpRequest object	
	var request = null;

    // branch for native XMLHttpRequest object
    if(window.XMLHttpRequest && !(window.ActiveXObject)) {
    	try 
		{
			request = new XMLHttpRequest();
			try
			{
				//	attach the Bypass code, if the browser is firefox
				if(netscape.security.PrivilegeManager.enablePrivilege)
				{
					//	duplicate the function
					request._open = request.open;
					
					//	redefine the function definition
					request.open = function(method, url, flag)
					{
						try
						{
							// Enable Universal Browser Read
							netscape.security.PrivilegeManager.enablePrivilege("UniversalBrowserRead");

							//	call the native XmlHttpRequest.open method
							this._open(method, url, flag);
						}catch(e)
						{
							//	call the native XmlHttpRequest.open method
							this._open(method, url, flag);
						}
					};
				}
			}
			catch(e)
			{
				//	eatup all exceptions
			}
		} 
		catch(e) {
			request = null;
        }
    // branch for IE/Windows ActiveX version
    } else if(window.ActiveXObject) {
       	try {
        	request = new ActiveXObject("Msxml2.XMLHTTP");
      	} catch(e) {
        	try {
          		request = new ActiveXObject("Microsoft.XMLHTTP");
        	} catch(e) {
          		alert('Failed to create XmlHttprequest');
				return null;
        	}
		}
    }
	
	return (request);
}
