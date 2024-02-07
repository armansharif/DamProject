package com.dam.modules.dam.service;

import com.dam.modules.dam.model.DamParam;
import com.dam.modules.dam.model.Damdari;
import com.dam.modules.dam.repository.DamParamRepository;
import com.dam.modules.dam.repository.DamdariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DamdariService {

    private DamdariRepository damdariRepository;
    private DamParamRepository damParamRepository;

    @Autowired
    public DamdariService(DamdariRepository damdariRepository, DamParamRepository damParamRepository) {
        this.damdariRepository = damdariRepository;
        this.damParamRepository = damParamRepository;
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

    public List<DamParam> findDamdariParam(Long damdariId){
        Damdari damdari = findDamdari(damdariId);
        return damParamRepository.findDamParamByDamdari(damdari) ;
    }



    public void saveDamParam(DamParam damParam){
        damParamRepository.save(damParam);
    }

    public Optional<DamParam> getDamParamById(Long id){
        return damParamRepository.findById(id);
    }

}
