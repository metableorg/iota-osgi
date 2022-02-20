package org.metable.iota.client.library.helper;

import org.iota.client.Client;

public class ClientNode {



    public static Client build(final String url) {
        Client iota = Client.Builder().withNode(url).finish();
        return iota;
    }

}
