package com.groupChatV2.Leandro.Server;

import com.groupChatV2.Leandro.model.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final Logger logger = LogManager.getLogger("ServerLogger");

    public static void startServer(int port){
        logger.info("Starting server");
        try(ServerSocket serverSocket = new ServerSocket(port)){
            logger.info("Server running and listening for new clients on port :{}...",port);
            while(true){
                Socket clientSocket = serverSocket.accept();
                NewConnectionHandler.startHandling(clientSocket);
            }
        }catch (IOException e){
            logger.error("Cant initialize the server", e);
        }finally {
            logger.info("Closing application");
        }
    }

    public static Set<User> getUsersList(){
        return usersList;
    }

    public static UUID getServerUUID(){
        return serverUUID;
    }
}
