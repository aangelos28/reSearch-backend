package edu.edu.cs518.angelopoulos.researchbackend.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
public class EtdDocument {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Getter @Setter
    private Long id;

    private String filename;
}