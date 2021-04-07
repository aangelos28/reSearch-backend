package edu.cs518.angelopoulos.research.common.repositories;

import edu.cs518.angelopoulos.research.common.models.EtdDocument;
import org.springframework.data.repository.CrudRepository;

public interface EtdDocumentRepository extends CrudRepository<EtdDocument, Long> {
}
