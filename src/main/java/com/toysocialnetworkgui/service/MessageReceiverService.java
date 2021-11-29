package com.toysocialnetworkgui.service;

import com.toysocialnetworkgui.domain.MessageReceiver;
import com.toysocialnetworkgui.repository.db.MessageReceiverDbRepo;

import java.util.List;

public class MessageReceiverService {
    MessageReceiverDbRepo repo;

    public MessageReceiverService(MessageReceiverDbRepo repo) {
        this.repo = repo;
    }

    /**
     * Adds a MessageReceiver to the repository
     * @param idMessage - the id of the message
     * @param receiver - the email of the receiver
     */
    public void save(int idMessage, String receiver) {
        repo.save(new MessageReceiver(idMessage, receiver));
    }

    /**
     * Returns a list with the ids of the messages received by a user
     * @param email the email of the user
     * @return list of ids
     */
    public List<Integer> getMessageIdsReceivedBy(String email) {
        return repo.getMessageIdsReceivedBy(email);
    }

    /**
     * Returns a list with the emails of the users that received a message
     * @param id id of the message
     * @return list of strings
     */
    public List<String> getMessageReceivers(int id) {
        return repo.getMessageReceivers(id);
    }
}
