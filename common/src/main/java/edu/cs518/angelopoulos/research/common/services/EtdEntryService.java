package edu.cs518.angelopoulos.research.common.services;

import edu.cs518.angelopoulos.research.common.models.*;
import edu.cs518.angelopoulos.research.common.repositories.EtdDocumentRepository;
import edu.cs518.angelopoulos.research.common.repositories.EtdEntryMetaRepository;
import edu.cs518.angelopoulos.research.common.repositories.EtdEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EtdEntryService {
    private final EtdEntryRepository etdEntryRepository;
    private final EtdEntryMetaRepository etdEntryMetaRepository;
    private final EtdDocumentRepository etdDocumentRepository;

    private final Path etdDocumentStore;

    Logger logger = LoggerFactory.getLogger(EtdEntryService.class);

    public static class EtdEntryNotFoundException extends Exception {
        public EtdEntryNotFoundException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class EtdEntryValidationException extends Exception {
        public EtdEntryValidationException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class EtdEntryCreationException extends Exception {
        public EtdEntryCreationException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class EtdDocumentResult {
        public InputStreamResource resource;
        public File file;
    }

    @Autowired
    public EtdEntryService(EtdEntryRepository etdEntryRepository, EtdEntryMetaRepository etdEntryMetaRepository,
                           EtdDocumentRepository etdDocumentRepository,
                           @Value("${data.etd.documentstore}") String etdDocumentStore) {
        this.etdEntryRepository = etdEntryRepository;
        this.etdEntryMetaRepository = etdEntryMetaRepository;
        this.etdDocumentRepository = etdDocumentRepository;
        this.etdDocumentStore = Paths.get(etdDocumentStore).toAbsolutePath().normalize();
    }

    /**
     * Search an ETD entry by title. Pages the results.
     *
     * @param title Title to search for
     * @param page Index of the page to return
     * @param pageSize Size of the page to return
     * @return Page of EtdEntryMeta results that match the given title
     */
    public SearchPage<EtdEntryMeta> findByTitle(String title, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return this.etdEntryMetaRepository.simpleSearch(title, pageable);
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
     * Gets ETD entry metadata with the specified id
     *
     * @param entryId ID of the ETD entry
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
     * Gets the ETD entry with the specified id
     *
     * @param entryId ID of the ETD entry
     * @return Etd entry data
     * @throws EtdEntryService.EtdEntryNotFoundException If ETD entry with the specified ID is not found
     */
    public EtdEntry getEtdEntry(Long entryId) throws EtdEntryService.EtdEntryNotFoundException {
        Optional<EtdEntry> etdEntryOptional = etdEntryRepository.findById(entryId);
        EtdEntry etdEntry;

        if (etdEntryOptional.isPresent()) {
            etdEntry = etdEntryOptional.get();
        } else {
            throw new EtdEntryNotFoundException("Could not locate ETD entry with id " + entryId);
        }

        return etdEntry;
    }

    /**
     * Returns the corresponding EtdEntryMeta objects for a list of EtdEntry objects.
     *
     * @param etdEntries List of ETD entries
     * @return List of EtdEntryMeta objects
     */
    public List<EtdEntryMeta> getMetasForEtdEntries(final List<EtdEntry> etdEntries) {
        List<Long> etdEntryIds = etdEntries.stream().map(EtdEntry::getId).collect(Collectors.toList());

        if (etdEntryIds.isEmpty()) {
            return new ArrayList<>();
        }

        return etdEntryMetaRepository.findAllByIdIn(etdEntryIds);
    }

    /**
     * Creates a new ETD entry with a specific ETD document.
     *
     * @param etdEntryMeta ETD entry metadata
     * @param etdDocumentFile ETD document file
     * @param user User to associate with the ETD entry
     * @throws EtdEntryCreationException If the ETD entry could not be created
     * @throws EtdEntryValidationException If the ETD entry metadata could not be validated
     */
    @Transactional
    public EtdEntry createEtdEntry(EtdEntryMeta etdEntryMeta, MultipartFile etdDocumentFile, User user) throws EtdEntryCreationException, EtdEntryValidationException {
        // Validate ETD entry metadata
        validateEtdEntryMeta(etdEntryMeta);

        // Validate ETD document MIME type
        if (!Objects.equals(etdDocumentFile.getContentType(), "application/pdf")) {
            logger.error("ETD document file {} with invalid type {} was attempted to be uploaded.", etdDocumentFile.getName(), etdDocumentFile.getContentType());
            throw new EtdEntryValidationException("Failed to create ETD entry, ETD document file has invalid type.");
        }

        // Insert etd entry in database
        EtdEntry etdEntry = new EtdEntry();
        try {
            etdEntry.setUser(user);
            etdEntry = etdEntryRepository.save(etdEntry);
        } catch (Exception e) {
            e.printStackTrace();
            throw new EtdEntryCreationException("Failed to create ETD entry in database.");
        }

        // Insert etd entry meta in ElasticSearch
        try {
            etdEntryMeta.setId(etdEntry.getId());
            etdEntryMeta.setDateIssued(new Date());
            etdEntryMeta = etdEntryMetaRepository.save(etdEntryMeta);
        } catch (Exception e) {
            e.printStackTrace();
            etdEntryRepository.deleteById(etdEntry.getId());
            throw new EtdEntryCreationException("Failed to create ETD entry meta in ElasticSearch.");
        }

        // Create etd entry directory in etd store
        try {
            createEtdEntryDirectory(etdEntry);

            // Validate file path
            String cleanFileName = StringUtils.cleanPath(Objects.requireNonNull(etdDocumentFile.getOriginalFilename()));
            if (cleanFileName.contains("..")) {
                throw new EtdEntryCreationException("Failed to create ETD entry. ETD document path has invalid characters.");
            }

            // Create ETD document
            Path filePath = Paths.get(cleanFileName);
            EtdDocument etdDocument = new EtdDocument();
            etdDocument.setFilename(filePath.getFileName().toString());
            etdDocument.setEtdEntry(etdEntry);

            // Copy ETD document to directory
            try {
                addFileToEtdEntryDirectory(etdEntry, etdDocumentFile, filePath);
            } catch (IOException e) {
                // Delete ETD entry
                etdEntryMetaRepository.deleteById(etdEntryMeta.getId());
                etdEntryRepository.deleteById(etdEntry.getId());
                deleteEtdEntryDirectory(etdEntry);

                throw new EtdEntryCreationException("Failed to add file to ETD entry directory.");
            }

            // Finally, add ETD document to ETD entry in the database
            etdDocumentRepository.save(etdDocument);
        } catch (Exception e) {
            // Delete ETD entry
            etdEntryMetaRepository.deleteById(etdEntryMeta.getId());
            etdEntryRepository.deleteById(etdEntry.getId());

            try {
                deleteEtdEntryDirectory(etdEntry);
            } catch (Exception ignored) { }

            throw new EtdEntryCreationException("Failed to create ETD entry directory.");
        }

        return etdEntry;
    }

    /**
     * Deletes the ETD entry and metadata with the specified id.
     *
     * @param id ID of the ETD entry to delete
     */
    public void deleteEtdEntry(final Long id) {
        // Delete entry from ElasticSearch
        etdEntryRepository.deleteById(id);

        // Delete entry from database
        etdEntryMetaRepository.deleteById(id);
    }

    /**
     * Validates an ETD entry metadata. Checks for the existence of essential fields.
     *
     * @param etdEntryMeta ETD entry metadata to check
     * @throws EtdEntryValidationException If ETD entry metadata is not valid
     */
    public void validateEtdEntryMeta(EtdEntryMeta etdEntryMeta) throws EtdEntryValidationException {
        if (etdEntryMeta.getTitle() == null || etdEntryMeta.getTitle().trim().isEmpty()) {
            throw new EtdEntryValidationException("Failed to create ETD entry. Title is null or empty.");
        }
        if (etdEntryMeta.getContributorAuthor() == null || etdEntryMeta.getContributorAuthor().trim().isEmpty()) {
            throw new EtdEntryValidationException("Failed to create ETD entry. Author is null or empty.");
        }
        if (etdEntryMeta.getPublisher() == null || etdEntryMeta.getPublisher().trim().isEmpty()) {
            throw new EtdEntryValidationException("Failed to create ETD entry. Publisher is null or empty.");
        }
        if (etdEntryMeta.getContributorDepartment() == null || etdEntryMeta.getContributorDepartment().trim().isEmpty()) {
            throw new EtdEntryValidationException("Failed to create ETD entry. Department is null or empty.");
        }
        if (etdEntryMeta.getSubject() == null || etdEntryMeta.getSubject().isEmpty()) {
            throw new EtdEntryValidationException("Failed to create ETD entry. Subject is null or empty.");
        }
        if (etdEntryMeta.getDescriptionAbstract() == null || etdEntryMeta.getDescriptionAbstract().trim().isEmpty()) {
            throw new EtdEntryValidationException("Failed to create ETD entry. Abstract is null or empty.");
        }
    }

    /**
     * Gets the first ETD document data belonging to an ETD entry.
     *
     * @param etdEntryId Id of the ETD entry
     * @return InputStreamResource for the ETD document
     * @throws EtdEntryNotFoundException If ETD entry with the specified ID is not found
     * @throws FileNotFoundException If no ETD document is found
     */
    public EtdDocumentResult getEtdDocument(Long etdEntryId) throws EtdEntryNotFoundException, FileNotFoundException {
        Optional<EtdEntry> etdEntryOptional = etdEntryRepository.findById(etdEntryId);
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
    public EtdEntry insertEtdEntryFromDisk(EtdEntryMeta etdEntryMeta, File sourceDirectory, File[] pdfFiles) {
        EtdEntry etdEntry = new EtdEntry();

        // Note for now we only add the first pdf file
        EtdDocument etdDocument = new EtdDocument();
        etdDocument.setFilename(pdfFiles[0].getName());

        // Create the directory for the ETD entry
        try {
            etdEntry = etdEntryRepository.save(etdEntry);
            etdDocument.setEtdEntry(etdEntry);
            etdDocumentRepository.save(etdDocument);

            mapEtdEntryDirectory(etdEntryMeta, sourceDirectory, etdEntry);
            etdEntryRepository.save(etdEntry);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Failed to create directory for ETD entry {}", etdEntry.getId().toString());
        }

        return etdEntry;
    }

    private Path getEtdEntryPath(EtdEntry etdEntry) {
        return etdDocumentStore.resolve(etdEntry.getId().toString());
    }

    private Path getEtdDocumentPath(EtdEntry etdEntry, EtdDocument etdDocument) {
        return getEtdEntryPath(etdEntry).resolve(etdDocument.getFilename());
    }

    private void mapEtdEntryDirectory(EtdEntryMeta etdEntryMeta, File sourceDir, EtdEntry etdEntry) throws IOException {
        Path originalEntryPath = sourceDir.toPath();
        Path destEntryPath = getEtdEntryPath(etdEntry);

        if (!originalEntryPath.equals(destEntryPath)) {
            Files.move(originalEntryPath, destEntryPath);
        }

        etdEntry.setDocuments(new ArrayList<>());
    }

    private void createEtdEntryDirectory(EtdEntry etdEntry) throws IOException {
        Path etdEntryPath = getEtdEntryPath(etdEntry);
        Files.createDirectory(etdEntryPath);
        etdEntry.setDocuments(new ArrayList<>());
    }

    private void deleteEtdEntryDirectory(EtdEntry etdEntry) throws IOException {
        Path etdEntryPath = getEtdEntryPath(etdEntry);
        Files.delete(etdEntryPath);
    }

    private void addFileToEtdEntryDirectory(EtdEntry etdEntry, MultipartFile file, Path filePath) throws IOException {
        Path etdEntryPath = getEtdEntryPath(etdEntry);
        Path etdDocumentPath = etdEntryPath.resolve(filePath);
        Files.copy(file.getInputStream(), etdDocumentPath, StandardCopyOption.REPLACE_EXISTING);
    }
}
