package com.toysocialnetworkgui.validator;

import com.toysocialnetworkgui.domain.MessageReceiver;

public class MessageReceiverValidator implements Validator<MessageReceiver> {

    /**
     * Validates a MessageReceiver object
     * @param messageReceiver - the messageReceiver to be validated
     * @throws ValidatorException - if id message < 1 or receiver is empty string
     */
    @Override
    public void validate(MessageReceiver messageReceiver) throws ValidatorException {
        if (messageReceiver.getIdMessage() < 1)
            throw new ValidatorException("Message id must be greater than 0");
        if (messageReceiver.getReceiver().isEmpty())
            throw new ValidatorException("Message receiver must have a receiver email");
    }
}
