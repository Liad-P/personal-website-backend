package liad.dev.document;

import java.nio.ByteBuffer;

import liad.dev.exceptions.DocumentNotFound;

public interface DocumentLoader {
  ByteBuffer load(String documentId) throws DocumentNotFound;
}

