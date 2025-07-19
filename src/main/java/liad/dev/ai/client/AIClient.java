package liad.dev.ai.client;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import liad.dev.ai.chat.IChatSession;

import java.util.concurrent.CompletableFuture;

/**
 * An interface representing a generic client for an AI model.
 * It provides methods for text generation, conversational chat, and creating
 * embeddings.
 */
public interface AIClient {

    /**
     * Create a new chat session with the AI model.
     */
    IChatSession createChatSession();

    /**
     * Create a new chat session with the AI model.
     * @param sessionId An optional session ID to use for the chat session.
     */
    IChatSession createChatSession(String sessionId);


    /**
     * Generates a single, complete response for a given prompt.
     *
     * @param prompt  The input text to the model.
     * @param options A map of optional parameters for the model (e.g., temperature,
     *                maxTokens).
     * @return The generated text as a String.
     */
    String generate(String prompt, Map<String, Object> options);

    /**
     * Generates a response as a stream of text chunks.
     *
     * @param prompt  The input text to the model.
     * @param options A map of optional parameters for the model.
     * @return A Stream of strings, where each string is a part of the response.
     */
    Stream<String> generateStream(String prompt, Map<String, Object> options);

    /**
     * Sends a list of messages as conversation history and gets the next AI
     * response.
     *
     * @param messages A list of message maps, each with "role" and "content" keys.
     * @param options  A map of optional parameters for the model.
     * @return A map representing the AI's response message.
     */
    Map<String, String> chat(List<Map<String, String>> messages, Map<String, Object> options);

    /**
     * Sends conversation history and gets a streaming response from the AI.
     *
     * @param messages A list of message maps as conversation context.
     * @param options  A map of optional parameters for the model.
     * @return A Stream of maps, each representing a chunk of the AI's response.
     */
    Stream<Map<String, String>> chatStream(List<Map<String, String>> messages, Map<String, Object> options);

    /**
     * Asynchronously generates a single, complete response.
     *
     * @param prompt  The input text to the model.
     * @param options A map of optional parameters for the model.
     * @return A CompletableFuture that will complete with the generated text.
     */
    CompletableFuture<String> generateAsync(String prompt, Map<String, Object> options);

    /**
     * Asynchronously gets a response for a multi-turn conversation.
     *
     * @param messages A list of message maps as conversation context.
     * @param options  A map of optional parameters for the model.
     * @return A CompletableFuture that will complete with a map representing the
     *         AI's response.
     */
    CompletableFuture<Map<String, String>> chatAsync(List<Map<String, String>> messages, Map<String, Object> options);

    /**
     * Creates a vector embedding for a single piece of text.
     *
     * @param text The text to embed.
     * @return A List of Floats representing the vector embedding.
     */
    List<Float> embed(String text);

    /**
     * Creates vector embeddings for a batch of texts.
     *
     * @param texts The list of texts to embed.
     * @return A List of vector embeddings.
     */
    List<List<Float>> embedBatch(List<String> texts);

    public static String generateSystemPrompt(String name) {
        return String.format("""
            You are a helpful assistant.
            Please provide concise and accurate responses to user queries based on the information you have access to.
            Your job is to answer user questions about %s's personal website and related topics.
            Your responses should be informative and convincing about the skills and experiences of %s.
            Keep strictly to the information provided in the documents you have access to.
            If you do not know the answer, say "I don't know" or "I don't have that information".
            If the user asks for information not related to %s, say "I don't know".
            """, name, name, name);
    }
}
