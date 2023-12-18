package com.example.reservation.service;

import com.example.reservation.domain.MemberEntity;
import com.example.reservation.domain.ReservationEntity;
import com.example.reservation.domain.ReviewEntity;
import com.example.reservation.domain.StoreEntity;
import com.example.reservation.exception.impl.*;
import com.example.reservation.model.FindReview;
import com.example.reservation.model.FindReviewResult;
import com.example.reservation.model.Review;
import com.example.reservation.repository.MemberRepository;
import com.example.reservation.repository.ReservationRepository;
import com.example.reservation.repository.ReviewRepository;
import com.example.reservation.repository.StoreRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    //리뷰 작성
    //예약을 해서 이용을 한 사람만 리뷰 작성 가능
    public ReviewEntity newReview(Review review) {
        //ReviewController로 받은 review의 storeName으로 매장이 있는지 찾기
        StoreEntity storeEntity = getStoreEntity(review.getStoreName());

        //매장 이름과 예약자 아이디로 예약된 내역이 있는지 확인
        List<ReservationEntity> reservationEntityList = this.reservationRepository
                .findAllByStoreNameAndName(review.getStoreName(), review.getName());

        //예약이 하나도 없으면 리뷰 작성 불가능
        if (reservationEntityList.isEmpty()) {
            throw new NoExistsReservationException();
        }
        return reviewRepository.save(review.toEntity(storeEntity));
    }

    //매장에 작성된 리뷰 찾기
    //입력 받은 매장 이름으로 등록된 리뷰 리스트를 가져오고
    //리뷰가 하나도 없으면 예외처리
    public FindReviewResult findReview(String storeName) {
        StoreEntity storeEntity = getStoreEntity(storeName);

        List<ReviewEntity> reviewEntityList = this.reviewRepository.findAllByStoreName(storeName);

        if (reviewEntityList.isEmpty()) {
            throw new NoExistsReviewException();
        }
        //화면 출력을 위해 리뷰 리스트 중 필요한 내용(리뷰 등록 유저, 리뷰 내용, 점수, 등록시간) 등을 리스트로 만들고
        List<FindReview> findReviewList = reviewEntityList.stream()
                .map(e -> new FindReview(e.getId(), e.getName(), e.getReview(), e.getScore(), e.getRegiTime()))
                .collect(Collectors.toList());
        //매장 이름과 위치 정보를 포함한 findReviewResult 타입으로 리턴
        return new FindReviewResult(storeName, storeEntity.getLocation(), findReviewList);
    }

    //리뷰 수정
    //매장이 존재하는지 확인, 매장이름과 리뷰번호를 이용하여 해당 매장에 등록된 리뷰인지 확인
    //맞다면 새 리뷰를 저장
    public void modifyReview(Review review, Long reviewId) {
        StoreEntity storeEntity = getStoreEntity(review.getStoreName());

        ReviewEntity reviewEntity = this.reviewRepository.findByStoreNameAndId(storeEntity.getStoreName(), reviewId)
                .orElseThrow(() -> new RuntimeException("해당 매장에 등록된 리뷰가 아닙니다."));

        reviewEntity.setReview(review.getText());
        reviewEntity.setScore(review.getScore());
        this.reviewRepository.save(reviewEntity);
    }

    //리뷰 삭제 (고객용)
    //삭제는 아이디 비밀번호를 한번 입력해야 삭제 가능
    //리뷰 삭제를 위해선 리뷰에 등록된 아이디와 입력한 아이디가 맞아야 하고
    //입력한 비밀번호와 membertable의 비밀번호가 맞아야 삭제 가능
    public ReviewEntity deleteReviewUser(Long reviewId, String userName, String password) {
        //입력한 아이디가 존재하는지 확인
        MemberEntity memberEntity = this.memberRepository.findByUserName(userName)
                .orElseThrow(NoExistsUsernameException::new);
        //예약내역이 있는지 확인
        ReviewEntity reviewEntity = this.reviewRepository.findById(reviewId)
                .orElseThrow(NoExistsReservationException::new);

        //리뷰에 등록된 이름과 입력한 이름이 맞는지 확인
        if (!Objects.equals(reviewEntity.getName(), userName)) {
            throw new NoMatchNameException();
        }

        //memberEntity의 비밀번호와 입력한 비밀번호가 일치하는지 확인
        if (!passwordEncoder.matches(password, memberEntity.getPassword())) {
            throw new NoMatchPasswordException();
        }
        //맞으면 리뷰 삭제
        this.reviewRepository.deleteById(reviewId);

        return reviewEntity;
    }

    //리뷰 삭제 매장 관리자용
    public ReviewEntity deleteReviewManager(Long reviewId, String userName, String password) {
        //reviewId로 등록된 리뷰가 있는지 확인
        ReviewEntity reviewEntity = this.reviewRepository.findById(reviewId)
                .orElseThrow(NoExistsReservationException::new);
        //입력한 아이디가 존재하는지 확인
        MemberEntity memberEntity = this.memberRepository.findByUserName(userName)
                .orElseThrow(NoExistsUsernameException::new);

        //reviewEntity의 가게이름으로 storeEntity에서 가게가 있는지 확인
        StoreEntity storeEntity = this.storeRepository.findByStoreName(reviewEntity.getStoreName())
                .orElseThrow(NoExistsStorenameException::new);

        //그 가게에 등록된 점장의 이름과 입력한 이름이 맞는지 확인
        if (!Objects.equals(storeEntity.getManagerName(), userName)) {
            throw new NoMatchNameException();
        }

        //member 테이블에 등록된 비밀번호와 입력한 password가 일치하는지 확인
        if (!passwordEncoder.matches(password, memberEntity.getPassword())) {
            throw new NoMatchPasswordException();
        }

        //모든 정보가 일치하면 리뷰 삭제
        this.reviewRepository.deleteById(reviewId);

        return reviewEntity;
    }

    //메서드 추출
    //입력으로 들어온 매장 이름이 존재하는지 확인
    private StoreEntity getStoreEntity(String storeName) {
        return this.storeRepository.findByStoreName(storeName)
                .orElseThrow(NoExistsStorenameException::new);
    }
}
