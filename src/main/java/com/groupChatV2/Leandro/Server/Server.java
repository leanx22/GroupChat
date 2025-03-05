package com.groupChatV2.Leandro.Server;

import com.groupChatV2.Leandro.Utils.DaemonThreadFactory;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final Set<User> usersList = Collections.synchronizedSet(new HashSet<>());
    private final UUID serverUUID = UUID.randomUUID();
    private static final Logger logger = LogManager.getLogger("ServerLogger");

    private ExecutorService clientListenerPool;
    private final ExecutorService connectionHandlerPool = Executors.newFixedThreadPool(2);

    private final ChatBroadcaster chatMessagesBroadcaster = new ChatBroadcaster(this);

    public void startServer(int port, int maxClients){
        logger.info("Starting server");
        this.clientListenerPool = Executors.newFixedThreadPool(maxClients, new DaemonThreadFactory());
        try(ServerSocket serverSocket = new ServerSocket(port)){
            logger.info("Server running and listening for new clients on port :{}...",port);
            while(true){
                Socket clientSocket = serverSocket.accept();
                connectionHandlerPool.execute(new NewConnectionHandler(clientSocket, this));
            }
        }catch (IOException e){
            logger.error("Cant initialize the server", e);
        }finally {
            logger.info("Closing application");
            connectionHandlerPool.shutdownNow();
            clientListenerPool.shutdownNow();
        }
    }

    public Set<User> getUsersList(){
        return usersList;
    }

    public UUID getServerUUID(){
        return serverUUID;
    }

    public ExecutorService getClientListenerThreadPool(){
        return this.clientListenerPool;
    }

    public ChatBroadcaster getChatMessagesBroadcaster(){
        return this.chatMessagesBroadcaster;
    }
}
