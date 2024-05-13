package com.dam.modules.dam.service;

import com.dam.commons.Consts;
import com.dam.commons.Routes;
import com.dam.commons.utils.BaseCommonUtils;
import com.dam.commons.utils.BaseDateUtils;
import com.dam.commons.utils.DateUtils;
import com.dam.modules.dam.model.*;
import com.dam.modules.dam.repository.*;

import com.dam.modules.notification.repository.NotificationRepository;
import com.dam.modules.notification.service.NotificationService;
import com.dam.modules.ticketing.repository.TicketRepository;
import com.dam.modules.user.model.Users;
import org.json.JSONArray;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@Service
public class DamService {

    private static final Logger LOG = Logger.getLogger(DamService.class.getName());
    private DamRepository damRepository;
    private TicketRepository ticketRepository;
    private SampleDataRepository sampleDataRepository;

    private DamdariRepository damdariRepository;

    private MessageSource messageSource;

    private DamStatusRepository damStatusRepository;

    private FlagRepository flagRepository;

    private MobilityRepository mobilityRepository;

    private DamParamRepository damParamRepository;

    private NotificationService notificationService;

    private ImpDateRepository impDateRepository;

    private HistoricalFlagRepository historicalFlagRepository;

    private WaterDrinkRepository waterDrinkRepository;


    @Autowired
    public DamService(DamRepository damRepository, TicketRepository ticketRepository, SampleDataRepository sampleDataRepository, DamdariService damdariService, DamdariRepository damdariRepository, MessageSource messageSource, DamStatusRepository damStatusRepository, FlagRepository flagRepository, MobilityRepository mobilityRepository, DamParamRepository damParamRepository, NotificationRepository notificationRepository, NotificationService notificationService, ImpDateRepository impDateRepository, ResourceRepository resourceRepository, HistoricalFlagRepository historicalFlagRepository, WaterDrinkRepository waterDrinkRepository) {
        this.damRepository = damRepository;
        this.ticketRepository = ticketRepository;
        this.sampleDataRepository = sampleDataRepository;
        this.damdariRepository = damdariRepository;
        this.messageSource = messageSource;
        this.damStatusRepository = damStatusRepository;
        this.flagRepository = flagRepository;
        this.mobilityRepository = mobilityRepository;
        this.damParamRepository = damParamRepository;
        this.notificationService = notificationService;
        this.impDateRepository = impDateRepository;
        this.historicalFlagRepository = historicalFlagRepository;
        this.waterDrinkRepository = waterDrinkRepository;
    }

    public List<Dam> findAllDam(String sort,
                                int page,
                                int perPage) {
        Pageable sortedAndPagination =
                PageRequest.of(page, perPage, Sort.by(sort).ascending());

        return damRepository.getDams(sortedAndPagination);
    }

    public List<Flag> findAllFlags() {
        return flagRepository.findAll();
    }

