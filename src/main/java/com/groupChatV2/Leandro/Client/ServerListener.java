package com.groupChatV2.Leandro.Client;

import com.groupChatV2.Leandro.model.Packets.ChatMessagePacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.SocketException;
import java.util.UUID;

public class ServerListener extends Thread{
    private final ServerConnectionManager serverConnectionManager;
    private final UUID uuid;
    private boolean interrupt = false;
    private static final Logger logger = LogManager.getLogger("ClientLogger");

    public ServerListener(ServerConnectionManager serverConnectionManager, UUID uuid){
        this.serverConnectionManager = serverConnectionManager;
        this.uuid = uuid;
    }

    @Override
    public void run() {
        try{
            while(!interrupt){
                Object packet = serverConnectionManager.getInput().readObject();
                if(packet instanceof ChatMessagePacket userTextMessage){
                    if(userTextMessage.getAuthorUUID().equals(uuid)) continue;
                    System.out.println("*----------------------------*");
                    System.out.println("> "+userTextMessage.getFormattedMessage());
                    System.out.println("*----------------------------*");
                }
            }
        }catch (SocketException e){
            logger.error("Connection lost",e);
        }
        catch (IOException e){
            logger.error("Failed to read received packet",e);
        } catch (ClassNotFoundException e) {
            logger.error("Unparseable packet", e);
        }finally {
            logger.info("Server listening interrupted.");
        }
    }

    @Override
    public void interrupt() {
        this.interrupt = true;
    }
}
