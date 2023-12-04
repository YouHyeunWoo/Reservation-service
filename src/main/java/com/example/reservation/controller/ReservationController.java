package com.example.reservation.controller;

import com.example.reservation.domain.ReservationEntity;
import com.example.reservation.model.GetReservation;
import com.example.reservation.model.NewReservation;
import com.example.reservation.model.ReservationResult;
import com.example.reservation.model.Visit;
import com.example.reservation.service.ReservationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/reservation")
@AllArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    //예약하기
    //NewReservation타입의
    //가게이름, 예약자이름, 예약자 전화번호, 인원수, 방문시간을 RequestBody로 전달
    //로그인한 계정 이용 가능(유저 권한)
    @PostMapping()
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> reservation(@RequestBody NewReservation request) {
        ReservationEntity reservationEntity =
                this.reservationService.reservation(request);

        return ResponseEntity.ok(reservationEntity);
    }

    //예약 취소하기
    //로그인한 계정 이용 가능(유저 권한, 매니저 권한)
    @DeleteMapping("/cancel")
    @PreAuthorize("hasRole('USER') and hasRole('MANAGER')")
    public ResponseEntity<?> cancelReservation(@RequestParam Long reservationNumber,
                                               @RequestParam String name) {
        GetReservation reservation =
                this.reservationService.cancelReservation(reservationNumber, name);

        return ResponseEntity.ok(reservation);
    }

    //예약 확인하기
    //로그인한 계정 이용 가능(유저 권한, 매니저 권한)
    @GetMapping("/check")
    @PreAuthorize("hasRole('USER') and hasRole('MANAGER')")
    public ResponseEntity<?> checkReservation(@RequestParam String name) {
        ReservationResult reservationResult =
                this.reservationService.getReservationByName(name);

        return ResponseEntity.ok(reservationResult);
    }

    //매장 방문 확인하기
    @PostMapping("/visit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> visitStore(@RequestBody Visit request) {
        String result = this.reservationService.visitStore(request);

        return ResponseEntity.ok(result);
    }
}
