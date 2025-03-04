package com.groupChatV2.Leandro.Client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnectionManager implements AutoCloseable {
    private final Socket socket;
    private final ObjectOutputStream output;
    private final ObjectInputStream input;

    private static final Logger logger = LogManager.getLogger("ClientLogger");

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
        this.input.close();
        this.output.close();
        this.socket.close();
        logger.info("Connection closed.");
    }

    @Override
    public void close() throws IOException {
        this.closeConnection();
    }
}
