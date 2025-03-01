package com.groupChatV2.Leandro.model.Packets;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ChatMessagePacket {
    private final Date date;
    private final String authorUsername;
    private final UUID authorUID;
    private final String content;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");

    public ChatMessagePacket(Date date, String author, UUID authorUID,String content){
        this.date = date;
        this.content = content;
        this.authorUsername = author;
        this.authorUID = authorUID;
    }

    public ChatMessagePacket(Date date, String author, String content){
        this.date = date;
        this.content = content;
        this.authorUsername = author;
        this.authorUID = null;
    }

    public Date getDate(){
        return this.date;
    }

    public String getFormatedDate(){
        return dateFormat.format(this.date);
    }

    public String getAuthorUsername(){
        return this.authorUsername;
    }

    public String getContent(){
        return  this.content;
    }

    public UUID getAuthorUUID(){
        return this.authorUID;
    }

    public String getFormattedMessage(){
        return "["+ getAuthorUsername()+" | "+getFormatedDate()+"]: "+getContent();
    }
}
