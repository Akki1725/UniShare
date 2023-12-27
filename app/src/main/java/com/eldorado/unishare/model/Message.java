package com.eldorado.unishare.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages")
public class Message {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String text, senderId, receiverId, lastMsgId, nextMsgId;

    public Message() {

    }

    public Message(String message, String senderId, String receiverId) {
        text = message;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getLastMsgId() {
        return lastMsgId;
    }

    public void setLastMsgId(String lastMsgId) {
        this.lastMsgId = lastMsgId;
    }

    public String getNextMsgId() {
        return nextMsgId;
    }

    public void setNextMsgId(String nextMsgId) {
        this.nextMsgId = nextMsgId;
    }
}