package edu.edu.cs518.angelopoulos.researchbackend;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import edu.edu.cs518.angelopoulos.researchbackend.security.FirebaseSecrets;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
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
