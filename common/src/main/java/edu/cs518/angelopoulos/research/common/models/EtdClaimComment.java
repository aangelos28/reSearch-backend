package edu.cs518.angelopoulos.research.common.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class EtdClaimComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;

    @ManyToOne
    @Getter @Setter
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @Getter @Setter
    private EtdClaimReproducable reproducable;

    @Column(nullable = false)
    @Getter @Setter
    private String proofSourceCodeUrl;

    @Column(nullable = false)
    @Getter @Setter
    private String proofDatasetUrl;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Getter @Setter
    private String results;
}
