package com.backend.allreva.module.member.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.concert.artist.domain.Artist;
import com.backend.allreva.module.concert.artist.infra.jpa.ArtistJpaRepository;
import com.backend.allreva.module.member.application.MemberService;
import com.backend.allreva.module.member.application.dto.MemberArtistRequest;
import com.backend.allreva.module.member.application.dto.MemberRegisterRequest;
import com.backend.allreva.module.member.application.dto.NicknameDuplication;
import com.backend.allreva.module.member.application.dto.OAuthRegisterRequest;
import com.backend.allreva.module.member.application.dto.RefundAccountRequest;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.member.domain.MemberRepository;
import com.backend.allreva.module.member.domain.artist.MemberArtist;
import com.backend.allreva.module.member.domain.artist.MemberArtistRepository;
import com.backend.allreva.module.member.domain.value.LoginProvider;
import com.backend.allreva.module.member.exception.MemberErrorCode;
import com.backend.allreva.module.member.fixture.MemberFixture;
import com.backend.allreva.module.member.fixture.MemberRequestFixture;
import com.backend.allreva.support.IntegrationTestSupport;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("Member 통합 테스트")
class MemberIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberArtistRepository memberArtistRepository;

    @Autowired
    private ArtistJpaRepository artistJpaRepository;

    @AfterEach
    void tearDown() {
        memberArtistRepository.deleteAll();
        memberRepository.deleteAllInBatch();
        artistJpaRepository.deleteAll();
    }

    @Nested
    @DisplayName("registerByOAuth 테스트")
    class Describe_registerByOAuth {

        @Nested
        @DisplayName("신규 회원 OAuth 등록 시")
        class Context_신규_회원_등록 {

            @Test
            void 닉네임이_user_로_시작하는_랜덤값으로_저장된다() {
                OAuthRegisterRequest request = new OAuthRegisterRequest(
                        "newuser@kakao.com", LoginProvider.KAKAO, "https://example.com/profile.jpg");

                Member saved = memberService.registerByOAuth(request);

                assertThat(saved.getMemberInfo().getNickname()).startsWith("user-");
                assertThat(memberRepository.findById(saved.getId())).isPresent();
            }
        }

        @Nested
        @DisplayName("동시 최초 로그인으로 unique constraint 위반 시")
        class Context_중복_회원_등록 {

            @Test
            void DUPLICATE_OAUTH_MEMBER_예외가_발생한다() {
                memberRepository.save(MemberFixture.createTestMember("dup@kakao.com", LoginProvider.KAKAO));
                OAuthRegisterRequest request = new OAuthRegisterRequest(
                        "dup@kakao.com", LoginProvider.KAKAO, "https://example.com/profile.jpg");

                assertThatThrownBy(() -> memberService.registerByOAuth(request))
                        .isInstanceOf(CustomException.class)
                        .hasFieldOrPropertyWithValue("errorCode", MemberErrorCode.DUPLICATE_OAUTH_MEMBER);
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
                Member saved =
                        memberRepository.save(MemberFixture.createTestMember("exists@kakao.com", LoginProvider.KAKAO));
                String existingNickname = saved.getMemberInfo().getNickname();

                NicknameDuplication result = memberService.isDuplicatedNickname(existingNickname);

                assertThat(result.isDuplicated()).isTrue();
            }

            @Test
            void 중복된_닉네임이_없으면_false를_반환한다() {
                NicknameDuplication result = memberService.isDuplicatedNickname("nonexistentNickname");

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
                Member member =
                        memberRepository.save(MemberFixture.createTestMember("profile@kakao.com", LoginProvider.KAKAO));
                MemberRegisterRequest request =
                        MemberRequestFixture.createMemberRegisterRequestWithArtists(Collections.emptyList());

                memberService.updateMemberInfo(request, member);

                assertSoftly(softly -> {
                    softly.assertThat(member.getMemberInfo().getNickname()).isEqualTo(request.nickname());
                    softly.assertThat(member.getMemberInfo().getIntroduce()).isEqualTo(request.introduce());
                });
            }
        }
    }

    @Nested
    @DisplayName("관심 아티스트 수정")
    class Describe_관심_아티스트_수정 {

        @Nested
        @DisplayName("새로운 관심 아티스트를 추가할 때")
        class Context_새로운_아티스트_추가 {

            @Test
            void 아티스트가_성공적으로_추가된다() {
                Member member = memberRepository.save(
                        MemberFixture.createTestMember("artist-add@kakao.com", LoginProvider.KAKAO));
                artistJpaRepository.save(
                        Artist.builder().id("spotifyId1").name("Artist1").build());
                List<MemberArtistRequest> artistRequests = List.of(new MemberArtistRequest("spotifyId1", "Artist1"));
                MemberRegisterRequest request =
                        MemberRequestFixture.createMemberRegisterRequestWithArtists(artistRequests);

                memberService.updateMemberInfo(request, member);

                List<MemberArtist> saved = memberArtistRepository.findByMemberId(member.getId());
                assertThat(saved).hasSize(1);
                assertThat(saved.get(0).getArtistId()).isEqualTo("spotifyId1");
            }
        }

        @Nested
        @DisplayName("기존 관심 아티스트를 삭제할 때")
        class Context_기존_아티스트_삭제 {

            @Test
            void 아티스트가_성공적으로_삭제된다() {
                Member member = memberRepository.save(
                        MemberFixture.createTestMember("artist-del@kakao.com", LoginProvider.KAKAO));
                memberArtistRepository.save(MemberArtist.builder()
                        .memberId(member.getId())
                        .artistId("oldArtistId")
                        .build());
                MemberRegisterRequest request =
                        MemberRequestFixture.createMemberRegisterRequestWithArtists(Collections.emptyList());

                memberService.updateMemberInfo(request, member);

                assertThat(memberArtistRepository.findByMemberId(member.getId()))
                        .isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("환불 계좌 관리")
    class Describe_환불_계좌_관리 {

        @Nested
        @DisplayName("환불 계좌를 등록할 때")
        class Context_환불_계좌_등록 {

            @Test
            void 환불_계좌가_성공적으로_등록된다() {
                Member member =
                        memberRepository.save(MemberFixture.createTestMember("refund@kakao.com", LoginProvider.KAKAO));
                RefundAccountRequest request = MemberRequestFixture.createRefundAccountRequest();

                memberService.registerRefundAccount(request, member);

                assertSoftly(softly -> {
                    softly.assertThat(member.getRefundAccount().getBank()).isEqualTo(request.bank());
                    softly.assertThat(member.getRefundAccount().getNumber()).isEqualTo(request.number());
                });
            }
        }

        @Nested
        @DisplayName("환불 계좌를 삭제할 때")
        class Context_환불_계좌_삭제 {

            @Test
            void 환불_계좌가_성공적으로_삭제된다() {
                Member member = memberRepository.save(
                        MemberFixture.createTestMember("refund-del@kakao.com", LoginProvider.KAKAO));
                member.setRefundAccount("국민은행", "123-456-789");

                memberService.deleteRefundAccount(member);

                assertSoftly(softly -> {
                    softly.assertThat(member.getRefundAccount().getBank()).isEmpty();
                    softly.assertThat(member.getRefundAccount().getNumber()).isEmpty();
                });
            }
        }
    }
}
