package com.groupChatV2.Leandro.model.Packets;

public class ErrorPacket extends Packet{
    private final String errorInfo;

    public ErrorPacket(String text){
        this.errorInfo = text;
    }

    public String getErrorMessage(){
        return this.errorInfo;
    }
}
