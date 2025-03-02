package com.groupChatV2.Leandro.Client;

import com.groupChatV2.Leandro.Exceptions.ConnectionRefusedException;
import com.groupChatV2.Leandro.model.Packets.ChatMessagePacket;
import com.groupChatV2.Leandro.model.Packets.ErrorPacket;
import com.groupChatV2.Leandro.model.Packets.RegistrationPacket;
import com.groupChatV2.Leandro.model.User;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.UUID;

public class Client {
    private static UUID userUUID;

    public static void startClient(int port, String serverIP){
        System.out.println("Starting GroupChatV2 client...");

        try(
                ServerConnectionManager svConnectionManager = new ServerConnectionManager(new Socket(serverIP,port));
                BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        )
        {
            System.out.println("> Enter your username: ");
            String username = keyboard.readLine();
            RegistrationPacket registrationPacket = new RegistrationPacket(username);
            System.out.println("Sending username to the server...");
            svConnectionManager.getOutput().writeObject(registrationPacket);

            System.out.println("Waiting server response...");
            Object receivedPacket = svConnectionManager.getInput().readObject();
            if(receivedPacket instanceof ErrorPacket){
                throw new ConnectionRefusedException(((ErrorPacket) receivedPacket).getErrorMessage());
            }
            registrationPacket = (RegistrationPacket)receivedPacket;
            userUUID = registrationPacket.getUUID();
            System.out.println("Connection approved. Your UID: "+userUUID);

            //Start listener thread loop
            Thread serverListener = new Thread(new ServerListener(svConnectionManager, userUUID));
            serverListener.setDaemon(true);
            serverListener.setName("SERVER_LISTENER");
            serverListener.setPriority(Thread.NORM_PRIORITY);
            serverListener.start();

            //start sender loop
            String text;
            while(true){
                text = keyboard.readLine();
                if(text == null)break;
                svConnectionManager.getOutput().writeObject(new ChatMessagePacket(new Date(), username, userUUID, text));
                if(text.equalsIgnoreCase("exit"))break;
            }
        }catch(IOException e){
            System.out.println("IOException!: ");
            e.printStackTrace();
        }catch (ClassNotFoundException e){
            System.out.println("Cant parse the class: "+e.getMessage());
        }catch (ConnectionRefusedException e){
            System.out.println("The connection was refused by the server: "+e.getMessage());
        }

    }

}
