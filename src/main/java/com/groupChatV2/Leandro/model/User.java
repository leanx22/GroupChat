package com.groupChatV2.Leandro.model;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

public class User {
    private final String username;
    private final String IP;
    private final ObjectOutputStream output;
    private final ObjectInputStream input;
    private final UUID uuid;

    //TODO const.
    public User(UUID uuid, String username, String IP, ObjectOutputStream output, ObjectInputStream input){
        this.uuid = uuid;
        this.username = username;
        this.IP = IP;
        this.output = output;
        this.input = input;
    }

    public String getUsername(){
        return this.username;
    }

    public String getIP(){
        return this.IP;
    }

    public ObjectOutputStream getOutput(){
        return this.output;
    }

    public ObjectInputStream getInput(){
        return this.input;
    }

    public UUID getUUID(){
        return this.uuid;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)return false;
        if(!(obj instanceof User)) return false;

        boolean res = false;
        if(((User) obj).getUUID() == this.uuid){
            res = true;
        }

        return res;
    }
}
