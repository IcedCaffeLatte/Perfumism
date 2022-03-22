package com.ladder.perfumism.perfume.controller;

import com.ladder.perfumism.perfume.controller.dto.response.PerfumeDetailResponse;
import com.ladder.perfumism.perfume.controller.dto.response.PerfumeLikeResponse;
import com.ladder.perfumism.perfume.controller.dto.response.PerfumeListResponse;
import com.ladder.perfumism.perfume.service.PerfumeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.net.URI;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Api(tags = {"향수"})
public class PerfumeRestController {

    private PerfumeService perfumeService;

    public PerfumeRestController(PerfumeService perfumeService) {
        this.perfumeService = perfumeService;
    }

    @GetMapping("/perfumes/{perfume_id}")
    @ApiOperation(value = "향수 상세 정보", notes = "단일 향수 상세 정보를 조회하는 API 입니다.")
    @ApiImplicitParam(name = "perfume_id", value = "향수 ID", required = true)
    public ResponseEntity<PerfumeDetailResponse> viewDetailPerfume(@PathVariable(value = "perfume_id") Long perfumeId) {
        return ResponseEntity.ok().body(perfumeService.viewDetailPerfume(perfumeId));
    }

    @PostMapping("/auth/perfumes/likes/{perfume_id}")
    @ApiOperation(value = "향수 좋아요 누르기", notes = "<b>(로그인 필요)</b> 특정 향수에 좋아요를 할 수 있는 API 입니다.")
    @ApiImplicitParam(name = "perfume_id", value = "좋아할 향수 ID", required = true)
    public ResponseEntity<Void> likePerfume(@ApiParam(hidden = true) @AuthenticationPrincipal String email,
        @PathVariable(value = "perfume_id") Long perfumeId) {
        Long newPerfumeLikeId = perfumeService.likePerfume(email, perfumeId);
        URI uri = URI.create("/api/perfumes/" + perfumeId + "/" + newPerfumeLikeId);
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/auth/perfumes/likes/{perfume_id}")
    @ApiOperation(value = "향수 좋아요 여부 조회", notes = "<b>(로그인 필요)</b> 자신이 특정 향수에 좋아요 했는지 확인하는 API 입니다.")
    @ApiImplicitParam(name = "perfume_id", value = "좋아요 여부를 확인할 향수 ID", required = true)
    public ResponseEntity<PerfumeLikeResponse> isLikeThisPerfume(@ApiParam(hidden = true) @AuthenticationPrincipal String email,
        @PathVariable(value = "perfume_id") Long perfumeId) {
        return ResponseEntity.ok().body(perfumeService.isLikeThisPerfume(email, perfumeId));
    }

    @DeleteMapping("/auth/perfumes/likes/{perfume_id}")
    @ApiOperation(value = "리뷰 좋아요 취소", notes = "<b>(로그인 필요)</b> 특정 향수에 좋아요를 취소하는 API 입니다.")
    @ApiImplicitParam(name = "perfume_id", value = "좋아요 취소할 향수 ID", required = true)
    public ResponseEntity<Void> notLikeThisPerfumeAnymore(@ApiParam(hidden = true) @AuthenticationPrincipal String email,
        @PathVariable(value = "perfume_id") Long perfumeId) {
        perfumeService.notLikeThisPerfumeAnymore(email, perfumeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/auth/perfumes/likes/my-favorite")
    @ApiOperation(value = "내가 좋아요 누른 향수 목록", notes = "<b>(로그인 필요)</b> 내가 좋아요 누른 향수의 목록을 받아오는 API 입니다.")
    public ResponseEntity<PerfumeListResponse> myFavoritePerfumeList(
        @PageableDefault(sort = "id", direction = Direction.DESC) Pageable pageable,
        @ApiParam(hidden = true) @AuthenticationPrincipal String email) {
        return ResponseEntity.ok().body(perfumeService.myFavoritePerfumeList(email, pageable));
    }

    @GetMapping("/perfumes")
    @ApiOperation(value = "향수 목록", notes = "향수 목록 API 입니다.")
    public ResponseEntity<PerfumeListResponse> normalPerfumeList(@PageableDefault(sort = "id", direction = Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok().body(perfumeService.normalPerfumeList(pageable));
    }
}