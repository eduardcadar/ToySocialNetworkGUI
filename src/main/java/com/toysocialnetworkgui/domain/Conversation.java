package com.toysocialnetworkgui.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Conversation {
    private Integer ID;
    private List<Message> messages;
    private List<String> participants;
    private LocalDateTime date;

    public Conversation() {}

    public Conversation(Integer ID, List<String> participants) {
        this.ID = ID;
        this.participants = participants;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getID() {
        return ID;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "participants=" + participants +
                '}';
    }
}
