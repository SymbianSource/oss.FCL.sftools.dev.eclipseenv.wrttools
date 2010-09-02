package org.symbian.tools.tmw.ui.deployment.bluetooth;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

public final class BluetoothRule implements ISchedulingRule {
    public static final ISchedulingRule INSTANCE = new BluetoothRule();

    private BluetoothRule() {
        // No instantiation
    }

    public boolean contains(ISchedulingRule rule) {
        return isConflicting(rule);
    }

    public boolean isConflicting(ISchedulingRule rule) {
        return rule instanceof BluetoothRule;
    }

}
