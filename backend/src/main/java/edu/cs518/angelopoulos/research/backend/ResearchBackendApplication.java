package edu.cs518.angelopoulos.research.backend;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import edu.cs518.angelopoulos.research.backend.security.FirebaseSecrets;
import edu.cs518.angelopoulos.research.common.config.CommonLibraryConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
@EntityScan(basePackages = {
        "edu.cs518.angelopoulos.research.backend.models"
})
@EnableJpaRepositories(basePackages = {
        "edu.cs518.angelopoulos.research.backend.repositories"
})
@ComponentScan(basePackages = {
        "edu.cs518.angelopoulos.research.backend.controllers",
        "edu.cs518.angelopoulos.research.backend.services",
        "edu.cs518.angelopoulos.research.backend.config"
})
@Import(CommonLibraryConfig.class)
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
