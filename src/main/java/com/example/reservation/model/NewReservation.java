package com.example.reservation.model;

import com.example.reservation.domain.ReservationEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class NewReservation {
    private String storeName;
    private String name;
    private String phone;
    private int count;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime reservationTime;

    public ReservationEntity toEntity() {
        return ReservationEntity.builder()
                .storeName(this.storeName)
                .name(this.name)
                .phone(this.phone)
                .count(this.count)
                .reservationTime(this.reservationTime)
                .build();

    }
}
