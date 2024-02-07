package com.dam.modules.dam.controller;

import com.dam.commons.Routes;
import com.dam.config.JsonResponseBodyTemplate;
import com.dam.modules.dam.model.Dam;
import com.dam.modules.dam.model.DamStatus;
import com.dam.modules.dam.model.Damdari;
import com.dam.modules.jwt.JwtUtils;
import com.dam.modules.user.model.Users;
import com.dam.modules.user.service.UserService;
import com.dam.modules.dam.service.DamStatusService;
import com.dam.modules.dam.service.DamService;
import liquibase.pro.packaged.is;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.domain.Null;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Or;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(produces = "application/json")

public class DamController {


    private DamService damService;
    private DamStatusService damStatusService;
    private UserService userService;
    private Environment env;
    private Path rootImages;
    private final JwtUtils jwtUtils;

    @Autowired
    public DamController(DamService damService, DamStatusService damStatusService, UserService userService, Environment env, JwtUtils jwtUtils) {
        this.damService = damService;
        this.damStatusService = damStatusService;
        this.userService = userService;
        this.env = env;
        this.jwtUtils = jwtUtils;
    }


    @GetMapping(value = {Routes.Get_damdari_dams})
    public ResponseEntity<Object> findDamsOfDamdari(
            @PathVariable(required = false) String damdariId,
            @RequestParam(required = false) String isFahli,
            @RequestParam(required = false) String hasLangesh,
            @RequestParam(required = false) String typeId,
            @RequestParam(required = false, defaultValue = "id") String sort,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int perPage,
            HttpServletResponse response) {
        try {
            List<Dam> damList = this.damService.findAllDamsOfDamdari(sort, page, perPage, damdariId, isFahli, hasLangesh, typeId);
            return ResponseEntity.ok()
                    .body(damList);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    JsonResponseBodyTemplate.
                            createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping(value = {Routes.Get_dams_hasProblem, Routes.Get_dams_hasProblem_damdariId})
    public ResponseEntity<Object> findDamsOfDamdariHasProblem(
            @PathVariable(required = false) String damdariId,
            @RequestParam(required = false, defaultValue = "1") String isFahli,
            @RequestParam(required = false, defaultValue = "1") String hasLangesh,
            @RequestParam(required = false, defaultValue = "1") String hasTab,
            @RequestParam(required = false) String typeId,
            @RequestParam(required = false, defaultValue = "id") String sort,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int perPage,
            HttpServletResponse response) {
        try {
            List<Dam> damList = this.damService.findAllDamsOfDamdariHasProblem(sort, page, perPage, damdariId, isFahli, hasLangesh, hasTab, typeId);
            return ResponseEntity.ok()
                    .body(damList);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    JsonResponseBodyTemplate.
                            createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping(value = {Routes.Get_dams})
    public List<Dam> getAllDams(
            @And({
                    @Spec(path = "isFahli", spec = Equal.class),
                    @Spec(path = "typeId", spec = Equal.class),
                    @Spec(path = "hasLangesh", spec = Equal.class)
            }) Specification<Dam> spec,
            @RequestParam(required = false, defaultValue = "id") String sort,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int perPage,
            HttpServletResponse response) {
        return damService.findAll(sort, page, perPage, spec);
    }

    @GetMapping(value = {Routes.Get_dam})
    public ResponseEntity<Object> findDam(
            @PathVariable String damId,
            @RequestParam(required = false, defaultValue = "id") String sort,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int perPage,
            HttpServletResponse response) {
        try {
            Dam dam = this.damService.findDam(damId).get();
            dam.setLastDamStatus(this.damService.findLastDamStatus(damId));
            return ResponseEntity.ok()
                    .body(dam);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    JsonResponseBodyTemplate.
                            createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = {Routes.Get_dam_status})
    public ResponseEntity<Object> findDamStatus(
            @PathVariable String damId,
            @RequestParam(required = false, defaultValue = "id") String sort,
            @RequestParam(required = false, defaultValue = "0") int sortType,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int perPage,
            HttpServletResponse response) {
        try {
            List<DamStatus> damStatusList = this.damService.findAllDamStatus(sort, page, perPage, damId,sortType);
            return ResponseEntity.ok()
                    .body(damStatusList);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    JsonResponseBodyTemplate.
                            createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = {Routes.POST_dam_status})
    public ResponseEntity<Object> addDamStatus(
            @PathVariable String damId,
            @RequestParam String statusString,
            @RequestParam(required = false, defaultValue = "id") String sort,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int perPage,
            HttpServletResponse response) {
        try {


            /*
             *
             * to do:
             *     check dam by dam id
             *     create status dto
             *     save damStatus
             */

            return ResponseEntity.ok()
                    .body("");

        } catch (Exception e) {
            return new ResponseEntity<>(
                    JsonResponseBodyTemplate.
                            createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping(value = {Routes.DELETE_dam_delete})
    public ResponseEntity<Object> deleteDam(@RequestParam(required = true) Long id, HttpServletResponse response) {
        try {
            damService.deleteDam(id);
            return ResponseEntity.ok()
                    .body(JsonResponseBodyTemplate
                            .createResponseJson("success", response.getStatus(), "Item deleted successfully").toString());
        } catch (Exception e) {
            return new ResponseEntity<>(
                    JsonResponseBodyTemplate.
                            createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @PostMapping(value = {Routes.POST_dam_add})
    public ResponseEntity<Object> addDam(String json, MultipartFile fileImage, HttpServletResponse response, HttpServletRequest request) {
        try {

            String rootImagesProperty = env.getProperty("road.marking.rootImagesProperty");
            String urlImages = env.getProperty("road.marking.urlImagesProperty");

            Path rootImages = Paths.get(rootImagesProperty);


            JSONObject jsonObject = new JSONObject(json);
            Dam dam = new Dam();

            if (jsonObject.has("created_at"))
                dam.setCreatedAt((jsonObject.getString("created_at")) == null ? "" : jsonObject.getString("created_at"));

            Long user_id = jwtUtils.getUserId(request);
            if (user_id != null) {
                Optional<Users> user = this.userService.findUser(user_id);
                if (user.isPresent()) {
                    //     dam.setUsers(user.get());
                }
            }
            if (jsonObject.has("device_id"))
                dam.setName((jsonObject.getString("device_id")) == null ? "" : jsonObject.getString("device_id"));

            if (fileImage != null)
                dam.setFileImage(fileImage);

            Dam dam_saved = damService.addDam(dam, rootImages, urlImages);
            return ResponseEntity.ok()
                    .body(dam_saved);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    JsonResponseBodyTemplate.
                            createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = {Routes.POST_data}, method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<Object> getData(@RequestBody String body,@RequestParam(required = false) String data, HttpServletResponse response ) {
        try {

            damService.addSampleData("DATA:" + data);
            damService.addSampleData("body:" + body);

            damService.importData(body);
            return ResponseEntity.ok()
                    .body(JsonResponseBodyTemplate
                            .createResponseJson("success", response.getStatus(), "Data saved successfully").toString());
        } catch (Exception e) {
            return new ResponseEntity<>(
                    JsonResponseBodyTemplate.
                            createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
//
//    @PutMapping(value = {"/dam/check", "/dam/check/"})
//    public ResponseEntity<Object> checkDam(
//            @RequestParam Long dam_id,
//            @RequestParam boolean is_check,
//            HttpServletResponse response) {
//        try {
//            Dam dam = damService.findDam(dam_id).orElse(null);
//            if (dam != null) {
//
//               // dam.setChecked(is_check);
//                damService.saveDam(dam);
//                return ResponseEntity.ok()
//                        .body(dam);
//            } else {
//                return new ResponseEntity<>(
//                        JsonResponseBodyTemplate.
//                                createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), "Dam not found").toString(),
//                        HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//
//        } catch (Exception e) {
//            return new ResponseEntity<>(
//                    JsonResponseBodyTemplate.
//                            createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString(),
//                    HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//    }

}
