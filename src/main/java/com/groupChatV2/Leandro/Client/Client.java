package com.groupChatV2.Leandro.Client;

import com.groupChatV2.Leandro.Exceptions.ConnectionRefusedException;
import com.groupChatV2.Leandro.model.Packets.ChatMessagePacket;
import com.groupChatV2.Leandro.model.Packets.ErrorPacket;
import com.groupChatV2.Leandro.model.Packets.RegistrationPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.UUID;

public class Client {
    private static UUID userUUID;
    private static final Logger logger = LogManager.getLogger("ClientLogger");

    public static void startClient(int port, String serverIP){
        logger.info("Starting client");
        try(
                ServerConnectionManager svConnectionManager = new ServerConnectionManager(new Socket(serverIP,port));
                BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        )
        {
            System.out.println("> Enter your username: ");
            String username = keyboard.readLine();
            RegistrationPacket registrationPacket = new RegistrationPacket(username);

            logger.info("Starting server registration, please wait...");
            svConnectionManager.getOutput().writeObject(registrationPacket);
            Object receivedPacket = svConnectionManager.getInput().readObject();
            if(receivedPacket instanceof ErrorPacket){
                throw new ConnectionRefusedException(((ErrorPacket) receivedPacket).getErrorMessage());
            }
            registrationPacket = (RegistrationPacket)receivedPacket;
            userUUID = registrationPacket.getUUID();
            logger.info("Connection settled. Your UUID: {}",userUUID);

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
            logger.error(e);
        }catch (ClassNotFoundException e){
            logger.error("Unable to parse class",e);
        }catch (ConnectionRefusedException e){
            logger.error("Connection refused by the server",e);
        }finally {
            logger.info("Closing application");
        }

    }

}
