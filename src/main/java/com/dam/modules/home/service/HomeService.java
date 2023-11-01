package com.dam.modules.home.service;

import com.dam.modules.home.model.Services;
import com.dam.modules.home.model.Slider;
import com.dam.modules.home.repository.ServicesRepository;
import com.dam.modules.home.repository.SliderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeService {

    private ServicesRepository servicesRepository;
    private SliderRepository sliderRepository;

    @Autowired
    public HomeService(ServicesRepository servicesRepository, SliderRepository sliderRepository) {
        this.servicesRepository = servicesRepository;
        this.sliderRepository = sliderRepository;
    }
    public List<Services> findAllServices (){
        return servicesRepository.findAll();
    }

    public List<Slider> findAllSlider(){
        return sliderRepository.findAll();
    }
}
