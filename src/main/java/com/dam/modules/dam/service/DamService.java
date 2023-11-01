package com.dam.modules.dam.service;

import com.dam.commons.Consts;
import com.dam.modules.dam.model.Dam;
import com.dam.modules.dam.model.DamStatus;
import com.dam.modules.dam.model.Dashboard;
import com.dam.modules.dam.model.DynamicCharts;
import com.dam.modules.dam.repository.DamStatusRepository;
import com.dam.modules.dam.repository.DamRepository;

import com.dam.modules.ticketing.consts.ConstTicketing;
import com.dam.modules.ticketing.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;


@Service
public class DamService {

    private static final Logger LOG = Logger.getLogger(DamService.class.getName());
    private DamRepository damRepository;
    private TicketRepository ticketRepository;


    private DamStatusRepository damStatusRepository;

    @Autowired
    public DamService(DamRepository damRepository, TicketRepository ticketRepository, DamStatusRepository damStatusRepository) {
        this.damRepository = damRepository;
        this.ticketRepository = ticketRepository;
        this.damStatusRepository = damStatusRepository;
    }

    public List<Dam> findAllDam(String sort,
                                int page,
                                int perPage) {
        Pageable sortedAndPagination =
                PageRequest.of(page, perPage, Sort.by(sort).ascending());

        return damRepository.getDams(sortedAndPagination);
    }

    @Transactional
    public Dam saveDam(Dam dam) throws IOException {

        return this.damRepository.save(dam);
    }

    @Transactional
    public Dam addDam(Dam dam, Path rootImages, String urlImages) throws IOException {

        Dam dam_saved = this.damRepository.save(dam);
        String imagePath = rootImages.toFile().getAbsolutePath();

        if (dam.getFileImage() != null && !dam.getFileImage().isEmpty()) {
            MultipartFile fileImage = dam.getFileImage();
            byte[] bytesImages = fileImage.getBytes();
            String fileNameImage = UUID.randomUUID() + "." + Objects.requireNonNull(fileImage.getContentType()).split("/")[1];
            Files.write(Paths.get(imagePath + File.separator + fileNameImage), bytesImages);
            dam.setPhoto(urlImages + "/" + fileNameImage);
        }
        return dam_saved;
    }

    @Transactional
    public Dam addDam(Dam dam) throws IOException {

        Dam dam_saved = this.damRepository.save(dam);
        return dam_saved;
    }

    public void deleteDam(Long id) throws Exception {
        this.damRepository.deleteById(id);
    }

    public List<Dam> findAll(String sort,
                             int page,
                             int perPage,
                             Specification<Dam> spec) {
        Pageable sortedAndPagination =
                PageRequest.of(page, perPage, Sort.by(sort).ascending());
        return damRepository.findAll(spec, sortedAndPagination);
    }

    public List<Dam> findAllDamsOfDamdari(String sort,
                                          int page,
                                          int perPage,
                                          String damdariId,
                                          String isFahli,
                                          String hasLangesh,
                                          String typeId) {
        Pageable sortedAndPagination =
                PageRequest.of(page, perPage, Sort.by(sort).ascending());

        return damRepository.findDamsOfDamdari(damdariId, isFahli, hasLangesh, typeId, sortedAndPagination);
    }

    public List<Dam> findAllDamsOfDamdariHasProblem(String sort,
                                                    int page,
                                                    int perPage,
                                                    String damdariId,
                                                    String isFahli,
                                                    String hasLangesh,
                                                    String hasTab,
                                                    String typeId) {
        Pageable sortedAndPagination =
                PageRequest.of(page, perPage);

        return damRepository.findDamsOfDamdariHasProblem(damdariId, isFahli, hasLangesh, hasTab, typeId, sortedAndPagination);
    }

