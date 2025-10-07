package org.clientside;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable{
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;
    private String IPaddr;
    private int port;
    private ClientObserver clientObserver;
    public Client(ClientObserver clientObserver,String IPaddr,int port){
        done=false;
        this.clientObserver=clientObserver;
        this.IPaddr=IPaddr;
        this.port=port;

    }
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
    public void shutdown(){
        done=true;
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
    public void sendMessage(String message){
        out.println(message);
    }
}
