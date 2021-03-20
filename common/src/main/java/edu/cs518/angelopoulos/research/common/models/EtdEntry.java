package edu.cs518.angelopoulos.research.common.models;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
public class EtdEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@GeneratedValue(generator = "uuid2")
    //@GenericGenerator(name = "uuid2", strategy = "uuid2")
    //@Column(columnDefinition = "BINARY(16)")
    @Getter @Setter
    private Long id;
    //private UUID id;

    @Column(unique = true, nullable = false)
    @Getter @Setter
    private Long originalId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter
    private List<EtdDocument> documents;

    @UpdateTimestamp
    @Getter
    private Timestamp updatedAt;
}

