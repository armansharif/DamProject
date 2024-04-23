package com.dam.modules.dam.model;

import com.dam.modules.user.model.Users;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
public class Damdari {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String photo;
    private String address;
    private String gPS;


    @JsonBackReference
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "damdari")
    private Set<Users> users = new HashSet<>();


    @JsonManagedReference
    @OneToMany(mappedBy = "damdari",cascade = CascadeType.ALL)
    private List<Dam> dams;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonIgnore
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonManagedReference
    @ManyToOne
    private City city;


    @JsonIgnore
    @OneToMany(mappedBy = "damdari", cascade = CascadeType.ALL)
    private List<Resource> resource;

    @JsonBackReference
    @OneToMany(mappedBy = "damdari", cascade = CascadeType.ALL)
    private List<DamParam> damParams;

    @JsonIgnore
    @OneToMany(mappedBy = "damdari", cascade = CascadeType.ALL)
    private List<Milking> milking;
}
