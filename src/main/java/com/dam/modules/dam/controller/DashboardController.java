package com.dam.modules.dam.controller;

import com.dam.commons.Routes;
import com.dam.config.JsonResponseBodyTemplate;
import com.dam.modules.dam.model.Dam;
import com.dam.modules.dam.model.Damdari;
import com.dam.modules.dam.model.Dashboard;
import com.dam.modules.dam.service.DamService;
import com.dam.modules.dam.service.DamdariService;
import com.dam.modules.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping(produces = "application/json")

public class DashboardController {
    private UserService userService;

    private DamdariService damdariService;

    private DamService  damService;

    @Autowired
    public DashboardController(UserService userService, DamdariService damdariService, DamService damService) {
        this.userService = userService;
        this.damdariService = damdariService;
        this.damService = damService;
    }

    @ApiOperation(value = "DateFormat = 1402-02-20" )
    @GetMapping(value = {Routes.Get_dashboard,Routes.Get_dashboard_damdariId})
    public ResponseEntity<Object> getDashboard(
            @PathVariable(required = false) String damdariId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            HttpServletResponse response) {
        try {
           Dashboard dashboard = damService.getDashboardData(damdariId,fromDate,toDate);
            return ResponseEntity.ok()
                    .body(dashboard);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    JsonResponseBodyTemplate.
                            createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
