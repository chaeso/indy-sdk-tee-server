package com.sk.indysdkdemo;

import com.sun.jna.Callback;
import com.sun.jna.ptr.IntByReference;
import org.zeromq.SocketType;
import org.zeromq.ZAuth;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import zmq.io.mechanism.curve.Curve;

import java.io.IOException;

public class CryptoExample {

    final static String softHSMServerBaseUrl = "http://127.0.0.1:7999";

    private class ReqResult {
        String result;
        String status;
    }

    public static Callback keyGenCb = new Callback() {

        @SuppressWarnings({ "unused", "unchecked" })
        public void callback() {
        }
    };

    public CryptoExample() {
        ZContext context = new ZContext();
        ZAuth auth=new ZAuth(context);

        ZMQ.Socket client = context.createSocket(SocketType.REQ);
        Curve curve = new Curve();
        String[] clientKeys = curve.keypairZ85();

        client.setCurvePublicKey(clientKeys[0].getBytes());
        client.setCurveSecretKey(clientKeys[1].getBytes());

        // 주의 : 아래 키는 defensiveMatrix 에서 생성한 public key 를 가져와 써야 한다.
        client.setCurveServerKey("t}^YcgLp]lSg5d2PfIj@MfN>T0&HTK:>nsT[nwb8".getBytes());
        client.connect("tcp://127.0.0.1:7000");
    }

    public static Callback encryptCb = new Callback() {

        @SuppressWarnings({ "unused", "unchecked" })
        public String callback(int xcommand_handle, String msg, int l, IntByReference resultLen) throws IOException {
            System.out.println("[before encryption] ======> " + msg);
            String result = "";

            try (ZContext context = new ZContext(); ZAuth auth=new ZAuth(context)) {
                ZMQ.Socket client = context.createSocket(SocketType.REQ);
                Curve curve = new Curve();
                String[] clientKeys = curve.keypairZ85();

                client.setCurvePublicKey(clientKeys[0].getBytes());
                client.setCurveSecretKey(clientKeys[1].getBytes());
                // 주의 : 아래 키는 defensiveMatrix 에서 생성한 public key 를 가져와 써야 한다.
                client.setCurveServerKey("t}^YcgLp]lSg5d2PfIj@MfN>T0&HTK:>nsT[nwb8".getBytes());
                client.connect("tcp://127.0.0.1:7000");

                boolean send = client.send("{\"action\":\"encrypt\", \"msg\":\""+msg+"\"}");
                byte[] recv = client.recv();
                result = recv.toString();
            }

            System.out.println("result = " + result);
            resultLen.setValue(result.length());
            return result;
        }
    };

    public static Callback decryptCb = new Callback() {

        @SuppressWarnings({ "unused", "unchecked" })
        public String callback(int xcommand_handle, String msg, int l, IntByReference resultLen) throws IOException {
            String result = "";

            try (ZContext context = new ZContext(); ZAuth auth=new ZAuth(context)) {
                ZMQ.Socket client = context.createSocket(SocketType.REQ);
                Curve curve = new Curve();
                String[] clientKeys = curve.keypairZ85();

                client.setCurvePublicKey(clientKeys[0].getBytes());
                client.setCurveSecretKey(clientKeys[1].getBytes());
                // 주의 : 아래 키는 defensiveMatrix 에서 생성한 public key 를 가져와 써야 한다.
                client.setCurveServerKey("t}^YcgLp]lSg5d2PfIj@MfN>T0&HTK:>nsT[nwb8".getBytes());
                client.connect("tcp://127.0.0.1:7000");

                boolean send = client.send("{\"action\":\"decrypt\", \"msg\":\""+msg+"\"}");
                byte[] recv = client.recv();
                result = recv.toString();
            }
            System.out.println("result = " + result);
            resultLen.setValue(result.length());
            return result;
        }
    };
}
