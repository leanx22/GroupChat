package com.groupChatV2.Leandro.Server;

import com.groupChatV2.Leandro.Exceptions.ConnectionRefusedException;
import com.groupChatV2.Leandro.model.Packets.ErrorPacket;
import com.groupChatV2.Leandro.model.Packets.RegistrationPacket;
import com.groupChatV2.Leandro.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

///Handles fresh connections from clients.
public class NewConnectionHandler implements Runnable{

    private final Socket socket;
    private final Server server;
    private static final Logger logger = LogManager.getLogger("ServerLogger");

    public NewConnectionHandler(Socket clientSocket, Server server){
        this.socket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("CONNECTION HANDLER | "+Thread.currentThread().threadId());
        Thread.currentThread().setPriority(Thread.NORM_PRIORITY);

        logger.info("Handling a new connection from: {}", socket.getInetAddress().getHostAddress());

        try{
            ConnectionManager connectionManager = new ConnectionManager(socket);
            Object receivedPacket = connectionManager.getInput().readObject();
            if(!(receivedPacket instanceof RegistrationPacket registrationPacket)){
                connectionManager.getOutput().writeObject(new ErrorPacket("Registration packet expected!"));
                connectionManager.closeConnection();
                throw new ConnectionRefusedException("Bad packet.");
            }
            String username = registrationPacket.getUsername();
            if(username.length()<3){ //TODO better usernameValidation.
                connectionManager.getOutput().writeObject(new ErrorPacket("Username can not be less than 3 characters!"));
                connectionManager.closeConnection();
                throw new ConnectionRefusedException("Bad username.");
            }

            User user = new User(UUID.randomUUID(), username, connectionManager);
            connectionManager.getOutput().writeObject(new RegistrationPacket(user.getUUID()));
            server.getUsersList().add(user);

            server.getClientListenerThreadPool().execute(new ClientListener(user, server));

            logger.info("Connection handled successfully. UUID: {}", user.getUUID());

        }
        catch (ClassNotFoundException e){
            logger.error("The received packet could not be parsed",e);
        }catch (ConnectionRefusedException e){
            logger.error("Connection refused.",e);
        }
        catch (IOException e) {
            logger.error("An IOException was captured. Check log file for details.",e);
        }
    }
}