    public List<HistoricalFlag> findAllHistoricalFlags(Long damId) {
        return historicalFlagRepository.findAllByDam(damId);
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
            String device_id = "";
            String devEui = "";
            String receivedAt = "";
            Long bat = null;
            Long Gastric_momentum = null;
            Float temperature = null;
            Long gastricMomentum = null;
            Long accX = null;
            Long accY = null;
            Long accZ = null;
            String time = null;
            Long timestamp = null;
            Long rssi = null;
            Long channelRssi = null;
            Float snr = null;
            String frequencyOffset = null;
            Long channelIndex = null;

            String gatewayId = null;
            String eui = null;


            if (jsonData.has("end_device_ids")) {
                JSONObject endDeviceIds = jsonData.getJSONObject("end_device_ids");
                device_id = endDeviceIds.getString("device_id");
                devEui = endDeviceIds.getString("dev_eui");
            }
            if (jsonData.has("uplink_message")) {
                JSONObject uplinkMessage = jsonData.getJSONObject("uplink_message");

                if (uplinkMessage.has("decoded_payload")) {
                    JSONObject decodedPayload = uplinkMessage.getJSONObject("decoded_payload");
                    //   Gastric_momentum = decodedPayload.getLong("Gastric_momentum");

                    if (decodedPayload.has("Bat"))
                        bat = decodedPayload.getLong("Bat");
                    if (decodedPayload.has("Temperature"))
                        temperature = decodedPayload.getFloat("Temperature");
                    if (decodedPayload.has("Gastric_momentum"))
                        gastricMomentum = decodedPayload.getLong("Gastric_momentum");
                    if (decodedPayload.has("acc_x"))
                        accX = decodedPayload.getLong("acc_x");
                    if (decodedPayload.has("acc_y"))
                        accY = decodedPayload.getLong("acc_y");
                    if (decodedPayload.has("acc_z"))
                        accZ = decodedPayload.getLong("acc_z");
                }
                if (uplinkMessage.has("rx_metadata")) {
                    JSONArray rxMetadataArray = uplinkMessage.getJSONArray("rx_metadata");


                    JSONObject rxMetadata = rxMetadataArray.getJSONObject(0);

                    JSONObject gatewayIds = rxMetadata.getJSONObject("gateway_ids");

                    if (gatewayIds.has("gateway_id"))
                        gatewayId = gatewayIds.getString("gateway_id");

                    if (gatewayIds.has("eui"))
                        eui = gatewayIds.getString("eui");

                    if (rxMetadata.has("time"))
                        time = rxMetadata.getString("time");

                    if (rxMetadata.has("frequencyOffset"))
                        frequencyOffset = rxMetadata.getString("frequencyOffset");

                    if (rxMetadata.has("timestamp"))
                        timestamp = rxMetadata.getLong("timestamp");

                    if (rxMetadata.has("rssi"))
                        rssi = rxMetadata.getLong("rssi");

                    if (rxMetadata.has("channelRssi"))
                        channelRssi = rxMetadata.getLong("channelRssi");

                    if (rxMetadata.has("snr"))
                        snr = rxMetadata.getFloat("snr");

                    if (rxMetadata.has("channelIndex"))
                        channelIndex = rxMetadata.getLong("channelIndex");

                }

            }
            receivedAt = jsonData.getString("received_at");


            Dam dam = damRepository.findDamByDeviceId(device_id).orElse(null);
            if (dam == null) {
                Damdari damdari = damdariRepository.findDamdariById(Consts.DEFAULT_DAMDARI);
                dam = new Dam();
                dam.setDeviceId(device_id);
                dam.setDamdari(damdari);
                dam.setName(devEui);
                damRepository.save(dam);
            }

            DamStatus damStatus = new DamStatus();
            damStatus.setDam(dam);

            if (accX != null)
                damStatus.setACCX(accX);
            if (accZ != null)
                damStatus.setACCZ(accZ);
            if (accY != null)
                damStatus.setACCY(accY);
            if (temperature != null)
                damStatus.setTemperature(temperature);
            if (gastricMomentum != null)
                damStatus.setGastricMomentum(gastricMomentum);
            if (receivedAt != null)
                damStatus.setDatetime(receivedAt);
            if (bat != null)
                damStatus.setBattery(bat);

            damStatus.setTime(time);
            damStatus.setTimestamp(timestamp);
            damStatus.setRssi(rssi);
            damStatus.setChannelRssi(channelRssi);
            damStatus.setChannelIndex(channelIndex);
            damStatus.setSnr(snr);
            damStatus.setEui(eui);
            damStatus.setGatewayId(gatewayId);
            damStatus.setFrequencyOffset(frequencyOffset);

            if (damStatus.getGPS() == null && dam.getDamdari().getGPS() != null) {
                damStatus.setGPS(dam.getDamdari().getGPS());
            }
            DamStatus lastDamstatus = damStatusRepository.findLastDamStatus(dam.getId() + "");

            if (BaseCommonUtils.isNotNull(temperature) && BaseCommonUtils.isNotNull(lastDamstatus.getTemperature())) {
                boolean drankWater = (( temperature* 100) / lastDamstatus.getTemperature() ) < 96.5;
                if (drankWater) {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    WaterDrink existWaterDrank = waterDrinkRepository.findWaterDrinkByDate(dateFormat.format(date), dam.getId());
                    if (existWaterDrank != null) {
                        existWaterDrank.setCount(existWaterDrank.getCount() + 1);
                        waterDrinkRepository.save(existWaterDrank);
                        damStatus.setWaterDrinkCount(Long.valueOf(existWaterDrank.getCount()));
                    } else {
                        WaterDrink waterDrink = new WaterDrink();
                        waterDrink.setDamId(dam.getId());
                        waterDrink.setCount(1);
                        waterDrinkRepository.save(waterDrink);
                        damStatus.setWaterDrinkCount(Long.valueOf( 1));
                    }
                }
            }

            damStatusRepository.save(damStatus);
            insertMobility(dam);

            List<DamParam> damParams = damParamRepository.findDamParamByDamdari(dam.getDamdari());

//            1,دما
//            2,PH
//            3,فعالیت


            Float maxTemp = damParams.get(0).getMax();
            Float minTemp = damParams.get(0).getMin();
            Damdari damdari = dam.getDamdari();
            Set<Users> users = damdari.getUsers();
            if (temperature > maxTemp) {
                try {
                    Flag flagOfTab = flagRepository.getById(Long.parseLong(Consts.FLAG_OF_ANIMAL_TAB));
                    Set<Flag> flagSet = dam.getFlag();
                    flagSet.add(flagOfTab);
                    dam.setFlag(flagSet);
                    damRepository.save(dam);
                    HistoricalFlag historicalFlag = new HistoricalFlag(dam.getId(), flagOfTab.getId(), "system");
                    historicalFlagRepository.save(historicalFlag);

                } catch (Exception e) {
                }

                for (Users user : users) {
                    String message = "دمای بدن دام  " + dam.getId() + " - " + dam.getName() + " بیشتر از حد مجاز است (تب داشتن)";
                    notificationService.saveNotification(message, "", 1, "رد کردن",
                            Routes.PUT_dam_flag.replace("{damId}", dam.getId().toString()).replace("{flagId}", Consts.FLAG_OF_ANIMAL_TAB + ""),
                            user, 0, 0, dam.getId(), 2
                    );
                }

            }
//            else if (temperature < minTemp) {
//                for (Users user : users) {
//                    String message = "دمای بدن دام  " + dam.getId() + " - " + dam.getName() + " کمتر از حد مجاز است (آب خوردن)";
//                    notificationService.saveNotification(message, "", 0, "", "", user, 0, 0, dam.getId(), 2
//                    );
//                }
//            }

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "data.invalid");
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

