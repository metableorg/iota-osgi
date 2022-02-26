package org.metable.iota.client.library;

import org.iota.client.BalanceAddressResponse;
import org.iota.client.Client;
import org.iota.client.GetAddressesBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.metable.iota.client.library.helper.ClientNode;
import org.metable.iota.client.library.helper.NodeUrl;

public class AddressTest {

    private static String URL = NodeUrl.CHRYSALIS_DEVNET_IOTA_CAFE_API_1_1;

    @Test
    public void generateAddresses() {
        // Arrange.
        Client iota = ClientNode.build(URL);
        final String seed = "3247dc686272fcbe1176c67ceec16e2c583feee725d86c6d5a1c1e111d399d15";

        // Act.
        String[] addresses = GetAddressesBuilder.from(seed).withClient(iota).withRange(0, 10).finish();

        // Assert.
        Assert.assertEquals(10, addresses.length);

//        for (String address : addresses) {
//            System.out.println(address);
//        }
    }

    @Test
    public void getAddressBalance() {
        // Arrange.
        final String address = "atoi1qz5cxyv40u7nr95rsftccuu09vrlcf4r02zpn2utymesym3ygcazjyn3ljr";
        Client iota = ClientNode.build(URL);

        // Act.
        BalanceAddressResponse response = iota.getAddress().balance(address);

        // Assert. If this fails, request tokens for the token faucet here: https://faucet.chrysalis-devnet.iota.cafe/
        Assert.assertEquals(100_000_000, response.balance());
    }
}
