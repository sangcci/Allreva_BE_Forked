package com.backend.allreva.module.member.application;

import com.backend.allreva.module.artist.application.ArtistService;
import com.backend.allreva.module.artist.domain.Artist;
import com.backend.allreva.module.member.application.dto.MemberArtistRequest;
import com.backend.allreva.module.member.application.dto.MemberRegisterRequest;
import com.backend.allreva.module.member.application.dto.NicknameDuplication;
import com.backend.allreva.module.member.application.dto.RefundAccountRequest;
import com.backend.allreva.module.member.application.port.MemberDetailRepository;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.member.domain.MemberArtist;
import com.backend.allreva.module.member.domain.MemberArtistRepository;
import com.backend.allreva.module.member.domain.MemberRepository;
import com.backend.allreva.module.member.domain.value.MemberRole;
import com.backend.allreva.module.member.fixture.MemberFixture;
import com.backend.allreva.module.member.fixture.MemberRequestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayName("MemberService 단위 테스트")
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberArtistRepository memberArtistRepository;

    @Mock
    private MemberDetailRepository memberDetailRepository;

    @Mock
    private ArtistService artistService;

    @Nested
    @DisplayName("회원 정보 조회")
    class Describe_회원_정보_조회 {

        @Nested
        @DisplayName("닉네임 중복 확인 시")
        class Context_닉네임_중복_확인 {

            @Test
            @DisplayName("중복된 닉네임이 있으면 true를 반환한다")
            void 중복된_닉네임이_있으면_true를_반환한다() {
                // given
                given(memberRepository.existsByMemberInfoNickname(anyString())).willReturn(true);

                // when
                NicknameDuplication result = memberService.isDuplicatedNickname("existingNickname");

                // then
                assertThat(result.isDuplicated()).isTrue();
            }

            @Test
            @DisplayName("중복된 닉네임이 없으면 false를 반환한다")
            void 중복된_닉네임이_없으면_false를_반환한다() {
                // given
                given(memberRepository.existsByMemberInfoNickname(anyString())).willReturn(false);

                // when
                NicknameDuplication result = memberService.isDuplicatedNickname("newNickname");

                // then
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
            @DisplayName("회원 정보가 성공적으로 수정된다")
            void 회원_정보가_성공적으로_수정된다() {
                // given
                Member member = MemberFixture.createMember(1L, MemberRole.USER);
                MemberRegisterRequest request = MemberRequestFixture.createMemberRegisterRequestWithArtists(Collections.emptyList());
                given(memberRepository.save(any(Member.class))).willReturn(member);
                given(memberArtistRepository.findByMemberId(any())).willReturn(Collections.emptyList());

                // when
                memberService.updateMemberInfo(request, member);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(member.getMemberInfo().getNickname()).isEqualTo(request.nickname());
                    softly.assertThat(member.getMemberInfo().getIntroduce()).isEqualTo(request.introduce());
                });
                verify(memberRepository, times(1)).save(member);
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
            @DisplayName("아티스트가 성공적으로 추가된다")
            void 아티스트가_성공적으로_추가된다() {
                // given
                Member member = MemberFixture.createMember(1L, MemberRole.USER);
                List<MemberArtistRequest> artistRequests = List.of(
                        new MemberArtistRequest("spotifyId1", "Artist1")
                );
                MemberRegisterRequest request = MemberRequestFixture.createMemberRegisterRequestWithArtists(artistRequests);

                Artist artist = Artist.builder().id("spotifyId1").name("Artist1").build();
                given(artistService.getArtistById(anyString())).willReturn(artist);
                given(memberArtistRepository.findByMemberId(any())).willReturn(Collections.emptyList());
                given(memberRepository.save(any(Member.class))).willReturn(member);

                // when
                memberService.updateMemberInfo(request, member);

                // then
                verify(artistService, times(1)).saveIfNotExist(any());
                verify(memberArtistRepository, times(1)).saveAll(any());
                verify(memberArtistRepository, times(1)).deleteAll(any());
            }
        }

        @Nested
        @DisplayName("기존 관심 아티스트를 삭제할 때")
        class Context_기존_아티스트_삭제 {

            @Test
            @DisplayName("아티스트가 성공적으로 삭제된다")
            void 아티스트가_성공적으로_삭제된다() {
                // given
                Member member = MemberFixture.createMember(1L, MemberRole.USER);
                MemberRegisterRequest request = MemberRequestFixture.createMemberRegisterRequestWithArtists(Collections.emptyList());

                MemberArtist existingArtist = MemberArtist.builder()
                        .memberId(1L)
                        .artistId("oldArtistId")
                        .build();
                given(memberArtistRepository.findByMemberId(any())).willReturn(List.of(existingArtist));
                given(memberRepository.save(any(Member.class))).willReturn(member);

                // when
                memberService.updateMemberInfo(request, member);

                // then
                verify(memberArtistRepository, times(1)).deleteAll(any());
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
            @DisplayName("환불 계좌가 성공적으로 등록된다")
            void 환불_계좌가_성공적으로_등록된다() {
                // given
                Member member = MemberFixture.createMember(1L, MemberRole.USER);
                RefundAccountRequest request = MemberRequestFixture.createRefundAccountRequest();
                given(memberRepository.save(any(Member.class))).willReturn(member);

                // when
                memberService.registerRefundAccount(request, member);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(member.getRefundAccount().getBank()).isEqualTo(request.bank());
                    softly.assertThat(member.getRefundAccount().getNumber()).isEqualTo(request.number());
                });
                verify(memberRepository, times(1)).save(member);
            }
        }

        @Nested
        @DisplayName("환불 계좌를 삭제할 때")
        class Context_환불_계좌_삭제 {

            @Test
            @DisplayName("환불 계좌가 성공적으로 삭제된다")
            void 환불_계좌가_성공적으로_삭제된다() {
                // given
                Member member = MemberFixture.createMember(1L, MemberRole.USER);
                member.setRefundAccount("국민은행", "123-456-789");
                given(memberRepository.save(any(Member.class))).willReturn(member);

                // when
                memberService.deleteRefundAccount(member);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(member.getRefundAccount().getBank()).isEmpty();
                    softly.assertThat(member.getRefundAccount().getNumber()).isEmpty();
                });
                verify(memberRepository, times(1)).save(member);
            }
        }
    }
}
