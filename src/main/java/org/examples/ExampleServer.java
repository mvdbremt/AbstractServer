package org.examples;

import org.serverside.Server;
import org.serverside.ServerObserver;

import java.net.Socket;

public class ExampleServer implements ServerObserver {
    Server server;
    int clientcount;

    public ExampleServer(){
        server=new Server(this,9999);
        clientcount=0;
        server.run();
    }
    @Override
    public void handleConnection(Socket client) {
        System.out.println("New connection");
        server.createNewConnectionHandler(client).setConnectionName("Client"+clientcount);
        clientcount++;
    }

    @Override
    public void handleServerShutdown(Server server) {

    }

    @Override
    public void handleClientShutdown(Server.ConnectionHandler client) {
        System.out.println("Client"+ client.getConnectionName() +"lost");
    }

    @Override
    public void receiveMessage(String message, Server.ConnectionHandler connectionHandler) {
        server.broadcast(connectionHandler.getConnectionName()+" : "+message);
    }



    public static void main(String[] args) {
        ExampleServer exampleServer=new ExampleServer();
    }
}
