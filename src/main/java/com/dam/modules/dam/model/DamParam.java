package com.dam.modules.dam.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class DamParam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String farsiName;

    @Column(precision=8, scale=2)
    private Float min;

    @Column(precision=8, scale=2)
    private Float max;

    @JsonIgnore
    @ManyToOne
    private Damdari damdari;


}
