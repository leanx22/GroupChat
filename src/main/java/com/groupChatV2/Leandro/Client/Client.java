package com.groupChatV2.Leandro.Client;

import com.groupChatV2.Leandro.Exceptions.ConnectionRefusedException;
import com.groupChatV2.Leandro.model.Packets.ErrorPacket;
import com.groupChatV2.Leandro.model.Packets.RegistrationPacket;
import com.groupChatV2.Leandro.model.User;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class Client {
    private static UUID userUUID;

    public static void startClient(int port, String serverIP){
        System.out.println("Starting GroupChatV2 client...");

        try(
                Socket socket = new Socket(serverIP, port);
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            ){

            System.out.println("> Enter your username: ");
            String username = keyboard.readLine();
            RegistrationPacket registrationPacket = new RegistrationPacket(username);
            System.out.println("Please, wait...");
            output.writeObject(registrationPacket);

            Object receivedPacket = input.readObject();
            if(receivedPacket instanceof ErrorPacket){
                throw new ConnectionRefusedException(((ErrorPacket) receivedPacket).getErrorMessage());
            }
            registrationPacket = (RegistrationPacket)input.readObject();
            userUUID = registrationPacket.getUUID();
            System.out.println("Connection approved. Your UID: "+userUUID);

            //Start listener thread loop

            //start sender loop

        }catch(IOException e){

        }catch (ClassNotFoundException e){
            System.out.println("Cant parse the class: "+e.getMessage());
        }catch (ConnectionRefusedException e){
            System.out.println("The connection was refused by the server: "+e.getMessage());
        }

    }

}
