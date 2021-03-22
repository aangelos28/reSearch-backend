package edu.cs518.angelopoulos.research.common.services;

import edu.cs518.angelopoulos.research.common.models.EtdEntryMeta;
import edu.cs518.angelopoulos.research.common.models.EtdDocument;
import edu.cs518.angelopoulos.research.common.models.EtdEntry;
import edu.cs518.angelopoulos.research.common.models.EtdEntryMetaSearchQuery;
import edu.cs518.angelopoulos.research.common.repositories.EtdEntryMetaRepository;
import edu.cs518.angelopoulos.research.common.repositories.EtdEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class EtdEntryService {
    private final EtdEntryRepository etdEntries;
    private final EtdEntryMetaRepository etdEntryMetaRepository;

    private final String etdDocumentStore;

    Logger logger = LoggerFactory.getLogger(EtdEntryService.class);

    public static class EtdEntryNotFoundException extends Exception {
        public EtdEntryNotFoundException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class EtdDocumentResult {
        public InputStreamResource resource;
        public File file;
    }

    @Autowired
    public EtdEntryService(EtdEntryRepository etdEntries, EtdEntryMetaRepository etdEntryMetaRepository,
                           @Value("${data.etd.documentstore}") String etdDocumentStore) {
        this.etdEntries = etdEntries;
        this.etdEntryMetaRepository = etdEntryMetaRepository;
        this.etdDocumentStore = etdDocumentStore;
    }

    /**
     * Search an ETD entry by title. Pages the results.
     *
     * @param title Title to search for
     * @param page Index of the page to return
     * @param pageSize Size of the page to return
     * @return Page of EtdEntryMeta results that match the given title
     */
    public Page<EtdEntryMeta> findByTitle(String title, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return this.etdEntryMetaRepository.findByTitle(title, pageable);
    }

    /**
     * Perform an advanced search with multiple fields to find EtdEntryMetas.
     * Pages the results.
     *
     * @param query Object containing the query information
     * @param page Index of the page to return
     * @param pageSize Size of the page to return
     * @return Page of EtdEntryMeta results that matched the given query data
     */
    public SearchPage<EtdEntryMeta> advancedSearch(EtdEntryMetaSearchQuery query, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return this.etdEntryMetaRepository.advancedSearch(query, pageable);
    }

    /**
     * Get ETD entry metadata with the specified id
     *
     * @param entryId Id of the ETD entry
     * @return EtdEntryMeta metadata for ETD entry
     * @throws EtdEntryService.EtdEntryNotFoundException If ETD entry with the specified ID is not found
     */
    public EtdEntryMeta getEtdEntryMeta(Long entryId) throws EtdEntryService.EtdEntryNotFoundException {
        Optional<EtdEntryMeta> etdEntryMetaOptional = etdEntryMetaRepository.findById(entryId);
        EtdEntryMeta etdEntryMeta;

        if (etdEntryMetaOptional.isPresent()) {
            etdEntryMeta = etdEntryMetaOptional.get();
        } else {
            throw new EtdEntryService.EtdEntryNotFoundException("Could not locate ETD meta entry with id " + entryId);
        }

        return etdEntryMeta;
    }

    /**
     * Get the first ETD document data belonging to an ETD entry.
     *
     * @param etdEntryId Id of the ETD entry
     * @return InputStreamResource for the ETD document
     * @throws EtdEntryNotFoundException If ETD entry with the specified ID is not found
     * @throws FileNotFoundException If no ETD document is found
     */
    public EtdDocumentResult getEtdDocument(Long etdEntryId) throws EtdEntryNotFoundException, FileNotFoundException {
        Optional<EtdEntry> etdEntryOptional = etdEntries.findById(etdEntryId);
        EtdEntry etdEntry;

        if (etdEntryOptional.isPresent()) {
            etdEntry = etdEntryOptional.get();
        } else {
            throw new EtdEntryNotFoundException("Could not locate ETD entry with id " + etdEntryId);
        }

        EtdDocument etdDocument = etdEntry.getDocuments().get(0);

        Path etdDocumentPath = getEtdDocumentPath(etdEntry, etdDocument);
        File etdDocumentFile = new File(String.valueOf(etdDocumentPath));

        EtdDocumentResult result = new EtdDocumentResult();
        result.resource = new InputStreamResource(new FileInputStream(etdDocumentFile));
        result.file = etdDocumentFile;

        return result;
    }

    /**
     * Inserts an ETD entry from the disk to the database.
     *
     * @param etdEntryMeta EtdEntryMeta object containing the metadata of the EtdEntry
     * @param pdfFiles Array of PDF file handles
     * @return EtdEntry object that was inserted in the database
     */
    public EtdEntry insertEtdEntryFromDisk(EtdEntryMeta etdEntryMeta, File[] pdfFiles) {
        EtdEntry etdEntry = new EtdEntry();
        etdEntry.setOriginalId(etdEntryMeta.getOriginalId());

        // Note for now we only add the first pdf file
        EtdDocument etdDocument = new EtdDocument();
        etdDocument.setFilename(pdfFiles[0].getName());

        // Create the directory for the ETD entry
        try {
            etdEntries.save(etdEntry);
            etdEntry = etdEntries.findByOriginalId(etdEntryMeta.getOriginalId());

            mapEtdEntryDirectory(etdEntryMeta, etdEntry);
            etdEntry.getDocuments().add(etdDocument);
            etdEntries.save(etdEntry);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Failed to create directory for ETD entry {}", etdEntry.getId().toString());
        }

        return etdEntry;
    }

    private Path getEtdEntryPath(EtdEntry etdEntry) {
        return Paths.get(etdDocumentStore + "/" + etdEntry.getId().toString());
    }

    private Path getOriginalEtdEntryPath(EtdEntryMeta etdEntryMeta) {
        return Paths.get(etdDocumentStore + "/" + etdEntryMeta.getOriginalId());
    }

    private Path getEtdDocumentPath(EtdEntry etdEntry, EtdDocument etdDocument) {
        return Paths.get(etdDocumentStore + "/" + etdEntry.getId() + "/" + etdDocument.getFilename());
    }

    private void mapEtdEntryDirectory(EtdEntryMeta etdEntryMeta, EtdEntry etdEntry) throws IOException {
        Path originalEntryPath = getOriginalEtdEntryPath(etdEntryMeta);
        Path destEntryPath = getEtdEntryPath(etdEntry);

        Files.move(originalEntryPath, destEntryPath);

        etdEntry.setDocuments(new ArrayList<>());
    }

    private void createEtdEntryDirectory(EtdEntry etdEntry) throws IOException {
        Path etdEntryPath = getEtdEntryPath(etdEntry);
        Files.createDirectory(etdEntryPath);
        etdEntry.setDocuments(new ArrayList<>());
    }
}
