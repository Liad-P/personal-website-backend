package liad.dev.ai.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.genai.Chat;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;

public class ChatSessionGemini implements IChatSession {

    private final Chat chat;
    private final List<Part> parts = new ArrayList<>();
    private String sessionId = "Unknown Session ID";

    public ChatSessionGemini(Chat chat) {
        this.chat = chat;
    }
    public ChatSessionGemini(Chat chat, String sessionId) {
        this.chat = chat;
        this.sessionId = sessionId;
    }

    @Override
    public ChatMessage sendMessage(String text) {
        List<Part> currentMessageParts = new ArrayList<>(this.parts);
        currentMessageParts.add(Part.fromText(text));
        Content userMessage = Content.builder().role("user").parts(
                currentMessageParts)
                .build();

        try {
            GenerateContentResponse response = chat.sendMessage(userMessage);
            this.parts.clear(); // Clear parts for the next message
            String responseText = response.text();
            return new ChatMessage(responseText, "model");
        } catch (Exception e) {
            throw new RuntimeException("Error sending message to Gemini", e);
        }
    }

    @Override
    public void addPDF(Object pdf) {
        if (pdf instanceof byte[]) {
            parts.add(Part.fromBytes((byte[]) pdf, "application/pdf"));
        } else {
            throw new IllegalArgumentException("PDF must be a byte array");
        }
    }

    @Override
    public void addMarkdown(Object markdown) {
        if (markdown instanceof byte[]) {
            parts.add(Part.fromBytes((byte[]) markdown, "text/markdown"));
        } else {
            throw new IllegalArgumentException("markdown must be a byte array");
        }
    }

    @Override
    public List<ChatMessage> getHistory() {
        List<ChatMessage> chatMessages = new ArrayList<>();
        List<Content> history = this.chat.getHistory(true);
        for (Content content : history) {
            String text = content.text();
            chatMessages.add(new ChatMessage(text, content.role().get()));
        }
        return chatMessages;
    }

    @Override
    public Optional<String> getSessionId() {
        return Optional.of(this.sessionId); 
    }
}
