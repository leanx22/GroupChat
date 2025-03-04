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
public class NewConnectionHandler extends Thread{

    private final Socket socket;
    private static final Logger logger = LogManager.getLogger("ServerLogger");

    private NewConnectionHandler(Socket clientSocket){
        this.socket = clientSocket;
    }

    public static void startHandling(Socket clientSocket){
        Thread thread = new Thread(new NewConnectionHandler(clientSocket));
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        Thread.currentThread().setName("FRESH CONNECTION HANDLER ("+Thread.currentThread().threadId()+")");
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

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
            Server.getUsersList().add(user);

            logger.info("Connection handled successfully. UUID: {}", user.getUUID());

            ClientListener.startNewListener(user);

        }catch (ClassNotFoundException e){
            logger.error("The received packet could not be parsed",e);
        }catch (ConnectionRefusedException e){
            logger.error("Connection refused.",e);
        }
        catch (IOException e) {
            logger.error("An IOException was captured. Check log file for details.",e);
        }
    }

    /*
    @Override
    public void run() {

        Thread.currentThread().setName("NEW CONNECTION HANDLER ("+Thread.currentThread().threadId()+")");
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        System.out.println("["+Thread.currentThread().getName()+"] Handling a new connection from: "+socket.getInetAddress().getHostAddress());

        try(
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        ){
            System.out.println("["+Thread.currentThread().getName()+"]: Waiting for user to send the registration packet...");
            Object receivedPacket = input.readObject();
            if(!(receivedPacket instanceof RegistrationPacket registerData)){
                output.writeObject(new ErrorPacket("Bad packet!"));
                throw new ConnectionRefusedException("Connection from "+socket.getInetAddress().getHostAddress()+" was " +
                        "refused because the received packet was not of the expected type!");
            }

            String username = registerData.getUsername();
            if(username.length() < 3){
                //TODO better username check.
                output.writeObject(new ErrorPacket("Username can not be less than 3 characters!"));
                throw new ConnectionRefusedException("Connection from "+socket.getInetAddress().getHostAddress()+" was refused!");
            }

            System.out.println("["+Thread.currentThread().getName()+"]: Packet received successfully!");

            System.out.println("["+Thread.currentThread().getName()+"]: Adding user to server clients list.");
            User user = new User(UUID.randomUUID(),registerData.getUsername(), socket);
            Server.getUsersList().add(user);

            System.out.println("["+Thread.currentThread().getName()+"]: Sending UUID to client.");
            output.writeObject(new RegistrationPacket(user.getUUID()));

            System.out.println("["+Thread.currentThread().getName()+"]: New connection handled successfully! Starting new Thread to handle the client.");
            new Thread(new ClientListener(user)).start();

        }catch (ClassNotFoundException e) {
            System.out.println("This packet is not a registration packet!: "+e.getMessage());
        }catch (ConnectionRefusedException e){
            System.out.println(e.getMessage());
        }catch (IOException e){
            System.out.println("IOException -> Failed to create input/output stream or an error occurred " +
                    "trying to write stream header. Message: "+e.getMessage());
        }
    }
    */
}
