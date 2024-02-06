package com.dam.modules.dam.service;

import com.dam.modules.dam.model.Dam;
import com.dam.modules.dam.model.Damdari;
import com.dam.modules.dam.repository.DamdariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DamdariService {

    private DamdariRepository damdariRepository;

    @Autowired
    public DamdariService(DamdariRepository damdariRepository) {
        this.damdariRepository = damdariRepository;
    }

    public List<Damdari> findAllDamdari(String sort,
                                        int page,
                                        int perPage) {
        Pageable sortedAndPagination =
                PageRequest.of(page, perPage, Sort.by(sort).ascending());

        return damdariRepository.findAllDamdari(sortedAndPagination);
    }

    public Damdari findDamdari(Long id){
        return damdariRepository.findDamdariById(id);
    }

}
