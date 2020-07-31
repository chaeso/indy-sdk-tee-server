package main

import (
	"encoding/json"
	"fmt"
	zmq "github.com/pebbe/zmq4"
	"log"
	"runtime"
	"time"
)

type Request struct {
	Action string `json:"action"`
	Msg    string `json:"msg"`
}

type Response struct {
	Result string `json:"result"`
}

const (
	fail        = "fail"
	success     = "success"
	cryptoLabel = "PAULAES"
	cryptoClass = "CKO_SECRET_KEY"
	cryptoMode  = "CBC-PAD"
)

func checkErr(err error) {
	if err != nil {
		log.SetFlags(0)
		_, filename, lineno, ok := runtime.Caller(1)
		if ok {
			log.Fatalf("%v:%v: %v", filename, lineno, err)
		} else {
			log.Fatalln(err)
		}
	}
}

func testClient(server_publicZ85 string, port int) {

	fmt.Println("testClient started")
	client_public, client_secret, err := zmq.NewCurveKeypair()
	checkErr(err)
	zmq.AuthCurveAdd("domain1", client_public)

	//  Create and connect client socket
	client, _ := zmq.NewSocket(zmq.REQ)
	defer client.Close()

	//server_public := zmq.Z85decode(server_publicZ85)
	server_public := server_publicZ85
	client.ClientAuthCurve(server_public, client_public, client_secret)

	address := fmt.Sprintf("tcp://127.0.0.1:%d", port)
	//address := "tcp://127.0.0.1:6888"
	log.Printf("DefensiveMatrix Address : %s\n", address)

	client.Connect(address)
	//client.Connect(fmt.Sprintf("tcp://172.31.25.217:%d", port))
	i := 0
	start := time.Now()
	loop := 1000
	var retMsg_ string

	for i < loop {

		////////////////////////////////////////////////////////////////
		// ENCRYOTION
		////////////////////////////////////////////////////////////////
		json, _ := json.Marshal(Request{
			Action: "encrypt",
			Msg:    fmt.Sprintf("hell world %d", i),
		})

		fmt.Printf("[E] Client --> Server : %v\n", string(json))
		_, err := client.Send(string(json), 0)
		checkErr(err)
		retMsg, retErr := client.Recv(0)
		//_, retErr := client.Recv(0)
		checkErr(retErr)
		fmt.Printf("[E] Client <-- Server : %s\n\n\n", retMsg)

		//time.Sleep(3 * time.Second)
		retMsg_ = retMsg
		i++

	}

	elapsed := time.Since(start)

	////////////////////////////////////////////////////////////////
	// DECRYPTION Test for the last message
	////////////////////////////////////////////////////////////////
	resp := Response{}
	json.Unmarshal([]byte(retMsg_), &resp)

	reqDec, _ := json.Marshal(Request{
		Action: "decrypt",
		Msg:    resp.Result,
	})

	fmt.Printf("[D] Client --> Server : %v\n", string(reqDec))
	_, err = client.Send(string(reqDec), 0)
	checkErr(err)
	retMsg, retErr := client.Recv(0)
	//_, retErr := client.Recv(0)
	fmt.Printf("[D] Client <-- Server : %s\n", retMsg)
	checkErr(retErr)

	////////////////////////////////////////////////////////////////
	fmt.Printf(`

        Encryption : %d times --> Elapsed %s

`, loop, elapsed)
}

func runServer(port int) {
	fmt.Println("Server starts.....")
	domainName := "domain1"

	// zmq.AuthSetVerbose(true)
	serverPublic, serverSecret, err := zmq.NewCurveKeypair()
	if err != nil {
		fmt.Println("Failed to create key generation")
		return
	}

	//keyZ85encoded := zmq.Z85encode(serverPublic)
	//fmt.Printf("server key --> %s (len:%d)\n", keyZ85encoded, len(keyZ85encoded))

	fmt.Printf("server key --> %s (len:%d)\n", serverPublic, len(serverPublic))

	//  Start the authentication engine. This engine
	//  allows or denies incoming connections (talking to the libzmq
	//  core over a protocol called ZAP).
	zmq.AuthStart()
	zmq.AuthAllow(domainName, "127.0.0.1")
	//zmq.AuthAllow(domainName, "172.31.25.217")
	zmq.AuthCurveAdd(domainName, zmq.CURVE_ALLOW_ANY)
	//zmq.AuthAllow(domainName, "")
	server, err := zmq.NewSocket(zmq.REP)
	if err != nil {
		fmt.Println("Failed to create server socket")
		return
	}
	server.ServerAuthCurve(domainName, serverSecret)

	bindPort := fmt.Sprintf("tcp://*:%d", port)
	//bindPort := "tcp://0.0.0.0:6888"
	//bindPort := "tcp://*:6888"
	fmt.Printf("bindPort = %s\n", bindPort)
	server.Bind(bindPort)

	//go testClient(keyZ85encoded, port)
	go testClient(serverPublic, port)
	for true {
		msgBytes, err := server.RecvBytes(0)
		if err != nil {
			fmt.Println(err)
		} else {
			req := Request{}
			parseErr := json.Unmarshal(msgBytes, &req)
			if parseErr != nil {
				server.Send("ParseErr", 0)
				continue
			}

			if req.Action == "encrypt" || req.Action == "decrypt" {
				var r string
				var err error
				if req.Action == "encrypt" {
					r, err = req.Msg, nil
				} else {
					r, err = req.Msg, nil
				}
				checkErr(err)
				res, _ := json.Marshal(Response{Result: r})
				server.SendBytes(res, 0)
			} else {
				server.Send("Not defined request", 0)
			}
		}
	}
}

func main() {
	runServer(7000)
}
