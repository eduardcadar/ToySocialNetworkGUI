package com.toysocialnetworkgui.domain;

public class MessageReceiver {
    private int idMessage;
    private String receiver;

    public MessageReceiver(int idMessage, String receiver) {
        this.idMessage = idMessage;
        this.receiver = receiver;
    }

    /**
     * @return int - id of the message
     */
    public int getIdMessage() {
        return idMessage;
    }

    /**
     * @return String - email of the receiver of the message
     */
    public String getReceiver() {
        return receiver;
    }
}
