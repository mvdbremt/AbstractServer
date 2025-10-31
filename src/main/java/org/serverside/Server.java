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

/**
 *Server inspired by server of "Simple TCP Chat Room in Java" by NeuralNine
 */
public class Server implements Runnable {

    private ArrayList<ConnectionHandler> connections;
    private  ServerSocket server;
    private boolean done;
    private ExecutorService pool;
    private ServerObserver serverObserver;
    private int port;

    /**
     * Constructor for server
     * @param serverObserver ServerObserver is necessary to have a class that handles the different events
     * @param port port of the server
     */
    public Server(ServerObserver serverObserver,int port){

        connections=new ArrayList<>();
        done=false;
        this.serverObserver=serverObserver;
        this.port=port;
    }

    /**
     * This will start the server and from there all should be automatic
     */
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

    /**
     * This enables a handling before accepting a new client
     * @param client client for which the connection is approved
     * @return handler in case it is needed
     */
    public ConnectionHandler createNewConnectionHandler(Socket client){

        ConnectionHandler handler = new ConnectionHandler(client);
        connections.add(handler);
        pool.execute(handler);
        return handler;
    }


    /**
     * This will send a message to all clients connected
     * @param message message to be sent
     */
    public void broadcast(String message){
        //
        for (ConnectionHandler handler: connections){
            if(handler !=null){
                handler.sendMessage(message);
            }
        }
    }

    /**
     * This will send a message to all clients connected excepted the one given
     * @param exception client not concerned by broadcast
     * @param message  message to be sent
     */
    public void broadcastToAllExcept(ConnectionHandler exception,String message){

        for (ConnectionHandler handler: connections){
            if(handler !=null){
                if (handler.equals(exception)) continue;
                handler.sendMessage(message);
            }
        }
    }

    /**
     * This will send a message to all clients connected excepted those given
     * @param exception clients not concerned by broadcast
     * @param message message to be sent
     */
    public void broadcastToAllExcept(ArrayList<ConnectionHandler> exception,String message){

        for (ConnectionHandler handler: connections){
            if(handler !=null){
                if (exception.contains(handler)) continue;
                handler.sendMessage(message);
            }
        }
    }

    /**
     * Gets connections to server
     * @return ArrayList of connections to server
     */
    public ArrayList<ConnectionHandler> getConnections() {
        return connections;
    }

    /**
     * Gets port of server
     * @return port of server
     */
    public int getPort() {
        return port;
    }


    /**
     * Handles server shutdown
     */
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

    /**
     * One connectionHandler per client
     */
    public class ConnectionHandler implements Runnable{

        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String connectionName;

        /**
         * Constructor to initialise client socket in ConnectionHandler
         * @param client client that the ConnectionHandler must handle
         */
        public ConnectionHandler(Socket client){
            this.client=client;
        }

        /**
         * Lets server change name of client (not necessarily used)
         * @param connectionName new name
         */
        public void setConnectionName(String connectionName) {

            this.connectionName = connectionName;
        }

        /**
         * Gets connection name
         * @return connection name
         */
        public String getConnectionName() {
            return connectionName;
        }

        /**
         * Runs client once it is accepted
         */
        @Override
        public void run() {

            try {
                out=new PrintWriter(client.getOutputStream(),true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String message;
                while ((message=in.readLine())!= null){
                    serverObserver.receiveMessage(message,this);
                }
            } catch (IOException e) {
                shutdown();
            }
        }

        /**
         * Sends message to the client
         * @param message message to be sent
         */
        public void sendMessage(String message){
            out.println(message);
        }

        /**
         * Handles shutdown of client
         */
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
