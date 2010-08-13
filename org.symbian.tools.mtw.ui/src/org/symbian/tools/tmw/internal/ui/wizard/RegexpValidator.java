package org.symbian.tools.tmw.internal.ui.wizard;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.symbian.tools.tmw.ui.TMWCoreUI;

public class RegexpValidator implements IValidator {
    private final String errorMessage;
    private final Pattern pattern;
    private final boolean match;

    public RegexpValidator(String pattern, String errorMessage, boolean match) {
        this.errorMessage = errorMessage;
        this.match = match;
        this.pattern = Pattern.compile(pattern);
    }

    public IStatus validate(Object value) {
        String string = value.toString();
        Matcher matcher = pattern.matcher(string);
        if (match && !matcher.matches()) {
            return new Status(IStatus.ERROR, TMWCoreUI.PLUGIN_ID, MessageFormat.format(errorMessage, string));
        } else if (!match && matcher.find()) {
            return new Status(IStatus.ERROR, TMWCoreUI.PLUGIN_ID, MessageFormat.format(errorMessage,
                    string.substring(matcher.start(), matcher.end())));
        } else {
            return Status.OK_STATUS;
        }
    }

}
