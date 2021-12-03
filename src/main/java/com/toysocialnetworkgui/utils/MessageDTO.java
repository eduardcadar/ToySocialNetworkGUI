package com.toysocialnetworkgui.utils;

import java.time.LocalDateTime;
import java.util.List;

public class MessageDTO {
    private Integer ID;
    private final String sender, message;
    private List<String> receivers;
    private LocalDateTime date;
    private final String msgRepliedTo;

    public MessageDTO(String sender, String message, String msgRepliedTo) {
        this.sender = sender;
        this.message = message;
        this.msgRepliedTo = msgRepliedTo;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getID() {
        return ID;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getMsgRepliedTo() {
        return msgRepliedTo;
    }
}
