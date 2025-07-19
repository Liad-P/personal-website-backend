package liad.dev.ai.chat;

import java.util.UUID;

public class ChatMessage {
    private final String messageText;
    private final String messageID;
    private final String messageUser;

    public ChatMessage(String messageText, String messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageID = UUID.randomUUID().toString();
    }

    public ChatMessage(String messageText, String messageUser, String messageID) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageID = messageID;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public String toStringJson() {
        return String.format("{\"messageText\": \"%s\", \"messageID\": \"%s\", \"messageUser\": \"%s\"}",
                escapeJson(messageText), messageID, escapeJson(messageUser));
    }

    private String escapeJson(String value) {
        return value.replace("\\\\", "\\\\\\\\")
                .replace("\"", "\\\\\"")
                .replace("\b", "\\\\b")
                .replace("\f", "\\\\f")
                .replace("\n", "\\\\n")
                .replace("\r", "\\\\r")
                .replace("\t", "\\\\t");
    }
}
