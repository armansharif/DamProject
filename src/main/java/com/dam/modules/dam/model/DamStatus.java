package com.dam.modules.dam.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Data
public class DamStatus implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    private String gPS;
    private Long battery;

    private Long aCCX;
    private Long aCCY;
    private Long aCCZ;
    private Long gYROX;
    private Long gYROY;
    private Long gYROZ;

    private Long gastricMomentum;

/*"time":"2024-02-21T11:40:50.383683Z",
"timestamp":2340976348,
"rssi":-99,
"channel_rssi":-99,
"snr":12.8,
"frequency_offset":"1511",
"uplink_token":"CiIKIAoUZXVpLTI0ZTEyNGZmZmVmM2Q0ZGISCCThJP/+89TbENztodwIGgwIwsLXrgYQ6v3f5gEg4JbB6JDBAQ==",
"channel_index":3,
"received_at":"2024-02-21T11:40:50.427390021Z"*/

    private String time;
    private Long timestamp;
    private Long rssi;
    private Long channelRssi;
    @Column(precision = 8, scale = 2)
    private Float snr;
    private String frequencyOffset;
    private Long channelIndex;
    private String receivedAt;

    private String gatewayId;
    private String    eui;



    @Column(precision = 8, scale = 2)
    private Float pH;
    private Long activeId;
    private Long settingConf;
    private String datetime;

    @Column(precision = 8, scale = 2)
    private Float temperature;

    @Lob
    @Column(length = 1000)
    private String description;


    @Column(name = "created_at", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @JsonIgnore
    @Column(name = "updated_at")
    // @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonIgnore
    @ManyToOne
    private Dam dam;

}
