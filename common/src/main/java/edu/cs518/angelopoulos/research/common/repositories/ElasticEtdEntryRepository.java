package edu.cs518.angelopoulos.research.common.repositories;

import edu.cs518.angelopoulos.research.common.models.EtdEntryMeta;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.lang.NonNull;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ElasticEtdEntryRepository extends ElasticsearchRepository<EtdEntryMeta, Long> {
    @NonNull
    //Optional<ElasticEtdEntry> findById(@NonNull UUID id);
    Optional<EtdEntryMeta> findById(@NonNull Long id);

    EtdEntryMeta findByTitle(String title);
    EtdEntryMeta findByContributorAuthor(String contributorAuthor);
    EtdEntryMeta findByContributorDepartment(String contributorDepartment);
    EtdEntryMeta findByContributorCommitteeChair(String contributorCommitteeChair);

    EtdEntryMeta findByDateAccessioned(Date dateAccessioned);
    EtdEntryMeta findByDateIssued(Date dateIssued);
    EtdEntryMeta findByDateAvailable(Date dateAvailable);

    EtdEntryMeta findBySubject(List<String> subject); // TODO may not work
    EtdEntryMeta findByDegreeGrantor(String degreeGrantor);
    EtdEntryMeta findByPublisher(String publisher);
    EtdEntryMeta findByDegreeName(String degreeName);
}
