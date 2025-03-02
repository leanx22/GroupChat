package com.groupChatV2.Leandro.Client;

import com.groupChatV2.Leandro.model.Packets.ChatMessagePacket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.UUID;

public class ServerListener extends Thread{
    private final ServerConnectionManager serverConnectionManager;
    private final UUID uuid;

    public ServerListener(ServerConnectionManager serverConnectionManager, UUID uuid){
        this.serverConnectionManager = serverConnectionManager;
        this.uuid = uuid;
    }

    @Override
    public void run() {
        try{
            while(true){
                Object packet = serverConnectionManager.getInput().readObject();
                if(packet instanceof ChatMessagePacket userTextMessage){
                    if(userTextMessage.getAuthorUUID().equals(uuid)) continue;
                    System.out.println(userTextMessage.getFormattedMessage());
                    System.out.println("*----------------------------*");
                }
            }
        }catch (IOException ioe){
            System.out.println("["+Thread.currentThread().getName()+"]: Can not read received object.");
        } catch (ClassNotFoundException e) {
            System.out.println("["+Thread.currentThread().getName()+"]: Error while trying to parse the received packet.");
        }
    }
}
