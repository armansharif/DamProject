package com.dam.modules.ticketing.repository;


import com.dam.modules.ticketing.model.Ticket;
import com.dam.modules.user.model.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAll(Specification<Ticket> spec, Pageable pageable);

    List<Ticket> findAllByStatus(int status, Pageable pageable);

    Long countAllByStatus(int status);

    List<Ticket> findAllByUsers(Users users);


}
