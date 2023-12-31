package com.example.reservation.controller;

import com.example.reservation.domain.ReviewEntity;
import com.example.reservation.model.Auth;
import com.example.reservation.model.FindReviewResult;
import com.example.reservation.model.Review;
import com.example.reservation.service.ReviewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;

    //리뷰 등록
    @PostMapping("/new")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> newReview(@RequestBody Review request) {
        ReviewEntity reviewEntity = reviewService.newReview(request);

        return ResponseEntity.ok(reviewEntity);
    }

    //리뷰 찾아보기
    @GetMapping("/find")
    @PreAuthorize("hasRole('USER') and hasRole('MANAGER')")
    public ResponseEntity<?> findReview(@RequestParam String storeName) {
        FindReviewResult findReviewResult =
                this.reviewService.findReview(storeName);

        return ResponseEntity.ok(findReviewResult);
    }

    //리뷰 수정
    @PutMapping("/modify")
    @PreAuthorize("hasRole('USER')")
    public void modifyReview(@RequestParam Long reviewId,
                             @RequestBody Review review) {
        this.reviewService.modifyReview(review, reviewId);
    }

    //리뷰 제거 (고객용)
    @DeleteMapping("/delete/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteReviewUser(@RequestParam Long reviewId,
                                              @RequestBody Auth.signIn auth
    ) {
        ReviewEntity reviewEntity = this.reviewService.deleteReviewUser(reviewId,
                auth.getUserName(), auth.getPassword());
        return ResponseEntity.ok(reviewEntity);
    }

    //리뷰 제거(매니저용)
    @DeleteMapping("/delete/manager")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> deleteReviewManager(@RequestParam Long reviewId,
                                                 @RequestBody Auth.signIn auth

    ) {
        ReviewEntity reviewEntity = this.reviewService.deleteReviewManager(reviewId,
                auth.getUserName(), auth.getPassword());
        return ResponseEntity.ok(reviewEntity);
    }
}
