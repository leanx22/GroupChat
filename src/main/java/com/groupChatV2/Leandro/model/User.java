package com.groupChatV2.Leandro.model;

import com.groupChatV2.Leandro.Server.ConnectionManager;
import java.util.UUID;

public class User {
    private final String username;
    private final UUID uuid;
    private final ConnectionManager connection;

    //TODO const.
    public User(UUID uuid, String username, ConnectionManager connectionManager){
        this.uuid = uuid;
        this.username = username;
        this.connection = connectionManager;
    }

    public String getUsername(){
        return this.username;
    }

    public ConnectionManager getConnectionManager(){
        return this.connection;
    }

    public UUID getUUID(){
        return this.uuid;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)return false;
        if(!(obj instanceof User)) return false;
        return ((User) obj).getUUID() == this.uuid;
    }
}
