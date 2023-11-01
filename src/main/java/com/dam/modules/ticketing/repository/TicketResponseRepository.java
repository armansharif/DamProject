package com.dam.modules.ticketing.repository;

import com.dam.modules.ticketing.model.TicketResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketResponseRepository extends JpaRepository<TicketResponse, Long> {
    List<TicketResponse> findAll(Specification<TicketResponse> spec, Pageable pageable);
}
