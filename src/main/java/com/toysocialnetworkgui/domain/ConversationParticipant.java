package com.toysocialnetworkgui.domain;

public class ConversationParticipant {
    private final int idConversation;
    private final String participant;

    public ConversationParticipant(int idConversation, String participant) {
        this.idConversation = idConversation;
        this.participant = participant;
    }

    /**
     * @return int - id of the conversation
     */
    public int getIdConversation() {
        return idConversation;
    }

    /**
     * @return String - email of the participant of the conversation
     */
    public String getParticipant() {
        return participant;
    }
}
