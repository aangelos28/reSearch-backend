package edu.edu.cs518.angelopoulos.researchbackend.controllers;

import com.google.api.services.storage.Storage;
import com.google.firebase.auth.FirebaseToken;
import edu.edu.cs518.angelopoulos.researchbackend.services.FirebaseAuthService;
import edu.edu.cs518.angelopoulos.researchbackend.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API for interacting with user profiles.
 */
@RestController
public class UserController {
    private final UserService userService;
    private final FirebaseAuthService firebaseAuthService;

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService, FirebaseAuthService firebaseAuthService) {
        this.userService = userService;
        this.firebaseAuthService = firebaseAuthService;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Checks if a user profile exists.
     *
     * @return True if the user profile exists, false otherwise
     */
    @GetMapping(path = "/private/user/exists")
    public boolean checkUserExists() {
        final String userId = firebaseAuthService.getUserIdToken().getUid();

        // Ensure that the user profile does not already exist
        return userService.checkUserExistsByFirebaseId(userId);
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    public static class CreateUserRequest {
        public String fullName;
    }

    /**
     * Creates a new user profile if it does not already exist.
     * The firebase id is used as the identifier.
     *
     * @param requestBody Request with additional user profile info
     */
    @PostMapping(path = "/private/user/create")
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequest requestBody) {
        final FirebaseToken userIdToken = firebaseAuthService.getUserIdToken();
        final String userId = userIdToken.getUid();

        final String fullName = !requestBody.fullName.isEmpty() ? requestBody.fullName : userIdToken.getName();

        // Ensure that the user profile does not already exist
        if (userService.checkUserExistsByFirebaseId(userId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // Create the user profile
        userService.createUserWithFirebaseId(userId, fullName);
        logger.info(String.format("Created user profile with firebase id %s", userId));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    public static class UpdateUserRequest {
        public String fullName;
    }

    /**
     * Updates the information of a user profile.
     *
     * @param requestBody Request with new user info
     */
    @PutMapping(path = "/private/user/update")
    public ResponseEntity<String> updateUser(@RequestBody UpdateUserRequest requestBody) {
        final String userId = firebaseAuthService.getUserIdToken().getUid();

        userService.updateUserWithFirebaseId(userId, requestBody.fullName);
        logger.info(String.format("Updated user profile with firebase id %s", userId));

        return ResponseEntity.ok().build();
    }

    //////////////////////////////////////////////////////////////////////////////////////////
}