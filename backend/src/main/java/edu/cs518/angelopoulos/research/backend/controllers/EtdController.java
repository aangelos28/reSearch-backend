package edu.cs518.angelopoulos.research.backend.controllers;

import com.google.firebase.auth.FirebaseToken;
import edu.cs518.angelopoulos.research.common.models.User;
import edu.cs518.angelopoulos.research.backend.services.FirebaseAuthService;
import edu.cs518.angelopoulos.research.common.services.FavoriteEtdEntryService;
import edu.cs518.angelopoulos.research.common.services.UserService;
import edu.cs518.angelopoulos.research.common.models.EtdEntry;
import edu.cs518.angelopoulos.research.common.models.EtdEntryMeta;
import edu.cs518.angelopoulos.research.common.services.EtdEntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
public class EtdController {
    private final EtdEntryService etdEntryService;
    private final FirebaseAuthService firebaseAuthService;
    private final UserService userService;
    private final FavoriteEtdEntryService favoriteEtdEntryService;

    Logger logger = LoggerFactory.getLogger(EtdController.class);

    @Autowired
    public EtdController(EtdEntryService etdEntryService, FirebaseAuthService firebaseAuthService, UserService userService,
                         FavoriteEtdEntryService favoriteEtdEntryService) {
        this.etdEntryService = etdEntryService;
        this.firebaseAuthService = firebaseAuthService;
        this.userService = userService;
        this.favoriteEtdEntryService = favoriteEtdEntryService;
    }

