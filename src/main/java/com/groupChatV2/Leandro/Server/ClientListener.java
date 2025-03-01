package com.groupChatV2.Leandro.Server;

import com.groupChatV2.Leandro.model.Packets.ChatMessagePacket;
import com.groupChatV2.Leandro.model.User;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

///Iterates through server's client list one by one, listening if someone sends something to the server, then prints it.
public class ClientListener implements Runnable{

    public ClientListener(){}

    @Override
    public void run() {
        System.out.println("Running client listener...");
        Thread.currentThread().setName("CLIENT_LISTENER");
        Thread.currentThread().setPriority(Thread.NORM_PRIORITY);

        while(true){
            Set<User> usersList = Server.getUsersList();
            if(usersList.isEmpty()) continue;
            for(User user: usersList){
                try{
                    ChatMessagePacket messagePacket = (ChatMessagePacket)user.getInput().readObject();
                    if(messagePacket == null){
                        ChatBroadcaster.broadcastToAllClients(new ChatMessagePacket(new Date(), "SERVER", "A user has disconnected"));
                        usersList.remove(user);
                        continue;
                    }
                    if(messagePacket.getContent().equalsIgnoreCase("disconnect")){
                        ChatBroadcaster.broadcastToAllClients(new ChatMessagePacket(new Date(), "SERVER", messagePacket.getAuthorUsername()+"has disconnected"));
                        usersList.remove(user);
                        continue;
                    }
                    ChatBroadcaster.broadcastToAllClients(messagePacket);
                } catch (ClassNotFoundException e) {
                    System.out.println("Cant parse the received packet to ChatMessagePacket! -> "+e.getMessage());
                } catch (IOException e) {
                    System.out.println("IO Error: ");
                    e.printStackTrace();
                }
            }
        }
    }
}
