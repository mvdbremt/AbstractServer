package org.clientside;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable{
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;
    private String IPaddr;
    private int port;
    public Client(String IPaddr,int port){
        done=false;
        this.IPaddr=IPaddr;
        this.port=port;

    }
    @Override
    public void run() {

    }
}