    public Dashboard getDashboardData(String damdariId) {
        Dashboard dashboard = new Dashboard();

        dashboard.setCountOfDamdari(damRepository.countOfDamdari(null));
        dashboard.setCountOfDam(damRepository.countOfDam(damdariId));
        dashboard.setCountOfDamWithTab(damRepository.countOfDamWithTab(damdariId));
        dashboard.setCountOfDamIsFahli(damRepository.countOfFahliDams(damdariId));
        dashboard.setCountOfDamHasLangesh(damRepository.countOfLangeshDams(damdariId));
        dashboard.setAvgOfMilk(damRepository.avgOfMilk(damdariId));

        dashboard.setCountOfTicket(ticketRepository.countAllByStatus(ConstTicketing.TICKET_STATUS_OPEN));


//        Map<String, List<ChartDto>> charts = new HashMap<>();
//        charts.put("typeOfDam", damRepository.typeOfDamChartDto(damdariId));
//        charts.put("compareTabInMonths", damRepository.compareTabOverMonthChartDto(damdariId));
//
//        dashboard.setCharts(charts);

        //chart no 1
        DynamicCharts dynamicCharts = new DynamicCharts();
        dynamicCharts.setPosition(1);
        dynamicCharts.setChartTitle("تنوع دام های هوشمند");
        dynamicCharts.setData(damRepository.typeOfDamChartDto(damdariId));
        Map<String, DynamicCharts> charts = new HashMap<>();
        charts.put("typeOfDam", dynamicCharts);


        //chart no 2
        dynamicCharts = new DynamicCharts();
        dynamicCharts.setPosition(2);
        dynamicCharts.setChartTitle("تعداد دام های هوشمند شده در هر ماه");
        dynamicCharts.setData(damRepository.countDateOfDamChartDto(damdariId));
        charts.put("countDamInMonths", dynamicCharts);


        //chart no 3
        dynamicCharts = new DynamicCharts();
        dynamicCharts.setPosition(3);
        dynamicCharts.setChartTitle("میزان شیر دهی");
        dynamicCharts.setData(damRepository.amountOfMilkingChartDto(damdariId));
        charts.put("amountMilkingInDays", dynamicCharts);


        //chart no 4
        dynamicCharts = new DynamicCharts();
        dynamicCharts.setPosition(4);
        dynamicCharts.setChartTitle("میزان مصرف علوفه در هر ماه");
        dynamicCharts.setData(damRepository.amountOfFodderChartDto(damdariId));
        charts.put("amountFodderInMonths", dynamicCharts);


        //chart no 5
        dynamicCharts = new DynamicCharts();
        dynamicCharts.setPosition(5);
        dynamicCharts.setChartTitle("مقایسه دام های دارای تب در هر ماه");
        dynamicCharts.setData(damRepository.historicalTabOfDamChartDto(damdariId));
        charts.put("compareTabInMonths", dynamicCharts);


        //chart no 6
        dynamicCharts = new DynamicCharts();
        dynamicCharts.setPosition(6);
        dynamicCharts.setChartTitle("مقایسه دام های دارای لنگش در هر ماه");
        dynamicCharts.setData(damRepository.historicalLangechOfDamChartDto(damdariId));
        charts.put("compareLangeshInMonths", dynamicCharts);


        //chart no 7
        dynamicCharts = new DynamicCharts();
        dynamicCharts.setPosition(7);
        dynamicCharts.setChartTitle("مقایسه دام های فحلی در هر ماه");
        dynamicCharts.setData(damRepository.historicalFahliOfDamChartDto(damdariId));
        charts.put("compareFahliInMonths", dynamicCharts);


        //chart no 9
        dynamicCharts = new DynamicCharts();
        dynamicCharts.setPosition(7);
        dynamicCharts.setChartTitle("پراکندگی دام هوشمند");
        dynamicCharts.setData(damRepository.locationOfDamChartDto(damdariId));
        charts.put("locationOfDams", dynamicCharts);


        dashboard.setCharts(charts);


        return dashboard;
    }


    public List<DamStatus> findAllDamStatus(String sort,
                                            int page,
                                            int perPage,
                                            String damId) {
        Pageable sortedAndPagination =
                PageRequest.of(page, perPage, Sort.by(sort).ascending());
        return damStatusRepository.findAllDamStatus(damId, sortedAndPagination);
    }

    public DamStatus findLastDamStatus(
            String damId) {
        return damStatusRepository.findLastDamStatus(damId);
    }

    public List<DamStatus> findAllDamGps(String sort,
                                         int page,
                                         int perPage,
                                         String damId, String damdariId) {
        Pageable sortedAndPagination =
                PageRequest.of(page, perPage, Sort.by(sort).ascending());
        return damStatusRepository.findAllDamStatus(damId, sortedAndPagination);
    }

    public Optional<Dam> findDam(String damId) {

        return this.damRepository.findDam(damId);
    }
}
