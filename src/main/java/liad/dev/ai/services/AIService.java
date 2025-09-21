package liad.dev.ai.services;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;

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

    final static String DEFAULT_DOCUMENT = "Liad-CV.pdf";
    
    @ConfigProperty(name = "CONTEXT_DOCUMENT_IDS", defaultValue = DEFAULT_DOCUMENT)
    String documentsAsString;

    List<String> documents;

    Map<String, IChatSession> chatSessions = new HashMap<>();

    final String defaultSessionID = "default-session";

    public IChatSession initializeChatSession() {
        return this.initializeChatSession(this.defaultSessionID);
    }

    public IChatSession initializeChatSession(String sessionID) {
        var chatSession = aiClient.createChatSession(sessionID);
        this.documents = this.documentsAsString != null ? List.of(this.documentsAsString.split("[;]")) : List.of(DEFAULT_DOCUMENT);
        this.documents.forEach(document -> {
            try {
                if (document == null || document.isEmpty()) {
                    // Make sure there's always at least one document to load
                    Log.warn("No document ID provided to load context from.");
                    document = DEFAULT_DOCUMENT.split(",")[0];
                }
                ByteBuffer documentByteBuffer = documentLoader.load(document);
                if (document.endsWith(".md")) {
                    Log.info("Adding markdown document to chat session: " + document);
                    chatSession.addMarkdown(documentByteBuffer.array());
                } else if (document.endsWith(".pdf")) {
                    Log.info("Adding PDF document to chat session: " + document);
                    chatSession.addPDF(documentByteBuffer.array());
                }
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
