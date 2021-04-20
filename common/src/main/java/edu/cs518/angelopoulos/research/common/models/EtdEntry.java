package edu.cs518.angelopoulos.research.common.models;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor
public class EtdEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter @Setter
    private User user;

    @OneToMany(mappedBy = "etdEntry", cascade = CascadeType.ALL)
    @Getter @Setter
    private List<EtdDocument> documents;

    @OneToMany(mappedBy = "etdEntry", cascade = CascadeType.ALL)
    @Getter @Setter
    private List<EtdClaimComment> claimComments;

    @UpdateTimestamp
    @Getter
    private Timestamp updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EtdEntry etdEntry = (EtdEntry) o;
        return Objects.equals(id, etdEntry.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
