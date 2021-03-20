package edu.cs518.angelopoulos.research.backend.controllers;

import edu.cs518.angelopoulos.research.common.models.EtdEntryMeta;
import edu.cs518.angelopoulos.research.common.services.EtdEntryMetaService;
import edu.cs518.angelopoulos.research.common.services.EtdEntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
public class EtdController {
    private final EtdEntryService etdEntryService;
    private final EtdEntryMetaService etdEntryMetaService;

    Logger logger = LoggerFactory.getLogger(EtdController.class);

    @Autowired
    public EtdController(EtdEntryService etdEntryService, EtdEntryMetaService etdEntryMetaService) {
        this.etdEntryService = etdEntryService;
        this.etdEntryMetaService = etdEntryMetaService;
    }

    /**
     * DTO for transferring user data.
     */
    public static class EtdEntryMetaSearchResponse {
        public Integer totalPages;
        public Long totalResultsInPages;

        public List<EtdEntryMeta> pageResults;
    }

    @GetMapping(path = "/public/etd/search")
    public ResponseEntity<EtdEntryMetaSearchResponse> searchEtdEntryByTitle(@RequestParam(name = "q") String query,
                                                                    @RequestParam(name = "p") Integer pageNumber) {
        try {
            Page<EtdEntryMeta> etdEntryMetaPage = this.etdEntryMetaService.findByTitle(query, pageNumber, 20);

            EtdEntryMetaSearchResponse etdEntryMetaSearchResponse = new EtdEntryMetaSearchResponse();
            etdEntryMetaSearchResponse.pageResults = etdEntryMetaPage.toList();
            etdEntryMetaSearchResponse.totalPages = etdEntryMetaPage.getTotalPages();
            etdEntryMetaSearchResponse.totalResultsInPages = etdEntryMetaPage.getTotalElements();

            return ResponseEntity.ok(etdEntryMetaSearchResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping(path = "/public/etd/{entryId}/download")
    public ResponseEntity<InputStreamResource> downloadEtdDocument(@PathVariable Long entryId) throws IOException, EtdEntryService.EtdEntryNotFoundException {
        EtdEntryService.EtdDocumentResult etdDocumentResult = etdEntryService.getEtdDocument(entryId);

        File etdDocumentFile = etdDocumentResult.file;

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + etdDocumentFile.getName());

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(etdDocumentFile.length())
                .contentType(MediaType.APPLICATION_PDF)
                .body(etdDocumentResult.resource);
    }
}
