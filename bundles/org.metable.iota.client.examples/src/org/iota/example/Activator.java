package org.iota.example;

import java.io.FileInputStream;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        final String propertiesFile = "./iota_test_data.properties";

        Example.nodeInfo();

        // Uncomment the line below to generate a new test data set.
        // Example.generateSeedAndAddresses(propertiesFile);

        Properties properties = new Properties();
        properties.load(new FileInputStream(propertiesFile));

        Example.getBalanceBySeed(properties.getProperty("seed"));
        Example.getBalanceByAddress(properties.getProperty("address_0"));
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }
}
