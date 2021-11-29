package com.toysocialnetworkgui.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message {
    private Integer ID;
    private final String sender, message;
    private List<String> receivers;
    private LocalDateTime date;
    private Integer idMsgRepliedTo;

    public Message(String sender, String message) {
        this.sender = sender;
        this.message = message;
        this.date = null;
        this.idMsgRepliedTo = null;
    }

    public Message(String sender, String message, int idMsgRepliedTo) {
        this.sender = sender;
        this.message = message;
        this.idMsgRepliedTo = idMsgRepliedTo;
    }

    /**
     * Sets the list of receivers of the message
     * @param receivers - list with the emails of the receivers
     */
    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }

    /**
     * Sets the date of the message
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
                "\nTo: " + receivers +
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
     * @return list with the message receivers
     */
    public List<String> getReceivers() {
        return receivers;
    }

    /**
     * @return id of the message
     */
    public Integer getID() {
        return ID;
    }

    /**
     * Sets the message id
     * @param ID
     */
    public void setID(Integer ID) {
        this.ID = ID;
    }

    /**
     * @return id of the message replied to
     */
    public Integer getIdMsgRepliedTo() {
        return idMsgRepliedTo;
    }

    /**
     * Sets the id of the message replied to
     * @param idMsgRepliedTo
     */
    public void setIdMsgRepliedTo(Integer idMsgRepliedTo) {
        this.idMsgRepliedTo = idMsgRepliedTo;
    }

    /**
     * @return true if the message is a reply, false otherwise
     */
    public boolean isReply() {
        return idMsgRepliedTo != null;
    }
}
