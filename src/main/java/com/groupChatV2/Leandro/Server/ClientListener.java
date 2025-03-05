package com.groupChatV2.Leandro.Server;

import com.groupChatV2.Leandro.model.Packets.ChatMessagePacket;
import com.groupChatV2.Leandro.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Date;

public class ClientListener implements Runnable{

    private final User client;
    private final Server server;
    private static final Logger logger = LogManager.getLogger("ServerLogger");

    public ClientListener(User client, Server server){
        this.server = server;
        this.client = client;
    }

    @Override
    public void run(){
        Thread.currentThread().setName("CLIENT_LISTENER / "+client.getUUID().getLeastSignificantBits());
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        logger.info("{}: Listening started.",client.getUUID());

        try{
            while(true){
                Object receivedPacket = client.getConnectionManager().getInput().readObject();
                if(receivedPacket instanceof ChatMessagePacket chatMessagePacket){
                    if(chatMessagePacket.getContent().equalsIgnoreCase("exit"))break;
                    server.getChatMessagesBroadcaster().broadcastToAllClients(chatMessagePacket);
                }
            }
            handleDisconnection(client);
        }
        catch (ClassNotFoundException | IOException e){
            logger.warn("Unexpected client disconnection or unparseable packet",e);
            handleDisconnection(client);
        }

    }

    private void handleDisconnection(User client){
        try {
            server.getUsersList().remove(client);
            client.getConnectionManager().closeConnection();
            logger.info("{} has disconnected successfully.",client.getUUID());
            server.getChatMessagesBroadcaster().broadcastToAllClients(new ChatMessagePacket(new Date(), "SERVER", server.getServerUUID(), client.getUsername()+" has disconnected."));
        } catch (IOException ioe) {
            logger.error("Failed to close the socket",ioe);
        }
    }

}
