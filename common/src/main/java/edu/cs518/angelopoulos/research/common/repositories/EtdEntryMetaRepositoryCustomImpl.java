package edu.cs518.angelopoulos.research.common.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.util.List;

public class EtdEntryMetaRepositoryCustomImpl implements EtdEntryMetaRepositoryCustom {
    private final ElasticsearchOperations elasticsearch;

    @Autowired
    public EtdEntryMetaRepositoryCustomImpl(ElasticsearchOperations elasticsearch) {
        this.elasticsearch = elasticsearch;
    }

    // TODO implement
}
