package edu.cs518.angelopoulos.research.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class EtdEntryMetaSearchQuery {
    @Getter @Setter
    private String title;

    @Getter @Setter
    private String subject;

    @Getter @Setter
    private String author;

    @Getter @Setter
    private String department;

    @Getter @Setter
    private String degreeGrantor;

    @Getter @Setter
    private String publisher;
}
