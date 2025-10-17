package org.clientside;


import java.net.Socket;

/**
 * Used to interact with client
 */
public interface ClientObserver {
    /**
     * Force handle of client connection
     * @param client client that is connecting
     */
    public void handleConnection(Socket client);

    /**
     * Force handle of client shutdown
     * @param client client that is shutting down
     */
    public void handleClientShutdown(Client client);

    /**
     * called when client receives message
     * @param message message that was received
     */
    public void receiveMessage(String message);
}
