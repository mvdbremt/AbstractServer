package org.clientside;


import java.net.Socket;

public interface ClientObserver {
    // makes user handle connection to server
    public void handleConnection(Socket client);

    // makes user handle shutdown of client
    public void handleClientShutdown(Client client);

    // called when client receives message
    public void receiveMessage(String message);
}
