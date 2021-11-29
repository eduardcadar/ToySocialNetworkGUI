package com.toysocialnetworkgui.service;

import com.toysocialnetworkgui.domain.Message;
import com.toysocialnetworkgui.repository.db.MessageDbRepo;

public class MessageService {
    MessageDbRepo repo;

    public MessageService(MessageDbRepo repo) {
        this.repo = repo;
    }

    /**
     * Adds a message to the repository
     * @param sender the email of the message sender
     * @param message the text of the message
     */
    public Message save(String sender, String message) {
        return repo.save(new Message(sender, message));
    }

    /**
     * Adds a reply message to the repository
     * @param sender email of the message sender
     * @param message text of the message
     * @param idMsgRepliedTo id of the message replied to
     * @return
     */
    public Message save(String sender, String message, int idMsgRepliedTo) {
        return repo.save(new Message(sender, message, idMsgRepliedTo));
    }

    /**
     * @param id int id of the message
     * @return the message with the id given, null if no message has id
     */
    public Message getMessage(int id) {
        return repo.getMessage(id);
    }
}
