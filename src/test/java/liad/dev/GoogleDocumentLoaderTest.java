package liad.dev;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import liad.dev.document.GoogleDocumentLoader;
import liad.dev.exceptions.DocumentNotFound;

@Tag("integration")
@QuarkusTest
public class GoogleDocumentLoaderTest {

    @Inject
    GoogleDocumentLoader documentLoader;

    @Test
    public void testLoadDocument() throws DocumentNotFound {
        // The bucket and project ID are configured in application.properties for the test profile
        ByteBuffer pdf = documentLoader.load("Liad-Peretz-CV.pdf");
        assertNotNull(pdf);
        assertFalse(pdf.array().length > 0, "Loaded PDF should not be empty");
        // Further checks can be added here, like checking the content of the PDF.
    }

    @Test
    public void testLoadDocumentNotFound() {
        // The bucket and project ID are configured in application.properties for the test profile
        DocumentNotFound exception = assertThrows(DocumentNotFound.class, () -> {
            documentLoader.load("non-existent-document.pdf");
        });

        assertEquals("Document not found in bucket: personal-website-pdfs with ID: non-existent-document.pdf", exception.getMessage());
    }
}
