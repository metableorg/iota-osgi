package org.iota.wallet.example;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.iota.wallet.Account;
import org.iota.wallet.AccountManager;
import org.iota.wallet.AccountManagerBuilder;
import org.iota.wallet.AccountSignerType;
import org.iota.wallet.BrokerOptions;
import org.iota.wallet.ClientOptions;
import org.iota.wallet.ClientOptionsBuilder;
import org.iota.wallet.ErrorListener;
import org.iota.wallet.EventManager;
import org.iota.wallet.StrongholdStatusEvent;
import org.iota.wallet.StrongholdStatusListener;

public class Example implements ErrorListener, StrongholdStatusListener {

    public void newAccount() {
        Path accountBackupPath = Paths.get("./account_backup");

        AccountManagerBuilder builder = AccountManager.Builder().withStorage(accountBackupPath.toString(), null);
        AccountManager manager = builder.finish();
        manager.setStrongholdPassword("YepThisISSecure");

        final String mnemonic = manager.generateMnemonic();

        System.out.println("mnemonic: " + mnemonic);

        // null means "generate one for me"
        manager.storeMnemonic(AccountSignerType.STRONGHOLD, mnemonic);

        BrokerOptions mqtt = new BrokerOptions();

        ClientOptions clientOptions = new ClientOptionsBuilder()
                .withNode("https://api.lb-0.h.chrysalis-devnet.iota.cafe").withMqttBrokerOptions(mqtt).build();

        Account account = manager.createAccount(clientOptions).signerType(AccountSignerType.STRONGHOLD)
                .alias("Test Account 1").initialise();

        System.out.println("id: " + account.id());
        System.out.println("alias: " + account.alias());
        System.out.println("balance available: " + account.balance().getAvailable());
        System.out.println("address: " + account.generateAddress().getReadable());
    }

    public void getAccounts() {
        Path accountBackupPath = Paths.get("./account_backup");

        AccountManagerBuilder builder = AccountManager.Builder().withStorage(accountBackupPath.toString(), null);
        AccountManager manager = builder.finish();
        manager.setStrongholdPassword("YepThisISSecure");

        final Account[] accounts = manager.getAccounts();

        for (Account account : accounts) {
            System.out.println("alias: " + account.alias());
            System.out.println("balance available: " + account.balance().getAvailable());
        }
    }

    public void restoreAccounts() {
        Path accountBackupPath = Paths.get("./account_backup");

        AccountManagerBuilder builder = AccountManager.Builder().withStorage(accountBackupPath.toString(), null);
        AccountManager manager = builder.finish();
        manager.setStrongholdPassword("YepThisISSecure");
        manager.storeMnemonic(AccountSignerType.STRONGHOLD,
                "limit blade apart riot order color gallery robot film flush blouse neglect unknown family episode duty lab blouse catalog view frog female cute strategy");
        BrokerOptions mqtt = new BrokerOptions();
        ClientOptions clientOptions = new ClientOptionsBuilder()
                .withNode("https://api.lb-0.h.chrysalis-devnet.iota.cafe").withMqttBrokerOptions(mqtt).build();
        manager.createAccount(clientOptions).signerType(AccountSignerType.STRONGHOLD).alias("Test Account 1")
                .initialise();
    }

//    public void importAccounts() {
//        Path accountBackupPath = Paths.get("./account_backup");
//
//        AccountManagerBuilder builder = AccountManager.Builder().withStorage(accountBackupPath.toString(), null);
//        AccountManager manager = builder.finish();
//        manager.setStrongholdPassword("YepThisISSecure");
//        BrokerOptions mqtt = new BrokerOptions();
//        ClientOptions clientOptions = new ClientOptionsBuilder()
//                .withNode("https://api.lb-0.h.chrysalis-devnet.iota.cafe").withMqttBrokerOptions(mqtt).build();
//        manager.createAccount(clientOptions).signerType(AccountSignerType.STRONGHOLD).alias("Test Account 1")
//                .initialise();
//        // manager.importAccounts(accountBackupPath.toString() + "/wallet.stronghold", "YepThisISSecure");
//        // manager.createAccount(null)
//        // manager.syncAccounts().execute();
//
//        final Account[] accounts = manager.getAccounts();
//
//        for (Account account : accounts) {
//            System.out.println("alias: " + account.alias());
//            System.out.println("balance available: " + account.balance().getAvailable());
//        }
//    }

    public Example() {
        EventManager.subscribeErrors(this);
        EventManager.subscribeStrongholdStatusChange(this);
    }

    @Override
    public void onError(String error) {
        // System.out.println("ON_ERROR: " + error);
    }

    @Override
    public void onStrongholdStatusChange(StrongholdStatusEvent event) {
        // System.out.println("STRONGHOLD STATUS: " + event.status());
        // System.out.println("seconds left: " + event.unlockedDuration().getSeconds());
    }
}
