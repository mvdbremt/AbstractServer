package org.serverside;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class Server implements Runnable {
    /*
    Server inspired by server of "Simple TCP Chat Room in Java" by NeuralNine
     */
    private ArrayList<ConnectionHandler> connections;
    private  ServerSocket server;
    private boolean done =false;
    private ExecutorService pool;
    private ServerObserver serverObserver;
    private int port;

    public Server(ServerObserver serverObserver,int port){
        connections=new ArrayList<>();
        done=false;
        this.serverObserver=serverObserver;
        this.port=port;
    }
    @Override
    public void run() {
        try {
            server = new ServerSocket(port);
            pool= Executors.newCachedThreadPool();
            while (!done){
                Socket client = server.accept();
                this.serverObserver.handleConnection(client);
            }

        } catch (IOException e) {
            shutdown();
        }
    }

    public ConnectionHandler createNewConnectionHandler(Socket client){
        ConnectionHandler handler = new ConnectionHandler(client);
        connections.add(handler);
        pool.execute(handler);
        return handler;
    }


    public void broadcast(String message){
        for (ConnectionHandler handler: connections){
            if(handler !=null){
                handler.sendmessage(message);
            }
        }
    }
    public void broadcastToAllExcept(ConnectionHandler exception,String message){
        for (ConnectionHandler handler: connections){
            if(handler !=null){
                if (handler.equals(exception)) continue;
                handler.sendmessage(message);
            }
        }
    }

    public ArrayList<ConnectionHandler> getConnections() {
        return connections;
    }

    public int getPort() {
        return port;
    }



    public void shutdown()  {

        try {
            serverObserver.handleServerShutdown(this);
            done=true;
            pool.shutdown();
            if(!server.isClosed()){
                server.close();
            }
            for (ConnectionHandler handler:connections){
                handler.shutdown();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    class ConnectionHandler implements Runnable{

        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String connectionName;

        public ConnectionHandler(Socket client){
            this.client=client;
        }

        public void setConnectionName(String connectionName) {
            this.connectionName = connectionName;
        }

        public String getConnectionName() {
            return connectionName;
        }

        @Override
        public void run() {
            try {
                out=new PrintWriter(client.getOutputStream(),true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String message;
                while ((message=in.readLine())!= null){
                    serverObserver.receiveMessage(message);
                }
            } catch (IOException e) {
                shutdown();
            }
        }
        public void sendmessage(String message){
            out.println(message);
        }
        public void shutdown(){

            try {
                serverObserver.handleClientShutdown(this);
                in.close();
                out.close();
                if(!client.isClosed()){
                    client.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
