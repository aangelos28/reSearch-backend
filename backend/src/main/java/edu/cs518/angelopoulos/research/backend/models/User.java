package edu.cs518.angelopoulos.research.backend.models;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

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
}
