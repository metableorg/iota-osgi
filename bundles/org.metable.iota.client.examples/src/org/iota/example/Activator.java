package org.iota.example;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        Example.nodeInfo();
        Example.getBalanceBySeed();
        Example.getBalanceByAddress(DevelopmentSeed.ADDRESSES[1]);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }
}
