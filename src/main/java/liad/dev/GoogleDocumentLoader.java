package liad.dev;

import java.nio.ByteBuffer;
import java.util.List;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;

import jakarta.annotation.PostConstruct;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GoogleDocumentLoader implements DocumentLoader {

    @ConfigProperty(name = "gcp.bucket", defaultValue = "Unknown bucket")
    String targetBucket;

    @ConfigProperty(name = "gcp.project.id", defaultValue = "Unknown bucket")
    String targetProjectID;

    Storage storage;
    Bucket bucket;

    GoogleDocumentLoader() {
    }
    
    @PostConstruct
    void initialize() {
        this.storage = StorageOptions.newBuilder().setProjectId(targetProjectID).build().getService();
        this.bucket = this.storage.get(targetBucket);
    }    


    @Override
    public List<ByteBuffer> load(String documentId) throws DocumentNotFound {
        Blob blob = null;
        try {
            blob = this.bucket.get(documentId);
        } catch (StorageException e) {
            throw new DocumentNotFound(
                    String.format("Document not found in bucket: %s with ID: %s", targetBucket, documentId));
        }
        if (blob == null) {
            throw new DocumentNotFound(
                    String.format("Document not found in bucket: %s with ID: %s", targetBucket, documentId));
        }
        byte[] blobContent = blob.getContent();

        if (blobContent == null || blobContent.length == 0) {
            throw new DocumentNotFound(
                    String.format("Document not found in bucket: %s with ID: %s", targetBucket, documentId));
        }
        
        ByteBuffer byteBuffer = ByteBuffer.wrap(blobContent);
        return List.of(byteBuffer);

    }
    
}
