package com.groupChatV2.Leandro.Server;

import com.groupChatV2.Leandro.Utils.DaemonThreadFactory;
import com.groupChatV2.Leandro.Utils.Validations;
import com.groupChatV2.Leandro.model.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.BindException;
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

        if(Validations.isMaxClientCountExpensive(maxClients)){
            logger.warn("The configured maximum number of clients is too high for your hardware. " +
                    "This may lead to excessive resource usage and performance issues. Consider lowering the maximum client limit.");
        }

        this.clientListenerPool = Executors.newFixedThreadPool(maxClients, new DaemonThreadFactory());
        try(ServerSocket serverSocket = new ServerSocket(port)){
            logger.info("Server running and listening for new clients on port :{}...",port);
            while(true){
                Socket clientSocket = serverSocket.accept();
                connectionHandlerPool.execute(new NewConnectionHandler(clientSocket, this));
            }
        }catch (BindException e){
            logger.error("Cant bind to port {}", port, e);
        }
        catch (IOException e){
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
