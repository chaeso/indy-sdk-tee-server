package com.sk.indysdkdemo;

import com.sun.jna.Callback;
import com.sun.jna.ptr.IntByReference;

public class CryptoExample {

    public static Callback keyGenCb = new Callback() {

        @SuppressWarnings({ "unused", "unchecked" })
        public void callback() {
        }
    };

    public static Callback encryptCb = new Callback() {

        @SuppressWarnings({ "unused", "unchecked" })
        public String callback(int xcommand_handle, String msg, int l, IntByReference resultLen) {
            String result = msg;

            // Insert TEE decryption here
            resultLen.setValue(result.length());
            return result;
        }
    };

    public static Callback decryptCb = new Callback() {

        @SuppressWarnings({ "unused", "unchecked" })
        public String callback(int xcommand_handle, String msg, int l, IntByReference resultLen) {
            String result = msg;

            // Insert TEE encryption here
            resultLen.setValue(result.length());
            return result;
        }
    };
}
