package com.dam.modules.dam.service;

import com.dam.commons.Consts;
import com.dam.modules.dam.model.*;
import com.dam.modules.dam.repository.DamStatusRepository;
import com.dam.modules.dam.repository.DamRepository;

import com.dam.modules.dam.repository.DamdariRepository;
import com.dam.modules.dam.repository.SampleDataRepository;
import com.dam.modules.ticketing.repository.TicketRepository;
import org.apache.tomcat.util.bcel.Const;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
    private SampleDataRepository sampleDataRepository;

    private DamdariRepository damdariRepository;

    private MessageSource messageSource;

    private DamStatusRepository damStatusRepository;

    @Autowired
    public DamService(DamRepository damRepository, TicketRepository ticketRepository, SampleDataRepository sampleDataRepository, DamdariService damdariService, DamdariRepository damdariRepository, MessageSource messageSource, DamStatusRepository damStatusRepository) {
        this.damRepository = damRepository;
        this.ticketRepository = ticketRepository;
        this.sampleDataRepository = sampleDataRepository;
        this.damdariRepository = damdariRepository;
        this.messageSource = messageSource;
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
    public SampleData addSampleData(String data) throws IOException {
        SampleData s = new SampleData();
        s.setData(data);
        return sampleDataRepository.save(s);
    }

    public void importData(String data) {
        //end_device_ids
        //--device_id

        //uplink_message/decoded_payload
        //--"Bat":98,
        //--"Gastric_momentum":1013141,
        //--"Temperature":44.12,
        //--"acc_x":-10,
        //--"acc_y":36,
        //--"acc_z":-42
        try {
            JSONObject jsonData = new JSONObject(data);

            JSONObject endDeviceIds = jsonData.getJSONObject("end_device_ids");
            String device_id = endDeviceIds.getString("device_id");

            JSONObject uplinkMessage = jsonData.getJSONObject("uplink_message");
            String devEui = endDeviceIds.getString("dev_eui");


            JSONObject decodedPayload = uplinkMessage.getJSONObject("decoded_payload");

            Long bat = decodedPayload.getLong("Bat");
            Long Gastric_momentum = decodedPayload.getLong("Gastric_momentum");
            Float temperature = decodedPayload.getFloat("Temperature");
            Long accX = decodedPayload.getLong("acc_x");
            Long accY = decodedPayload.getLong("acc_y");
            Long accZ = decodedPayload.getLong("acc_z");

            String receivedAt = jsonData.getString("received_at");


            Dam dam = damRepository.findDamByDeviceId(device_id).orElse(null);
            if (dam == null) {
                Damdari damdari = damdariRepository.findDamdariById(1L);
                dam = new Dam();
                dam.setDeviceId(device_id);
                dam.setDamdari(damdari);
                dam.setName(devEui);
                damRepository.save(dam);
            }

            DamStatus damStatus = new DamStatus();
            damStatus.setACCX(accX);
            damStatus.setACCZ(accZ);
            damStatus.setACCY(accY);
            damStatus.setTemperature(temperature);
            damStatus.setDatetime(receivedAt);
            damStatus.setDam(dam);
            damStatus.setBattery(bat);
            damStatusRepository.save(damStatus);


        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("data.invalid", null, null));
        }
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

    public Dashboard getDashboardData(String damdariId, String fromDate, String toDate) {
        Dashboard dashboard = new Dashboard();

        List<String> bimarFlags = new ArrayList<>();
        bimarFlags.add(Consts.FLAG_OF_ANIMAL_BARDAR);
        bimarFlags.add(Consts.FLAG_OF_ANIMAL_TAB);
        bimarFlags.add(Consts.FLAG_OF_ANIMAL_ASTANEH_ZAYEMAN);
        bimarFlags.add(Consts.FLAG_OF_ANIMAL_BIMAR);


//        dashboard.setCountOfDam(damRepository.countOfDam(damdariId));
//        dashboard.setCountOfNeedCare(damRepository.countOf(damdariId,  bimarFlags));
//        dashboard.setCountOfNeedCare(damRepository.countOf(damdariId,  Arrays.asList(Consts.FLAG_OF_ANIMAL_BARDAR)));
//        dashboard.setCountOfNeedCare(damRepository.countOf(damdariId,  Arrays.asList(Consts.FLAG_OF_ANIMAL_FAHLI)));
//        dashboard.setCountOfNeedCare(damRepository.countOf(damdariId,  Arrays.asList(Consts.FLAG_OF_ANIMAL_SHIRI)));
//        dashboard.setCountOfNeedCare(damRepository.countOf(damdariId,  Arrays.asList(Consts.FLAG_OF_ANIMAL_ASTANEH_ZAYEMAN)));


        //     dashboard.setAvgOfMilk(damRepository.avgOfMilk(damdariId));

        //dashboard.setCountOfDamIsFahli(damRepository.countOfFahliDams(damdariId));
        //   dashboard.setCountOfTicket(ticketRepository.countAllByStatus(ConstTicketing.TICKET_STATUS_OPEN));
        // dashboard.setCountOfDamWithTab(damRepository.countOfDamWithTab(damdariId));
        //   dashboard.setCountOfDamHasLangesh(damRepository.countOfLangeshDams(damdariId));

//        Map<String, List<ChartDto>> charts = new HashMap<>();
//        charts.put("typeOfDam", damRepository.typeOfDamChartDto(damdariId));
//        charts.put("compareTabInMonths", damRepository.compareTabOverMonthChartDto(damdariId));
//
//        dashboard.setCharts(charts);
        //    کل دامها
        //    دام های فحلی
        //    نیازمند مراقبت
        //    فحلی
        //    شیری
        //    باردار

        List<DynamicInfos> infos = new ArrayList<>();

        DynamicInfos dynamicInfos = new DynamicInfos();


        if (damdariId == null) {
            dynamicInfos = new DynamicInfos();
            dynamicInfos.setPosition(0);
            dynamicInfos.setTitle("دامداری");
            dynamicInfos.setIcon("../icon/damdari.png");
            dynamicInfos.setData(damRepository.countOfDamdari(null).toString());
            infos.add(dynamicInfos);
        }


        dynamicInfos = new DynamicInfos();
        dynamicInfos.setPosition(1);
        dynamicInfos.setTitle("کل");
        dynamicInfos.setIcon("../icon/total.png");
        dynamicInfos.setData(damRepository.countOfDam(damdariId).toString());
        infos.add(dynamicInfos);


        dynamicInfos = new DynamicInfos();
        dynamicInfos.setPosition(2);
        dynamicInfos.setTitle("حامله");
        dynamicInfos.setIcon("../icon/pregnant.png");
        dynamicInfos.setData(damRepository.countOf(damdariId, Arrays.asList(Consts.FLAG_OF_ANIMAL_BARDAR)).toString());
        infos.add(dynamicInfos);

        dynamicInfos = new DynamicInfos();
        dynamicInfos.setPosition(3);
        dynamicInfos.setTitle("نیازمند مراقبت");
        dynamicInfos.setIcon("../icon/care.png");
        dynamicInfos.setData(damRepository.countOf(damdariId, bimarFlags).toString());
        infos.add(dynamicInfos);

        dynamicInfos = new DynamicInfos();
        dynamicInfos.setPosition(4);
        dynamicInfos.setTitle("فحلی");
        dynamicInfos.setIcon("../icon/fahli.png");
        dynamicInfos.setData(damRepository.countOf(damdariId, Arrays.asList(Consts.FLAG_OF_ANIMAL_FAHLI)).toString());
        infos.add(dynamicInfos);

        dynamicInfos = new DynamicInfos();
        dynamicInfos.setPosition(5);
        dynamicInfos.setTitle("شیری");
        dynamicInfos.setIcon("../icon/milking.png");
        dynamicInfos.setData(damRepository.countOf(damdariId, Arrays.asList(Consts.FLAG_OF_ANIMAL_SHIRI)).toString());
        infos.add(dynamicInfos);

        dynamicInfos = new DynamicInfos();
        dynamicInfos.setPosition(6);
        dynamicInfos.setTitle("آستانه زایمان");
        dynamicInfos.setIcon("../icon/birth.png");
        dynamicInfos.setData(damRepository.countOf(damdariId, Arrays.asList(Consts.FLAG_OF_ANIMAL_ASTANEH_ZAYEMAN)).toString());
        infos.add(dynamicInfos);

        dashboard.setInfos(infos);

        //chart no 1
        DynamicCharts dynamicCharts = new DynamicCharts();
        dynamicCharts.setPosition(1);
        dynamicCharts.setChartTitle("تنوع دام های هوشمند");
        dynamicCharts.setData(damRepository.typeOfDamChartDto(damdariId));
        Map<String, DynamicCharts> charts = new HashMap<>();
        charts.put("typeOfDam", dynamicCharts);


        if (damdariId == null) {
            //chart no 2
            dynamicCharts = new DynamicCharts();
            dynamicCharts.setPosition(2);
            dynamicCharts.setChartTitle("تعداد دام های هوشمند شده در هر ماه");
            dynamicCharts.setData(damRepository.countDateOfDamChartDto(damdariId));
            charts.put("countDam", dynamicCharts);
        }


        //chart no 3
        dynamicCharts = new DynamicCharts();
        dynamicCharts.setPosition(3);
        dynamicCharts.setChartTitle("میزان شیر دهی");
        dynamicCharts.setData(damRepository.amountOfMilkingChartDto(damdariId, fromDate, toDate));
        charts.put("amountOfMilking", dynamicCharts);


        //chart no 3
        dynamicCharts = new DynamicCharts();
        dynamicCharts.setPosition(4);
        dynamicCharts.setChartTitle("میانگین PH");
        dynamicCharts.setData(damRepository.avgOfPhChartDto(damdariId, fromDate, toDate));
        charts.put("avgOfPH", dynamicCharts);


        //chart no 3
        dynamicCharts = new DynamicCharts();
        dynamicCharts.setPosition(5);
        dynamicCharts.setChartTitle("میانگین دما");
        dynamicCharts.setData(damRepository.avgOfTemperatureChartDto(damdariId, fromDate, toDate));
        charts.put("avgOfTEMP", dynamicCharts);

        //chart no 4
        dynamicCharts = new DynamicCharts();
        dynamicCharts.setPosition(6);
        dynamicCharts.setChartTitle("میزان مصرف علوفه در هر ماه");
        dynamicCharts.setData(damRepository.amountOfFodderChartDto(damdariId, fromDate, toDate));
        charts.put("amountFodder", dynamicCharts);


        //chart no 5
        dynamicCharts = new DynamicCharts();
        dynamicCharts.setPosition(7);
        dynamicCharts.setChartTitle("میزان مصرف آب در هر ماه");
        dynamicCharts.setData(damRepository.amountOfWaterChartDto(damdariId, fromDate, toDate));
        charts.put("amountWater", dynamicCharts);


//        //chart no 5
//        dynamicCharts = new DynamicCharts();
//        dynamicCharts.setPosition(5);
//        dynamicCharts.setChartTitle("مقایسه دام های دارای تب در هر ماه");
//        dynamicCharts.setData(damRepository.historicalTabOfDamChartDto(damdariId));
//        charts.put("compareTabInMonths", dynamicCharts);
//
//
//        //chart no 6
//        dynamicCharts = new DynamicCharts();
//        dynamicCharts.setPosition(6);
//        dynamicCharts.setChartTitle("مقایسه دام های دارای لنگش در هر ماه");
//        dynamicCharts.setData(damRepository.historicalLangechOfDamChartDto(damdariId));
//        charts.put("compareLangeshInMonths", dynamicCharts);
//
//
//        //chart no 7
//        dynamicCharts = new DynamicCharts();
//        dynamicCharts.setPosition(7);
//        dynamicCharts.setChartTitle("مقایسه دام های فحلی در هر ماه");
//        dynamicCharts.setData(damRepository.historicalFahliOfDamChartDto(damdariId));
//        charts.put("compareFahliInMonths", dynamicCharts);

        if (damdariId == null) {
            //chart no 9
            dynamicCharts = new DynamicCharts();
            dynamicCharts.setPosition(8);
            dynamicCharts.setChartTitle("پراکندگی دام هوشمند");
            dynamicCharts.setData(damRepository.locationOfDamChartDto(damdariId));
            charts.put("locationOfDams", dynamicCharts);
        }

        dashboard.setCharts(charts);


        return dashboard;
    }


    public List<DamStatus> findAllDamStatus(String sort,
                                            int page,
                                            int perPage,
                                            String damId,
                                            int sortType) {
        Pageable sortedAndPagination = null;
        if (sortType == 1)
            sortedAndPagination = PageRequest.of(page, perPage, Sort.by(sort).ascending());
        else
            sortedAndPagination = PageRequest.of(page, perPage, Sort.by(sort).descending());
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
