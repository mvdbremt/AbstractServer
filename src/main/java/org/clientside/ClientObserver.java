package org.clientside;


import java.net.Socket;

public interface ClientObserver {
    public void handleConnection(Socket client);
    public void handleClientShutdown(Client client);
    public void receiveMessage(String message);
}
