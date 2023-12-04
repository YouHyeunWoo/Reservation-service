package com.example.reservation.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FindReview {
    private Long reviewId;
    private String name;
    private String review;
    private Long score;
    private LocalDateTime regiTime;
}
