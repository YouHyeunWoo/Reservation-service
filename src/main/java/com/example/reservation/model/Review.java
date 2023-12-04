package com.example.reservation.model;

import com.example.reservation.domain.ReviewEntity;
import com.example.reservation.domain.StoreEntity;
import lombok.Data;

import java.time.LocalDateTime;

//리뷰 등록시 사용할 모델
@Data
public class Review {
    private String storeName;
    private String name;
    private String review;
    private Long score;

    public ReviewEntity toEntity(StoreEntity store) {
        return ReviewEntity.builder()
                .store(store)
                .storeName(this.storeName)
                .name(this.name)
                .review(this.review)
                .score(this.score)
                .regiTime(LocalDateTime.now())
                .build();
    }
}
