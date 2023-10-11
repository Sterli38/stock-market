package com.example.stockmarket.dao.repositories;

import com.example.stockmarket.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Participant findByName(String name);

}
