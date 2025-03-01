package com.groupChatV2.Leandro.model.Packets;

import java.util.UUID;

///DataPacket used to prepare a user to use the chat, singleUse by NewConnectionHandler.
public class RegistrationPacket extends Packet{
    private String username;
    private UUID uuid;

    public RegistrationPacket(String username){
        this.username = username;
    }
    public RegistrationPacket(UUID UUID){
        this.uuid = UUID;
    }

    public String getUsername(){
        return this.username;
    }

    public UUID getUUID(){
        return this.uuid;
    }

}
