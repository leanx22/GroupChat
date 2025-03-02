package com.groupChatV2.Leandro.Server;

import com.groupChatV2.Leandro.model.Packets.ChatMessagePacket;
import com.groupChatV2.Leandro.model.User;

import java.io.EOFException;
import java.io.IOException;
import java.util.Date;

public class ClientListener implements Runnable{

    private final User client;

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
        System.out.println("["+Thread.currentThread().getName()+"] Starting to listen.");

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
            System.out.println("["+Thread.currentThread().getName()+"] WARNING: Client disconnection or unparseable packet: "+e.getMessage());
            handleDisconnection(client);
        }

    }

    private void handleDisconnection(User client){
        System.out.println("[" + Thread.currentThread().getName() + "] Closing connection with client...");
        try {
            Server.getUsersList().remove(client);
            client.getConnectionManager().closeConnection();
            System.out.println("[" + Thread.currentThread().getName() + "] Client connection closed and removed from list.");
            ChatBroadcaster.broadcastToAllClients(new ChatMessagePacket(new Date(), "SERVER", Server.getServerUUID(), client.getUsername()+" has disconnected."));
        } catch (IOException ioe) {
            System.out.println("[" + Thread.currentThread().getName() + "] ERROR: Failed to close socket: " + ioe.getMessage());
        }
    }

}
