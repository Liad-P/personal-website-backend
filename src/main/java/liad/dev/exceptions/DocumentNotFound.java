package liad.dev.exceptions;

import java.io.IOException;

public class DocumentNotFound extends IOException {
    public DocumentNotFound(String message) {
        super(message);
    }

    public DocumentNotFound() {
        super("Document not found");
    }
}