package edu.cs518.angelopoulos.research.backend.controllers;

import edu.cs518.angelopoulos.research.common.services.EtdEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RestController
public class FilesController {
    private final EtdEntryService etdEntryService;

    @Autowired
    public FilesController(EtdEntryService etdEntryService) {
        this.etdEntryService = etdEntryService;
    }

    @GetMapping(path = "/public/files/etd/{entryId}/download")
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
