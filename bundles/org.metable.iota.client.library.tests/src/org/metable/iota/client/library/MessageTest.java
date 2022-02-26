package org.metable.iota.client.library;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.iota.client.Client;
import org.iota.client.IndexationPayload;
import org.iota.client.Message;
import org.iota.client.MessageId;
import org.iota.client.MessageMetadata;
import org.junit.Assert;
import org.junit.Test;
import org.metable.iota.client.library.helper.ClientNode;
import org.metable.iota.client.library.helper.NodeUrl;

public class MessageTest {

    private static String URL = NodeUrl.CHRYSALIS_DEVNET_IOTA_CAFE_API_1_1;

    @Test
    public void customPayload() throws UnsupportedEncodingException {
        // Arrange.
        Client iota = ClientNode.build(URL);
        final String key = UUID.randomUUID().toString();

        // Act.
        IndexationPayload payload = IndexationPayload.fromStrings(key, "Hello again, IOTA");

        iota.message().finishIndex(payload);

        final MessageId[] messageIds = iota.getMessage().indexString(key);
        final String value = new String(
                iota.getMessage().data(messageIds[0]).payload().get().getAsIndexation().get().data(), "UTF-8");

        // Assert.
        Assert.assertEquals("Hello again, IOTA", value);
    }

    @Test
    public void getMessageMetadata() {
        // Arrange.
        Client iota = ClientNode.build(URL);
        Message message = iota.message().finish();

        // Act.
        MessageMetadata result = iota.getMessage().metadata(message.id());

        // Assert.
        Assert.assertEquals(message.id().toString(), result.messageId());
    }

    @Test
    public void sendSimpleMessage() {
        // Arrange.
        Client iota = ClientNode.build(URL);

        // Act.
        Message result = iota.message().finish();

        // Assert.
        Assert.assertEquals(4, result.parents().length);
    }

    @Test
    public void storeKeyValuePair() throws UnsupportedEncodingException {
        // Arrange.
        Client iota = ClientNode.build(URL);
        final String key = UUID.randomUUID().toString();

        // Act.
        iota.message().withIndexString(key).withDataString("Hello, IOTA").finish();

        final MessageId[] messageIds = iota.getMessage().indexString(key);

        final String value = new String(
                iota.getMessage().data(messageIds[0]).payload().get().getAsIndexation().get().data(), "UTF-8");

        // Assert.
        Assert.assertEquals("Hello, IOTA", value);
    }
}
