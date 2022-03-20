package com.ladder.perfumism.review.service;

import com.ladder.perfumism.global.exception.BusinessException;
import com.ladder.perfumism.global.exception.ErrorCode;
import com.ladder.perfumism.member.domain.Member;
import com.ladder.perfumism.member.domain.MemberRepository;
import com.ladder.perfumism.review.controller.dto.request.ReviewWriteRequest;
import com.ladder.perfumism.review.controller.dto.response.ReviewLikeResponse;
import com.ladder.perfumism.review.controller.dto.response.ReviewPageResponse;
import com.ladder.perfumism.perfume.domain.Perfume;
import com.ladder.perfumism.perfume.domain.PerfumeRepository;
import com.ladder.perfumism.review.controller.dto.response.ReviewResponse;
import com.ladder.perfumism.review.domain.Review;
import com.ladder.perfumism.review.domain.ReviewLike;
import com.ladder.perfumism.review.domain.ReviewLikeRepository;
import com.ladder.perfumism.review.domain.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PerfumeRepository perfumeRepository;
    private final MemberRepository memberRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    public ReviewService(ReviewRepository reviewRepository, PerfumeRepository perfumeRepository,
        MemberRepository memberRepository, ReviewLikeRepository reviewLikeRepository) {
        this.reviewRepository = reviewRepository;
        this.perfumeRepository = perfumeRepository;
        this.memberRepository = memberRepository;
        this.reviewLikeRepository = reviewLikeRepository;
    }

    @Transactional
    public Long writeReview(String email, Long perfumeId, ReviewWriteRequest request) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND_BY_EMAIL));
        Perfume perfume = perfumeRepository.findById(perfumeId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PERFUME_NOT_FOUND_BY_ID));

        alreadyWritten(member, perfume);

        Review review = reviewRepository.save(Review.createReview(perfume, member, request));

        averageGrade(perfume);
        perfume.increaseTotalSurvey();

        return review.getId();
    }

    private void averageGrade(Perfume perfume) {
        Double avgGrade = reviewRepository.avgGradeByPerfumeId(perfume.getId());
        if (avgGrade == null) {
            perfume.saveGrade(0.0);
            return;
        }
        perfume.saveGrade(avgGrade);
    }

    private void alreadyWritten(Member member, Perfume perfume) {
        if (reviewRepository.existsByMemberIdAndPerfumeId(member, perfume)) {
            throw new BusinessException(ErrorCode.REVIEW_ALREADY_WRITTEN);
        }
    }

    @Transactional(readOnly = true)
    public ReviewPageResponse getReviewPage(Long perfumeId, Pageable pageable) {
        Perfume perfume = perfumeRepository.findById(perfumeId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PERFUME_NOT_FOUND_BY_ID));

        Page<Review> reviewList = reviewRepository.findByPerfumeId(perfume, pageable);

        return ReviewPageResponse.from(reviewList);
    }

    @Transactional
    public void changeReview(String email, Long reviewId, ReviewWriteRequest request) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND_BY_ID));

        isYourReview(email, review);

        if (!request.getGrade().equals(review.getGrade())) {
            review.changeGrade(request.getGrade());

            averageGrade(review.getPerfumeId());

        }

        review.changeContent(request.getContent());

        reviewRepository.save(review);
    }

    private void isYourReview(String email, Review review) {
        if (!review.getMemberId().getEmail().equals(email)) {
            throw new BusinessException(ErrorCode.REVIEW_NOT_YOUR_REVIEW);
        }
    }

    @Transactional
    public void removeReview(String email, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND_BY_ID));

        isYourReview(email, review);

        // TODO: 이 리뷰에 모든 좋아요 삭제

        review.saveDeletedTime();
        averageGrade(review.getPerfumeId());
        review.getPerfumeId().decreaseTotalSurvey();
    }

    @Transactional(readOnly = true)
    public ReviewPageResponse getMyReviewPage(String email, Pageable pageable) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND_BY_EMAIL));

        Page<Review> reviewList = reviewRepository.findByMemberId(member, pageable);

        return ReviewPageResponse.from(reviewList);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getMyPerfumeReview(String email, Long perfumeId) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND_BY_EMAIL));
        Perfume perfume = perfumeRepository.findById(perfumeId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PERFUME_NOT_FOUND_BY_ID));

        Review review = reviewRepository.findByMemberIdAndPerfumeId(member, perfume)
            .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_WRITTEN_THIS_PERFUME));

        return ReviewResponse.from(review);
    }

    @Transactional
    public Long likeReview(String email, Long reviewId) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND_BY_EMAIL));
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND_BY_ID));

        doNotLikeYourReview(member, review);
        alreadyLikeReview(member, review);

        ReviewLike reviewLike = reviewLikeRepository.save(ReviewLike.createReviewLike(review, member));

        review.saveLike(reviewLikeRepository.countByReviewId(review));

        return reviewLike.getId();
    }

    private void alreadyLikeReview(Member member, Review review) {
        if (reviewLikeRepository.existsByMemberIdAndReviewId(member, review)) {
            throw new BusinessException(ErrorCode.REVIEW_ALREADY_LIKE);
        }
    }

    private void doNotLikeYourReview(Member member, Review review) {
        if (member.getId().equals(review.getMemberId().getId())) {
            throw new BusinessException(ErrorCode.REVIEW_NO_LIKE_YOURSELF);
        }
    }

    @Transactional(readOnly = true)
    public ReviewLikeResponse isLikeThisReview(String email, Long reviewId) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND_BY_EMAIL));
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND_BY_ID));

        Boolean liked = reviewLikeRepository.existsByMemberIdAndReviewId(member,review);

        return ReviewLikeResponse.from(liked);
    }

    @Transactional
    public void notLikeThisReviewAnymore(String email, Long reviewId){
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND_BY_EMAIL));
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND_BY_ID));

        ReviewLike reviewLike = reviewLikeRepository.findByMemberIdAndReviewId(member, review)
            .orElseThrow(()-> new BusinessException(ErrorCode.REVIEW_NOT_LIKE_THIS_REVIEW));

        reviewLike.saveDeletedTime();

        review.saveLike(reviewLikeRepository.countByReviewId(review));
    }
}
