package com.sk.indysdkdemo;

import com.sun.jna.Callback;
import com.sun.jna.ptr.IntByReference;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import java.io.IOException;

public class CryptoExample {

    final static String softHSMServerBaseUrl = "http://127.0.0.1:7999";

    private class ReqResult {
        String result;
        String status;
    }

    private interface EncryptService {
        @FormUrlEncoded
        @POST("action/SymmetricEncrypt")
        Call<ReqResult> encrypt(@Field("label") String label,
                                  @Field("class") String cls,
                                  @Field("msg") String msg);
    }

    private interface DecryptService {
        @FormUrlEncoded
        @POST("action/SymmetricDecrypt")
        Call<ReqResult> decrypt(@Field("label") String label,
                                @Field("class") String cls,
                                @Field("msg") String msg);
    }

    public static Callback keyGenCb = new Callback() {

        @SuppressWarnings({ "unused", "unchecked" })
        public void callback() {
        }
    };

    private static Retrofit newRetrofitInstance() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(softHSMServerBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

    public static Callback encryptCb = new Callback() {

        @SuppressWarnings({ "unused", "unchecked" })
        public String callback(int xcommand_handle, String msg, int l, IntByReference resultLen) throws IOException {
            System.out.println("[before encryption] ======> " + msg);
            EncryptService service = newRetrofitInstance().create(EncryptService.class);

            String result = service.encrypt("PAULAES", "CKO_SECRET_KEY", msg).execute().body().result;
            System.out.println("result = " + result);
            resultLen.setValue(result.length());
            return result;
        }
    };

    public static Callback decryptCb = new Callback() {

        @SuppressWarnings({ "unused", "unchecked" })
        public String callback(int xcommand_handle, String msg, int l, IntByReference resultLen) throws IOException {
            DecryptService service = newRetrofitInstance().create(DecryptService.class);

            String result = service.decrypt("PAULAES", "CKO_SECRET_KEY", msg).execute().body().result;
            System.out.println("result = " + result);
            resultLen.setValue(result.length());
            return result;
        }
    };
}
