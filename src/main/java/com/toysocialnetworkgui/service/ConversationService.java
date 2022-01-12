package com.toysocialnetworkgui.service;

import com.toysocialnetworkgui.domain.Conversation;
import com.toysocialnetworkgui.domain.ConversationParticipant;
import com.toysocialnetworkgui.repository.db.ConversationDbRepo;
import com.toysocialnetworkgui.repository.db.ConversationParticipantDbRepo;
import com.toysocialnetworkgui.repository.observer.Observable;
import com.toysocialnetworkgui.utils.ConversationDTO;

import java.util.*;

public class ConversationService implements Observable {
    private final ConversationDbRepo conversationRepo;
    private final ConversationParticipantDbRepo participantsRepo;

    public ConversationDbRepo getConvRepo() { return conversationRepo; }
    public ConversationParticipantDbRepo getParticipantsRepo() { return participantsRepo; }

    public ConversationService(ConversationDbRepo conversationRepo, ConversationParticipantDbRepo participantsRepo) {
        this.conversationRepo = conversationRepo;
        this.participantsRepo = participantsRepo;
    }

    /**
     * Adds a ConversationParticipant to the repository
     * @param idConversation - the id of the conversation
     * @param participant - the email of the participant
     */
    public void addConversationParticipant(int idConversation, String participant) {
        participantsRepo.save(new ConversationParticipant(idConversation, participant));
    }

    /**
     * Returns a list with the ids of the conversations of a user
     * @param email the email of the user
     * @return list of ids
     */
    public List<Integer> getUserConversations(String email) {
        return participantsRepo.getUserConversations(email);
    }

    /**
     * returns the conversation between the participants
     * if the conversation doesn't exist, it creates the conversation
     * @param participants list of emails
     * @return conversation
     */
    public Conversation getConversation(List<String> participants) {
        List<Integer> userConversations = participantsRepo.getUserConversations(participants.get(0));
        List<ConversationParticipant> conversationParticipants = participantsRepo.getAll();
        conversationParticipants = conversationParticipants.stream()
                .filter(p -> userConversations.contains(p.getIdConversation()))
                .toList();
        Map<Integer, List<String>> participantsMap = new HashMap<>();
        conversationParticipants.forEach(p -> {
            participantsMap.putIfAbsent(p.getIdConversation(), new ArrayList<>());
            participantsMap.get(p.getIdConversation()).add(p.getParticipant());
        });
        Conversation conv;
        for (Map.Entry<Integer, List<String>> entry : participantsMap.entrySet())
            if (new HashSet<>(entry.getValue()).equals(new HashSet<>(participants))) {
                conv = conversationRepo.getConversation(entry.getKey());
                conv.setParticipants(participants);
                return conv;
            }
        conv = conversationRepo.save(new Conversation());
        conv.setParticipants(participants);
        participants.forEach(x -> addConversationParticipant(conv.getID(), x));
        notifyObservers();
        return conv;
    }

    /**
     * Returns a list with the emails of the users that participate in a conversation
     * @param id id of the conversation
     * @return list of strings
     */
    public Conversation getConversation(int id) {
        Conversation conversation = conversationRepo.getConversation(id);
        conversation.setParticipants(participantsRepo.getConversationParticipants(id));
        return conversation;
    }

    public List<String> getConversationParticipants(int id) {
        return participantsRepo.getConversationParticipants(id);
    }
}
