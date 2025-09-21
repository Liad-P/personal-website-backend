package liad.dev.ai.chat;

import java.util.List;
import java.util.Optional;

public interface IChatSession {

    /**
     * Sends a message to the chat session.
     *
     * @param text the text of the message to send
     * @return the sent ChatMessage
     */
    ChatMessage sendMessage(String text);

    /**
     * Adds a PDF document to the chat session.
     *
     * @param pdf the PDF document to add
     */
    void addPDF(Object pdf);

    /**
     * Adds a markdown document to the chat session.
     *
     * @param markdown the markdown document to add
     */
    void addMarkdown(Object markdown);

    /**
     * Retrieves the chat history.
     *
     * @return a list of ChatMessage objects representing the chat history
     */
    List<ChatMessage> getHistory();

    /**
     * Gets the session ID of the chat session.
     *
     * @return an Optional containing the session ID if available, otherwise empty
     */
    Optional<String> getSessionId();
}
