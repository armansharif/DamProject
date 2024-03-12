package com.dam.modules.ticketing.model;

import com.dam.commons.Consts;
import com.dam.modules.ticketing.consts.ConstTicketing;
import com.dam.modules.user.model.Users;
import com.fasterxml.jackson.annotation.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Ticket {
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

    int status = ConstTicketing.TICKET_STATUS_OPEN;

    @Lob
    @Column(length = 512)
    private String text;

    private String title;

    @JsonManagedReference
    @ManyToOne
    TicketCategory ticketCategory;

    @JsonIncludeProperties(value = {"id","name","family"})
    @ManyToOne
    Users users;


    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<TicketResponse> responseList;

    public Ticket() {
    }
}
