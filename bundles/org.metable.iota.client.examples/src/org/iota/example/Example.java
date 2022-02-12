package org.iota.example;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import org.iota.client.BalanceAddressResponse;
import org.iota.client.Client;
import org.iota.client.ClientMessageBuilder;
import org.iota.client.GetAddressesBuilder;
import org.iota.client.IndexationPayload;
import org.iota.client.Message;
import org.iota.client.MessageId;
import org.iota.client.MessageMetadata;
import org.iota.client.MessagePayload;
import org.iota.client.MessagePayloadType;
import org.iota.client.MessageWrap;
import org.iota.client.MqttListener;
import org.iota.client.NodeInfoWrapper;
import org.iota.client.Output;
import org.iota.client.OutputsOptions;
import org.iota.client.RegularEssence;
import org.iota.client.RustHex;
import org.iota.client.SecretKey;
import org.iota.client.TopicEvent;
import org.iota.client.TransactionPayload;
import org.iota.client.Util;
import org.iota.client.UtxoInput;

public class Example {

    private static String URL = NodeUrl.CHRYSALIS_DEVNET_IOTA_CAFE_API_1_1;

    public static void consolidate() {
        Client iota = node();

        String seed = "NONSECURE_USE_OF_DEVELOPMENT_SEED_1";

        // Here all funds will be send to the address with the lowest index in the range
        String address = Util.consolidateFunds(iota, seed, 0, 0, 150);

        System.out.println("Funds consolidated to" + address);
    }

    @SuppressWarnings("unused")
    public static void createMaxDust() {
        Client iota = node();
        String seed = "NONSECURE_USE_OF_DEVELOPMENT_SEED_1";
        String seed_2 = "NONSECURE_USE_OF_DEVELOPMENT_SEED_2";

        String[] new_addresses = iota.getAddresses(seed_2).withRange(0, 1).finish();

        Message dustAllowanceMessage = iota.message().withSeed(seed)
                .withDustAllowanceOutput(new_addresses[0], 10_000_000).finish();

        MessageWrap[] msgs = iota.retryUntilIncluded(dustAllowanceMessage.id(), -1, -1);

        // Split funds to own addresses
        String[] addresses = iota.getAddresses(seed)
                // We start from index 1 so we can send remaining balance to the address with index 0
                .withRange(1, 101).finish();

        ClientMessageBuilder message_builder = iota.message().withSeed(seed);
        for (String address : addresses) {
            // Make sure to re-set the builder as the instance is a clone of the old one due to JNI limits
            message_builder = message_builder.withOutput(address, 1_000_001);
        }
        Message message = message_builder.finish();

        System.out.println("First transaction sent: https://explorer.iota.org/devnet/message/" + message.id());

        msgs = iota.retryUntilIncluded(message.id(), -1, -1);

        // At this point we have 100 Mi on 100 addresses and we will just send it to the final address
        // We use the outputs directly so we don't double spend them

        List<UtxoInput> initial_outputs = new ArrayList<>();
        Optional<MessagePayload> payload = message.payload();
        if (payload.isPresent() && payload.get().payloadType().equals(MessagePayloadType.TRANSACTION)) {
            TransactionPayload tx = payload.get().getAsTransaction().get();
            RegularEssence essence = tx.essence().getAsRegular().get();
            Output[] outputs = essence.outputs();
            for (int index = 0; index < outputs.length; index++) {
                Output output = outputs[index];
                if (output.asSignatureLockedSingleOutput().amount() == 1_000_001) {
                    initial_outputs.add(UtxoInput.from(tx.id(), index));
                }
            }
        }

        String[] first_address_old_seed = iota.getAddresses(seed).withRange(0, 1).finish();
        List<MessageId> sent_messages = new ArrayList<>();
        for (UtxoInput input : initial_outputs) {
            MessageId message_id = iota.message().withSeed(seed).withInput(input).withInputRange(1, 101)
                    .withOutput(new_addresses[0], 1)
                    // send remaining iotas back
                    .withOutput(first_address_old_seed[0], 1_000_000).finish().id();
            System.out.printf("Transaction %i sent: https://explorer.iota.org/devnet/message/%s" + input.index(),
                    message_id);
            sent_messages.add(message_id);
        }
        // only check last message, if this gets confirmed all other messages should also be confirmed
        msgs = iota.retryUntilIncluded(sent_messages.get(sent_messages.size() - 1), -1, -1);
        // Send all funds back to first address
        long total_balance = iota.getBalance(seed).finish();

        System.out.println("Total balance: " + total_balance);

        message = iota.message().withSeed(seed).withOutput(first_address_old_seed[0], total_balance).finish();

        System.out.println("Final tx sent: https://explorer.iota.org/devnet/message/" + message.id());

        msgs = iota.retryUntilIncluded(message.id(), -1, -1);
    }

    public static void customPayload() {
        Client iota = node();

        IndexationPayload indexation_payload = IndexationPayload.fromStrings("Your Index", "Your Data");

        Message message = iota.message().finishIndex(indexation_payload);

        System.out.printf("Message ID: %s", message.id());
    }

    public static String[] generateAddresses(final String seed) {
        Client iota = node();
        String[] addresses = GetAddressesBuilder.from(seed).withClient(iota).withRange(0, 10).finish();
        return addresses;
    }

    public static String generateSeed() {
        SecretKey secret_key = SecretKey.generate();
        return RustHex.encode(secret_key.toBytes());
    }

