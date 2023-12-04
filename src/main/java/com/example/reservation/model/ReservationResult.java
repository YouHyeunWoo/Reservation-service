package com.example.reservation.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ReservationResult {
    private String name;
    private List<GetReservation> getReservations;
}
