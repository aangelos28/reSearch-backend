package edu.cs518.angelopoulos.research.backend.services;

import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class FirebaseAuthService {
    /**
     * Gets the Firebase ID token for the current user.
     *
     * @return Firebase ID token of current user.
     */
    public FirebaseToken getUserIdToken() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (FirebaseToken) auth.getPrincipal();
    }

    /**
     * Checks if a user is authenticated
     *
     * @return True if the current user is authenticated, false otherwise.
     */
    public boolean isAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() != null;
    }
}
