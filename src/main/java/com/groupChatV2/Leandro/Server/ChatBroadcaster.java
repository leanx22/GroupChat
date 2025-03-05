package com.groupChatV2.Leandro.Server;

import com.groupChatV2.Leandro.model.Packets.ChatMessagePacket;
import com.groupChatV2.Leandro.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public final class ChatBroadcaster{

    private final Server server;
    private static final Logger logger = LogManager.getLogger("ServerLogger");

    public ChatBroadcaster(Server server) {
        this.server = server;
    }

    private synchronized void sendChatPacketToClient(User client, ChatMessagePacket packet){
        try{
            client.getConnectionManager().getOutput().writeObject(packet);
        }catch (IOException e){
            logger.error("Unable to send a chat packet to {}",client.getConnectionManager().getIP(),e);
        }
    }

    public synchronized void broadcastToAllClients(ChatMessagePacket packet){
        synchronized (server.getUsersList()){
            for(User client: server.getUsersList()){
                if(packet.getAuthorUUID() == client.getUUID()){
                    continue;
                }
                sendChatPacketToClient(client, packet);
            }
        }
    }

}
