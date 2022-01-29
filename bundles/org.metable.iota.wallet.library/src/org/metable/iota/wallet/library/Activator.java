package org.metable.iota.wallet.library;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        System.loadLibrary("iota_wallet_java");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }
}
