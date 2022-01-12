package com.toysocialnetworkgui.domain;

import java.time.LocalDateTime;

public class Message {
    private Integer ID, idConversation;
    private final String sender, message;
    private LocalDateTime date;

    public Message(Integer idConversation, String sender, String message) {
        this.idConversation = idConversation;
        this.sender = sender;
        this.message = message;
    }

    public void setIdConversation(Integer idConversation) {
        this.idConversation = idConversation;
    }

    /**
     * @return id of the conversation
     */
    public Integer getIdConversation() {
        return idConversation;
    }

    /**
     * Sets the date of the message
     *
     * @param date - LocalDateTime
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    /**
     * @return returns the sender of the message
     */
    public String getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return "From: " + sender +
                "\nMessage: " + message +
                "\nSent on: " + date;
    }

    /**
     * @return returns the text of the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return returns the date and time of the message
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * @return id of the message
     */
    public Integer getID() {
        return ID;
    }

    /**
     * Sets the message id
     *
     * @param ID - the id of the message
     */
    public void setID(Integer ID) {
        this.ID = ID;
    }
}
