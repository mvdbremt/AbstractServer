package org.serverside;

import java.net.Socket;

public interface ServerObserver {
    // makes user handle connection of new socket
    public void handleConnection(Socket client);

    // makes user handle shutdown of server
    public void handleServerShutdown(Server server);

    // makes user handle shutdown of a client
    public void handleClientShutdown(Server.ConnectionHandler client);

    // called when server receives message
    public void receiveMessage(String message, Server.ConnectionHandler connectionHandler);
}
