package com.toysocialnetworkgui.service;

import com.toysocialnetworkgui.domain.Message;
import com.toysocialnetworkgui.repository.db.MessageDbRepo;

import java.util.List;

public class MessageService {
    private final MessageDbRepo repo;

    public MessageDbRepo getRepo() { return repo; }

    public MessageService(MessageDbRepo repo) {
        this.repo = repo;
    }

    /**
     * Adds a message to the repository
     * @param idConversation the id of the conversation where the message is sent
     * @param sender the email of the message sender
     * @param message the text of the message
     * @return the saved message
     */
    public Message save(int idConversation, String sender, String message) {
        return repo.save(new Message(idConversation, sender, message));
    }

    /**
     * @param idConversation the id of the conversation
     * @return list with a conversation's messages
     */
    public List<Message> getConversationMessages(int idConversation) {
        return repo.getConversationMessages(idConversation);
    }

    public List<Message> getConversationMessagesPage(int idConversation, int firstrow, int rowcount) {
        return repo.getConversationMessagesPage(idConversation, firstrow, rowcount);
    }

    /**
     * @param id int id of the message
     * @return the message with the id given, null if no message has id
     */
    public Message getMessage(int id) {
        return repo.getMessage(id);
    }
}
