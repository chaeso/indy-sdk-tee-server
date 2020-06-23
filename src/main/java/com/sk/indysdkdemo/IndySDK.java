package com.sk.indysdkdemo;

import net.minidev.json.JSONObject;
import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.LibIndy;
import org.hyperledger.indy.sdk.pool.Pool;
import org.hyperledger.indy.sdk.wallet.Wallet;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class IndySDK {
    protected static final String WALLET_CREDENTIALS = "{\"key\":\"8dvfYSt5d1taSd6yJdpjq4emkwsPDDLYxkNFysFD2cZY\", \"key_derivation_method\":\"RAW\"}";

    public void run(String walletName) {
        JSONObject json = new JSONObject();
        json.put("id", walletName);

        String issuerWalletConfig = json.toString();
        String currentDirectory = System.getProperty("user.dir");

        File f = new File(System.getProperty("user.dir") + "/src/lib/macos/libindy.dylib");
        LibIndy.init(f);

        try {
            Wallet.registerTeeMethod(CryptoExample.keyGenCb, CryptoExample.encryptCb, CryptoExample.decryptCb);
        } catch (IndyException e) {
            e.printStackTrace();
        }

        try {
            Pool.setProtocolVersion(2).get();

            // Issuer Create and Open Wallet
            Wallet.createWallet(issuerWalletConfig, WALLET_CREDENTIALS).get();
            Wallet.openWallet(issuerWalletConfig, WALLET_CREDENTIALS).get();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IndyException e) {
            e.printStackTrace();
        }

    }
}
