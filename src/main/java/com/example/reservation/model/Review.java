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
    private String text;  //Review클래스의 review속성은 지양
                            // 동일 클래스명과 동일 속성은 지양 >> text로바꿈
    private Long score;

    public ReviewEntity toEntity(StoreEntity store) {
        return ReviewEntity.builder()
                .store(store)
                .storeName(this.storeName)
                .name(this.name)
                .review(this.text)
                .score(this.score)
                .regiTime(LocalDateTime.now())
                .build();
    }
}
