package org.iota.wallet.example;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
//        System.out.println("new account");
//        (new Example()).newAccount();;
//        System.out.println();
//        System.out.println("get account");
//        (new Example()).getAccounts();

        // (new Example()).restoreAccounts();
        (new Example()).getAccounts();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }

}
