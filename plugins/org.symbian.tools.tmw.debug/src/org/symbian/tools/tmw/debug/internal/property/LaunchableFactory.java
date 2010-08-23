package org.symbian.tools.tmw.debug.internal.property;

import org.eclipse.core.runtime.IAdapterFactory;

public class LaunchableFactory implements IAdapterFactory {

    @SuppressWarnings("rawtypes")
    public Object getAdapter(Object adaptableObject, Class adapterType) {
		// It is only needed to exist
		return null;
	}

    @SuppressWarnings("rawtypes")
    public Class[] getAdapterList() {
		// It is only needed to exist
		return null;
	}

}
