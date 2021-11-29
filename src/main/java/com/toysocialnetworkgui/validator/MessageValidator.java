package com.toysocialnetworkgui.validator;

import com.toysocialnetworkgui.domain.Message;

public class MessageValidator implements Validator<Message> {

    /**
     * Validates a message
     * @param message - the message to be validated
     * @throws ValidatorException - if message is empty, if there is no sender
     */
    @Override
    public void validate(Message message) throws ValidatorException {
        if (message.getMessage() == null) throw new ValidatorException("The message can't be empty");
        if (message.getSender() == null) throw new ValidatorException("Message must have a sender");
    }
}
