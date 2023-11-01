package com.dam.modules.ticketing.model;

import com.dam.modules.user.model.Users;
import com.fasterxml.jackson.annotation.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Data
public class TicketResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private LocalDateTime createdAt;


    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Lob
    @Column(length = 512)
    private String text;

    @JsonIgnore
    @ManyToOne
    Ticket ticket;

    @JsonIncludeProperties(value = {"id","name","family"})
    @ManyToOne
    Users users;

    public TicketResponse() {

    }
}
