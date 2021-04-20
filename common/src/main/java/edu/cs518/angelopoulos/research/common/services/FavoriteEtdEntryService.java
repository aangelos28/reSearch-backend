package edu.cs518.angelopoulos.research.common.services;

import edu.cs518.angelopoulos.research.common.models.EtdEntry;
import edu.cs518.angelopoulos.research.common.models.User;
import edu.cs518.angelopoulos.research.common.repositories.EtdEntryRepository;
import edu.cs518.angelopoulos.research.common.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteEtdEntryService {
    private final UserRepository users;
    private final EtdEntryRepository etdEntries;

    public static class FavoriteEtdEntryNotFound extends Exception {
        public FavoriteEtdEntryNotFound(String errorMessage) {
            super(errorMessage);
        }
    }

    @Autowired
    public FavoriteEtdEntryService(UserRepository users, EtdEntryRepository etdEntries) {
        this.users = users;
        this.etdEntries = etdEntries;
    }

    /**
     * Adds a new favorite ETD entry to a user's list.
     *
     * @param user The user for which to add the new favorite
     * @param etdEntry ETD entry to add as a favorite
     */
    public void addFavoriteEtdEntry(User user, final EtdEntry etdEntry) {
        user.getFavoriteEtdEntries().add(etdEntry);
        users.save(user);
    }

    /**
     * Removed a favorite ETD entry from a user's list.
     *
     * @param user The user for which to remove the favorite
     * @param etdEntry ETD entry to remove from favorites
     * @throws FavoriteEtdEntryNotFound ETD entry not a user's favorite
     */
    public void removeFavoriteEtdEntry(User user, final EtdEntry etdEntry) throws FavoriteEtdEntryNotFound {
        final boolean removed = user.getFavoriteEtdEntries().remove(etdEntry);

        if (!removed) {
            throw new FavoriteEtdEntryNotFound("");
        }

        users.save(user);
    }

    /**
     * Checks which ETD entry IDs are contained in a user's favorite list.
     * For each match, returns true, false otherwise.
     *
     * @param user The user for which to check the favorite list
     * @param etdEntryIds ETD entry IDs to check
     * @return List of boolean values for every passed entry ID.
     */
    public List<Boolean> checkEtdEntriesFavorite(final User user, final List<Long> etdEntryIds) {
        final List<EtdEntry> userFavorites = user.getFavoriteEtdEntries();

        return etdEntryIds.stream()
                .map(etdEntryId -> userFavorites.stream()
                        .map(EtdEntry::getId)
                        .anyMatch(favoriteId -> favoriteId.equals(etdEntryId)))
                .collect(Collectors.toList());
    }
}
