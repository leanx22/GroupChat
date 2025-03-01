package com.groupChatV2.Leandro.Server;

import com.groupChatV2.Leandro.model.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Server {
    private static final Set<User> usersList = Collections.synchronizedSet(new HashSet<>());

    public static void startServer(int port){
        System.out.println("Starting server on port: "+port);

        try(ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("Server socket created successfully!");

            Thread clientListener = new Thread(new ClientListener());
            clientListener.setDaemon(true);
            clientListener.start();

            System.out.println("Server listening for new connections on port :"+port+"...");
            while(true){
                Socket clientSocket = serverSocket.accept();
                new Thread(new NewConnectionHandler(clientSocket)).start();
            }
        }catch (IOException e){
            System.out.println("Cant initialize the server: "+e.getMessage());
        }
    }

    public static Set<User> getUsersList(){
        return usersList;
    }
}
