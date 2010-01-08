package org.symbian.tools.wrttools.dialogs;

import java.io.File;
import java.text.MessageFormat;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.symbian.tools.wrttools.util.ProjectUtils;

public class AptanaProjectSelectionDialog extends TitleAreaDialog {
	private File project;
	private Text location;

	public AptanaProjectSelectionDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("WebRuntime Tools");
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Select Aptana WRT Project");
		setMessage("Select an Aptana WRT project or Aptana workspace containing one");
		Composite root = (Composite) super.createDialogArea(parent);

		Composite workingArea = new Composite(root, SWT.NONE);
		workingArea.setLayoutData(new GridData(GridData.FILL_BOTH));

		workingArea.setLayout(new GridLayout(2, false));
		Label label = new Label(workingArea, SWT.NONE);
		label.setLayoutData(new GridData(GridData.BEGINNING,
				GridData.BEGINNING, false, false, 2, 1));
		label.setText("Specify Aptana project or Aptana workspace location:");
		location = new Text(workingArea, SWT.BORDER);
		location.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		location.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});

		Button browse = new Button(workingArea, SWT.NONE);
		browse.setText("Browse...");
		browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browse();
			}
		});

		location.setText(ProjectUtils.getDefaultAptanaLocation());

		return root;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		validate();
		setErrorMessage(null);
	}
	
	protected void browse() {
		DirectoryDialog dialog = new DirectoryDialog(getShell());
		dialog.setFilterPath(location.getText());
		dialog.setMessage("Select Aptana WRT project or workspace");
		dialog.setText("Web Runtime Tools");

		String string = dialog.open();
		if (string != null) {
			location.setText(string);
		}
	}

	protected void validate() {
		String error = null;
		File f = new File(location.getText());
		if (f.isDirectory()) {
			if (!ProjectUtils.isAptanaProject(f)) {
				File[] files = f.listFiles();
				f = null;
				for (File file : files) {
					if (ProjectUtils.isAptanaProject(file)) {
						f = file;
						break;
					}
				}
				if (f == null) {
					error = MessageFormat.format("{0} is not a WRT project or an Aptana workspace containing WRT projects", location.getText());
				}
			}
		} else {
			error = "Specified folder does not exist";
		}
		project = f;
		setErrorMessage(error);
		Button button = getButton(IDialogConstants.OK_ID);
		if (button != null) {
			button.setEnabled(error == null);
		}
	}

	@Override
	protected void cancelPressed() {
		project = null;
		super.cancelPressed();
	}

	public File getProject() {
		return project;
	}
}
