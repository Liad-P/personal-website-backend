package liad.dev.ai.services;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import liad.dev.ai.chat.IChatSession;
import liad.dev.ai.client.AIClient;
import liad.dev.document.DocumentLoader;
import liad.dev.exceptions.DocumentNotFound;

@ApplicationScoped
public class AIService {

    @Inject
    AIClient aiClient;

    @Inject
    DocumentLoader documentLoader;

    final List<String> documents = Arrays.asList("Liad-Peretz-CV.pdf");

    Map<String, IChatSession> chatSessions = new HashMap<>();

    final String defaultSessionID = "default-session";

    public IChatSession initializeChatSession() {
        return this.initializeChatSession(this.defaultSessionID);
    }

    public IChatSession initializeChatSession(String sessionID) {
        var chatSession = aiClient.createChatSession(sessionID);
        this.documents.forEach(document -> {
            try {
                ByteBuffer pdfByteBuffer = documentLoader.load(document);
                chatSession.addPDF(pdfByteBuffer.array());
            } catch (DocumentNotFound e) {
                Log.error("Document not found: " + document + ". Error: " + e.getMessage(), e);
            }
        });
        this.chatSessions.put(sessionID, chatSession);
        return chatSession;
    }

    public IChatSession getChatSession() {
        return this.getChatSession(this.defaultSessionID);
    }
    public IChatSession getChatSession(String sessionID) {
        if (!this.chatSessions.containsKey(sessionID)) {
            throw new IllegalStateException("Chat session is not initialized with this sessionID. Call initialize() first.");
        }
        return this.chatSessions.get(sessionID);
    }

    public void closeChatSession() {
        this.closeChatSession(this.defaultSessionID);
    }

    public void closeChatSession(String sessionID) {
        if (this.chatSessions.containsKey(sessionID)) {
            this.chatSessions.remove(sessionID); 
            Log.info("Chat session with ID: " + sessionID + " closed.");
        } else {
            Log.warn("No chat session to close.");
        }
    }
}
