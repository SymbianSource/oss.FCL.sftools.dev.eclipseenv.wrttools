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
package org.symbian.tools.wrttools.core.deploy.device;

import java.io.IOException;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

import org.eclipse.core.runtime.IStatus;

import org.symbian.tools.wrttools.Activator;

public class ServicesProvider {
	
	static final UUID OBEX_OBJECT_PUSH = new UUID(0x1105);
	 
	private static String serviceURL;
	private static String btAddress;
	
	public static String getServicesFound(final String deviceName){
		try {
			serviceURL="";
			btAddress = DeviceListProvider.getDeviceAddress(deviceName, true);


	        UUID serviceUUID = OBEX_OBJECT_PUSH;

	        final Object serviceSearchCompletedEvent = new Object();

	        DiscoveryListener listener = new DiscoveryListener() {

	            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
	            }

	            public void inquiryCompleted(int discType) {
	            }

	            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
	            	try{	            		
	            		for (int i = 0; i < servRecord.length; i++) {
	            			if(servRecord[i].getHostDevice().getFriendlyName(false).equals(deviceName)){	            				
	            				serviceURL = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
	            			}
	            		}
	            	} catch(BluetoothStateException e){
	            		Activator.log(IStatus.ERROR, e.getMessage(), e);
	            	} catch (IOException e) {
	            		Activator.log(IStatus.ERROR, e.getMessage(), e);
					}
	            }

	            public void serviceSearchCompleted(int transID, int respCode) {
	                synchronized(serviceSearchCompletedEvent){
	                    serviceSearchCompletedEvent.notifyAll();
	                }
	            }

	        };

	        UUID[] searchUuidSet = new UUID[] { serviceUUID };
	        int[] attrIDs =  new int[] {
	                0x0100 // Service name
	        };
	        
	        RemoteDevice bt = new WrapperRemoteDevice(btAddress);
	        synchronized(serviceSearchCompletedEvent) {
	        	LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attrIDs, searchUuidSet, bt, listener);
	        	serviceSearchCompletedEvent.wait();
	        }

	    } catch(IOException e){
	    	Activator.log(IStatus.ERROR, "Error in Bluetooth service discovery", e);		
		} catch (InterruptedException e) {
			Activator.log(IStatus.ERROR, "Error in Bluetooth service discovery", e);		
		}	    
	    return serviceURL;
	}
}