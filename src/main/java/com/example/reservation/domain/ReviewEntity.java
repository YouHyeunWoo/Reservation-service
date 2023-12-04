package com.example.reservation.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String storeName;

    private String name;

    private String review;

    private Long score;

    private LocalDateTime regiTime;

    @ManyToOne
    @JoinColumn(name = "storeId")
    private StoreEntity store;
}
