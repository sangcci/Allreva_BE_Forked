package com.backend.allreva.member.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.member.command.application.MemberService;
import com.backend.allreva.member.command.input.MemberInfoUpdateCommand;
import com.backend.allreva.member.domain.LoginProvider;
import com.backend.allreva.member.domain.Member;
import com.backend.allreva.member.domain.MemberErrorCode;
import com.backend.allreva.member.domain.MemberRepository;
import com.backend.allreva.member.domain.MemberStatus;
import com.backend.allreva.member.domain.NicknameDuplication;
import com.backend.allreva.member.fixture.MemberFixture;
import com.backend.allreva.member.fixture.MemberRequestFixture;
import com.backend.allreva.member.query.application.MemberFinder;
import com.backend.allreva.support.IntegrationTestSupport;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("Member 통합 테스트")
class MemberIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MemberService memberCommandService;

    @Autowired
    private MemberFinder memberQueryService;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("DELETE FROM member");
    }

    @Nested
    @DisplayName("registerByOAuth 테스트")
    class Describe_registerByOAuth {

        @Nested
        @DisplayName("신규 회원 OAuth 등록 시")
        class Context_신규_회원_등록 {

            @Test
            void 닉네임이_user_로_시작하는_랜덤값으로_저장된다() {
                Member saved = memberCommandService.registerByOAuth(
                        "newuser@kakao.com", LoginProvider.KAKAO, "https://example.com/profile.jpg");

                assertThat(saved.getMemberInfo().getNickname()).startsWith("user-");
                assertThat(memberRepository.findById(saved.getId())).isPresent();
            }

            @Test
            void 온보딩_미완료_상태로_저장된다() {
                Member saved = memberCommandService.registerByOAuth(
                        "newuser@kakao.com", LoginProvider.KAKAO, "https://example.com/profile.jpg");

                assertThat(saved.getMemberStatus()).isEqualTo(MemberStatus.REGISTERED);
            }
        }

        @Nested
        @DisplayName("동시 최초 로그인으로 unique constraint 위반 시")
        class Context_중복_회원_등록 {

            @Test
            void DUPLICATE_MEMBER_ACCOUNT_예외가_발생한다() {
                memberRepository.save(MemberFixture.createTestMember(MemberFixture.EMAIL, LoginProvider.KAKAO));

                assertThatThrownBy(() -> memberCommandService.registerByOAuth(
                                MemberFixture.EMAIL, LoginProvider.KAKAO, "https://example.com/profile.jpg"))
                        .isInstanceOf(CustomException.class)
                        .hasFieldOrPropertyWithValue("errorCode", MemberErrorCode.DUPLICATE_MEMBER_ACCOUNT);
            }
        }
    }

    @Nested
    @DisplayName("회원 정보 조회")
    class Describe_회원_정보_조회 {

        @Nested
        @DisplayName("닉네임 중복 확인 시")
        class Context_닉네임_중복_확인 {

            @Test
            void 중복된_닉네임이_있으면_true를_반환한다() {
                Member saved = memberRepository.save(MemberFixture.createTestMember());
                String existingNickname = saved.getMemberInfo().getNickname();

                NicknameDuplication result = memberQueryService.isDuplicatedNickname(existingNickname);

                assertThat(result.isDuplicated()).isTrue();
            }

            @Test
            void 중복된_닉네임이_없으면_false를_반환한다() {
                NicknameDuplication result = memberQueryService.isDuplicatedNickname("nonexistentNickname");

                assertThat(result.isDuplicated()).isFalse();
            }
        }
    }

    @Nested
    @DisplayName("회원 정보 수정")
    class Describe_회원_정보_수정 {

        @Nested
        @DisplayName("회원 프로필을 수정할 때")
        class Context_회원_프로필_수정 {

            @Test
            void 회원_정보가_성공적으로_수정된다() {
                Member member = memberRepository.save(MemberFixture.createTestMember());
                MemberInfoUpdateCommand request = Instancio.of(MemberRequestFixture.memberInfoUpdateCommandModel())
                        .create();

                memberCommandService.updateMemberInfo(request, member.getId());
                Member updatedMember = memberRepository.findById(member.getId()).orElseThrow();

                assertSoftly(softly -> {
                    softly.assertThat(updatedMember.getMemberInfo().getNickname())
                            .isEqualTo(request.nickname());
                    softly.assertThat(updatedMember.getMemberInfo().getIntroduce())
                            .isEqualTo(request.introduce());
                });
            }

            @Test
            void 온보딩_미완료_회원이면_일반_회원으로_전환된다() {
                Member member = memberCommandService.registerByOAuth(
                        "newuser@kakao.com", LoginProvider.KAKAO, "https://example.com/profile.jpg");
                MemberInfoUpdateCommand request = Instancio.of(MemberRequestFixture.memberInfoUpdateCommandModel())
                        .create();

                memberCommandService.updateMemberInfo(request, member.getId());
                Member updatedMember = memberRepository.findById(member.getId()).orElseThrow();

                assertThat(updatedMember.getMemberStatus()).isEqualTo(MemberStatus.ACTIVE);
            }
        }
    }

    @Nested
    @DisplayName("환불 계좌 관리")
    class Describe_환불_계좌_관리 {

        @Nested
        @DisplayName("환불 계좌를 수정할 때")
        class Context_환불_계좌_수정 {

            @Test
            void 환불_계좌가_성공적으로_수정된다() {
                Member member = memberRepository.save(MemberFixture.createTestMember());
                String bank = "국민은행";
                String number = "123-456-789";

                memberCommandService.updateRefundAccount(bank, number, member.getId());
                Member updatedMember = memberRepository.findById(member.getId()).orElseThrow();

                assertSoftly(softly -> {
                    softly.assertThat(updatedMember.getRefundAccount().getBank())
                            .isEqualTo(bank);
                    softly.assertThat(updatedMember.getRefundAccount().getNumber())
                            .isEqualTo(number);
                });
            }
        }

        @Nested
        @DisplayName("환불 계좌를 초기화할 때")
        class Context_환불_계좌_초기화 {

            @Test
            void 환불_계좌가_성공적으로_초기화된다() {
                Member member = memberRepository.save(MemberFixture.createTestMember());
                member.updateRefundAccount("국민은행", "123-456-789");

                memberCommandService.resetRefundAccount(member.getId());
                Member updatedMember = memberRepository.findById(member.getId()).orElseThrow();

                assertSoftly(softly -> {
                    softly.assertThat(updatedMember.getRefundAccount().getBank())
                            .isEmpty();
                    softly.assertThat(updatedMember.getRefundAccount().getNumber())
                            .isEmpty();
                });
            }
        }
    }
}
