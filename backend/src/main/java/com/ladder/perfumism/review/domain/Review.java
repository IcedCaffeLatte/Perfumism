package com.ladder.perfumism.review.domain;

import com.ladder.perfumism.global.domain.BaseEntity;
import com.ladder.perfumism.global.exception.BusinessException;
import com.ladder.perfumism.global.exception.ErrorCode;
import com.ladder.perfumism.member.domain.Member;
import com.ladder.perfumism.perfume.domain.Perfume;
import com.ladder.perfumism.review.controller.dto.request.ReviewWriteRequest;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Where;

@Getter
@Entity
@Where(clause = "deleted_at is null")
public class Review extends BaseEntity {

    private static final int MAX_GRADE = 5;
    private static final int MIN_GRADE = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(targetEntity = Perfume.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "perfume_id")
    private Perfume perfumeId;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member memberId;

    @Column(name = "grade")
    private Integer grade;

    @Lob
    @Column(name = "content")
    private String content;

    @Column(name = "total_like")
    private Integer totalLike;

    public Review() {
    }

    public Review(Long id, Perfume perfumeId, Member memberId, Integer grade, String content, Integer totalLike) {
        this.id = id;
        this.perfumeId = perfumeId;
        this.memberId = memberId;
        this.grade = grade;
        this.content = content;
        this.totalLike = totalLike;
    }

    @Builder
    public Review(Perfume perfumeId, Member memberId, Integer grade, String content, Integer totalLike) {
        this.perfumeId = perfumeId;
        this.memberId = memberId;
        this.grade = grade;
        this.content = content;
        this.totalLike = totalLike;
    }

    public static Review createReview(Perfume perfume, Member member, ReviewWriteRequest request) {
        overGrade(request.getGrade());
        underGrade(request.getGrade());

        return Review.builder()
            .perfumeId(perfume)
            .memberId(member)
            .grade(request.getGrade())
            .content(request.getContent())
            .totalLike(0)
            .build();
    }

    static void overGrade(Integer grade) {
        if (grade > MAX_GRADE) {
            throw new BusinessException(ErrorCode.REVIEW_OVER_GRADE);
        }
    }

    static void underGrade(Integer grade) {
        if (grade < MIN_GRADE) {
            throw new BusinessException(ErrorCode.REVIEW_UNDER_GRADE);
        }
    }

    public void changeGrade(Integer grade) {
        overGrade(grade);
        underGrade(grade);
        this.grade = grade;
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public void saveLike(Integer totalLike) {
        this.totalLike = totalLike;
    }
}