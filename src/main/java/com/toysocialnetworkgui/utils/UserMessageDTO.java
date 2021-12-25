package com.toysocialnetworkgui.utils;

import com.toysocialnetworkgui.domain.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserMessageDTO {
    private final User sender;
    private final String messageText;
    private final LocalDateTime date;

    public User getSender() {
        return sender;
    }

    public String getMessageText() {
        return messageText;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public UserMessageDTO(User sender, String messageText, LocalDateTime date) {
        this.sender = sender;
        this.messageText = messageText;
        this.date = date;
    }

    @Override
    public String toString() {
        return sender + ", " +
                "sent on " + date.format(DateTimeFormatter.ofPattern(CONSTANTS.DATETIME_PATTERN)) +
                ": " + messageText;
    }
}
