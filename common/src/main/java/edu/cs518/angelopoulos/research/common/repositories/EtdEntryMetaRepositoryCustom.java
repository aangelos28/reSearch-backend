package edu.cs518.angelopoulos.research.common.repositories;

import edu.cs518.angelopoulos.research.common.models.EtdEntryMeta;
import edu.cs518.angelopoulos.research.common.models.EtdEntryMetaSearchQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Repository;

@Repository
public interface EtdEntryMetaRepositoryCustom {
    SearchPage<EtdEntryMeta> simpleSearch(final String title, Pageable pageable);
    SearchPage<EtdEntryMeta> advancedSearch(EtdEntryMetaSearchQuery query, Pageable pageable);
}
