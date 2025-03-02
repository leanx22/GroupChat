package com.groupChatV2.Leandro.Server;

import com.groupChatV2.Leandro.model.Packets.ChatMessagePacket;
import com.groupChatV2.Leandro.model.User;

import java.io.IOException;
import java.io.ObjectOutputStream;

public final class ChatBroadcaster{

    private ChatBroadcaster(){}

    private static void sendChatPacketToClient(User client, ChatMessagePacket packet){
        try{
            client.getConnectionManager().getOutput().writeObject(packet);
        }catch (IOException e){
            System.out.println("An error has occurred trying to send a Chat packet to: "+client.getConnectionManager().getIP());
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
