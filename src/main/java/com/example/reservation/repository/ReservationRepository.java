package com.example.reservation.repository;

import com.example.reservation.domain.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    List<ReservationEntity> findAllByName(String name);

    Optional<ReservationEntity> findById(Long reservationNumber);

    List<ReservationEntity> findAllByStoreNameAndName(String storeName, String name);

    @Transactional
    void deleteById(Long reservationNumber);

}
