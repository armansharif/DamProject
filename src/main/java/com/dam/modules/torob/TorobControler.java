package com.dam.modules.torob;


import com.dam.commons.Routes;
import com.dam.config.JsonResponseBodyTemplate;
import com.dam.modules.dam.model.Dam;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping(produces = "application/json")
public class TorobControler {
    private GetDataFromTorobImpl getDataFromTorob;

    @Autowired
    public TorobControler(GetDataFromTorobImpl getDataFromTorob) {
        this.getDataFromTorob = getDataFromTorob;
    }

    @GetMapping(value = {"/getTorob"})
    public ResponseEntity<Object>  getTorobLinks(
            HttpServletResponse response) {
        try {
            getDataFromTorob.getData("");
            return ResponseEntity.ok()
                    .body("OK");
        } catch (Exception e) {
            return new ResponseEntity<>(
                    JsonResponseBodyTemplate.
                            createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
