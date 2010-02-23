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

package org.symbian.tools.wrttools.core.deploy;


import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.core.IWRTConstants;
import org.symbian.tools.wrttools.core.deploy.device.DeployDeviceInfo;
import org.symbian.tools.wrttools.core.deploy.device.DeviceListProvider;
import org.symbian.tools.wrttools.core.deploy.emulator.EmulatorListProvider;
import org.symbian.tools.wrttools.core.deployer.DeployerMessages;

public class DeployPreferencePage extends PreferencePage implements 
IWorkbenchPreferencePage {     
	
	private Composite contents;
	private Button deviceRadioButton;
	private Button emulatorRadioButton;
	private Combo selectionCombo;
	private Button searchDevices;
	private Text emulatorDeployerPathText;
	private Label selectionComboLabel;
	private Button debugButton;
	private IRunnableWithProgress runnable;
	
	private Logger log = Logger.getLogger(getClass().getName());
	
	
	private final HashMap<String, String> emulatorHashMap = EmulatorListProvider.populateEmulators();
	private Set<String> emulatorKeySet = emulatorHashMap.keySet();
	private String[] emulatorItems = (String[])emulatorKeySet.toArray(new String[emulatorKeySet.size()]);
	private static String[] devices=null;

	
	public DeployPreferencePage() {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	//--------------------------------------------------------------------------------------//
	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate various
	 * types of preferences. Each field editor knows how to save and restore itself.
	 */
	public void createFieldEditors(Composite parent) {
		GridLayout layout = new GridLayout(4, false);
		parent.setLayout(layout);
		deviceRadioButton = new Button(parent, SWT.RADIO);
		deviceRadioButton.setText("Device");
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		deviceRadioButton.setLayoutData(gd);

		emulatorRadioButton = new Button(parent, SWT.RADIO);
		emulatorRadioButton.setText("Emulator"); 
		gd = new GridData();
		gd.horizontalSpan = 2;
		emulatorRadioButton.setLayoutData(gd);

		selectionComboLabel = new Label(parent, SWT.READ_ONLY);
		selectionCombo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		selectionCombo.setLayoutData(gd);
		selectionCombo.setSize(100,100);

		searchDevices = new Button(parent,SWT.PUSH);
		searchDevices.setVisible(false);
		searchDevices.setText("Search");

		emulatorDeployerPathText = new Text(parent, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 4;
		gd.grabExcessHorizontalSpace = true;
		emulatorDeployerPathText.setLayoutData(gd);
		emulatorDeployerPathText.setVisible(false);	
		
		debugButton = new Button(parent, SWT.CHECK);
		debugButton.setText("Enable diagnostic logging");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 4;
		debugButton.setLayoutData(gd);

		//---------------------------start setting  event listener---------------------//
		selectionCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (emulatorRadioButton != null	&& emulatorRadioButton.getSelection()) {
					if (selectionCombo.getText().trim().equalsIgnoreCase(	"None")) { //$NON-NLS-1$
						emulatorDeployerPathText.setText(emulatorHashMap.get(selectionCombo.getText()));
						emulatorDeployerPathText.setText("");
					} else {
						emulatorDeployerPathText.setText(emulatorHashMap.get(selectionCombo.getText()).concat(IWRTConstants.DEPLOY_PATH));
					}
		/* TODO XXX */
					log.info("DeployPreferencePage.addSelectionListener: Emulator Deployer Path Text: "+ emulatorDeployerPathText);
				}
			}
		});

		emulatorRadioButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				selectionCombo.removeAll();
				selectionCombo.setItems(emulatorItems);
				updateButtonStates();
				contents.layout();
			}
		});

		deviceRadioButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				// once the status is set the UI will be up and the scanning for the deployment devices
				// will begin in the JOB thread so lets put the status message so that user gets the information
				// that devices are being loaded.
				selectionCombo.removeAll();
				if(devices!=null && DeviceListProvider.isBloothToothConnected()){
					selectionCombo.setItems(devices);
				}				
				if(DeviceListProvider.isBloothToothConnected()){
					updateButtonStates();
					contents.layout();
				}
			}
		});	

		searchDevices.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if(DeviceListProvider.isBloothToothConnected()){
				selectionCombo.removeAll();
				loadDevices("");//$NON-NLS-1$
				}
				else{
					Shell shell = new Shell();
					MessageDialog.openInformation(shell,"Search Problem",DeployerMessages.getString("Deployer.bluetooth.notconnected.msg"));
				}
			}
		});
	}

	//------------------------------------------------//
	
	public void init(IWorkbench workbench) {
	}
	
	//---------------------------------------------------------------------------//
	
	/**
	 * Update the state of the deploy button based on various conditions.
	 */
	private void updateButtonStates() {
		if (!Platform.OS_WIN32.equals(Platform.getOS())) {			
			emulatorRadioButton.setVisible(false);
			emulatorRadioButton.setSelection(false);
			deviceRadioButton.setSelection(true);	
			deviceRadioButton.setVisible(false);
		}	
		if(emulatorRadioButton != null && emulatorRadioButton.getSelection()){
			searchDevices.setVisible(false);
			emulatorDeployerPathText.setEnabled(false);
			emulatorDeployerPathText.setVisible(true);
			selectionComboLabel.setText("Select Emulator:");				

			String storeEmlItem=	getPreferenceStore().getString(PreferenceConstants.SELECTED_EMULATOR_NAME);
			if(emulatorItems!=null&&emulatorItems.length>0){
				int i=0;
				for( String emulatorItem: emulatorItems ){						
					if(storeEmlItem.trim().equalsIgnoreCase(emulatorItem.trim())){break;}
					i++;						
				}
				selectionCombo.setItems(emulatorItems);	
				selectionCombo.select(i);
				emulatorDeployerPathText.setText(getPreferenceStore().getString(PreferenceConstants.SELECTED_EMULATOR_PATH));

			}else{
				emulatorDeployerPathText.setText("");
			}	
			
			/* TODO XXX */
			log.info("DeployPreferencePage.updateButtonStates: Emulator Deployer Path Text: "+ emulatorDeployerPathText);


		} else if(deviceRadioButton != null && deviceRadioButton.getSelection()){		
			searchDevices.setVisible(true);
			searchDevices.setEnabled(true);
			selectionComboLabel.setText("Select Device:");
			emulatorDeployerPathText.setVisible(false);

			if(devices!=null&&devices.length>1){
				String storeDeviceName=	getPreferenceStore().getString(PreferenceConstants.SELECTED_DEVICE_NAME);
				int i=0;
				for( String devicename: devices ){						
					if(storeDeviceName.trim().equalsIgnoreCase(devicename.trim())){break;}
					i++;						
				}
				if(DeviceListProvider.isBloothToothConnected()){
					selectionCombo.setItems(devices);	
					selectionCombo.select(i);
				}
			}else{				
				selectionCombo.add(getPreferenceStore().getString(PreferenceConstants.SELECTED_DEVICE_NAME));
				selectionCombo.select(0);
			}
		}
	}
			
		public Control createContents(Composite parent) {
			initializeDialogUnits(parent);					
			Composite result= new Composite(parent, SWT.NONE);
			result.setFont(parent.getFont());			
			GridLayout layout= new GridLayout();
			layout.marginHeight= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
			layout.marginWidth= 0;
			layout.verticalSpacing= convertVerticalDLUsToPixels(10);
			layout.horizontalSpacing= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
			result.setLayout(layout);
			
			createFieldEditors(result);	
			contents = result;
			initFields();	
			
			Dialog.applyDialogFont(result);		
			
			return result;
		}
		
		private void initFields() {
			performDefaults();
		}	
		
		public void performDefaults() {
			IPreferenceStore prefStore= getPreferenceStore();				
			deviceRadioButton.setSelection(PreferenceConstants.WRT_DEPLOY_CHOICE_DEVICE.equals(prefStore.getString(PreferenceConstants.WRT_DEPLOY_CHOICE)));
			emulatorRadioButton.setSelection(PreferenceConstants.WRT_DEPLOY_CHOICE_EMULATOR.equals(prefStore.getString(PreferenceConstants.WRT_DEPLOY_CHOICE)));
			selectionCombo.removeAll();

			if(emulatorRadioButton != null && emulatorRadioButton.getSelection()){
				selectionCombo.setItems(emulatorItems);			
				emulatorDeployerPathText.setText(prefStore.getString(PreferenceConstants.SELECTED_EMULATOR_PATH));
				/* TODO XXX */
				log.info("DeployPreferencePage.performDefaults: Emulator Deployer Path Text: "+ emulatorDeployerPathText);
			} 
			
			/* TODO XXX */
			log.info("DeployPreferencePage.performDefaults: emulatorRadioButton not selected: "+ emulatorDeployerPathText);

			/*if(deviceRadioButton != null && deviceRadioButton.getSelection()){ 	
				devices = new String[1];	
				devices[0]	=prefStore.getString(PreferenceConstants.SELECTED_DEVICE_NAME);
			}
			*/
			
			debugButton.setSelection(prefStore.getBoolean(PreferenceConstants.DEBUG_ENABLED));
			updateButtonStates();
			super.performDefaults();
		}
	   protected void performApply() {
	        super.performOk();
	    }
		public boolean performOk() {
			boolean wantSelection = false;
		
			if(selectionCombo.getSelectionIndex() < 0 || (selectionCombo.getItem(selectionCombo.getSelectionIndex()).toString().equalsIgnoreCase("none"))){
				Shell shell = new Shell();
				wantSelection = MessageDialog.openQuestion(shell,"Widget Deployment Settings","You have not made any selection. Do you want to select device?");
			}
			if(!wantSelection){
				IPreferenceStore prefOkStore = getPreferenceStore();		
	
				if (deviceRadioButton.getSelection()) {
					prefOkStore.setValue(PreferenceConstants.WRT_DEPLOY_CHOICE,	PreferenceConstants.WRT_DEPLOY_CHOICE_DEVICE);
				
					prefOkStore.setValue(PreferenceConstants.SELECTED_DEVICE_NAME, selectionCombo.getText());
								
				} else if (emulatorRadioButton.getSelection()) {
					prefOkStore.setValue(PreferenceConstants.WRT_DEPLOY_CHOICE,	PreferenceConstants.WRT_DEPLOY_CHOICE_EMULATOR);
					prefOkStore.setValue(PreferenceConstants.SELECTED_EMULATOR_NAME, selectionCombo.getText());
					prefOkStore.setValue(PreferenceConstants.SELECTED_EMULATOR_PATH, emulatorDeployerPathText.getText());
				}
	
				boolean debugEnabled = debugButton.getSelection();
				prefOkStore.setValue(PreferenceConstants.DEBUG_ENABLED, 
						Boolean.valueOf(debugEnabled).toString());
				
/* TODO -- do not enable blueCove diagnostics
				Activator.enableBlueCoveDiagnostics(debugEnabled);
*/
	
				boolean res = super.performOk();
				return res;
			}
			return false;
	}
	    
	    /**
		 * Runs a thread to load the devices present for the deployment.
		 * Does not effect the loading of the UI.
		 * @param device the device earlier saved .
		 */
		private void loadDevices(final String device) {		
			runnable = new IRunnableWithProgress(){
				public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				List<DeployDeviceInfo> deviceInfo = DeviceListProvider
						.getDevices(monitor);
				devices = new String[deviceInfo.size() + 1];
				int i = 1;

				devices[0] = DeployMessages.getString("View.none.text");//$NON-NLS-1$
				for (DeployDeviceInfo deployDeviceInfo : deviceInfo) {
					devices[i] = deployDeviceInfo.getDeviceName();
					i++;
				}
				// refresh the devices combo in the async thread
				deviceRadioButton.getDisplay().asyncExec(new Runnable() {

					public void run() {
						// put the devices found in the combo
						if(DeviceListProvider.isBloothToothConnected())
							selectionCombo.setItems(devices);
						if (device == null || device.length() < 1) {
							selectionCombo.setText(DeployMessages
									.getString("View.none.text")); //$NON-NLS-1$
						} else {
							selectionCombo.setText(device);
						}
						
						// Let the user know that the device can now be selected
						Shell shell = new Shell();
						MessageDialog.openInformation(shell,"Deployment Preferences: Search Completed","Device Search Completed. Make the device selection.");
					}
				});

			}
		};		
			
			// Use the progess service to execute the runnable

		ProgressMonitorDialog dialog = new ProgressMonitorDialog(searchDevices.getDisplay().getActiveShell());		
		try {
		    	 dialog.run(true, true, runnable);
		} catch (InvocationTargetException e) {
		    	 Activator.log(IStatus.ERROR, "Exception loading devices", e);
		    	
		} catch (InterruptedException e) {
		    	 Activator.log(IStatus.ERROR, "Exception loading devices", e);
		}
		
		
/*	
		ProgressMonitorDialog dialogDone = new ProgressMonitorDialog(selectionCombo.getShell());
		try {
		dialogDone.run(true, true, new IRunnableWithProgress() {
		public void run(IProgressMonitor monitor ) {
		monitor.beginTask("Searching for Devices", IProgressMonitor.UNKNOWN);

		monitor.setTaskName("Deployment Completed. Select the Deployment Device.");
		monitor.done();
		}
		});
		} catch (InvocationTargetException e) {
		// TODO Auto-generated catch block
			Activator.log(IStatus.ERROR, "Exception loading devices", e);
		
		} catch (InterruptedException e) {
		// TODO Auto-generated catch block
			Activator.log(IStatus.ERROR, "Exception loading devices", e);
		}
*/
		}
	}