    public Dam editDam(String id, String name,
                       String microChipCode,
                       String baharBand,
                       String label) {
        Dam dam = damRepository.findDam(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "dam.notFound"));

        if (name != null) {
            dam.setName(name);
        }
        if (microChipCode != null) {
            dam.setMicroChipCode(microChipCode);
        }
        if (baharBand != null) {
            dam.setBaharBand(baharBand);
        }
        if (label != null) {
            dam.setLable(label);
        }
        damRepository.save(dam);

        return dam;
    }

    public Dam updateFlag(Long damId, Long flagId, Long value) {
        Dam dam = damRepository.findDam(damId + "").orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageSource.getMessage("dam.notFound", null, null)));
        Flag flag = flagRepository.findById(flagId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageSource.getMessage("flag.notFound", null, null)));
        if (value == 0) {
            Set<Flag> flags = dam.getFlag();
            if (!flags.stream().filter(f -> f.getId() == flagId).collect(Collectors.toList()).isEmpty()) {
                try {
                    flags.remove(flag);
                    dam.setFlag(flags);
                    damRepository.save(dam);
                } catch (Exception e) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "data.invalid");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "flag.notFound");
            }
        } else {
            HistoricalFlag historicalFlag = new HistoricalFlag(dam.getId(), flag.getId(), "user");
            historicalFlagRepository.save(historicalFlag);
        }
        return dam;

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
                                          String[] flags,
                                          String typeId) {
        Pageable sortedAndPagination =
                PageRequest.of(page, perPage, Sort.by(sort).ascending());
        if (flags == null)
            return damRepository.findDamsOfDamdari(damdariId, typeId, sortedAndPagination);
        else
            return damRepository.findDamsOfDamdariByFlag(damdariId, Arrays.asList(flags), typeId, sortedAndPagination);
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


        String today = DateUtils.addDaysToJalaliDate(BaseDateUtils.getTodayJalali(), 1);
        String lastMonth = DateUtils.addDaysToJalaliDate(today, -30);
        if (BaseCommonUtils.isNull(fromDate)) {
            fromDate = lastMonth.replace("/", "-");
        }
        if (BaseCommonUtils.isNull(toDate)) {
            toDate = today.replace("/", "-");
        } else {
            toDate = DateUtils.addDaysToJalaliDate(toDate.replace("-", "/"), 1);
        }


        List<String> bimarFlags = new ArrayList<>();
        // bimarFlags.add(Consts.FLAG_OF_ANIMAL_BARDAR);
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
            dynamicInfos.setFlags(Arrays.asList());
            infos.add(dynamicInfos);
        }


        dynamicInfos = new DynamicInfos();
        dynamicInfos.setPosition(1);
        dynamicInfos.setTitle("کل");
        dynamicInfos.setIcon("../icon/total.png");
        dynamicInfos.setFlags(Arrays.asList());
        dynamicInfos.setData(damRepository.countOfDam(damdariId).toString());
        infos.add(dynamicInfos);


        dynamicInfos = new DynamicInfos();
        dynamicInfos.setPosition(2);
        dynamicInfos.setTitle("حامله");
        dynamicInfos.setIcon("../icon/pregnant.png");
        dynamicInfos.setFlags(Arrays.asList(Integer.parseInt(Consts.FLAG_OF_ANIMAL_BARDAR)));//
        dynamicInfos.setData(damRepository.countOf(damdariId, Arrays.asList(Consts.FLAG_OF_ANIMAL_BARDAR)).toString());
        infos.add(dynamicInfos);

        dynamicInfos = new DynamicInfos();
        dynamicInfos.setPosition(3);
        dynamicInfos.setTitle("نیازمند مراقبت");
        dynamicInfos.setIcon("../icon/care.png");
        dynamicInfos.setFlags(Arrays.asList(Integer.parseInt(Consts.FLAG_OF_ANIMAL_TAB), Integer.parseInt(Consts.FLAG_OF_ANIMAL_ASTANEH_ZAYEMAN), Integer.parseInt(Consts.FLAG_OF_ANIMAL_BIMAR)));//
        dynamicInfos.setData(damRepository.countOf(damdariId, bimarFlags).toString());
        infos.add(dynamicInfos);

        dynamicInfos = new DynamicInfos();
        dynamicInfos.setPosition(4);
        dynamicInfos.setTitle("فحلی");
        dynamicInfos.setIcon("../icon/fahli.png");
        dynamicInfos.setFlags(Arrays.asList(Integer.parseInt(Consts.FLAG_OF_ANIMAL_FAHLI)));//

        dynamicInfos.setData(damRepository.countOf(damdariId, Arrays.asList(Consts.FLAG_OF_ANIMAL_FAHLI)).toString());
        infos.add(dynamicInfos);

        dynamicInfos = new DynamicInfos();
        dynamicInfos.setPosition(5);
        dynamicInfos.setTitle("شیری");
        dynamicInfos.setIcon("../icon/milking.png");
        dynamicInfos.setData(damRepository.countOf(damdariId, Arrays.asList(Consts.FLAG_OF_ANIMAL_SHIRI)).toString());
        dynamicInfos.setFlags(Arrays.asList(Integer.parseInt(Consts.FLAG_OF_ANIMAL_SHIRI)));//
        infos.add(dynamicInfos);

        dynamicInfos = new DynamicInfos();
        dynamicInfos.setPosition(6);
        dynamicInfos.setTitle("آستانه زایمان");
        dynamicInfos.setIcon("../icon/birth.png");
        dynamicInfos.setData(damRepository.countOf(damdariId, Arrays.asList(Consts.FLAG_OF_ANIMAL_ASTANEH_ZAYEMAN)).toString());
        dynamicInfos.setFlags(Arrays.asList(Integer.parseInt(Consts.FLAG_OF_ANIMAL_ASTANEH_ZAYEMAN)));//
        infos.add(dynamicInfos);

        dashboard.setInfos(infos);

        //chart no 1
        DynamicCharts dynamicCharts = new DynamicCharts();
        dynamicCharts.setPosition(1);
        dynamicCharts.setType("pie");
        dynamicCharts.setChartTitle("تنوع دام های هوشمند");
        dynamicCharts.setData(damRepository.typeOfDamChartDto(damdariId));
        dynamicCharts.setChartName("typeOfDam");
        List<DynamicCharts> chartList = new ArrayList<>();
        chartList.add(dynamicCharts);


        if (damdariId == null) {
            //chart no 2
            dynamicCharts = new DynamicCharts();
            dynamicCharts.setPosition(2);
            dynamicCharts.setChartTitle("تعداد دام های هوشمند شده در هر ماه");
            dynamicCharts.setType("linear");
            dynamicCharts.setChartName("countOfDam");
            dynamicCharts.setData(damRepository.countDateOfDamChartDto(damdariId));
            chartList.add(dynamicCharts);
        }


        //chart no 3
        dynamicCharts = new DynamicCharts();
        dynamicCharts.setPosition(3);
        dynamicCharts.setChartTitle("میزان شیر دهی");
        dynamicCharts.setType("linear");
        dynamicCharts.setChartName("amountOfMilking");
        dynamicCharts.setData(damRepository.amountOfMilkingChartDto(damdariId, fromDate, toDate));
        chartList.add(dynamicCharts);

        Damdari damdari = damdariRepository.findDamdariById(Long.parseLong(damdariId));
        if (damdari == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "damdari.notFound");
        List<DamParam> damParams = damParamRepository.findDamParamByDamdari(damdari);

