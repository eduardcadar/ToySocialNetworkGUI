package com.toysocialnetworkgui.utils;

import com.toysocialnetworkgui.domain.User;

import java.util.List;

public class ConversationDTO {
    private Integer id;
    private List<User> participants;

    public ConversationDTO(Integer id, List<User> participants) {
        this.id = id;
        this.participants = participants;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    @Override
    public String toString() {
        StringBuilder tostring = new StringBuilder("Conversation with: ");
        participants.forEach(p -> tostring.append(p.toString()).append(", "));

        return tostring.toString().substring(0, tostring.length() - 2);
    }
}
