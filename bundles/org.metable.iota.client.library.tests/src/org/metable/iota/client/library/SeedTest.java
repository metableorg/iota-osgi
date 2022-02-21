package org.metable.iota.client.library;

import org.iota.client.RustHex;
import org.iota.client.SecretKey;
import org.junit.Assert;
import org.junit.Test;

public class SeedTest {

    @Test
    public void generateSeed() {
        // Act.
        SecretKey secretKey = SecretKey.generate();
        String result = RustHex.encode(secretKey.toBytes());

        // Assert
        Assert.assertEquals(64, result.length());

        // System.out.println(result);
    }
}
