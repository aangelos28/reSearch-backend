package edu.cs518.angelopoulos.research.etdinserter.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cs518.angelopoulos.research.common.models.EtdEntryMeta;
import edu.cs518.angelopoulos.research.common.models.EtdEntry;
import edu.cs518.angelopoulos.research.common.repositories.EtdEntryMetaRepository;
import edu.cs518.angelopoulos.research.common.services.EtdEntryService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class EtdInserterService {
    private EtdEntryService etdEntryService;
    private EtdEntryMetaRepository elasticEtdEntries;

    private String etdDocumentStore;

    @Autowired
    public EtdInserterService(EtdEntryService etdEntryService, EtdEntryMetaRepository elasticEtdEntries,
                              @Value("${data.etd.documentstore}") String etdDocumentStore) {
        this.etdEntryService = etdEntryService;
        this.elasticEtdEntries = elasticEtdEntries;
        this.etdDocumentStore = etdDocumentStore;
    }

    public void insertFromDirectory(String directoryPath, boolean copyToStore) throws IOException {
        System.out.printf("Scanning '%s' for ETD entries...\n", directoryPath);

        // Note: this can take time
        if (copyToStore) {
            File etdDirectory = new File(directoryPath);
            File destEtdStore = new File(etdDocumentStore);

            FileUtils.copyDirectory(etdDirectory, destEtdStore);
        }

        File etdStoreDirectory = new File(etdDocumentStore);
        File[] entryDirectories = etdStoreDirectory.listFiles(File::isDirectory);
        assert entryDirectories != null;

        System.out.printf("Number of ETD entries to insert: %d\n", entryDirectories.length);

        // Use a buffer for bulk inserting entries to ElasticSearch
        List<EtdEntryMeta> etdEntryMetaBuffer = new ArrayList<>();
        final int bufferSize = 5000;

        int entriesInserted = 0;
        for (File entryDirectory : entryDirectories) {
            System.out.printf("Inserting entry %s...\n", entryDirectory.getName());

            // Get number of JSON files
            File[] jsonFiles = findFilesByType(entryDirectory.getPath(), ".json");
            if (jsonFiles.length > 1) continue;

            File[] pdfFiles = findFilesByType(entryDirectory.getPath(), ".pdf");
            if (pdfFiles.length < 1) continue;

            // Read JSON file and get ETD metadata
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            EtdEntryMeta etdEntryMeta = mapper.readValue(jsonFiles[0], EtdEntryMeta.class);

            // Create entry in ETD store and database
            EtdEntry etdEntry = this.etdEntryService.insertEtdEntryFromDisk(etdEntryMeta, pdfFiles);
            etdEntryMeta.setId(etdEntry.getId());

            // Add entry to ES buffer to bulk insert later
            etdEntryMetaBuffer.add(etdEntryMeta);

            // Index ETD entries in ElasticSearch
            if (entriesInserted % bufferSize == 0) {
                System.out.println("Indexing in ElasticSearch...");
                elasticEtdEntries.saveAll(etdEntryMetaBuffer);
                etdEntryMetaBuffer.clear();
            }

            ++entriesInserted;
        }

        // Index any remaining ETD entries
        System.out.println("Indexing in ElasticSearch...");
        elasticEtdEntries.saveAll(etdEntryMetaBuffer);
        etdEntryMetaBuffer.clear();

        System.out.printf("Inserted %d ETD entries, skipped %d\n", entriesInserted, entryDirectories.length - entriesInserted);
    }

    private File[] findFilesByType(String directoryPath, String type) {
        File directory = new File(directoryPath);
        return directory.listFiles((file, filename) ->
                filename.endsWith(type.toLowerCase()) || filename.endsWith(type.toUpperCase())
        );
    }
}
