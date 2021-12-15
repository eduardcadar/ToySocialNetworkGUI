package com.toysocialnetworkgui.validator;

import com.toysocialnetworkgui.domain.ConversationParticipant;

public class ConversationParticipantValidator implements Validator<ConversationParticipant> {

    /**
     * Validates a MessageReceiver object
     * @param conversationParticipant - the ConversationParticipant to be validated
     * @throws ValidatorException - if id message < 1 or receiver is empty string
     */
    @Override
    public void validate(ConversationParticipant conversationParticipant) throws ValidatorException {
        if (conversationParticipant.getIdConversation() < 1)
            throw new ValidatorException("Conversation id must be greater than 0");
        if (conversationParticipant.getParticipant().isEmpty())
            throw new ValidatorException("Message participant must have an email");
    }
}
