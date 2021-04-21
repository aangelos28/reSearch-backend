package edu.cs518.angelopoulos.research.common.repositories;

import edu.cs518.angelopoulos.research.common.models.EtdEntryMeta;
import edu.cs518.angelopoulos.research.common.models.EtdEntryMetaSearchQuery;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

public class EtdEntryMetaRepositoryCustomImpl implements EtdEntryMetaRepositoryCustom {
    private final ElasticsearchOperations elasticsearch;

    @Autowired
    public EtdEntryMetaRepositoryCustomImpl(ElasticsearchOperations elasticsearch) {
        this.elasticsearch = elasticsearch;
    }

    /**
     * Performs a simple search by title.
     *
     * @param title Title
     * @param pageable Pageable
     * @return Page with EtdEntryMeta results.
     */
    @Override
    public SearchPage<EtdEntryMeta> simpleSearch(String title, Pageable pageable) {
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withQuery(QueryBuilders.matchQuery("title", title).fuzziness(Fuzziness.ONE).prefixLength(1));

        NativeSearchQuery searchQuery = searchQueryBuilder.build();

        SearchHits<EtdEntryMeta> searchHits = elasticsearch.search(searchQuery, EtdEntryMeta.class, IndexCoordinates.of("etd_entries"));
        return SearchHitSupport.searchPageFor(searchHits, pageable);
    }

    /**
     * Performs an advanced search, matching all queried fields.
     *
     * @param query Query
     * @param pageable Pageable
     * @return Page with EtdEntryMeta results.
     */
    @Override
    public SearchPage<EtdEntryMeta> advancedSearch(EtdEntryMetaSearchQuery query, Pageable pageable) {
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();

        BoolQueryBuilder matchAllQuery = QueryBuilders.boolQuery();

        if (query.getTitle() != null) {
            matchAllQuery.must(QueryBuilders.matchQuery("title", query.getTitle()).fuzziness(Fuzziness.ONE).prefixLength(1));
        }
        if (query.getType() != null) {
            matchAllQuery.must(QueryBuilders.matchQuery("type", query.getType()));
        }
        if (query.getSubject() != null) {
            matchAllQuery.must(QueryBuilders.matchQuery("subject", query.getSubject()));
        }
        if (query.getAuthor() != null) {
            matchAllQuery.must(QueryBuilders.matchQuery("contributorAuthor", query.getAuthor()));
        }
        if (query.getDepartment() != null) {
            matchAllQuery.must(QueryBuilders.matchQuery("department", query.getDepartment()));
        }
        if (query.getDegreeGrantor() != null) {
            matchAllQuery.must(QueryBuilders.matchQuery("degreeGrantor", query.getDegreeGrantor()));
        }
        if (query.getPublisher() != null) {
            matchAllQuery.must(QueryBuilders.matchQuery("publisher", query.getPublisher()));
        }
        searchQueryBuilder.withQuery(matchAllQuery);

        NativeSearchQuery searchQuery = searchQueryBuilder.build();
        searchQuery.setPageable(pageable);

        SearchHits<EtdEntryMeta> searchHits = elasticsearch.search(searchQuery, EtdEntryMeta.class, IndexCoordinates.of("etd_entries"));
        return SearchHitSupport.searchPageFor(searchHits, pageable);
    }
}
