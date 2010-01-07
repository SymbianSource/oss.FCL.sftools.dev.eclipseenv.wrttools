/*******************************************************************************
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
 *******************************************************************************/
package org.symbian.tools.wrttools.core.internal.validation;

import java.io.IOException;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.ValidatorMessage;
import org.symbian.tools.wrttools.Activator;
import org.w3c.*;
import org.w3c.css.css.DocumentParser;
import org.w3c.css.css.StyleSheet;
import org.w3c.css.parser.CssError;
import org.w3c.css.properties.PropertiesLoader;
import org.w3c.css.util.ApplContext;
import org.w3c.css.util.Warning;

public class CssBuildValidator extends AbstractValidator {

	@Override
	public ValidationResult validate(IResource resource, int kind,
			ValidationState state, IProgressMonitor monitor) {
		return main(resource.getLocationURI().toString(), resource);
	}

	public ValidationResult main(String uri, IResource resource) {
		String language = "en";
		String profile = "css3"; // css2, css21 (default), css3, svg, svgbasic,
		// svgtiny, atsc-tv, mobile, tv
		String medium = ""; // (default), aural, braille, embossed,
		// handheld, print, projection, screen,
		// tty, tv, presentation
		int warningLevel = 2; // -1 (no warning), 0, 1, 2 (default, all the
		// warnings)

		// first, we get the parameters and create an application context
		ApplContext ac = new ApplContext(language);

		if (profile != null && !"none".equals(profile)) {
			if ("css1".equals(profile) || "css2".equals(profile)
					|| "css21".equals(profile) || "css3".equals(profile)
					|| "svg".equals(profile) || "svgbasic".equals(profile)
					|| "svgtiny".equals(profile)) {
				ac.setCssVersion(profile);
			} else {
				ac.setProfile(profile);
				ac.setCssVersion(PropertiesLoader.config
						.getProperty("defaultProfile"));
			}
		} else {
			ac.setProfile(profile);
			ac.setCssVersion(PropertiesLoader.config
					.getProperty("defaultProfile"));
		}

		// medium to use
		ac.setMedium(medium);

		// HTML document
		try {
			DocumentParser URLparser = new DocumentParser(ac, uri);

			return handleRequest(ac, uri, URLparser.getStyleSheet(),
					warningLevel, true, resource);
		} catch (Exception e) {
			Activator.log(e);
		}
		return null;
	}

	private ValidationResult handleRequest(ApplContext ac, String title,
			StyleSheet styleSheet, int warningLevel,
			boolean errorReport, IResource resource) throws Exception {

		if (styleSheet == null) {
			throw new IOException(ac.getMsg().getServletString("process") + " "
					+ title);
		}

		styleSheet.findConflicts(ac);
		ValidationResult result = new ValidationResult();
		CssError[] errors = styleSheet.getErrors().getErrors();
		for (CssError cssError : errors) {
			String msg = cssError.getException().getLocalizedMessage();
			if (msg != null && msg.trim().length() > 0) {
				ValidatorMessage message = createMessage(resource, cssError
						.getLine(), msg, IMarker.SEVERITY_ERROR);
				result.add(message);
			}
		}
		Warning[] warnings = styleSheet.getWarnings().getWarnings();
		for (Warning warning : warnings) {
			ValidatorMessage message = createMessage(resource, warning
					.getLine(), warning.getWarningMessage(),
					IMarker.SEVERITY_WARNING);
			result.add(message);
		}
		return result;
	}

	private ValidatorMessage createMessage(IResource resource, int line,
			String msg, int severity) {
		ValidatorMessage message = ValidatorMessage.create(msg, resource);
		message.setAttribute(IMarker.LINE_NUMBER, line);
		message.setAttribute(IMarker.SEVERITY, severity);
		return message;
	}
}
