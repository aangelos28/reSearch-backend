package edu.cs518.angelopoulos.research.common.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor
public class EtdClaimComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Getter @Setter
    private String claim;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @Getter @Setter
    private EtdClaimReproducible reproducible;

    @Getter @Setter
    private String proofSourceCodeUrl;

    @Getter @Setter
    private String proofDatasetUrl;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Getter @Setter
    private String results;

    @CreationTimestamp
    @Getter
    private Timestamp createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter @Setter
    private User user;

    @Getter
    private Long likes;

    @ManyToMany(fetch = FetchType.LAZY)
    @Getter
    private List<User> usersLiked;

    @ManyToMany(fetch = FetchType.LAZY)
    @Getter
    private List<User> usersDisliked;

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter @Setter
    private EtdEntry etdEntry;

    public void like() {
        ++likes;
    }
    public void dislike() {
        --likes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EtdClaimComment that = (EtdClaimComment) o;
        return id.equals(that.id) && Objects.equals(claim, that.claim) && reproducible == that.reproducible && Objects.equals(proofSourceCodeUrl, that.proofSourceCodeUrl) && Objects.equals(proofDatasetUrl, that.proofDatasetUrl) && Objects.equals(results, that.results) && Objects.equals(createdAt, that.createdAt) && Objects.equals(user, that.user) && Objects.equals(likes, that.likes) && Objects.equals(usersLiked, that.usersLiked) && Objects.equals(usersDisliked, that.usersDisliked) && Objects.equals(etdEntry, that.etdEntry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, claim, reproducible, proofSourceCodeUrl, proofDatasetUrl, results, createdAt, user, likes, usersLiked, usersDisliked, etdEntry);
    }
}
