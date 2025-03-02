package com.groupChatV2.Leandro.Main;

import com.groupChatV2.Leandro.Client.Client;
import com.groupChatV2.Leandro.Enumerators.Application.AppLaunchModes;
import com.groupChatV2.Leandro.Server.Server;
import com.groupChatV2.Leandro.Utils.ArgumentsParser;

public class Main {
    public static void main(String[] args) {
        try {

            ArgumentsParser arguments = new ArgumentsParser(args);

            if (arguments.getMode() == AppLaunchModes.SERVER) {
                Server.startServer(arguments.getPort());
            } else {
                System.out.println("Conectándose a " +arguments.getServerIP()  + " :" +arguments.getPort()+"...");
                Client.startClient(arguments.getPort(), arguments.getServerIP());
            }

        }catch (IllegalArgumentException e){
            System.out.println("[ERROR]"+e.getMessage());
        }
    }
}