//             ,دما
//             ,PH
//             ,فعالیت

        Float maxTemp = damParams.get(0).getMax();
        Float minTemp = damParams.get(0).getMin();
        Float maxPh = damParams.get(1).getMax();
        Float minPh = damParams.get(1).getMin();


        //chart no 3
        dynamicCharts = new DynamicCharts();
        dynamicCharts.setPosition(5);
        dynamicCharts.setLimitMin(minTemp);
        dynamicCharts.setLimitMax(maxTemp);
        dynamicCharts.setChartTitle("میانگین دما");
        dynamicCharts.setType("linear");
        dynamicCharts.setChartName("averageOfTemp");
        dynamicCharts.setData(damRepository.avgOfTemperatureChartDto(damdariId, fromDate, toDate));
        chartList.add(dynamicCharts);

        //chart no 4
        dynamicCharts = new DynamicCharts();
        dynamicCharts.setPosition(6);
        dynamicCharts.setChartTitle("میزان مصرف علوفه");
        dynamicCharts.setType("linear");
        dynamicCharts.setChartName("averageOfFodder");
        dynamicCharts.setData(damRepository.amountOfFodderChartDto(damdariId, fromDate, toDate));
        chartList.add(dynamicCharts);


        //chart no 5
        dynamicCharts = new DynamicCharts();
        dynamicCharts.setPosition(7);
        dynamicCharts.setChartTitle("میزان مصرف آب");
        dynamicCharts.setChartName("averageOfWater");
        dynamicCharts.setType("linear");
        dynamicCharts.setData(damRepository.amountOfWaterChartDto(damdariId, fromDate, toDate));
        chartList.add(dynamicCharts);


        //chart no 3
        dynamicCharts = new DynamicCharts();
        dynamicCharts.setPosition(4);
        dynamicCharts.setLimitMin(minPh);
        dynamicCharts.setLimitMax(maxPh);
        dynamicCharts.setChartTitle("میانگین PH");
        dynamicCharts.setType("linear");
        dynamicCharts.setChartName("averageOfPH");
        dynamicCharts.setData(damRepository.avgOfPhChartDto(damdariId, fromDate, toDate));
        chartList.add(dynamicCharts);

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
            dynamicCharts.setChartTitle("dispersionOfDam");
            dynamicCharts.setType("pie");
            dynamicCharts.setData(damRepository.locationOfDamChartDto(damdariId));
            chartList.add(dynamicCharts);
        }


        dashboard.setCharts(chartList);

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

    public List<ImpDate> findAllImpDate(Long damId, String sort,
                                        int page,
                                        int perPage) {
        Pageable sortedAndPagination =
                PageRequest.of(page, perPage, Sort.by(sort).ascending());
        Dam dam = damRepository.findDam(damId + "").orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "dam.notFound"));

        return impDateRepository.findLastImpDate(damId);
    }

    public List<DamStatus> findAllDamStatusByDate(String sort,
                                                  int page,
                                                  int perPage,
                                                  String damId,
                                                  String fromDate,
                                                  String toDate,
                                                  int sortType) {
        Pageable sortedAndPagination = null;
        if (sortType == 1)
            sortedAndPagination = PageRequest.of(page, perPage, Sort.by(sort).ascending());
        else
            sortedAndPagination = PageRequest.of(page, perPage, Sort.by(sort).descending());
        return damStatusRepository.findAllDamStatus(damId, fromDate, toDate, sortedAndPagination);
    }

    public ImpDate saveImpDate(Long damId, Long typeId, String description, String src, String date) {
        Dam dam = damRepository.findDam(damId + "").orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "dam.notFound"));

        ImpDate impDate = new ImpDate();
        impDate.setDam(dam);
        impDate.setDate(date);
        impDate.setTypeId(typeId);
        impDate.setDescription(description);
        impDate.setSrc(src);

        return impDateRepository.save(impDate);
    }


    public DamStatus findLastDamStatus(
            String damId) {
        return damStatusRepository.findLastDamStatus(damId);
    }

    public Mobility findLastMobility(String damId) {
        return mobilityRepository.findLastMobility(damId);
    }

    public List<DamStatus> findAllDamGps(String sort,
                                         int page,
                                         int perPage,
                                         String damId, String damdariId) {
        Pageable sortedAndPagination =
                PageRequest.of(page, perPage, Sort.by(sort).ascending());
        return damStatusRepository.findAllDamStatus(damId, sortedAndPagination);
    }

    public Dam findDam(String damId, String fromDate, String toDate) {
        Dam dam = damRepository.findDam(damId).get();
        dam.setLastDamStatus(findLastDamStatus(damId));
        dam.setLastMobility(findLastMobility(damId));

        List<DamParam> damParams = damParamRepository.findDamParamByDamdari(dam.getDamdari());

//            1,دما
//            2,PH
//            3,فعالیت
        String today = DateUtils.addDaysToJalaliDate(BaseDateUtils.getTodayJalali(), 1);
        String lastMonth = DateUtils.addDaysToJalaliDate(today, -30);
        if (BaseCommonUtils.isNull(fromDate)) {
            fromDate = lastMonth.replace("/", "-");
        }
        if (BaseCommonUtils.isNull(toDate)) {
            toDate = today.replace("/", "-");
        } else {
            toDate = DateUtils.addDaysToJalaliDate(toDate.replace("-", "/"), 1);
        }

        Float maxTemp = damParams.get(0).getMax();
        Float minTemp = damParams.get(0).getMin();

        Float maxPh = damParams.get(1).getMax();
        Float minPh = damParams.get(1).getMin();

        List<DynamicCharts> chartList = new ArrayList<>();
        DynamicCharts dynamicCharts = new DynamicCharts();
        //chart no 1
        dynamicCharts.setPosition(2);
        dynamicCharts.setChartTitle("PH");
        dynamicCharts.setType("linear");
        dynamicCharts.setChartName("PH");
        dynamicCharts.setLimitMax(maxPh);
        dynamicCharts.setLimitMin(minPh);
        dynamicCharts.setData(damRepository.phOfDamChartDto(damId, fromDate, toDate));
        chartList.add(dynamicCharts);

        //chart no 2
        dynamicCharts = new DynamicCharts();
        dynamicCharts.setPosition(1);
        dynamicCharts.setChartTitle("دما");
        dynamicCharts.setType("linear");
        dynamicCharts.setChartName("temp");
        dynamicCharts.setLimitMax(maxTemp);
        dynamicCharts.setLimitMin(minTemp);
        dynamicCharts.setData(damRepository.temperatureOfDamChartDto(damId, fromDate, toDate));
        chartList.add(dynamicCharts);

        dam.setCharts(chartList);

        return dam;
    }

    public void insertMobility(Dam dam) throws ParseException {
        String today = BaseDateUtils.getTodayJalali();

        if (mobilityRepository.existsMobilityInDate(today, dam.getId()) == 0) {

            Long mobilityValue = 0l;
            List<MobilityDto> mobilityDto = mobilityRepository.getGastricMomentum(dam.getId());
            if (mobilityDto.size() == 2) {
                //2024-03-08 07:33:58
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                Date firstDate = sdf.parse(mobilityDto.get(0).getDate());
                Date secondDate = sdf.parse(mobilityDto.get(1).getDate());
                long diff = secondDate.getTime() - firstDate.getTime();
                long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                mobilityValue = (mobilityDto.get(1).getGastric() - mobilityDto.get(0).getGastric()) / minutes;
                Mobility mobility = new Mobility();
                mobility.setValue(mobilityValue);
                mobility.setDate(today);
                mobility.setDam(dam);
                if (mobilityValue > Consts.MAX_NORMAL_MOBILITY) {
                    mobility.setStatus(messageSource.getMessage("mobility.high", null, Locale.getDefault()));
                } else if (mobilityValue < Consts.MIN_NORMAL_MOBILITY) {
                    mobility.setStatus(messageSource.getMessage("mobility.low", null, Locale.getDefault()));
                } else {
                    mobility.setStatus(messageSource.getMessage("mobility.normal", null, Locale.getDefault()));
                }
                mobilityRepository.save(mobility);
            }
        }
    }
}
