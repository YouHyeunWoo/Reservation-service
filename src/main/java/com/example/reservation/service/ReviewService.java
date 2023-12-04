package com.example.reservation.service;

import com.example.reservation.domain.ReservationEntity;
import com.example.reservation.domain.ReviewEntity;
import com.example.reservation.domain.StoreEntity;
import com.example.reservation.exception.impl.NoExistsReservationException;
import com.example.reservation.exception.impl.NoExistsReviewException;
import com.example.reservation.exception.impl.NoStorenameException;
import com.example.reservation.model.FindReview;
import com.example.reservation.model.FindReviewResult;
import com.example.reservation.model.Review;
import com.example.reservation.repository.ReservationRepository;
import com.example.reservation.repository.ReviewRepository;
import com.example.reservation.repository.StoreRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;

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

        reviewEntity.setReview(review.getReview());
        reviewEntity.setScore(review.getScore());
        this.reviewRepository.save(reviewEntity);
    }

    //리뷰 삭제
    public ReviewEntity deleteReview(){



        return null;
    }

    //메서드 추출
    //입력으로 들어온 매장 이름이 존재하는지 확인
    private StoreEntity getStoreEntity(String storeName) {
        return this.storeRepository.findByStoreName(storeName)
                .orElseThrow(NoStorenameException::new);
    }
}
