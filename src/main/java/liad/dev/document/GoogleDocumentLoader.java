package liad.dev.document;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;

import io.quarkus.logging.Log;

import jakarta.annotation.PostConstruct;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import liad.dev.exceptions.DocumentNotFound;

@ApplicationScoped
public class GoogleDocumentLoader implements DocumentLoader {

    @ConfigProperty(name = "gcp.bucket", defaultValue = "Unknown bucket")
    String targetBucket;

    @ConfigProperty(name = "gcp.project.id", defaultValue = "Unknown project id")
    String targetProjectID;

    @ConfigProperty(name = "google.application.credentials.file", defaultValue = "Unknown")
    String credentialsPath;

    Storage storage;
    Bucket bucket;
    Credentials credentials;

    private Map<String, ByteBuffer> documentCache = new HashMap<>();

    GoogleDocumentLoader(){}
    
    @PostConstruct
    void initialize() {
        if (credentialsPath == null || credentialsPath.isEmpty() || credentialsPath.equals("Unknown")) {
            // Using application default credentials
            // The env variable GOOGLE_APPLICATION_CREDENTIALS should be set to the path of the credentials file
            // or the application should be running in an environment where the default credentials are available (e.g., Google Cloud Platform)
            Log.info("Using application default credentials for Google Cloud Storage");
            try {
                this.storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.getApplicationDefault()).setProjectId(targetProjectID).build().getService();
            } catch (IOException e) {
                Log.error("Failed to initialize Google Cloud Storage client with application default authentication: " + e.getMessage(), e);
                throw new RuntimeException("Failed to initialize Google Cloud Storage client with application default authentication: " + e.getMessage(), e);
            }
        }
        else{
            try {
                Log.info("Using credentials from file: " + credentialsPath);
                System.out.println("Using credentials from path: " + credentialsPath);
                this.storage = StorageOptions.newBuilder()
                        .setProjectId(targetProjectID)
                        .setCredentials(GoogleCredentials.fromStream(new FileInputStream(credentialsPath)))
                        .build()
                        .getService();

            } 
            catch (FileNotFoundException e) {
                Log.error("Credentials file not found at path: " + credentialsPath, e);
                throw new RuntimeException("Credentials file not found at path: " + credentialsPath, e);
            }
            catch (IOException e) {
                Log.error("Failed to initialize Google Cloud Storage client with provided credentials: " + e.getMessage(), e);
                throw new RuntimeException(
                        "Failed to initialize Google Cloud Storage client with provided credentials: " + e.getMessage(),
                        e);
            } 
        }
        this.bucket = this.storage.get(targetBucket);
    }    

    @Override
    public ByteBuffer load(String documentId) throws DocumentNotFound {

        if (documentCache.containsKey(documentId)) {
            Log.info("Document with ID: " + documentId + " found in cache.");
            return documentCache.get(documentId);
        }

        Blob blob = null;
        try {
            blob = this.bucket.get(documentId);
            Log.info("Loaded document with ID: " + documentId + " from bucket.");
        } catch (StorageException e) {
            Log.error("Failed to load document with ID: " + documentId + " from bucket.", e);
            throw new DocumentNotFound(
                    String.format("Document not found in bucket: %s with ID: %s", targetBucket, documentId));
        }
        if (blob == null) {
            Log.error("Document with ID: " + documentId + " not found in bucket.");
            throw new DocumentNotFound(
                    String.format("Document not found in bucket: %s with ID: %s", targetBucket, documentId));
        }
        byte[] blobContent = blob.getContent();

        if (blobContent == null || blobContent.length == 0) {
            Log.error("Document with ID: " + documentId + " is empty or not found in bucket.");
            throw new DocumentNotFound(
                    String.format("Document not found in bucket: %s with ID: %s", targetBucket, documentId));
        }
        Log.info("Document with ID: " + documentId + " loaded successfully from bucket.");
        
        ByteBuffer byteBuffer = ByteBuffer.wrap(blobContent);

        // Cache the loaded document
        documentCache.put(documentId, byteBuffer);

        return byteBuffer;
    }
    
}
