package edu.edu.cs518.angelopoulos.researchbackend.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Class that contains Firebase secrets.
 */
public class FirebaseSecrets {
    /**
     * Audience of the secrets. This is the application name in Firebase.
     */
    @Getter
    private static String audience;

    /**
     * URL to the authority that issues tokens.
     */
    @Getter
    private static String issuerUri;

    /**
     * JSON containing Firebase service account credentials.
     */
    @Getter
    private static String credentials;

    static Logger logger = LoggerFactory.getLogger(FirebaseSecrets.class);

    // Class should not be constructed
    private FirebaseSecrets() {

    }

    /**
     * Read Firebase service account credentials from a JSON file.
     * The file must be in the classpath (for example under resources/). This function must be called
     * once in the beginning of the application so that other code can utilize its
     * values.
     *
     * @param firebaseSecretsFileName Path to the JSON file under the classpath
     */
    public static void read(String firebaseSecretsFileName) {
        try {
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource secretsFile = resourceLoader.getResource(String.format("classpath:%s", firebaseSecretsFileName));
            Reader jsonReader = new InputStreamReader(secretsFile.getInputStream(), UTF_8);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode secretsJson = mapper.readTree(jsonReader);

            // Read Firebase secrets
            JsonNode firebaseSecretsJson = secretsJson.get("firebase");
            audience = firebaseSecretsJson.get("audience").textValue();
            issuerUri = firebaseSecretsJson.get("issuer_uri").textValue();
            credentials = firebaseSecretsJson.get("credentials").toString();
        } catch (IOException e) {
            logger.error("Fatal: Could not find firebase.json");
            e.printStackTrace();
        }
    }
}
