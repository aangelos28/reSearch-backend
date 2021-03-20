package edu.cs518.angelopoulos.research.common.repositories;

import edu.cs518.angelopoulos.research.common.models.EtdEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EtdEntryRepository extends CrudRepository<EtdEntry, Long> {
    @NonNull
    Optional<EtdEntry> findById(@NonNull Long id);

    boolean existsById(@NonNull Long id);
    void deleteById(@NonNull Long id);

    EtdEntry findByOriginalId(Long id);
    boolean existsByOriginalId(Long id);
    void deleteByOriginalId(Long id);
}
