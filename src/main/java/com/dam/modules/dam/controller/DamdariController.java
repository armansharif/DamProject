package com.dam.modules.dam.controller;

import com.dam.commons.Routes;
import com.dam.config.JsonResponseBodyTemplate;
import com.dam.modules.dam.model.DamParam;
import com.dam.modules.dam.model.Damdari;
import com.dam.modules.dam.service.DamdariService;
import com.dam.modules.jwt.JwtUtils;
import com.dam.modules.user.model.Users;
import com.dam.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping(produces = "application/json")

public class DamdariController {

    private UserService userService;
    private MessageSource messageSource;
    private DamdariService damdariService;
    private final JwtUtils jwtUtils;

    @Autowired
    public DamdariController(UserService userService, MessageSource messageSource, DamdariService damdariService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.messageSource = messageSource;
        this.damdariService = damdariService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = {Routes.Get_damdari})
    public ResponseEntity<Object> findDamdaries(
            @PathVariable(required = false) String damdariId,
            @RequestParam(required = false, defaultValue = "id") String sort,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int perPage,
            HttpServletResponse response) {
        try {
            List<Damdari> DamdariList = this.damdariService.findAllDamdari(sort, page, perPage);
            return ResponseEntity.ok()
                    .body(DamdariList);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    JsonResponseBodyTemplate.
                            createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

@GetMapping(value = {Routes.GET_damdari_param})
public ResponseEntity<Object> findDamdariParam(
        @PathVariable(required = false) Long damdariId,
        HttpServletResponse response) {
    try {
        List<DamParam> damParams = this.damdariService.findDamdariParam(damdariId);
        return ResponseEntity.ok()
                .body(damParams);
    } catch (Exception e) {
        return new ResponseEntity<>(
                JsonResponseBodyTemplate.
                        createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}



    @PostMapping(value = {Routes.POST_damdari_param})
    @PreAuthorize("hasAuthority('OP_ACCESS_EDIT')")
    public ResponseEntity<Object> addDamStatus(
            @PathVariable Long damdariId,
            @RequestParam Long id,
            @RequestParam Float min,
            @RequestParam Float max,
            HttpServletRequest request,
            HttpServletResponse response) {

        Long user_id = jwtUtils.getUserId(request);
        Users user = userService.findUser(user_id).orElse(null);
        if (user != null) {

            Damdari damdari = damdariService.findDamdari(damdariId);

            if (!user.getDamdari().contains(damdari))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "user.access.denied");

            DamParam damParam = damdariService.getDamParamById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "param.notFound"));
            damParam.setDamdari(damdari);
            damParam.setMin(min);
            damParam.setMax(max);
            try {
                damdariService.saveDamParam(damParam);
            } catch (Exception e) {
                return new ResponseEntity<>(
                        JsonResponseBodyTemplate.
                                createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
            List<DamParam>  damParams = damdariService.findDamdariParam(damdariId);
            return ResponseEntity.ok()
                    .body(damParams);
        } else {
            return ResponseEntity.notFound().build();
        }

    }


}
