package com.groupChatV2.Leandro.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnectionManager implements AutoCloseable {
    private final Socket socket;
    private final ObjectOutputStream output;
    private final ObjectInputStream input;

    public ServerConnectionManager(Socket socket) throws IOException {
        this.socket = socket;
        this.output = new ObjectOutputStream(this.socket.getOutputStream());
        this.input = new ObjectInputStream(this.socket.getInputStream());
    }

    public ObjectOutputStream getOutput(){
        return this.output;
    }

    public ObjectInputStream getInput(){
        return this.input;
    }

    public void closeConnection() throws IOException {
        System.out.println("Closing server connection...");
        this.input.close();
        this.output.close();
        this.socket.close();
    }

    @Override
    public void close() throws IOException {
        this.closeConnection();
    }
}
