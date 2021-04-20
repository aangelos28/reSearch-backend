package edu.cs518.angelopoulos.research.common.repositories;

import org.springframework.data.repository.CrudRepository;
import edu.cs518.angelopoulos.research.common.models.User;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByFirebaseId(String firebaseId);
    boolean existsByFirebaseId(String firebaseId);
    void deleteByFirebaseId(String firebaseId);
}
