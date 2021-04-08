package edu.cs518.angelopoulos.research.common.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter @Setter
    private EtdEntry etdEntry;
}
