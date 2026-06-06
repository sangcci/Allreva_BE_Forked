package com.backend.allreva.recruitment.rent.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.concert.concert.ConcertEntity;
import com.backend.allreva.concert.concert.ConcertJpaRepository;
import com.backend.allreva.concert.concert.fixture.ConcertFixture;
import com.backend.allreva.member.domain.Member;
import com.backend.allreva.member.domain.MemberRepository;
import com.backend.allreva.member.fixture.MemberFixture;
import com.backend.allreva.recruitment.rent.RentBoardingSlotJpaRepository;
import com.backend.allreva.recruitment.rent.RentJpaRepository;
import com.backend.allreva.recruitment.rent.RentParticipantJpaRepository;
import com.backend.allreva.recruitment.rent.command.application.RentService;
import com.backend.allreva.recruitment.rent.command.input.RentIdCommand;
import com.backend.allreva.recruitment.rent.command.input.RentJoinCommand;
import com.backend.allreva.recruitment.rent.command.input.RentRegisterCommand;
import com.backend.allreva.recruitment.rent.domain.RentErrorCode;
import com.backend.allreva.recruitment.rent.domain.SortType;
import com.backend.allreva.recruitment.rent.fixture.RentFixture;
import com.backend.allreva.recruitment.rent.query.application.RentFinder;
import com.backend.allreva.recruitment.rent.query.model.HostedRentSummaryResult;
import com.backend.allreva.recruitment.rent.query.model.JoinedRentResult;
import com.backend.allreva.recruitment.rent.query.model.RentDetailResult;
import com.backend.allreva.recruitment.rent.query.model.RentParticipantResult;
import com.backend.allreva.recruitment.rent.query.model.RentSummaryResult;
import com.backend.allreva.support.IntegrationTestSupport;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@DisplayName("Rent Query Integration 테스트")
class RentQueryIntegrationTest extends IntegrationTestSupport {

    // rent 1개당 slot 3개 (날짜: 12-01, 12-02, 12-03)
    private static final List<LocalDate> SLOT_DATES =
            List.of(LocalDate.of(2030, 12, 1), LocalDate.of(2030, 12, 2), LocalDate.of(2030, 12, 3));

    @Autowired
    private RentFinder rentService;

    @Autowired
    private RentService rentCommandService;

    @Autowired
    private RentJpaRepository rentJpaRepository;

    @Autowired
    private RentBoardingSlotJpaRepository rentBoardingSlotJpaRepository;

    @Autowired
    private RentParticipantJpaRepository rentParticipantJpaRepository;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member savedMember;
    private String concertCode;

    @BeforeEach
    void setUp() {
        savedMember = memberRepository.save(MemberFixture.createTestMember());
        var concert = concertJpaRepository.save(ConcertEntity.from(
                Instancio.of(ConcertFixture.inProgressConcertModel()).create()));
        concertCode = concert.getConcertCode();
        doNothing().when(storageWriter).delete(any());
    }

    @AfterEach
    void tearDown() {
        rentParticipantJpaRepository.deleteAll();
        rentBoardingSlotJpaRepository.deleteAll();
        rentJpaRepository.deleteAll();
        concertJpaRepository.deleteAll();
        jdbcTemplate.execute("DELETE FROM member");
    }

    private RentRegisterCommand buildRegisterRequest() {
        return Instancio.of(RentFixture.rentRegisterRequestModel())
                .set(field(RentRegisterCommand.class, "concertCode"), concertCode)
                .set(field(RentRegisterCommand.class, "rentBoardingDateRequests"), SLOT_DATES)
                .create();
    }

    private RentJoinCommand buildJoinRequest(final Long rentId, final LocalDate boardingDate, final int passengerNum) {
        return Instancio.of(RentFixture.rentJoinRequestModel())
                .set(field(RentJoinCommand.class, "rentId"), rentId)
                .set(field(RentJoinCommand.class, "boardingDate"), boardingDate)
                .set(field(RentJoinCommand.class, "passengerNum"), passengerNum)
                .create();
    }

    @Nested
    @DisplayName("getRentDetail 테스트")
    class Describe_getRentDetail {

        private Long savedRentId;

        @BeforeEach
        void setUp() {
            savedRentId = rentCommandService.register(buildRegisterRequest(), savedMember.getId());
        }

        @Nested
        @DisplayName("concert가 있는 차대절 조회 시")
        class Context_with_concert {

            @Test
            @DisplayName("탑승 날짜 3개와 concert 정보가 포함된 상세가 반환된다")
            void it_returns_detail_with_concert_info() {
                // when
                RentDetailResult detail = rentService.getRentDetail(savedRentId);

                // then
                assertThat(detail.boardingDates()).hasSize(3);
            }
        }

