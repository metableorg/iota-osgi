package org.metable.iota.client.library;

import org.iota.client.Client;
import org.iota.client.InfoResponse;
import org.iota.client.NodeInfoWrapper;
import org.junit.Assert;
import org.junit.Test;
import org.metable.iota.client.library.helper.ClientNode;
import org.metable.iota.client.library.helper.NodeUrl;

public class NodeInfoTest {

    private static String URL = NodeUrl.CHRYSALIS_DEVNET_IOTA_CAFE_API_1_1;

    @Test
    public void getNodeHealth() {
        // Arrange.
        Client iota = ClientNode.build(URL);

        // Act.
        final boolean result = iota.getHealth();

        // Assert.
        Assert.assertTrue(result);
    }

    @Test
    public void getNodeInfo() {
        // Arrange.
        Client iota = ClientNode.build(URL);

        // Act.
        final InfoResponse info = iota.getInfo().nodeInfo();

        // Assert.
        Assert.assertEquals("HORNET", info.name());
        Assert.assertEquals("chrysalis-devnet", info.networkId());
        Assert.assertEquals("1.1.2", info.version());
    }

    @Test
    public void getNodeUrl() {
        // Arrange.
        Client iota = ClientNode.build(URL);

        // Act.
        NodeInfoWrapper info = iota.getInfo();

        // Assert.
        Assert.assertEquals(URL, info.url());
    }
}
