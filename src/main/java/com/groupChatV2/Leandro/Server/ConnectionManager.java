package com.groupChatV2.Leandro.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionManager {
    private final Socket socket;
    private final ObjectOutputStream output;
    private final ObjectInputStream input;

    public ConnectionManager(Socket socket) throws IOException {
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

    public String getIP(){
        return this.socket.getInetAddress().getHostAddress();
    }

    public void closeConnection() throws IOException{
        this.socket.close();
    }

}
