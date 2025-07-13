package liad.dev;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public interface DocumentLoader {
  List<ByteBuffer> load(String documentId) throws DocumentNotFound;
}

class DocumentNotFound extends IOException {
  public DocumentNotFound(String message) {
    super(message);
  }
  
  public DocumentNotFound() {
    super("Document not found");
  }
}
