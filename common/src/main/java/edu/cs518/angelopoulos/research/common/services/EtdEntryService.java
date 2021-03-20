package edu.cs518.angelopoulos.research.common.services;

import edu.cs518.angelopoulos.research.common.models.EtdEntryMeta;
import edu.cs518.angelopoulos.research.common.models.EtdDocument;
import edu.cs518.angelopoulos.research.common.models.EtdEntry;
import edu.cs518.angelopoulos.research.common.repositories.EtdEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@Service
public class EtdEntryService {
    private EtdEntryRepository etdEntries;

    private String etdDocumentStore;

    Logger logger = LoggerFactory.getLogger(EtdEntryService.class);

    @Autowired
    public EtdEntryService(EtdEntryRepository etdEntries, @Value("${data.etd.documentstore}") String etdDocumentStore) {
        this.etdEntries = etdEntries;
        this.etdDocumentStore = etdDocumentStore;
    }

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
