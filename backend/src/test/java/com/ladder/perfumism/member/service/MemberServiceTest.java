package com.ladder.perfumism.member.service;

import static com.ladder.perfumism.member.util.MemberFixture.EMAIL;
import static com.ladder.perfumism.member.util.MemberFixture.PASSWORD;
import static com.ladder.perfumism.member.util.MemberFixture.USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.ladder.perfumism.global.exception.BusinessException;
import com.ladder.perfumism.global.exception.ErrorCode;
import com.ladder.perfumism.member.controller.dto.request.ChangePasswordRequest;
import com.ladder.perfumism.member.controller.dto.request.CheckDuplicateRequest;
import com.ladder.perfumism.member.controller.dto.request.MemberSaveRequest;
import com.ladder.perfumism.member.controller.dto.response.CheckDuplicateResponse;
import com.ladder.perfumism.member.domain.Member;
import com.ladder.perfumism.member.domain.MemberRepository;
import com.ladder.perfumism.member.util.MemberFixture;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    private MemberSaveRequest memberSaveRequest;

    private String NEW_PASSWORD = "NewPassword1!";

    @BeforeEach
    void setup() {
        memberSaveRequest = MemberFixture.createMemberSaveRequest(EMAIL, PASSWORD, USERNAME);
    }

    @Test
    @DisplayName("?????? ???????????? ???????????? ?????? ??? ????????? ????????? ??? ?????? ErrorCode C04??? ???????????? ??????.")
    void saveMemberExceptionDuplicatedEmailTest() {
        // setup & given
        when(memberRepository.existsByEmail(EMAIL)).thenReturn(true);

        // when & then
        assertThatExceptionOfType(BusinessException.class)
            .isThrownBy(() -> memberService.saveMember(memberSaveRequest))
            .withMessageMatching(ErrorCode.MEMBER_EMAIL_DUPLICATED.getMessage());
    }

    @Test
    @DisplayName("???????????? ??? ????????? id??? ????????? ??? ??????.")
    void saveMemberTest() {
        // setup & given
        when(memberRepository.existsByEmail(EMAIL)).thenReturn(false);
        Member member = memberSaveRequest.toMember();
        ReflectionTestUtils.setField(member, "id", 1L);
        when(memberRepository.save(any())).thenReturn(member);

        // when
        Long result = memberService.saveMember(memberSaveRequest);

        // then
        assertThat(result).isEqualTo(1L);
    }

//    @Test
//    @DisplayName("???????????? ??? deleted_at??? ????????????.")
//    void resignMemberTest() {
//        // setup & given
//        Member member = memberSaveRequest.toMember();
//        when(memberRepository.findByEmail(any())).thenReturn(Optional.ofNullable(member));
//
//        // when
//        memberService.resignMember(EMAIL);
//
//        // then
//        assertThat(member.getDeletedAt()).isNotNull();
//    }

    @Test
    @DisplayName("???????????? ?????? ????????? ???????????? ErrorCode C01??? ????????????.")
    void findByEmailMemberNotFoundByEmailExceptionTest(){
        given(memberRepository.findByEmail(any())).willThrow(new BusinessException(ErrorCode.MEMBER_NOT_FOUND_BY_EMAIL));

        assertThatExceptionOfType(BusinessException.class)
            .isThrownBy(() -> memberService.findByEmail(EMAIL))
            .withMessageMatching(ErrorCode.MEMBER_NOT_FOUND_BY_EMAIL.getMessage());
    }

    @Test
    @DisplayName("???????????? ????????? ????????? ??? ??????.")
    void findByEmailTest() {
        // setup & given
        Member member = memberSaveRequest.toMember();
        given(memberRepository.findByEmail(any())).willReturn(Optional.ofNullable(member));

        // when
        Member testMember = memberService.findByEmail(EMAIL);

        // then
        assertThat(testMember.getUsername()).isEqualTo(USERNAME);
    }

    @Test
    @DisplayName("???????????? ??????????????? ?????? true??? ????????????.")
    void checkDuplicateEmailTest() {
        // setup & given
        when(memberRepository.existsByEmail(EMAIL)).thenReturn(true);
        CheckDuplicateRequest checkDuplicateRequest = new CheckDuplicateRequest(EMAIL);

        // when
        CheckDuplicateResponse checkDuplicateResponse = memberService.checkDuplicateEmail(checkDuplicateRequest);

        // then
        assertThat(checkDuplicateResponse.getResult()).isEqualTo(true);
    }

    @Test
    @DisplayName("??????????????? ??????????????? ?????? true??? ????????????.")
    void checkDuplicateUsernameTest() {
        // setup & given
        when(memberRepository.existsByUsername(USERNAME)).thenReturn(true);
        CheckDuplicateRequest checkDuplicateRequest = new CheckDuplicateRequest(USERNAME);

        // when
        CheckDuplicateResponse checkDuplicateResponse = memberService.checkDuplicateUsername(checkDuplicateRequest);

        // then
        assertThat(checkDuplicateResponse.getResult()).isEqualTo(true);
    }

//    @Test
//    @DisplayName("??????????????? ????????? ??? ??????")
//    void changePasswordTest() {
//        // setup & given
//        Member member = memberSaveRequest.toMember();
//        given(memberRepository.findByEmail(any())).willReturn(Optional.ofNullable(member));
//        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(EMAIL, NEW_PASSWORD);
//
//        // when
//        memberService.changePassword(changePasswordRequest);
//
//        // then
//        assertThat(member.getPassword()).isEqualTo(NEW_PASSWORD);
//    }
}
