package edu.cs518.angelopoulos.research.common.services;

import edu.cs518.angelopoulos.research.common.models.EtdEntryMeta;
import edu.cs518.angelopoulos.research.common.repositories.EtdEntryMetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EtdEntryMetaService {
    EtdEntryMetaRepository etdEntryMetaRepository;

    @Autowired
    public EtdEntryMetaService(EtdEntryMetaRepository etdEntryMetaRepository) {
        this.etdEntryMetaRepository = etdEntryMetaRepository;
    }

    public Page<EtdEntryMeta> findByTitle(String title, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return this.etdEntryMetaRepository.findByTitle(title, pageable);
    }
}
