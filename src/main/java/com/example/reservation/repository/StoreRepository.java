package com.example.reservation.repository;

import com.example.reservation.domain.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, Long> {
    Optional<StoreEntity> findByStoreName(String storeName);

    boolean existsByStoreName(String storeName);

    @Transactional
    void deleteByStoreName(String storeName);
}
