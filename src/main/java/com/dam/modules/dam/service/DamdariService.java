package com.dam.modules.dam.service;

import com.dam.modules.dam.model.DamParam;
import com.dam.modules.dam.model.Damdari;
import com.dam.modules.dam.model.Milking;
import com.dam.modules.dam.model.Resource;
import com.dam.modules.dam.repository.DamParamRepository;
import com.dam.modules.dam.repository.DamdariRepository;
import com.dam.modules.dam.repository.MilkingRepository;
import com.dam.modules.dam.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class DamdariService {

    private DamdariRepository damdariRepository;
    private DamParamRepository damParamRepository;
    private ResourceRepository resourceRepository;
    private MilkingRepository milkingRepository;

    @Autowired
    public DamdariService(DamdariRepository damdariRepository, DamParamRepository damParamRepository, ResourceRepository resourceRepository, MilkingRepository milkingRepository) {
        this.damdariRepository = damdariRepository;
        this.damParamRepository = damParamRepository;
        this.resourceRepository = resourceRepository;
        this.milkingRepository = milkingRepository;
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

    public Resource saveResource(Long damdariId, Long typeId, Long amount, String date){
        Damdari damdari = damdariRepository.findDamdariById(damdariId);
        Resource resource = new Resource();
        resource.setType(typeId);
        resource.setDamdari(damdari);
        resource.setAmount(amount);
        DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate ld = LocalDate.parse(date, DATEFORMATTER);
        LocalDateTime dateTime = LocalDateTime.of(ld, LocalDateTime.now().toLocalTime());
        resource.setCreatedAt(dateTime);
        return resourceRepository.save(resource);
    }

    public Milking saveMilking(Long damdariId,  Double liter, String date){
        Damdari damdari = damdariRepository.findDamdariById(damdariId);
        Milking milking = new Milking();
        milking.setDamdari(damdari);
        milking.setLiter(liter);
        DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate ld = LocalDate.parse(date, DATEFORMATTER);
        LocalDateTime dateTime = LocalDateTime.of(ld, LocalDateTime.now().toLocalTime());
        milking.setCreatedAt(dateTime);
        return milkingRepository.save(milking);
    }
}
