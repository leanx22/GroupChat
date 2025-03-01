package com.groupChatV2.Leandro.Server;

import com.groupChatV2.Leandro.Exceptions.ConnectionRefusedException;
import com.groupChatV2.Leandro.model.Packets.ErrorPacket;
import com.groupChatV2.Leandro.model.Packets.RegistrationPacket;
import com.groupChatV2.Leandro.model.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;

///Handle every new connection on the server.
public class NewConnectionHandler implements Runnable{

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public NewConnectionHandler(Socket clientSocket){
        this.socket = clientSocket;
    }

    @Override
    public void run() {
        System.out.println("Handling a new connection from: "+socket.getInetAddress().getHostAddress()+". On Thread (ID):"+Thread.currentThread().threadId());
        Thread.currentThread().setName("NEW_CLIENT_CONNECTION_HANDLER_THREAD_"+Thread.currentThread().threadId());
        Thread.currentThread().setDaemon(true);
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        try{
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());

            RegistrationPacket regitrationData = (RegistrationPacket) input.readObject();
            String username = regitrationData.getUsername();

            if(username.length() < 3){
                //TODO better username check.
                output.writeObject(new ErrorPacket("Username can not be less than 3 characters!"));
                throw new ConnectionRefusedException("Connection from "+socket.getInetAddress().getHostAddress()+" was refused!");
            }

            User user = new User(UUID.randomUUID(),regitrationData.getUsername(), socket.getInetAddress().getHostAddress(), output, input);
            Server.getUsersList().add(user);
            output.writeObject(new RegistrationPacket(user.getUUID()));

        }catch (IOException e){
            System.out.println("Failed to set input&output streams: "+e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Error trying to parse a class: "+e.getMessage());
        } catch (ConnectionRefusedException e){
            System.out.println(e.getMessage());
        }


    }
}
