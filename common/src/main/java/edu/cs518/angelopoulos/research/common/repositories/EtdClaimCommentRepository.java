package edu.cs518.angelopoulos.research.common.repositories;

import edu.cs518.angelopoulos.research.common.models.EtdClaimComment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EtdClaimCommentRepository extends CrudRepository<EtdClaimComment, Long> {
    List<EtdClaimComment> findAllByIdIn(List<Long> ids);
}
