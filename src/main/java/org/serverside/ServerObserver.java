package org.serverside;

import java.net.Socket;

public interface ServerObserver {
    /**
     * Force handle of client connection
     * @param client client that is connecting
     */

    public void handleConnection(Socket client);

    /**
     * Force handle of server shutdown
     * @param server server that is shutting down
     */
    public void handleServerShutdown(Server server);

    /**
     * Force handle of client shutdown
     * @param client client that is shutting down
     */
    public void handleClientShutdown(Server.ConnectionHandler client);

    /**
     * called when server receives message
     * @param message message that was received
     * @param connectionHandler client that sent message
     */
    public void receiveMessage(String message, Server.ConnectionHandler connectionHandler);
}
