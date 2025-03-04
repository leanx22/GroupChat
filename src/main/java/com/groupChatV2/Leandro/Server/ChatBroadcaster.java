package com.groupChatV2.Leandro.Server;

import com.groupChatV2.Leandro.model.Packets.ChatMessagePacket;
import com.groupChatV2.Leandro.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public final class ChatBroadcaster{

    private static final Logger logger = LogManager.getLogger("ServerLogger");

    private ChatBroadcaster(){}

    private static void sendChatPacketToClient(User client, ChatMessagePacket packet){
        try{
            client.getConnectionManager().getOutput().writeObject(packet);
        }catch (IOException e){
            logger.error("Unable to send a chat packet to {}",client.getConnectionManager().getIP(),e);
        }
    }

    public static void broadcastToAllClients(ChatMessagePacket packet){
        synchronized (Server.getUsersList()){
            for(User client: Server.getUsersList()){
                if(packet.getAuthorUUID() == client.getUUID()){
                    continue;
                }
                sendChatPacketToClient(client, packet);
            }
        }
    }

}
