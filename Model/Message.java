package com.michaelians.bluteeth.Model;

public class Message {

    String message, senderId, lastMsgId, nextMsgId;

    public Message() {

    }

    public Message(String message, String senderId) {
        this.message = message;
        this.senderId = senderId;
    }

    public String getText() {
        return message;
    }

    public void setText(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
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
