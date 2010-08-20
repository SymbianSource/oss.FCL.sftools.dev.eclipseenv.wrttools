package org.symbian.tools.tmw.previewer.preferences;

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.symbian.tools.tmw.previewer.IWrtEditingPreferences;
import org.symbian.tools.tmw.previewer.PreviewerPlugin;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class WrtEditingPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	private RadioGroupFieldEditor enableAutorefresh;

	public WrtEditingPreferencePage() {
		super(GRID);
		setPreferenceStore(PreviewerPlugin.getDefault().getPreferenceStore());
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
        Composite projectComposite = new Composite(getFieldEditorParent(), SWT.NONE);
        projectComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        projectComposite.setFont(getFieldEditorParent().getFont());

		String[][] namesAndValues = {
				{ "Enabled", MessageDialogWithToggle.ALWAYS },
				{ "Disabled", MessageDialogWithToggle.NEVER },
				{ "Prompt", MessageDialogWithToggle.PROMPT } };
		enableAutorefresh = new RadioGroupFieldEditor(
				IWrtEditingPreferences.PREF_AUTO_REFRESH,
				"Initial auto-refresh setting for Mobile Web Preview window",
				namesAndValues.length, namesAndValues, projectComposite, true);
		enableAutorefresh.setPreferenceStore(getPreferenceStore());
		enableAutorefresh.setPage(this);
		enableAutorefresh.load();
        addField(enableAutorefresh);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

//	@Override
//	protected void performDefaults() {
//		enableAutorefresh.loadDefault();
//		super.performDefaults();
//	}
//	
//	@Override
//	public boolean performOk() {
//		enableAutorefresh.store();
//		return super.performOk();
//	}
}