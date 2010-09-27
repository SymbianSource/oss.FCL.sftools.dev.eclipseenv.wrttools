package org.symbian.tools.wrttools.core.internal.validation;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.ValidatorMessage;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.ITMWProject;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.WRTProject;
import org.symbian.tools.wrttools.util.CoreUtil;
import org.symbian.tools.wrttools.util.ProjectUtils;
import org.symbian.tools.wrttools.util.Util;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings("restriction")
public class WrtApplicationValidator extends AbstractValidator {
	@Override
	public ValidationResult validate(IResource resource, int kind,
			ValidationState state, IProgressMonitor monitor) {
		final ITMWProject project = TMWCore.create(resource.getProject());
		if (project == null
				|| project.getTargetRuntime() == null
				|| !WRTProject.WRT11_RUNTIME.equals(project.getTargetRuntime()
						.getId())) {
			return null;
		}
		if (ProjectUtils.isPlist(resource)) {
			return validatePlist((IFile) resource);
		} else if (isProject(resource)) {
			return validateProject((IProject) resource);
		} else {
			return null;
		}
	}

	private ValidationResult validateProject(IProject resource) {
		ValidationResult result = new ValidationResult();
		try {
			isPlistPresent(resource, result);
		} catch (CoreException e) {
			Activator.log(e);
		}
		return result;
	}

	private boolean isProject(IResource resource) {
		return resource.getType() == IResource.PROJECT
				&& ProjectUtils.hasWrtNature(resource.getProject());
	}

	private ValidationResult validatePlist(IFile resource) {
		ValidationResult result = new ValidationResult();
		IStructuredModel structuredModel;
		try {
			structuredModel = StructuredModelManager.getModelManager()
					.getModelForRead(resource);
			try {
				if (structuredModel instanceof IDOMModel) {
					IDOMModel model = (IDOMModel) structuredModel;
					validateElement((IDOMElement) model.getDocument()
							.getDocumentElement(), result, resource);
					checkHtml(model, resource, result);
					checkPlistValues(model, resource, result);
				}
			} finally {
				if (structuredModel != null) {
					structuredModel.releaseFromRead();
				}
			}
		} catch (IOException e) {
			Activator.log(e);
		} catch (CoreException e) {
			Activator.log(e);
		}
		return result;
	}

	private static enum PListElements {
		plist, array, data, date, dict, real, integer, string, FALSE, TRUE, key, xml
	};