        @Nested
        @DisplayName("존재하지 않는 차대절 조회 시")
        class Context_not_found {

            @Test
            @DisplayName("NOT_FOUND 예외가 발생한다")
            void it_throws_not_found() {
                // when & then
                assertThatThrownBy(() -> rentService.getRentDetail(999L))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(RentErrorCode.RENT_NOT_FOUND.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("getRentSummaries 테스트")
    class Describe_getRentSummaries {

        @Nested
        @DisplayName("rent 15개(slot 3개씩)가 있는 경우")
        class Context_with_enough_data {

            @BeforeEach
            void setUp() {
                for (int i = 0; i < 15; i++) {
                    rentCommandService.register(buildRegisterRequest(), savedMember.getId());
                }
            }

            @Test
            @DisplayName("pageSize=10으로 조회하면 10개가 반환된다")
            void it_includes_open_rents() {
                // when
                List<RentSummaryResult> result = rentService.getRentSummaries(null, SortType.LATEST, null, null, 10);

                // then
                assertThat(result).hasSize(10);
            }

            @Test
            @DisplayName("마감된 차대절은 목록에 포함되지 않는다")
            void it_excludes_closed_rents() {
                // given
                List<RentSummaryResult> all = rentService.getRentSummaries(null, SortType.LATEST, null, null, 15);
                Long firstRentId = all.get(0).rentId();
                rentCommandService.close(
                        Instancio.of(RentFixture.rentIdRequestModel())
                                .set(field(RentIdCommand.class, "rentId"), firstRentId)
                                .create(),
                        savedMember.getId());

                // when
                List<RentSummaryResult> result = rentService.getRentSummaries(null, SortType.LATEST, null, null, 15);

                // then
                assertThat(result).hasSize(14);
                assertThat(result).noneMatch(r -> r.rentId().equals(firstRentId));
            }

            @Test
            @DisplayName("커서 기반으로 다음 페이지를 조회할 수 있다")
            void it_paginates_with_cursor() {
                // given
                List<RentSummaryResult> firstPage = rentService.getRentSummaries(null, SortType.LATEST, null, null, 10);
                assertThat(firstPage).hasSize(10);

                // when
                Long lastId = firstPage.get(firstPage.size() - 1).rentId();
                List<RentSummaryResult> secondPage =
                        rentService.getRentSummaries(null, SortType.LATEST, null, lastId, 10);

                // then
                assertThat(secondPage).hasSize(5);
            }
        }
    }

    @Nested
    @DisplayName("getRentHostSummaries 테스트")
    class Describe_getRentHostSummaries {

        @Nested
        @DisplayName("본인 rent 15개(slot 3개씩)와 타인 rent 5개가 있는 경우")
        class Context_has_own_rents {

            private Member otherMember;

            @BeforeEach
            void setUp() {
                for (int i = 0; i < 15; i++) {
                    rentCommandService.register(buildRegisterRequest(), savedMember.getId());
                }
                otherMember = memberRepository.save(MemberFixture.createOtherTestMember());
                for (int i = 0; i < 5; i++) {
                    rentCommandService.register(buildRegisterRequest(), otherMember.getId());
                }
            }

            @Test
            @DisplayName("본인이 주최한 차대절만 반환되고 타인 차대절은 포함되지 않는다")
            void it_returns_only_own_rents() {
                // when
                List<HostedRentSummaryResult> result = rentService.getRentHostSummaries(savedMember.getId(), null, 20);

                // then
                assertThat(result).hasSize(15);
            }

            @Test
            @DisplayName("pageSize=10으로 조회하면 10개가 반환된다")
            void it_paginates_first_page() {
                // when
                List<HostedRentSummaryResult> result = rentService.getRentHostSummaries(savedMember.getId(), null, 10);

                // then
                assertThat(result).hasSize(10);
            }

            @Test
            @DisplayName("커서 기반으로 다음 페이지를 조회할 수 있다")
            void it_paginates_with_cursor() {
                // given
                List<HostedRentSummaryResult> firstPage =
                        rentService.getRentHostSummaries(savedMember.getId(), null, 10);
                assertThat(firstPage).hasSize(10);

                // when
                Long lastId = firstPage.get(firstPage.size() - 1).rentId();
                List<HostedRentSummaryResult> secondPage =
                        rentService.getRentHostSummaries(savedMember.getId(), lastId, 10);

                // then
                assertThat(secondPage).hasSize(5);
            }
        }

        @Nested
        @DisplayName("등록한 차대절이 없는 경우")
        class Context_no_rents {

            @Test
            @DisplayName("빈 목록이 반환된다")
            void it_returns_empty_list() {
                // when
                List<HostedRentSummaryResult> result = rentService.getRentHostSummaries(savedMember.getId(), null, 10);

                // then
                assertThat(result).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("getRentHostDetail 테스트")
    class Describe_getRentHostDetail {

        private Long savedRentId;
        private static final LocalDate TARGET_DATE = LocalDate.of(2030, 12, 1);

        @BeforeEach
        void setUp() {
            savedRentId = rentCommandService.register(buildRegisterRequest(), savedMember.getId());
        }

        @Nested
        @DisplayName("본인이 주최한 차대절의 특정 날짜 조회 시")
        class Context_valid_request {

            @BeforeEach
            void setUp() {
                Member member2 = memberRepository.save(MemberFixture.createTestMemberWithIndex(2));
                Member member3 = memberRepository.save(MemberFixture.createTestMemberWithIndex(3));
                rentCommandService.join(buildJoinRequest(savedRentId, TARGET_DATE, 1), member2.getId());
                rentCommandService.join(buildJoinRequest(savedRentId, TARGET_DATE, 1), member3.getId());
            }

            @Test
            @DisplayName("해당 날짜의 참여자 목록이 반환된다")
            void it_returns_participants_of_date() {
                // when
                List<RentParticipantResult> result =
                        rentService.getRentHostDetail(savedMember.getId(), TARGET_DATE, savedRentId);

                // then
                assertThat(result).hasSize(2);
            }

            @Test
            @DisplayName("다른 날짜의 참여자는 포함되지 않는다")
            void it_excludes_other_date_participants() {
                // given
                LocalDate otherDate = LocalDate.of(2030, 12, 2);

                // when
                List<RentParticipantResult> result =
                        rentService.getRentHostDetail(savedMember.getId(), otherDate, savedRentId);

                // then
                assertThat(result).isEmpty();
            }
        }

        @Nested
        @DisplayName("타인의 차대절 조회 시")
        class Context_other_member_rent {

            @Test
            @DisplayName("NOT_FOUND 예외가 발생한다")
            void it_throws_not_found() {
                // given
                Member otherMember = memberRepository.save(MemberFixture.createOtherTestMember());

                // when & then
                assertThatThrownBy(() -> rentService.getRentHostDetail(otherMember.getId(), TARGET_DATE, savedRentId))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(RentErrorCode.RENT_NOT_FOUND.getMessage());
            }
        }

        @Nested
        @DisplayName("slot에 없는 날짜 조회 시")
        class Context_invalid_date {

            @Test
            @DisplayName("NOT_FOUND 예외가 발생한다")
            void it_throws_not_found() {
                // given
                LocalDate invalidDate = LocalDate.of(2030, 12, 31);

                // when & then
                assertThatThrownBy(() -> rentService.getRentHostDetail(savedMember.getId(), invalidDate, savedRentId))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(RentErrorCode.RENT_NOT_FOUND.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("getJoinedRentSummaries 테스트")
    class Describe_getJoinedRentSummaries {

        // rent 5개 × slot 3개(12-01, 12-02, 12-03) = 15 slot
        private final List<Long> rentIds = new ArrayList<>();
        private Member hostMember;
        private Member otherMember;
        private RentRegisterCommand savedRegisterRequest;

        @BeforeEach
        void setUp() {
            hostMember = memberRepository.save(MemberFixture.createTestMemberWithIndex(10));
            otherMember = memberRepository.save(MemberFixture.createOtherTestMember());
            savedRegisterRequest = buildRegisterRequest();

            for (int i = 0; i < 5; i++) {
                Long rentId = rentCommandService.register(savedRegisterRequest, hostMember.getId());
                rentIds.add(rentId);

                for (LocalDate date : SLOT_DATES) {
                    rentCommandService.join(buildJoinRequest(rentId, date, 1), savedMember.getId());
                    rentCommandService.join(buildJoinRequest(rentId, date, 1), otherMember.getId());
                }
            }
        }

        @Test
        @DisplayName("pageSize=10으로 조회하면 10개가 반환된다")
        void it_returns_first_page() {
            // when
            List<JoinedRentResult> result = rentService.getJoinedRentSummaries(savedMember.getId(), null, 10);

            // then
            assertThat(result).hasSize(10);
        }

        @Test
        @DisplayName("커서 기반으로 다음 페이지를 조회할 수 있다 (1페이지=10건, 2페이지=5건)")
        void it_paginates_with_cursor() {
            // given
            List<JoinedRentResult> firstPage = rentService.getJoinedRentSummaries(savedMember.getId(), null, 10);
            assertThat(firstPage).hasSize(10);

            // when
            Long lastParticipantId = firstPage.get(firstPage.size() - 1).rentParticipantId();
            List<JoinedRentResult> secondPage =
                    rentService.getJoinedRentSummaries(savedMember.getId(), lastParticipantId, 10);

            // then
            assertThat(secondPage).hasSize(5);
        }

        @Test
        @DisplayName("slot 정보(recruitmentCount, participateCount=2)가 정확히 매핑된다")
        void it_maps_slot_info_correctly() {
            // when
            List<JoinedRentResult> result = rentService.getJoinedRentSummaries(savedMember.getId(), null, 1);
            JoinedRentResult response = result.get(0);

            // then (savedMember + otherMember 각 1명씩 동일 slot 참여 → participateCount=2)
            assertThat(response.recruitmentCount()).isEqualTo(savedRegisterRequest.recruitmentCount());
            assertThat(response.participateCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("rent별이 아닌 slot별로 반환된다 (rent 5개 × slot 3개 = 15건)")
        void it_returns_per_slot_not_per_rent() {
            // when
            List<JoinedRentResult> result = rentService.getJoinedRentSummaries(savedMember.getId(), null, 20);

            // then
            assertThat(result).hasSize(15);
            rentIds.forEach(rentId -> {
                long count =
                        result.stream().filter(r -> r.rentId().equals(rentId)).count();
                assertThat(count).isEqualTo(3);
            });
        }

        @Test
        @DisplayName("타인의 참여 내역이 포함되지 않는다 (otherMember도 15건, savedMember 것과 격리)")
        void it_isolates_by_member() {
            // when
            List<JoinedRentResult> savedMemberResult =
                    rentService.getJoinedRentSummaries(savedMember.getId(), null, 20);
            List<JoinedRentResult> otherMemberResult =
                    rentService.getJoinedRentSummaries(otherMember.getId(), null, 20);

            // then
            assertThat(savedMemberResult).hasSize(15);
            assertThat(otherMemberResult).hasSize(15);
            assertThat(savedMemberResult)
                    .extracting(JoinedRentResult::rentParticipantId)
                    .doesNotContainAnyElementsOf(otherMemberResult.stream()
                            .map(JoinedRentResult::rentParticipantId)
                            .toList());
        }
    }

    @Nested
    @DisplayName("getJoinedRentDetail 테스트")
    class Describe_getJoinedRentDetail {

        private Long savedRentId;
        private RentJoinCommand savedJoinRequest;
        private static final LocalDate TARGET_DATE = LocalDate.of(2030, 12, 1);

        @BeforeEach
        void setUp() {
            Member hostMember = memberRepository.save(MemberFixture.createTestMemberWithIndex(20));
            savedRentId = rentCommandService.register(buildRegisterRequest(), hostMember.getId());
            savedJoinRequest = buildJoinRequest(savedRentId, TARGET_DATE, 2);
            rentCommandService.join(savedJoinRequest, savedMember.getId());
        }

        @Nested
        @DisplayName("본인 참여 내역 상세 조회 시")
        class Context_valid_request {

            @Test
            @DisplayName("참여 상세가 반환된다")
            void it_returns_participant_detail() {
                // when
                RentParticipantResult result =
                        rentService.getJoinedRentDetail(savedMember.getId(), TARGET_DATE, savedRentId);

                // then
                assertThat(result.depositorName()).isEqualTo(savedJoinRequest.depositorName());
                assertThat(result.passengerNum()).isEqualTo(2);
            }
        }

        @Nested
        @DisplayName("존재하지 않는 참여 내역 조회 시")
        class Context_not_found {

            @Test
            @DisplayName("NOT_FOUND 예외가 발생한다")
            void it_throws_not_found() {
                // when & then
                assertThatThrownBy(() -> rentService.getJoinedRentDetail(savedMember.getId(), TARGET_DATE, 999L))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(RentErrorCode.RENT_NOT_FOUND.getMessage());
            }
        }

        @Nested
        @DisplayName("타인의 참여 내역 조회 시")
        class Context_other_member_access {

            @Test
            @DisplayName("NOT_FOUND 예외가 발생한다")
            void it_throws_not_found_for_other_member() {
                // given
                Member otherMember = memberRepository.save(MemberFixture.createOtherTestMember());

                // when & then
                assertThatThrownBy(() -> rentService.getJoinedRentDetail(otherMember.getId(), TARGET_DATE, savedRentId))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(RentErrorCode.RENT_NOT_FOUND.getMessage());
            }
        }
    }
}