    public static void generateSeedAndAddresses(final String pathToPropertiesFile) throws IOException {
        final String seed = generateSeed();
        String[] addresses = generateAddresses(seed);

        Properties properties = new Properties();

        properties.put("seed", seed);

        for (int i = 0; i < addresses.length; ++i) {
            properties.put("address_" + i, addresses[i]);
        }

        FileOutputStream fos = new FileOutputStream(pathToPropertiesFile);
        properties.store(fos, "IOTA test data");
        fos.close();
    }

    public static void getBalance() {
        Client iota = node();

        String seed = "NONSECURE_USE_OF_DEVELOPMENT_SEED_1";

        long seed_balance = iota.getBalance(seed).finish();
        System.out.println("Account balance: " + seed_balance);

        String address = "atoi1qzt0nhsf38nh6rs4p6zs5knqp6psgha9wsv74uajqgjmwc75ugupx3y7x0r";

        BalanceAddressResponse response = iota.getAddress().balance(address);
        System.out.println("The balance of " + address + " is " + response.balance());

        UtxoInput[] outputs = iota.getAddress().outputs(address, new OutputsOptions());
        System.out.println("The outputs of address " + address + " are: " + Arrays.toString(outputs));
    }

    public static void getBalanceByAddress(final String address) {
        Client iota = node();

        BalanceAddressResponse response = iota.getAddress().balance(address);
        System.out.println("The balance of " + address + " is " + response.balance());

        UtxoInput[] outputs = iota.getAddress().outputs(address, new OutputsOptions());
        System.out.println("The outputs of address " + address + " are: " + Arrays.toString(outputs));
    }

    public static void getBalanceBySeed(final String seed) {
        Client iota = node();
        long seed_balance = iota.getBalance(seed).finish();
        System.out.println("Account balance: " + seed_balance);
    }

    public static void getDataMessage() {
        Client iota = node();

        Message message = iota.message().withIndexString("Hello").withDataString("Tangle").finish();

        System.out.println("Message sent https://explorer.iota.org/devnet/message/" + message.id());

        MessageId[] fetched_message_ids = iota.getMessage().indexString("Hello");
        System.out.println("Messages with Hello index: " + Arrays.toString(fetched_message_ids));
    }

    public static void getMessageMetadata() {
        Client iota = node();
        Message message = iota.message().finish();

        MessageMetadata metadata = iota.getMessage().metadata(message.id());

        System.out.println("Message metadata: " + metadata);
    }

    public static void getOutputs() {
        Client iota = node();

        String address = "atoi1qzt0nhsf38nh6rs4p6zs5knqp6psgha9wsv74uajqgjmwc75ugupx3y7x0r";

        UtxoInput[] outputs = iota.getAddress().outputs(address, new OutputsOptions());
        System.out.println("The outputs of address " + address + " are: " + Arrays.toString(outputs));
    }

    public static List<String> getValues(final String key) {
        Client iota = node();

        final MessageId[] messageIds = iota.getMessage().indexString(key);

        return Arrays.stream(messageIds).map(id -> {
            try {
                return new String(iota.getMessage().data(id).payload().get().getAsIndexation().get().data(), "UTF-8");
            } catch (RuntimeException | UnsupportedEncodingException e) {
                return e.getMessage();
            }
        }).collect(Collectors.toList());
    }

    @SuppressWarnings("unused")
    public static void mqtt() {
        Client iota = node();

        MqttListener listener = new MqttListener() {
            @Override
            public void onEvent(TopicEvent event) {
                System.out.println(event);
            }
        };

        // TODO: Make listeners with the Sync trait
        // iota.subscriber().withTopic(Topic.from("messages")).subscribe(listener);
    }

    private static Client node() {
        Client iota = Client.Builder().withNode(URL) // Insert your node URL here
                // .withNodeAuth("https://somechrysalisiotanode.com", "jwt_or_null",
                // "name_or_null", "password_or_null") //
                // Optional authentication
                .finish();
        return iota;
    }

    public static void nodeInfo() {
        Client iota = node();

        System.out.println("Node healthy: " + iota.getHealth());

        NodeInfoWrapper info = iota.getInfo();
        System.out.println("Node url: " + info.url());
        System.out.println("Node Info: " + info.nodeInfo());
    }

    public static void offlineExample() {
        String seed = "NONSECURE_USE_OF_DEVELOPMENT_SEED_1";
        String toAddress = "atoi1qruzprxum2934lr3p77t96pzlecxv8pjzvtjrzdcgh2f5exa22n6gek0qdq";
        long amount = 1_000_000;

        Offline offlineExample = new Offline(URL, seed);
        String[] inputAddresses = offlineExample.generateAddresses();
        String preparedData = offlineExample.prepareTransaction(inputAddresses, toAddress, amount);
        System.out.println("Prepared data: " + preparedData);
        String signedData = offlineExample.signTransaction(preparedData);
        System.out.println("Signed data: " + signedData);

        Message message = offlineExample.sendMessage(signedData);

        System.out.printf("Message ID: %s", message.id());
    }

    public static void simpleMessage() {
        Client iota = node();
        Message message = iota.message().finish();

        System.out.println("Empty message sent: " + URL + message.id().toString());
    }

    public static void storeValue(final String key, final String value) {
        Client iota = node();

        iota.message().withIndexString(key).withDataString(value).finish();
    }

    public static void transaction() {
        Client iota = node();

        String seed_1 = "NONSECURE_USE_OF_DEVELOPMENT_SEED_1";

        Message message = iota.message().withSeed(seed_1)
                // Insert the output address and amount to spent. The amount cannot be zero.
                .withOutput(
                        // We generate an address from our seed so that we send the funds to ourselves
                        iota.getAddresses(seed_1).withRange(0, 1).finish()[0], 1000000)
                .finish();

        System.out.println("Transaction sent: https://explorer.iota.org/devnet/message/" + message.id());
    }
}
