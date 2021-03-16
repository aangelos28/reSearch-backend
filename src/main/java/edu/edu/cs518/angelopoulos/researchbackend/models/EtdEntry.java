package edu.edu.cs518.angelopoulos.researchbackend.models;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Entity
public class EtdEntry {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    @Getter @Setter
    private UUID id;

    @Column(unique = true, nullable = false)
    @Getter @Setter
    private Long originalId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    private Set<EtdDocument> documents;

    @UpdateTimestamp
    @Getter
    private Timestamp updatedAt;
}
