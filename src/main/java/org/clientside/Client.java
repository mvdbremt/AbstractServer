package org.clientside;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Client inspired by client of "Simple TCP Chat Room in Java" by NeuralNine
 */
public class Client implements Runnable{
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private String IPaddr;
    private int port;
    private ClientObserver clientObserver;

    /**
     * Constructor for client
     * @param clientObserver ClientObserver is necessary to have a class that handles the different events
     * @param IPaddr IP address of server
     * @param port port of client
     */
    public Client(ClientObserver clientObserver,String IPaddr,int port){
        this.clientObserver=clientObserver;
        this.IPaddr=IPaddr;
        this.port=port;

    }

    /**
     * This will start the client and from there all should be automatic
     */
    @Override
    public void run() {
        try {
            client=new Socket(IPaddr,port);

        out=new PrintWriter(client.getOutputStream(),true);
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        clientObserver.handleConnection(client);
            String inMessage;
            while ((inMessage=in.readLine())!=null){
                clientObserver.receiveMessage(inMessage);
            }
        } catch (IOException e) {
            shutdown();
        }
    }

    /**
     * Shutdown of client
     */
    public void shutdown(){
        clientObserver.handleClientShutdown(this);
        try {
            in.close();
            out.close();
            if(!client.isClosed()){
                client.close();
            }
        } catch (IOException e) {
            //ignore
        }
    }

    /**
     * Sends message to server
     * @param message message to be sent
     */
    public void sendMessage(String message){
        out.println(message);
    }
}
