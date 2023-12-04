package com.example.reservation.repository;

import com.example.reservation.domain.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findAllByStoreName(String storeName);

    Optional<ReviewEntity> findByStoreNameAndId(String storeName, Long reviewId);

    @Transactional
    void deleteById(Long id);
}
