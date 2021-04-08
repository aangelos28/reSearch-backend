package edu.cs518.angelopoulos.research.common.services;

import edu.cs518.angelopoulos.research.common.models.EtdClaimComment;
import edu.cs518.angelopoulos.research.common.models.EtdEntry;
import edu.cs518.angelopoulos.research.common.models.User;
import edu.cs518.angelopoulos.research.common.repositories.EtdClaimCommentRepository;
import edu.cs518.angelopoulos.research.common.repositories.EtdEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EtdClaimCommentService {
    private final EtdClaimCommentRepository etdClaimCommentRepository;
    private final EtdEntryRepository etdEntryRepository;
    private final EtdEntryService etdEntryService;

    Logger logger = LoggerFactory.getLogger(EtdClaimCommentService.class);

    @Autowired
    public EtdClaimCommentService( EtdClaimCommentRepository etdClaimCommentRepository, EtdEntryRepository etdEntryRepository, EtdEntryService etdEntryService) {
        this.etdClaimCommentRepository = etdClaimCommentRepository;
        this.etdEntryRepository = etdEntryRepository;
        this.etdEntryService = etdEntryService;
    }

    /**
     * Gets the ETD claim comments of an ETD entry.
     *
     * @param entryId ID of the ETD entry to get comments of
     * @return List of ETD claim comments
     * @throws EtdEntryService.EtdEntryNotFoundException Target ETD entry not found
     */
    public List<EtdClaimComment> getEntryComments(final Long entryId) throws EtdEntryService.EtdEntryNotFoundException {
        final EtdEntry etdEntry = etdEntryService.getEtdEntry(entryId);

        return etdEntry.getClaimComments();
    }

    /**
     * Adds an ETD claim comment to an ETD entry.
     *
     * @param entryId ID of the ETD entry to add comment to
     * @param comment ETD claim comment
     * @param author Author of the comment
     * @throws EtdEntryService.EtdEntryNotFoundException Target ETD entry not found
     */
    public EtdClaimComment addCommentToEntry(final Long entryId, final EtdClaimComment comment, final User author) throws EtdEntryService.EtdEntryNotFoundException {
        final EtdEntry etdEntry = etdEntryService.getEtdEntry(entryId);

        comment.setEtdEntry(etdEntry);
        comment.setUser(author);

        return this.etdClaimCommentRepository.save(comment);
    }
}