	private void validateElement(IDOMElement element, ValidationResult result,
			IResource resource) {
		// showData("");
		PListElements[] values = PListElements.values();
		boolean isValidElement = false;
		for (PListElements validElement : values) {
			if (validElement.toString().equalsIgnoreCase(
					element.getNodeName().trim())) {
				isValidElement = true;
				break;

			}
			continue;
		}
		if (!isValidElement) {
			Object[] arguments = { "   ", element.getNodeName().trim() };
			String message = MessageFormat.format(
					ValidatorPropMessages
							.getString("plist.element.not.supported")
							+ "{0}"
							+ "{1}", arguments);
			result.add(createMessage(resource, message,
					element.getStartOffset(), element.getEndOffset()));
		}

		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node item = childNodes.item(i);
			if (item.getNodeType() == Node.ELEMENT_NODE) {
				validateElement((IDOMElement) item, result, resource);
			}
		}
	}

	private ValidatorMessage createMessage(IResource resource, String message,
			int startOffset, int endOffset) {
		ValidatorMessage msg = ValidatorMessage.create(message, resource);
		msg.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		msg.setAttribute(IMarker.CHAR_START, startOffset);
		msg.setAttribute(IMarker.CHAR_END, endOffset);
		return msg;
	}

	private boolean checkHtml(IDOMModel model, IFile plist,
			ValidationResult result) {
		String text = model.getStructuredDocument().get();
		IRegion region = CoreUtil.getIndexFileNameRegion(text);
		boolean checkHtml = false;
		if (region != null) {
			String htmlName = text.substring(region.getOffset(),
					region.getOffset() + region.getLength());
			IFile htmlFile = null;
			try {
				htmlFile = getFile(plist.getProject(), new Path(htmlName), 0,
						"html", "htm");
			} catch (CoreException e) {
				Activator.log(e);
			}
			if (htmlFile == null) {
				Object[] arguments = { htmlName, "<>", htmlName };
				String message = MessageFormat.format(
						ValidatorPropMessages
								.getString("plist.html.element.mismatch")
								+ "{0}" + "{1}" + "{2}", arguments);
				result.add(createMessage(plist, message, region.getOffset(),
						region.getOffset() + region.getLength()));
			}
		} else {
			String message = ValidatorPropMessages
					.getString("plist.html.element.mailHtml.missing");
			ValidatorMessage msg = createMessage(plist, message, 0, 0);
			setMessageOnRootElement(msg, model);
			result.add(msg);
		}

		return checkHtml;
	}

	private void setMessageOnRootElement(ValidatorMessage msg, IDOMModel model) {
		Element element = model.getDocument().getDocumentElement();
		int begin = 0, end = 0;
		if (element instanceof IDOMNode) {
			IDOMNode node = (IDOMNode) element;
			begin = node.getStartOffset();
			end = node.getFirstStructuredDocumentRegion().getEnd();
		}
		msg.setAttribute(IMarker.CHAR_START, begin);
		msg.setAttribute(IMarker.CHAR_END, end);
	}

	private IFile getFile(IContainer container, IPath path, int segment,
			String... extensions) throws CoreException {
		if (segment + 1 == path.segmentCount()) {
			return getFile(container, path.lastSegment(), extensions);
		} else {
			String name = path.segment(segment).toLowerCase();
			IResource[] members = container.members();
			for (IResource resource : members) {
				if (resource.getName().toLowerCase().equals(name)) {
					if (resource.getType() == IResource.FOLDER) {
						return getFile((IContainer) resource, path,
								segment + 1, extensions);
					}
				}
			}
		}
		return null;
	}

	private IFile getFile(IContainer container, String lastSegment,
			String[] extensions) throws CoreException {
		final Set<String> names = new TreeSet<String>();
		names.add(lastSegment.toLowerCase());
		for (String extension : extensions) {
			names.add(lastSegment.concat(".").concat(extension).toLowerCase());
		}
		IResource[] members = container.members();
		for (IResource resource : members) {
			if (resource.getType() == IResource.FILE
					&& names.contains(resource.getName().toLowerCase())
					&& resource.isAccessible()) {
				return (IFile) resource;
			}
		}
		return null;
	}

	private void checkPlistValues(IDOMModel model, IResource plist,
			ValidationResult result) {
		String source = model.getStructuredDocument().get();
		if (!hasAttributeValue(source, "DisplayName")) {
			Object[] arguments = { " 'Display Name' ",
					ValidatorPropMessages.getString("not.in.plist.file") };
			String message = MessageFormat.format(
					ValidatorPropMessages.getString("plist.mankey.mising")
							+ "{0}" + "{1}", arguments);
			ValidatorMessage msg = createMessage(plist, message, 0, 0);
			setMessageOnRootElement(msg, model);
			result.add(msg);
		} else { /* validating Widget name (Display name) */
			Matcher matcher = CoreUtil.getPropertyLookupPattern("DisplayName")
					.matcher(source);
			if (matcher.find()) {
				String strError = Util.validateWidgetName(matcher.group(1));
				if (strError != null) {
					Object[] arguments = {
							" 'Display Name' ",
							ValidatorPropMessages
									.getString("contains.invalid.character") };
					String message = MessageFormat.format(
							ValidatorPropMessages
									.getString("plist.mankey.mising")
									+ "{0}"
									+ "{1}", arguments);

					ValidatorMessage msg = createMessage(plist, message,
							matcher.start(), matcher.end());
					result.add(msg);
				}
			}
		}

		if (!hasAttributeValue(source, "MainHTML")) {
			Object[] arguments = { " 'MainHTML' ",
					ValidatorPropMessages.getString("not.in.plist.file") };
			String message = MessageFormat.format(
					ValidatorPropMessages.getString("plist.mankey.mising")
							+ "{0}" + "{1}", arguments);
			ValidatorMessage msg = createMessage(plist, message, 0, 0);
			setMessageOnRootElement(msg, model);
			result.add(msg);
		}
		if (!hasAttributeValue(source, "Identifier")) {
			Object[] arguments = { " 'Identifier' ",
					ValidatorPropMessages.getString("not.in.plist.file") };
			String message = MessageFormat.format(
					ValidatorPropMessages.getString("plist.mankey.mising")
							+ "{0}" + "{1}", arguments);
			ValidatorMessage msg = createMessage(plist, message, 0, 0);
			setMessageOnRootElement(msg, model);
			result.add(msg);
		} else { /* validating Widget Idenfier (UID) */
			Matcher matcher = CoreUtil.getPropertyLookupPattern("Identifier")
					.matcher(source);
			if (matcher.find()) {
				String strError = Util.validateWidgetID(matcher.group(1));
				if (strError != null) {
					Object[] arguments = {
							" 'Identifier' ",
							ValidatorPropMessages
									.getString("contains.invalid.character") };
					String message = MessageFormat.format(
							ValidatorPropMessages
									.getString("plist.mankey.mising")
									+ "{0}"
									+ "{1}", arguments);

					ValidatorMessage msg = createMessage(plist, message,
							matcher.start(1), matcher.end(1));
					result.add(msg);
				}
			}
		}
	}

	private boolean hasAttributeValue(String source, String propertyName) {
		Pattern pattern = CoreUtil.getPropertyLookupPattern(propertyName);
		boolean hasAttr = true;
		Matcher matcher = pattern.matcher(source);
		if (!matcher.find()) {
			hasAttr = false;
		} else {
			hasAttr = matcher.group(1).trim().length() > 0;
		}
		return hasAttr;
	}

	private void isPlistPresent(IProject project, ValidationResult result)
			throws CoreException {
		IResource[] members = project.members();
		for (IResource resource : members) {
			if (ProjectUtils.isPlist(resource)) {
				return;
			}
		}
		ValidatorMessage msg = ValidatorMessage.create(
				ValidatorPropMessages.getString("plist.File.Not.Present"),
				project);
		msg.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);

		result.add(msg);
	}
}
