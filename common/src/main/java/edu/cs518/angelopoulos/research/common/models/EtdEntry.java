package edu.cs518.angelopoulos.research.common.models;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Entity
@NoArgsConstructor
public class EtdEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;

    @Column(unique = true, nullable = false)
    @Getter @Setter
    private Long originalId;

    @ManyToOne
    @Getter @Setter
    private User user;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter
    private List<EtdDocument> documents;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter
    private List<EtdClaimComment> claimComments;

    @UpdateTimestamp
    @Getter
    private Timestamp updatedAt;
}

