package com.groupChatV2.Leandro.Utils;
import com.groupChatV2.Leandro.Enumerators.Application.AppLaunchModes;

public class ArgumentsParser {
    private final AppLaunchModes mode;
    private final String ip;
    private final int port;

    public ArgumentsParser(String[] args){
        if(args.length < 2){
            throw new IllegalArgumentException("Usage: java -jar GroupChat.jar <server|client> <port> [sv_ip (only client)]");
        }

        if(args[0].equalsIgnoreCase("server")){
            this.mode = AppLaunchModes.SERVER;
        }else{
            this.mode = AppLaunchModes.CLIENT;
        }

        try{
            this.port = Integer.parseInt(args[1]);
            if(this.port < 49152 || this.port > 65535){
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Port must be an int between 49152 and 65535");
        }

        if(this.mode == AppLaunchModes.CLIENT){
            if(args.length < 3){
                throw new IllegalArgumentException("Please, specify the server IP");
            }
            this.ip = args[2];
        }else{
            this.ip = null;
        }
    }

    public AppLaunchModes getMode(){
        return this.mode;
    }

    public int getPort(){
        return this.port;
    }

    public String getServerIP(){
        return this.ip;
    }



}
