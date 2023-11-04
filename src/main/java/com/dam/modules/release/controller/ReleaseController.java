package com.dam.modules.release.controller;

import com.dam.config.JsonResponseBodyTemplate;

import com.dam.modules.release.model.ReleaseApps;
import com.dam.modules.release.service.ReleaseService;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@ApiIgnore
@RestController
@RequestMapping(value = "/admin/release")
public class ReleaseController {

    private ReleaseService releaseService;

    @Autowired
    public ReleaseController(ReleaseService releaseService) {
        this.releaseService = releaseService;
    }

    @GetMapping(value = {"", "/"})
    public ResponseEntity<Object> getRelease(HttpServletResponse response) {
        try {
            List<ReleaseApps> releaseAppsList = releaseService.findAllRelease();
            return ResponseEntity.ok()
                    .body(releaseAppsList);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    JsonResponseBodyTemplate.
                            createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }



    @PostMapping(value = {"", "/"})
    // @PreAuthorize("hasAuthority('OP_ACCESS_ADD')")
    public ResponseEntity<Object> addRelease(@NotNull @ModelAttribute ReleaseApps releaseApps, HttpServletResponse response) {
        try {
            ReleaseApps release_Apps_saved = releaseService.addRelease(releaseApps);
            return ResponseEntity.ok()
                    .body(release_Apps_saved);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    JsonResponseBodyTemplate.
                            createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


}
