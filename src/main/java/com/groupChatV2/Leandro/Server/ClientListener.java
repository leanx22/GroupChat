package com.groupChatV2.Leandro.Server;

import com.groupChatV2.Leandro.model.Packets.ChatMessagePacket;
import com.groupChatV2.Leandro.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Date;

public class ClientListener implements Runnable{

    private final User client;
    private static final Logger logger = LogManager.getLogger("ServerLogger");

    private ClientListener(User client){
        this.client = client;
    }

    public static void startNewListener(User client){
        Thread thread = new Thread(new ClientListener(client));
        thread.setDaemon(true);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setName("CLIENT_LISTENER / "+client.getUUID().getLeastSignificantBits());
        thread.start();
    }

    @Override
    public void run(){
        logger.info("{}: Listening started.",client.getUUID());

        try{
            while(true){
                Object receivedPacket = client.getConnectionManager().getInput().readObject();
                if(receivedPacket instanceof ChatMessagePacket chatMessagePacket){
                    if(chatMessagePacket.getContent().equalsIgnoreCase("exit"))break;
                    ChatBroadcaster.broadcastToAllClients(chatMessagePacket);
                }
            }
            handleDisconnection(client);
        }
        catch (ClassNotFoundException | IOException e){
            logger.warn("Client disconnection or unparseable packet",e);
            handleDisconnection(client);
        }

    }

    private void handleDisconnection(User client){
        try {
            Server.getUsersList().remove(client);
            client.getConnectionManager().closeConnection();
            logger.info("{} has disconnected successfully.",client.getUUID());
            ChatBroadcaster.broadcastToAllClients(new ChatMessagePacket(new Date(), "SERVER", Server.getServerUUID(), client.getUsername()+" has disconnected."));
        } catch (IOException ioe) {
            logger.error("Failed to close the socket",ioe);
        }
    }

}