    /**
     * Returns the ETD entry metadata for the entry with the given ID.
     *
     * @param entryId ID of the ETD entry to get the metadata of.
     * @return ETD entry metadata
     */
    @GetMapping(path = "/public/etd/{entryId}")
    public ResponseEntity<EtdEntryMeta> getEtdEntryMeta(@PathVariable Long entryId) {
        try {
            EtdEntryMeta etdEntryMeta = this.etdEntryService.getEtdEntryMeta(entryId);
            return ResponseEntity.ok(etdEntryMeta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Gets all the metadata for the ETD entries created by the authenticated user.
     *
     * @return List of ETD entry meta objects belonging to the user
     */
    @GetMapping(path = "/private/etd/user/created")
    public ResponseEntity<List<EtdEntryMeta>> getUserCreatedEtdEntries() {
        final FirebaseToken userIdToken = firebaseAuthService.getUserIdToken();
        final String userId = userIdToken.getUid();
        User user = userService.getUserByFirebaseId(userId);

        final List<EtdEntryMeta> userCreatedEtdMetas = etdEntryService.getMetasForEtdEntries(user.getCreatedEtdEntries());

        return ResponseEntity.ok(userCreatedEtdMetas);
    }

    /**
     * Gets all the metadata for the ETD entries created by the authenticated user.
     *
     * @return List of ETD entry meta objects belonging to the user
     */
    @GetMapping(path = "/private/etd/user/favorite")
    public ResponseEntity<List<EtdEntryMeta>> getUserFavoriteEtdEntries() {
        final FirebaseToken userIdToken = firebaseAuthService.getUserIdToken();
        final String userId = userIdToken.getUid();
        User user = userService.getUserByFirebaseId(userId);

        final List<EtdEntryMeta> userFavoriteEtdMetas = etdEntryService.getMetasForEtdEntries(user.getFavoriteEtdEntries());

        return ResponseEntity.ok(userFavoriteEtdMetas);
    }

    /**
     * Adds an ETD entry to a user's favorites.
     *
     * @param entryId ID of the ETD entry
     */
    @PutMapping(path = "/private/etd/user/favorite/add")
    public ResponseEntity<?> addUserFavoriteEtdEntry(@RequestParam(name = "i") Long entryId) {
        final FirebaseToken userIdToken = firebaseAuthService.getUserIdToken();
        final String userId = userIdToken.getUid();
        User user = userService.getUserByFirebaseId(userId);

        try {
            final EtdEntry etdEntryToAdd = etdEntryService.getEtdEntry(entryId);
            favoriteEtdEntryService.addFavoriteEtdEntry(user, etdEntryToAdd);
        } catch (EtdEntryService.EtdEntryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok().build();
    }

    /**
     * Checks every ID in a list of ETD entry IDs for presence in a user's favorite list.
     * Every match is mapped to true, false otherwise.
     *
     * @param entryIds List of ETD entry IDs to check
     * @return List of boolean values for every passed entry ID.
     */
    @GetMapping(path = "/private/etd/user/favorite/check")
    public ResponseEntity<List<Boolean>> checkEtdEntriesAreFavorite(@RequestParam(name = "i") List<Long> entryIds) {
        final FirebaseToken userIdToken = firebaseAuthService.getUserIdToken();
        final String userId = userIdToken.getUid();
        User user = userService.getUserByFirebaseId(userId);

        final List<Boolean> matches = favoriteEtdEntryService.checkEtdEntriesFavorite(user, entryIds);

        return ResponseEntity.ok(matches);
    }

    /**
     * Deletes an ETD entry from a user's favorites.
     *
     * @param entryId ID of the ETD entry
     */
    @DeleteMapping(path = "/private/etd/user/favorite/remove")
    public ResponseEntity<?> removeUserFavoriteEtdEntry(@RequestParam(name = "i") Long entryId) {
        final FirebaseToken userIdToken = firebaseAuthService.getUserIdToken();
        final String userId = userIdToken.getUid();
        User user = userService.getUserByFirebaseId(userId);

        try {
            final EtdEntry etdEntryToRemove = etdEntryService.getEtdEntry(entryId);
            favoriteEtdEntryService.removeFavoriteEtdEntry(user, etdEntryToRemove);
        } catch (EtdEntryService.EtdEntryNotFoundException | FavoriteEtdEntryService.FavoriteEtdEntryNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok().build();
    }

    /**
     * Creates a new ETD entry with the specified metadata and ETD document file.
     *
     * @param etdEntryMeta    ETD entry metadata
     * @param etdDocumentFile ETD document multipart file
     * @return True if ETD entry was created, false otherwise
     */
    @PostMapping(path = "/private/etd/create", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createEtdEntry(
            @RequestPart("metadata") @NonNull EtdEntryMeta etdEntryMeta,
            @RequestPart("etdDocument") @NonNull MultipartFile etdDocumentFile) {
        // Get user ID so it can be associated with the new ETD entry
        final FirebaseToken userIdToken = firebaseAuthService.getUserIdToken();
        final String userId = userIdToken.getUid();
        User user = userService.getUserByFirebaseId(userId);

        try {
            EtdEntry createdEntry = etdEntryService.createEtdEntry(etdEntryMeta, etdDocumentFile, user);
            logger.info("Created new ETD entry with id {}.", createdEntry.getId());
        } catch (EtdEntryService.EtdEntryValidationException e) {
            logger.error("Failed to validate ETD entry.");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid ETD entry data.");
        } catch (EtdEntryService.EtdEntryCreationException e) {
            logger.error("Failed to create ETD entry.");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Failed to create ETD entry.");
        }

        return ResponseEntity.ok().build();
    }

    /**
     * Deletes an ETD entry with the specified ID.
     *
     * @param entryId ID of the ETD entry to delete
     * @return True if entry was deleted, false otherwise
     */
    @DeleteMapping(path = "/private/etd/delete/{entryId}")
    public ResponseEntity<?> deleteEtdEntry(@PathVariable Long entryId) {
        final FirebaseToken userIdToken = firebaseAuthService.getUserIdToken();
        final String userId = userIdToken.getUid();
        User user = userService.getUserByFirebaseId(userId);

        try {
            EtdEntry etdEntry = etdEntryService.getEtdEntry(entryId);
            if (etdEntry.getUser().equals(user)) {
                logger.info("Deleted ETD entry with ID {}", entryId);
                etdEntryService.deleteEtdEntry(entryId);
                return ResponseEntity.ok().build();
            }
        } catch (EtdEntryService.EtdEntryNotFoundException e) {
            logger.error("Failed to delete ETD entry with ID {}.", entryId);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Downloads the ETD document for the ETD entry with the specified ID.
     *
     * @param entryId ID of the ETD entry to get the document from
     * @return ETD document file data as stream
     */
    @GetMapping(path = "/public/etd/{entryId}/download")
    public ResponseEntity<InputStreamResource> downloadEtdDocument(@PathVariable Long entryId) {
        try {
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
        } catch (IOException | EtdEntryService.EtdEntryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
