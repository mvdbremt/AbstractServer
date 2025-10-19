package org.examples;

import org.clientside.Client;
import org.clientside.ClientObserver;

import java.net.Socket;
import java.util.Scanner;

public class ExampleClient implements ClientObserver {
    Client client;
    public ExampleClient(){
        client=new Client(this,"127.0.0.1",9999);
        Thread t =new Thread(client);
        t.start();
        Scanner sc=new Scanner(System.in);
        while (true){
            client.sendMessage(sc.nextLine());
        }

    }
    @Override
    public void handleConnection(Socket client) {
        System.out.println("Connected");
    }

    @Override
    public void handleClientShutdown(Client client) {

    }

    @Override
    public void receiveMessage(String message) {
        System.out.println(message);
    }

    public static void main(String[] args) {
        new ExampleClient();
    }
}
