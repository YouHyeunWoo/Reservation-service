package com.example.reservation.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FindReviewResult {
    private String storeName;
    private String location;
    private List<FindReview> result;
}
