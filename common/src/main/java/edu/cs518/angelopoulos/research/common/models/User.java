package edu.cs518.angelopoulos.research.common.models;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;

    @Column(unique = true, nullable = false)
    @Getter @Setter
    private String firebaseId;

    @Getter @Setter
    private String fullName;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @Getter
    private List<EtdEntry> etdEntries;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @Getter
    private List<EtdClaimComment> etdClaimComments;

    @CreationTimestamp
    @Getter
    private Timestamp createdAt;

    @UpdateTimestamp
    @Getter
    private Timestamp updatedAt;

    public User() {
    }

    public User(String firebaseId, String fullName) {
        this.firebaseId = firebaseId;
        this.fullName = fullName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) && firebaseId.equals(user.firebaseId) && Objects.equals(fullName, user.fullName) && Objects.equals(etdEntries, user.etdEntries) && Objects.equals(etdClaimComments, user.etdClaimComments) && Objects.equals(createdAt, user.createdAt) && Objects.equals(updatedAt, user.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firebaseId, fullName, etdEntries, etdClaimComments, createdAt, updatedAt);
    }
}
