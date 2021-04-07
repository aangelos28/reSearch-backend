package edu.cs518.angelopoulos.research.common.repositories;

import edu.cs518.angelopoulos.research.common.models.EtdEntryMeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EtdEntryMetaRepository extends ElasticsearchRepository<EtdEntryMeta, Long>, EtdEntryMetaRepositoryCustom {
    @NonNull
    Optional<EtdEntryMeta> findById(@NonNull Long id);

    List<EtdEntryMeta> findAllByIdIn(List<Long> ids);

    Page<EtdEntryMeta> findByTitle(String title, Pageable pageable);
}
