package edu.cs518.angelopoulos.research.backend.controllers;

import com.google.firebase.auth.FirebaseToken;
import edu.cs518.angelopoulos.research.backend.services.FirebaseAuthService;
import edu.cs518.angelopoulos.research.common.models.EtdClaimCommentLikeStatus;
import edu.cs518.angelopoulos.research.common.services.UserService;
import edu.cs518.angelopoulos.research.common.models.EtdClaimComment;
import edu.cs518.angelopoulos.research.common.models.EtdClaimReproducible;
import edu.cs518.angelopoulos.research.common.models.User;
import edu.cs518.angelopoulos.research.common.services.EtdClaimCommentService;
import edu.cs518.angelopoulos.research.common.services.EtdEntryService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class EtdClaimCommentController {
    private final EtdClaimCommentService etdClaimCommentService;
    private final UserService userService;
    private final FirebaseAuthService firebaseAuthService;

    Logger logger = LoggerFactory.getLogger(EtdClaimCommentController.class);

    @Autowired
    public EtdClaimCommentController(EtdClaimCommentService etdClaimCommentService, UserService userService,
                                     FirebaseAuthService firebaseAuthService) {
        this.etdClaimCommentService = etdClaimCommentService;
        this.userService = userService;
        this.firebaseAuthService = firebaseAuthService;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    private static class EtdClaimCommentDto {
        public Long id;
        public String authorId;
        public String authorName;
        public String claim;
        public int reproducible;
        public String proofSourceCodeUrl;
        public String proofDatasetUrl;
        public String results;
        public Long likes;
        public Date createdAt;
    }

    /**
     * Gets the ETD claim comments of an ETD entry.
     *
     * @param entryId ID of the ETD entry to get comments for
     * @return Comments
     */
    @GetMapping(path = "/public/etd/{entryId}/comments")
    public ResponseEntity<?> getEntryClaimComments(@PathVariable Long entryId) {
        try {
            final List<EtdClaimComment> entryComments = this.etdClaimCommentService.getEntryComments(entryId);
            final List<EtdClaimCommentDto> entryCommentDtos = entryComments.stream()
                    .map(c -> new EtdClaimCommentDto(c.getId(), c.getUser().getFirebaseId(), c.getUser().getFullName(),
                            c.getClaim(), c.getReproducible().getValue(),
                            c.getProofSourceCodeUrl(), c.getProofDatasetUrl(), c.getResults(), c.getLikes(),
                            new Date(c.getCreatedAt().getTime())))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(entryCommentDtos);
        } catch (EtdEntryService.EtdEntryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not get claim comments for entry with ID " + entryId);
        }
    }

    /**
     * Adds an ETD claim comment to an ETD entry.
     *
     * @param entryId    ID of the ETD entry to add comment to
     * @param commentDto Comment data
     */
    @PostMapping(path = "/private/etd/{entryId}/comment/add")
    public ResponseEntity<?> addClaimCommentToEntry(@PathVariable Long entryId, @RequestBody EtdClaimCommentDto commentDto) {
        final FirebaseToken userIdToken = firebaseAuthService.getUserIdToken();
        final String userId = userIdToken.getUid();
        User user = userService.getUserByFirebaseId(userId);

        // Build comment
        EtdClaimComment comment = new EtdClaimComment();
        comment.setClaim(commentDto.claim);
        comment.setProofSourceCodeUrl(commentDto.proofSourceCodeUrl);
        comment.setProofDatasetUrl(commentDto.proofDatasetUrl);
        comment.setResults(commentDto.results);
        comment.setLikes(0L);

        switch (commentDto.reproducible) {
            case 0:
                comment.setReproducible(EtdClaimReproducible.NO);
                break;
            case 1:
                comment.setReproducible(EtdClaimReproducible.PARTIALLY);
                break;
            case 2:
                comment.setReproducible(EtdClaimReproducible.YES);
        }

        try {
            final EtdClaimComment addedComment = this.etdClaimCommentService.addCommentToEntry(entryId, comment, user);
            final EtdClaimCommentDto addedCommentDto = new EtdClaimCommentDto(addedComment.getId(),
                    addedComment.getUser().getFirebaseId(), addedComment.getUser().getFullName(),
                    addedComment.getClaim(), addedComment.getReproducible().getValue(),
                    addedComment.getProofSourceCodeUrl(), addedComment.getProofDatasetUrl(),
                    addedComment.getResults(), addedComment.getLikes(), new Date(addedComment.getCreatedAt().getTime()));

            return ResponseEntity.ok(addedCommentDto);
        } catch (EtdEntryService.EtdEntryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not find ETD entry with ID " + entryId);
        }
    }

    @GetMapping(path = "/public/etd/comment/likes")
    public ResponseEntity<?> getCommentLikes(@RequestParam(name = "i") List<Long> commentIds) {
        final List<EtdClaimComment> comments = this.etdClaimCommentService.getComments(commentIds);

        if (comments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        final List<Long> commentLikes = this.etdClaimCommentService.getCommentLikes(comments);

        return ResponseEntity.ok(commentLikes);
    }

    @GetMapping(path = "/private/etd/comment/like-statuses")
    public ResponseEntity<?> getCommentLikeStatuses(@RequestParam(name = "i") List<Long> commentIds) {
        final FirebaseToken userIdToken = firebaseAuthService.getUserIdToken();
        final String userId = userIdToken.getUid();
        User user = userService.getUserByFirebaseId(userId);

        final List<EtdClaimComment> comments = this.etdClaimCommentService.getComments(commentIds);

        if (comments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        final List<EtdClaimCommentLikeStatus> commentLikeStatuses =
                this.etdClaimCommentService.getCommentLikeStatuses(user, comments);

        return ResponseEntity.ok(commentLikeStatuses);
    }

    @PutMapping(path = "/private/etd/comment/{commentId}/like")
    public ResponseEntity<?> likeComment(@PathVariable Long commentId) {
        final FirebaseToken userIdToken = firebaseAuthService.getUserIdToken();
        final String userId = userIdToken.getUid();
        User user = userService.getUserByFirebaseId(userId);

        try {
            this.etdClaimCommentService.likeComment(user, commentId);
            return ResponseEntity.ok().build();
        } catch (EtdClaimCommentService.EtdClaimAlreadyLikedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (EtdClaimCommentService.EtdClaimCommentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping(path = "/private/etd/comment/{commentId}/unlike")
    public ResponseEntity<?> unlikeComment(@PathVariable Long commentId) {
        final FirebaseToken userIdToken = firebaseAuthService.getUserIdToken();
        final String userId = userIdToken.getUid();
        User user = userService.getUserByFirebaseId(userId);

        try {
            this.etdClaimCommentService.removeCommentLike(user, commentId);
            return ResponseEntity.ok().build();
        } catch (EtdClaimCommentService.EtdClaimNotLikedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (EtdClaimCommentService.EtdClaimCommentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping(path = "/private/etd/comment/{commentId}/dislike")
    public ResponseEntity<?> dislikeComment(@PathVariable Long commentId) {
        final FirebaseToken userIdToken = firebaseAuthService.getUserIdToken();
        final String userId = userIdToken.getUid();
        User user = userService.getUserByFirebaseId(userId);

        try {
            this.etdClaimCommentService.dislikeComment(user, commentId);
            return ResponseEntity.ok().build();
        } catch (EtdClaimCommentService.EtdClaimAlreadyDislikedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (EtdClaimCommentService.EtdClaimCommentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping(path = "/private/etd/comment/{commentId}/undislike")
    public ResponseEntity<?> undislikeComment(@PathVariable Long commentId) {
        final FirebaseToken userIdToken = firebaseAuthService.getUserIdToken();
        final String userId = userIdToken.getUid();
        User user = userService.getUserByFirebaseId(userId);

        try {
            this.etdClaimCommentService.removeCommentDislike(user, commentId);
            return ResponseEntity.ok().build();
        } catch (EtdClaimCommentService.EtdClaimNotDislikedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (EtdClaimCommentService.EtdClaimCommentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
