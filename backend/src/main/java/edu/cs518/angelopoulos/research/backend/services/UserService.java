package edu.cs518.angelopoulos.research.backend.services;

import edu.cs518.angelopoulos.research.backend.models.User;
import edu.cs518.angelopoulos.research.backend.repositories.UserRepository;
import edu.cs518.angelopoulos.research.common.repositories.EtdEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository users;

    @Autowired
    public UserService(UserRepository users, EtdEntryRepository etdEntries) {
        this.users = users;
    }

    /**
     * Gets the user profile with a specific firebase id.
     *
     * @param userFirebaseId Firebase id of the user profile to get.
     * @return User profile
     */
    public User getUserByFirebaseId(final String userFirebaseId) {
        return users.findByFirebaseId(userFirebaseId);
    }

    /**
     * Checks if a user profile with a specific firebase id exists.
     *
     * @param userFirebaseId Firebase id of the user profile to check.
     * @return True if a user profile exists, false otherwise
     */
    public boolean checkUserExistsByFirebaseId(final String userFirebaseId) {
        return users.existsByFirebaseId(userFirebaseId);
    }

    /**
     * Creates a user profile with a specific firebase id.
     *
     * @param userFirebaseId Firebase id of the new user profile
     */
    public void createUserWithFirebaseId(final String userFirebaseId, final String fullName) {
        User newUser = new User();
        newUser.setFirebaseId(userFirebaseId);
        newUser.setFullName(fullName);

        users.save(newUser);
    }

    /**
     * Updates the information of a user profile with a specific firebase id.
     *
     * @param userFirebaseId Firebase id of the user profile to update
     * @param fullName New full name of the user
     */
    public void updateUserWithFirebaseId(final String userFirebaseId, final String fullName) {
        User user = users.findByFirebaseId(userFirebaseId);
        user.setFullName(fullName);

        users.save(user);
    }

    /**
     * Deletes an existing user profile with a specific firebase id.
     *
     * @param userFirebaseId Firebase id of the user profile to delete
     */
    public void deleteUserByFirebaseId(final String userFirebaseId) {
        users.deleteByFirebaseId(userFirebaseId);
    }
}
