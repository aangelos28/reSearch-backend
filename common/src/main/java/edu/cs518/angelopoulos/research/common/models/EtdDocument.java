package edu.cs518.angelopoulos.research.common.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
public class EtdDocument {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Getter @Setter
    private Long id;

    @Getter @Setter
    private String filename;
}
