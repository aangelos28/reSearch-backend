package edu.cs518.angelopoulos.research.backend.controllers;

import edu.cs518.angelopoulos.research.common.models.EtdEntryMeta;
import edu.cs518.angelopoulos.research.common.models.EtdEntryMetaSearchQuery;
import edu.cs518.angelopoulos.research.common.services.EtdEntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class EtdSearchController {
    private final EtdEntryService etdEntryService;

    Logger logger = LoggerFactory.getLogger(EtdSearchController.class);

    private final int PAGE_SIZE = 20;

    @Autowired
    public EtdSearchController(EtdEntryService etdEntryService) {
        this.etdEntryService = etdEntryService;
    }

    /**
     * DTO for transferring user data.
     */
    public static class EtdEntryMetaSearchResponse {
        public Integer totalPages;
        public Integer resultsPerPage;
        public Long totalResultsInPages;

        public List<EtdEntryMeta> pageResults;
    }

    /**
     * Searches an ETD entry by title.
     *
     * @param title Title to search for
     * @param pageNumber Index of the page to return
     * @return Page of results and related metadata
     */
    @GetMapping(path = "/public/etd/search")
    public ResponseEntity<EtdEntryMetaSearchResponse> searchEtdByTitle(
            @RequestParam(name = "t") String title,
            @RequestParam(name = "p") Integer pageNumber) {
        SearchPage<EtdEntryMeta> etdEntryMetaPage = this.etdEntryService.findByTitle(title, pageNumber, PAGE_SIZE);

        EtdEntryMetaSearchResponse etdEntryMetaSearchResponse = new EtdEntryMetaSearchResponse();
        etdEntryMetaSearchResponse.pageResults = etdEntryMetaPage.getContent()
                .stream().map(SearchHit::getContent).collect(Collectors.toList());
        etdEntryMetaSearchResponse.totalPages = etdEntryMetaPage.getTotalPages();
        etdEntryMetaSearchResponse.resultsPerPage = PAGE_SIZE;
        etdEntryMetaSearchResponse.totalResultsInPages = etdEntryMetaPage.getTotalElements();

        return ResponseEntity.ok(etdEntryMetaSearchResponse);
    }

    /**
     * Performs an advanced search for an ETD entry, searching for multiple fields.
     *
     * @param title Title
     * @param subject Subject
     * @param author Author
     * @param department Department
     * @param degreeGrantor Degree grantor (institute)
     * @param publisher Publisher (institute)
     * @param pageNumber Index of the page to return
     * @return Page of results and related metadata
     */
    @GetMapping(path = "/public/etd/search-advanced")
    public ResponseEntity<EtdEntryMetaSearchResponse> searchEtdEntryAdvanced(
            @RequestParam(name = "t", required = false) String title,
            @RequestParam(name = "tp", required = false) String type,
            @RequestParam(name = "s", required = false) String subject,
            @RequestParam(name = "a", required = false) String author,
            @RequestParam(name = "d", required = false) String department,
            @RequestParam(name = "dg", required = false) String degreeGrantor,
            @RequestParam(name = "pb", required = false) String publisher,
            @RequestParam(name = "p") Integer pageNumber) {
        EtdEntryMetaSearchQuery query = new EtdEntryMetaSearchQuery(title, type, subject, author, department, degreeGrantor, publisher);

        SearchPage<EtdEntryMeta> etdEntryMetaPage = etdEntryService.advancedSearch(query, pageNumber, PAGE_SIZE);

        EtdEntryMetaSearchResponse etdEntryMetaSearchResponse = new EtdEntryMetaSearchResponse();
        etdEntryMetaSearchResponse.pageResults = etdEntryMetaPage.getContent()
                .stream().map(SearchHit::getContent).collect(Collectors.toList());
        etdEntryMetaSearchResponse.totalPages = etdEntryMetaPage.getTotalPages();
        etdEntryMetaSearchResponse.resultsPerPage = PAGE_SIZE;
        etdEntryMetaSearchResponse.totalResultsInPages = etdEntryMetaPage.getTotalElements();

        return ResponseEntity.ok(etdEntryMetaSearchResponse);
    }
}
