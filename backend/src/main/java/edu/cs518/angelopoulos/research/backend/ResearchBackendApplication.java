package edu.cs518.angelopoulos.research.backend;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import edu.cs518.angelopoulos.research.backend.security.FirebaseSecrets;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
@EntityScan(basePackages = {"edu.cs518.angelopoulos.research.backend.models", "edu.cs518.angelopoulos.research.common.models"})
public class ResearchBackendApplication {

    public static void main(String[] args) {
        // Read Firebase secrets
        FirebaseSecrets.read("firebase.json");

        // Initialize Firebase
        try {
            InputStream firebaseCredentials = new ByteArrayInputStream(FirebaseSecrets.getCredentials().getBytes(StandardCharsets.UTF_8));
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(firebaseCredentials))
                    .build();
            FirebaseApp.initializeApp(options);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        SpringApplication.run(ResearchBackendApplication.class, args);
    }
}
