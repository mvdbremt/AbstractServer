package org.serverside;

import java.net.Socket;

public interface ServerObserver {

    public void handleConnection(Socket client);
    public void handleServerShutdown(Server server);
    public void handleClientShutdown(Server.ConnectionHandler client);
    public void receiveMessage(String message);
}
