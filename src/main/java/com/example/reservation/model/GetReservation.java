package com.example.reservation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetReservation {
    private Long reservationNumber;
    private String storeName;
    private LocalDateTime localDateTime;
}
