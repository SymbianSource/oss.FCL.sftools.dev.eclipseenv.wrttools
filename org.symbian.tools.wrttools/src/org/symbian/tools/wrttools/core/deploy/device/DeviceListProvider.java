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
import java.util.ArrayList;
import java.util.List;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;

import org.symbian.tools.wrttools.Activator;

public class DeviceListProvider {

	private static final List<DeployDeviceInfo> deviceInfo = new ArrayList<DeployDeviceInfo>();

	private static WrtDiscoveryListener listener;

	/**
	 * Check whether the bluetooth is on or not.
	 * 
	 * @return whether the device is on.
	 */
	public static boolean isBloothToothConnected() {
		return LocalDevice.isPowerOn();
	}

	public static String getDeviceAddress(String deviceName,
			boolean allowDiscovery) {
		String result = null;
		for (DeployDeviceInfo deployDeviceInfo : deviceInfo) {
			if (deployDeviceInfo.getDeviceName().equals(deviceName)) {
				result = deployDeviceInfo.getDeviceBlueToothAddress();
			}
		}
		if (result == null && allowDiscovery) {
			discoverDevices(new NullProgressMonitor(), deviceName);
			for (DeployDeviceInfo deployDeviceInfo : deviceInfo) {
				if (deployDeviceInfo.getDeviceName().equals(deviceName)) {
					result = deployDeviceInfo.getDeviceBlueToothAddress();
				}
			}
		}
		return result;
	}

	public static List<DeployDeviceInfo> getDevices(
			IProgressMonitor progressMonitor) {
		discoverDevices(progressMonitor, null);
		return new ArrayList<DeployDeviceInfo>(deviceInfo);
	}

	/**
	 * Discover devices. Finds all devices if desiredDevice is null. If non-null
	 * search stops when the desired device is found.
	 */
	private static void discoverDevices(final IProgressMonitor progressMonitor,
			final String desiredDevice) {

		final Object inquiryCompletedEvent = new Object();
		deviceInfo.clear();

		listener = new WrtDiscoveryListener(desiredDevice,
				inquiryCompletedEvent, progressMonitor);

		synchronized (inquiryCompletedEvent) {
			boolean started;
			try {
				started = LocalDevice.getLocalDevice().getDiscoveryAgent()
						.startInquiry(DiscoveryAgent.GIAC, listener);
				if (started) {
					inquiryCompletedEvent.wait();
				}
			} catch (BluetoothStateException e) {
				Activator.log(IStatus.ERROR, e.getMessage(), e);
			} catch (InterruptedException e) {
				Activator.log(IStatus.ERROR, e.getMessage(), e);
			}
		}
	}

	public static boolean isCanceled() {
		return listener != null && listener.isCanceled;
	}

	public static void cancelSearch() {
		if (listener != null) {
			try {
				listener.isCanceled = true;
				if (listener.progressMonitor != null &&
					!listener.progressMonitor.isCanceled()) {
						listener.progressMonitor.setCanceled(true);
				}
				LocalDevice.getLocalDevice().getDiscoveryAgent().cancelInquiry(
						listener);
			} catch (BluetoothStateException e) {
				Activator.log(IStatus.ERROR, e.getMessage(), e);
			}
		}
	}

	private static final class WrtDiscoveryListener implements
			DiscoveryListener {
		final String desiredDevice;
		final Object inquiryCompletedEvent;
		final IProgressMonitor progressMonitor;
		boolean isCanceled;

		private WrtDiscoveryListener(String desiredDevice,
				Object inquiryCompletedEvent,
				IProgressMonitor progressMonitor) {
			this.desiredDevice = desiredDevice;
			this.inquiryCompletedEvent = inquiryCompletedEvent;
			this.progressMonitor = progressMonitor;
		}

		public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
			try {
				if (btDevice.getFriendlyName(false) != null
						&& btDevice.getFriendlyName(false).length() > 0) {
					DeployDeviceInfo deployDeviceInfo = new DeployDeviceInfo(
							btDevice.getFriendlyName(false), btDevice
							.getBluetoothAddress());
					deviceInfo.add(deployDeviceInfo);
					//System.out.println("Friendly name  :  "+ (btDevice.getFriendlyName(false)));
					// System.out.println("----device size   :"+deviceInfo.
					// size());
					if (desiredDevice != null
							&& desiredDevice.equals(deployDeviceInfo
									.getDeviceName())) {
						cancelSearch();
					}
					checkCanceled();
				}
			} catch (BluetoothStateException e) {
				Activator.log(IStatus.ERROR, e.getMessage(), e);
			} catch (IOException e) {
				Activator.log(IStatus.ERROR, e.getMessage(), e);
			}
		}
		
		private void checkCanceled() {
			if (!isCanceled && progressMonitor != null) {
				if (progressMonitor.isCanceled()) {
					cancelSearch();
				}
			}
		}

		public void inquiryCompleted(int discType) {
			synchronized (inquiryCompletedEvent) {
				inquiryCompletedEvent.notifyAll();
			}
		}

		public void serviceSearchCompleted(int transID, int respCode) {
		}

		public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
		}
	}
}
