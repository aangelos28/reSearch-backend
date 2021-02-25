package edu.edu.cs518.angelopoulos.researchbackend.repositories;

import edu.edu.cs518.angelopoulos.researchbackend.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByFirebaseId(String firebaseId);
    boolean existsByFirebaseId(String firebaseId);
    void deleteByFirebaseId(String firebaseId);
}
