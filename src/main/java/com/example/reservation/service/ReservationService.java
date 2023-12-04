package com.example.reservation.service;

import com.example.reservation.domain.ReservationEntity;
import com.example.reservation.exception.impl.*;
import com.example.reservation.model.GetReservation;
import com.example.reservation.model.NewReservation;
import com.example.reservation.model.ReservationResult;
import com.example.reservation.model.Visit;
import com.example.reservation.repository.MemberRepository;
import com.example.reservation.repository.ReservationRepository;
import com.example.reservation.repository.StoreRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;

    //예약 하기 메소드
    //예약하려는 가게가 없거나, 회원이 아닌 아이디이면 예외처리 >> 나중에 바꿀까?
    //가게가 있으면 예약 테이블에 데이터 update
    public ReservationEntity reservation(NewReservation reservation) {
        this.storeRepository.findByStoreName(reservation.getStoreName())
                .orElseThrow(NoStorenameException::new);
        this.memberRepository.findByUserName(reservation.getName())
                .orElseThrow(NoUsernameException::new);
        if (reservation.getCount() > 10) {
            throw new MaximumNumberExceedException();
        }

        return this.reservationRepository.save(reservation.toEntity());
    }

    //예약 현황 확인 메소드 (예약자의 이름으로 저장된 모든 예약 확인)
    //이름으로 예약 테이블에서 예약을 찾아 리스트로 받고
    //ReservationEntity 중 예약자 이름과, 몇시 예약인지 두개의 데이터를 뽑아
    //새로운 GetReservation 모델의 리스트로 만들고
    //이름과 리스트를 ReservationResult 타입으로 반환
    //name(아이디)가 member 테이블에 있는지 확인
    //name(로그인아이디)는 유니크키 이므로 겹칠 일이 없다.
    public ReservationResult getReservationByName(String name) {
        boolean exists = memberRepository.existsByUserName(name);

        if (!exists) {
            throw new NoUsernameException();
        }

        List<ReservationEntity> reservationEntityList =
                this.reservationRepository.findAllByName(name);

        List<GetReservation> reservations = reservationEntityList.stream()
                .map(e -> new GetReservation(e.getId(), e.getStoreName(), e.getReservationTime()))
                .collect(Collectors.toList());

        return new ReservationResult(name, reservations);
    }

    //예약 취소
    //예약 번호, 이름(아이디)를 받아서 예약자가 맞는지 확인
    //현재 시간 이전의 예약은 취소 불가능
    public GetReservation cancelReservation(Long reservationNumber, String name) {
        ReservationEntity reservationEntity = this.reservationRepository.findById(reservationNumber)
                .orElseThrow(NoExistsReservationException::new);
        String reservationName = reservationEntity.getName();
        LocalDateTime reservationTime = reservationEntity.getReservationTime();

        if (!Objects.equals(reservationName, name)) {
            throw new NoMatchReservationNameException();
        }

        if (reservationTime.isBefore(LocalDateTime.now())) {
            throw new DoNotCancelPastReservationException();
        }
        this.reservationRepository.deleteById(reservationNumber);

        return new GetReservation(reservationNumber,
                reservationEntity.getStoreName(), reservationTime);
    }

    //예약 방문 확인
    //방문 확인을 받는 메소드
    //예약 번호와 예약자 이름(아이디)를 입력하여 확인을 한다
    public String visitStore(Visit visit) {
        ReservationEntity reservationEntity = this.reservationRepository.findById(visit.getReservationNumber())
                .orElseThrow(NoExistsReservationException::new);
        if (!Objects.equals(reservationEntity.getName(), visit.getName())) {
            throw new NoMatchReservationNameException();
        }

        //예약 시간이 (방문 시간-10분)보다 이전이라면(예약 시간보다 10분 늦게 온다면) >> 예약 취소 >> 다른 손님 받음
        if (reservationEntity.getReservationTime().isBefore(LocalDateTime.now().minusMinutes(10))) {
            this.reservationRepository.deleteById(visit.getReservationNumber());
            return "체크인 제한 시간 이후에 오셔서 예약이 취소 되었습니다";
        }
        //정상적인 시간에 도착했다면 확인됨
        return "예약이 확인되었습니다";
    }
}
