package com.groupChatV2.Leandro.Server;

import com.groupChatV2.Leandro.model.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Server {
    private static final Set<User> usersList = Collections.synchronizedSet(new HashSet<>());
    private static final UUID serverUUID = UUID.randomUUID();

    public static void startServer(int port){
        System.out.println("Starting server on port: "+port);

        try(ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("Server running and listening for new clients on port :"+port+"...");
            while(true){
                Socket clientSocket = serverSocket.accept();
                NewConnectionHandler.startAsDaemon(clientSocket);
            }
        }catch (IOException e){
            System.out.println("Cant initialize the server: "+e.getMessage());
        }finally {
            System.out.println("Server closing");
        }
    }

    public static Set<User> getUsersList(){
        return usersList;
    }

    public static UUID getServerUUID(){
        return serverUUID;
    }
}
