package edu.cs518.angelopoulos.research.common.services;

import edu.cs518.angelopoulos.research.common.models.EtdClaimComment;
import edu.cs518.angelopoulos.research.common.models.EtdClaimCommentLikeStatus;
import edu.cs518.angelopoulos.research.common.models.EtdEntry;
import edu.cs518.angelopoulos.research.common.models.User;
import edu.cs518.angelopoulos.research.common.repositories.EtdClaimCommentRepository;
import edu.cs518.angelopoulos.research.common.repositories.EtdEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing ETD claim comments.
 */
@Service
public class EtdClaimCommentService {
    private final EtdClaimCommentRepository etdClaimCommentRepository;
    private final EtdEntryRepository etdEntryRepository;
    private final EtdEntryService etdEntryService;

    Logger logger = LoggerFactory.getLogger(EtdClaimCommentService.class);

    public static class EtdClaimCommentNotFoundException extends Exception {
        public EtdClaimCommentNotFoundException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class EtdClaimAlreadyLikedException extends Exception {
        public EtdClaimAlreadyLikedException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class EtdClaimAlreadyDislikedException extends Exception {
        public EtdClaimAlreadyDislikedException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class EtdClaimNotLikedException extends Exception {
        public EtdClaimNotLikedException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class EtdClaimNotDislikedException extends Exception {
        public EtdClaimNotDislikedException(String errorMessage) {
            super(errorMessage);
        }
    }

    @Autowired
    public EtdClaimCommentService(EtdClaimCommentRepository etdClaimCommentRepository, EtdEntryRepository etdEntryRepository, EtdEntryService etdEntryService) {
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
     * Gets the ETD claim comment with the specified ID.
     *
     * @param commentId ID of the comment
     * @return ETD claim comment
     * @throws EtdClaimCommentNotFoundException Target ETD claim comment not found
     */
    public EtdClaimComment getComment(final Long commentId) throws EtdClaimCommentNotFoundException {
        final Optional<EtdClaimComment> etdClaimCommentOptional = etdClaimCommentRepository.findById(commentId);
        EtdClaimComment etdClaimComment;

        if (etdClaimCommentOptional.isPresent()) {
            etdClaimComment = etdClaimCommentOptional.get();
        } else {
            throw new EtdClaimCommentNotFoundException("Could not locate ETD claim comment with id " + commentId);
        }

        return etdClaimComment;
    }

    /**
     * Gets all the comments with the specified IDs.
     *
     * @param commentIds IDs of the comments to get
     * @return List of comments
     */
    public List<EtdClaimComment> getComments(final List<Long> commentIds) {
        return this.etdClaimCommentRepository.findAllByIdIn(commentIds);
    }

    /**
     * Adds an ETD claim comment to an ETD entry.
     *
     * @param entryId ID of the ETD entry to add comment to
     * @param comment ETD claim comment
     * @param author  Author of the comment
     * @throws EtdEntryService.EtdEntryNotFoundException Target ETD entry not found
     */
    public EtdClaimComment addCommentToEntry(final Long entryId, final EtdClaimComment comment, final User author) throws EtdEntryService.EtdEntryNotFoundException {
        final EtdEntry etdEntry = etdEntryService.getEtdEntry(entryId);

        comment.setEtdEntry(etdEntry);
        comment.setUser(author);

        return this.etdClaimCommentRepository.save(comment);
    }

    /**
     * Gets the likes for a list of comments.
     *
     * @param comments List of comments
     * @return List of likes
     */
    public List<Long> getCommentLikes(final List<EtdClaimComment> comments) {
        return comments.stream().map(EtdClaimComment::getLikes).collect(Collectors.toList());
    }

    /**
     * Gets the like statuses for a list of comments for a specific user.
     *
     * @param user User to check against
     * @param comments List of comments
     * @return List of comment like statuses
     */
    public List<EtdClaimCommentLikeStatus> getCommentLikeStatuses(final User user, final List<EtdClaimComment> comments) {
        final List<EtdClaimComment> likedComments = user.getLikedEtdClaimComments();
        final List<EtdClaimComment> dislikedComments = user.getDislikedEtdClaimComments();

        List<EtdClaimCommentLikeStatus> commentLikeStatuses = new ArrayList<>(comments.size());
        for (EtdClaimComment comment : comments) {
            if (likedComments.contains(comment)) {
                commentLikeStatuses.add(EtdClaimCommentLikeStatus.LIKED);
            }
            else if (dislikedComments.contains(comment)) {
                commentLikeStatuses.add(EtdClaimCommentLikeStatus.DISLIKED);
            }
            else {
                commentLikeStatuses.add(EtdClaimCommentLikeStatus.NONE);
            }
        }

        return commentLikeStatuses;
    }

    /**
     * Adds a like by a specific user to an ETD claim comment.
     *
     * @param user      User that liked the comment
     * @param commentId ID of the comment
     * @throws EtdClaimCommentNotFoundException The ETD claim comment with the specified ID was not found
     * @throws EtdClaimAlreadyLikedException    The ETD claim comment has already been liked by the user
     */
    public void likeComment(User user, final Long commentId) throws EtdClaimCommentNotFoundException, EtdClaimAlreadyLikedException {
        EtdClaimComment etdClaimComment = getComment(commentId);

        // Ensure user has not already liked the comment
        // TODO improve: not scalable. Use SQL query.
        if (user.getLikedEtdClaimComments().contains(etdClaimComment)) {
            throw new EtdClaimAlreadyLikedException("");
        }

        // Remove dislike, if user has disliked the comment previously
        // TODO improve: not scalable. Use SQL query.
        boolean removedDislike = user.getDislikedEtdClaimComments().remove(etdClaimComment);
        if (removedDislike) {
            etdClaimComment.like();
        }

        // Finally, like the comment and update data
        user.getLikedEtdClaimComments().add(etdClaimComment);
        etdClaimComment.like();

        etdClaimCommentRepository.save(etdClaimComment);
    }

    /**
     * Adds a dislike by a specific user to an ETD claim comment.
     *
     * @param user      User that disliked the comment
     * @param commentId ID of the comment
     * @throws EtdClaimCommentNotFoundException The ETD claim comment with the specified ID was not found
     * @throws EtdClaimAlreadyDislikedException The ETD claim comment has already been disliked by the user
     */
    public void dislikeComment(User user, final Long commentId) throws EtdClaimCommentNotFoundException, EtdClaimAlreadyDislikedException {
        EtdClaimComment etdClaimComment = getComment(commentId);

        // Ensure user has not already disliked the comment
        // TODO improve: not scalable. Use SQL query.
        if (user.getDislikedEtdClaimComments().contains(etdClaimComment)) {
            throw new EtdClaimAlreadyDislikedException("");
        }

        // Remove like, if the user has liked the comment previously
        // TODO improve: not scalable. Use SQL query.
        boolean removedLike = user.getLikedEtdClaimComments().remove(etdClaimComment);
        if (removedLike) {
            etdClaimComment.dislike();
        }

        // Finally, dislike the comment and update data
        user.getDislikedEtdClaimComments().add(etdClaimComment);
        etdClaimComment.dislike();

        etdClaimCommentRepository.save(etdClaimComment);
    }

    /**
     * Removes a like by a specific user from an ETD claim comment.
     * This basically "undos" a like.
     *
     * @param user      User that no longer likes the comment
     * @param commentId ID of the comment
     * @throws EtdClaimCommentNotFoundException The ETD claim comment with the specified ID was not found
     * @throws EtdClaimNotLikedException        The ETD claim comment has not been previously liked by the user
     */
    public void removeCommentLike(User user, final Long commentId) throws EtdClaimCommentNotFoundException, EtdClaimNotLikedException {
        EtdClaimComment etdClaimComment = getComment(commentId);

        // Ensure user has already liked the comment
        // TODO improve: not scalable. Use SQL query.
        if (!user.getLikedEtdClaimComments().contains(etdClaimComment)) {
            throw new EtdClaimNotLikedException("");
        }

        // Finally, remove like from the comment and update data
        user.getLikedEtdClaimComments().remove(etdClaimComment);
        etdClaimComment.dislike();

        etdClaimCommentRepository.save(etdClaimComment);
    }

    /**
     * Removes a dislike by a specific user from an ETD claim comment.
     * This basically "undos" a dislike.
     *
     * @param user      User that no longer dislikes the comment
     * @param commentId ID of the comment
     * @throws EtdClaimCommentNotFoundException The ETD claim comment with the specified ID was not found
     * @throws EtdClaimNotDislikedException     The ETD claim comment has not been previously disliked by the user
     */
    public void removeCommentDislike(User user, final Long commentId) throws EtdClaimCommentNotFoundException, EtdClaimNotDislikedException {
        EtdClaimComment etdClaimComment = getComment(commentId);

        // Ensure user has already disliked the comment
        // TODO improve: not scalable. Use SQL query.
        if (!user.getDislikedEtdClaimComments().contains(etdClaimComment)) {
            throw new EtdClaimNotDislikedException("");
        }

        // Finally, remove like from the comment and update data
        user.getDislikedEtdClaimComments().remove(etdClaimComment);
        etdClaimComment.like();

        etdClaimCommentRepository.save(etdClaimComment);
    }
}
