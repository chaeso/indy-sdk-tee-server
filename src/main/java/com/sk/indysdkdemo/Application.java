package com.sk.indysdkdemo;

import com.sun.jna.Callback;
import com.sun.jna.ptr.IntByReference;
import net.minidev.json.JSONObject;
import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.LibIndy;
import org.hyperledger.indy.sdk.pool.Pool;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Native;
import java.security.*;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class Application implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args)  {
		IndySDK indy = new IndySDK();
		indy.run("wallet" + LocalDateTime.now().toString());
	}
}
