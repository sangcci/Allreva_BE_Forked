package com.backend.allreva.module.recruitment.rent.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.storage.upload.StorageUploadService;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.fixture.ConcertFixture;
import com.backend.allreva.module.concert.concert.infra.jpa.ConcertJpaRepository;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.member.domain.MemberRepository;
import com.backend.allreva.module.member.fixture.MemberFixture;
import com.backend.allreva.module.recruitment.rent.application.RentService;
import com.backend.allreva.module.recruitment.rent.application.dto.RentDetailResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentSummaryResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.SortType;
import com.backend.allreva.module.recruitment.rent.domain.RentRepository;
import com.backend.allreva.module.recruitment.rent.domain.participant.RentParticipantRepository;
import com.backend.allreva.module.recruitment.rent.exception.RentErrorCode;
import com.backend.allreva.module.recruitment.rent.fixture.RentFixture;
import com.backend.allreva.module.recruitment.rent.infra.jpa.RentBoardingSlotJpaRepository;
import com.backend.allreva.module.recruitment.rent.infra.jpa.RentJpaRepository;
import com.backend.allreva.module.recruitment.rent.infra.jpa.RentParticipantJpaRepository;
import com.backend.allreva.support.IntegrationTestSupport;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@DisplayName("Rent Integration 테스트")
class RentIntegrationTest extends IntegrationTestSupport {

    private static final List<LocalDate> BOARDING_DATES = List.of(LocalDate.of(2030, 12, 1), LocalDate.of(2030, 12, 2));

    @Autowired
    private RentService rentService;

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private RentParticipantRepository rentParticipantRepository;

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

    @MockBean
    private StorageUploadService storageUploadService;

    private Member savedMember;
    private Long concertId;

    @BeforeEach
    void setUp() {
        savedMember = memberRepository.save(MemberFixture.createTestMember());
        Concert concert = concertJpaRepository.save(ConcertFixture.createTestConcert());
        concertId = concert.getId();
        doNothing().when(storageUploadService).deleteImage(any());
    }

    @AfterEach
    void tearDown() {
        rentParticipantJpaRepository.deleteAll();
        rentBoardingSlotJpaRepository.deleteAll();
        rentJpaRepository.deleteAll();
        concertJpaRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Nested
    @DisplayName("registerRent 테스트")
    class Describe_registerRent {

        @Nested
        @DisplayName("유효한 요청으로 차대절 등록 시")
        class Context_valid_request {

            private Long savedRentId;

            @BeforeEach
            void setUp() {
                savedRentId = rentService.registerRent(
                        RentFixture.createRentRegisterRequest(concertId, BOARDING_DATES), savedMember.getId());
            }

            @Test
            @DisplayName("차대절이 저장된다")
            void it_saves_rent() {
                var rent = rentRepository.findById(savedRentId).orElseThrow();
                assertThat(rent.getTitle()).isEqualTo("테스트 차대절");
                assertThat(rent.getMemberId()).isEqualTo(savedMember.getId());
                assertThat(rent.getConcertId()).isEqualTo(concertId);
            }

            @Test
            @DisplayName("탑승 날짜 슬롯이 생성된다")
            void it_creates_boarding_slots() {
                var slots = rentBoardingSlotJpaRepository.findAllByRent_Id(savedRentId);
                assertThat(slots).hasSize(2);
                slots.forEach(slot -> {
                    assertThat(slot.getRecruitmentCount()).isEqualTo(30);
                    assertThat(slot.getPassengerCount()).isEqualTo(0);
                });
            }
        }
    }

    @Nested
    @DisplayName("updateRent 테스트")
    class Describe_updateRent {

        private Long savedRentId;

        @BeforeEach
        void setUp() {
            savedRentId = rentService.registerRent(
                    RentFixture.createRentRegisterRequest(concertId, BOARDING_DATES), savedMember.getId());
        }

        @Nested
        @DisplayName("본인 차대절 수정 시")
        class Context_own_rent {

            private static final List<LocalDate> NEW_DATES = List.of(LocalDate.of(2030, 12, 1));

            @BeforeEach
            void setUp() {
                rentService.updateRent(
                        RentFixture.createRentUpdateRequest(savedRentId, NEW_DATES), savedMember.getId());
            }

            @Test
            @DisplayName("차대절 필드가 수정된다")
            void it_updates_rent_fields() {
                var rent = rentRepository.findById(savedRentId).orElseThrow();
                assertThat(rent.getUpRoute().getBoardingArea()).isEqualTo("부산역 앞");
                assertThat(rent.getUpRoute().getTime()).isEqualTo("09:00");
            }

            @Test
            @DisplayName("탑승 날짜 슬롯이 교체된다")
            void it_replaces_boarding_slots() {
                var slots = rentBoardingSlotJpaRepository.findAllByRent_Id(savedRentId);
                assertThat(slots).hasSize(1);
                assertThat(slots.get(0).getDate()).isEqualTo(LocalDate.of(2030, 12, 1));
                assertThat(slots.get(0).getRecruitmentCount()).isEqualTo(20);
            }
        }

        @Nested
        @DisplayName("타인의 차대절 수정 시")
        class Context_other_member {

            private Member otherMember;

            @BeforeEach
            void setUp() {
                otherMember = memberRepository.save(MemberFixture.createTestMember());
            }

            @Test
            @DisplayName("접근 거부 예외가 발생한다")
            void it_throws_access_denied() {
                assertThatThrownBy(() -> rentService.updateRent(
                                RentFixture.createRentUpdateRequest(savedRentId, BOARDING_DATES), otherMember.getId()))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(RentErrorCode.RENT_ACCESS_DENIED.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("closeRent 테스트")
    class Describe_closeRent {

        private Long savedRentId;

        @BeforeEach
        void setUp() {
            savedRentId = rentService.registerRent(
                    RentFixture.createRentRegisterRequest(concertId, BOARDING_DATES), savedMember.getId());
        }

        @Nested
        @DisplayName("본인 차대절 마감 시")
        class Context_own_rent {

            @Test
            @DisplayName("차대절이 마감된다")
            void it_closes_rent() {
                rentService.closeRent(RentFixture.createRentIdRequest(savedRentId), savedMember.getId());
                var rent = rentRepository.findById(savedRentId).orElseThrow();
                assertThat(rent.isClosed()).isTrue();
            }
        }

        @Nested
        @DisplayName("타인의 차대절 마감 시")
        class Context_other_member {

            @Test
            @DisplayName("접근 거부 예외가 발생한다")
            void it_throws_access_denied() {
                Member otherMember = memberRepository.save(MemberFixture.createTestMember());
                assertThatThrownBy(() -> rentService.closeRent(
                                RentFixture.createRentIdRequest(savedRentId), otherMember.getId()))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(RentErrorCode.RENT_ACCESS_DENIED.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("deleteRent 테스트")
    class Describe_deleteRent {

        private Long savedRentId;

        @BeforeEach
        void setUp() {
            savedRentId = rentService.registerRent(
                    RentFixture.createRentRegisterRequest(concertId, BOARDING_DATES), savedMember.getId());
        }

        @Nested
        @DisplayName("본인 차대절 삭제 시")
        class Context_own_rent {

            @Test
            @DisplayName("차대절이 소프트 삭제된다")
            void it_soft_deletes_rent() {
                rentService.deleteRent(RentFixture.createRentIdRequest(savedRentId), savedMember.getId());
                assertThat(rentRepository.findById(savedRentId)).isEmpty();
            }

            @Test
            @DisplayName("이미지 삭제가 호출된다")
            void it_deletes_image() {
                rentService.deleteRent(RentFixture.createRentIdRequest(savedRentId), savedMember.getId());
                verify(storageUploadService).deleteImage(any());
            }
        }

        @Nested
        @DisplayName("타인의 차대절 삭제 시")
        class Context_other_member {

            @Test
            @DisplayName("접근 거부 예외가 발생한다")
            void it_throws_access_denied() {
                Member otherMember = memberRepository.save(MemberFixture.createTestMember());
                assertThatThrownBy(() -> rentService.deleteRent(
                                RentFixture.createRentIdRequest(savedRentId), otherMember.getId()))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(RentErrorCode.RENT_ACCESS_DENIED.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("applyRent 테스트")
    class Describe_applyRent {

        private Long savedRentId;
        private static final LocalDate TARGET_DATE = LocalDate.of(2030, 12, 1);

        @BeforeEach
        void setUp() {
            savedRentId = rentService.registerRent(
                    RentFixture.createRentRegisterRequest(concertId, BOARDING_DATES), savedMember.getId());
        }

        @Nested
        @DisplayName("좌석이 남아 있는 경우")
        class Context_seats_available {

            @Test
            @DisplayName("참여자가 저장된다")
            void it_saves_participant() {
                Long participantId = rentService.joinRent(
                        RentFixture.createRentJoinRequest(savedRentId, TARGET_DATE, 2), savedMember.getId());
                assertThat(participantId).isNotNull();
            }

            @Test
            @DisplayName("슬롯의 탑승 인원이 증가한다")
            void it_increases_passenger_count() {
                rentService.joinRent(
                        RentFixture.createRentJoinRequest(savedRentId, TARGET_DATE, 3), savedMember.getId());
                var slot = rentBoardingSlotJpaRepository
                        .findByRent_IdAndDate(savedRentId, TARGET_DATE)
                        .orElseThrow();
                assertThat(slot.getPassengerCount()).isEqualTo(3);
            }
        }

        @Nested
        @DisplayName("이미 신청한 경우")
        class Context_already_applied {

            @BeforeEach
            void setUp() {
                rentService.joinRent(
                        RentFixture.createRentJoinRequest(savedRentId, TARGET_DATE, 2), savedMember.getId());
            }

            @Test
            @DisplayName("중복 신청 예외가 발생한다")
            void it_throws_already_exists() {
                assertThatThrownBy(() -> rentService.joinRent(
                                RentFixture.createRentJoinRequest(savedRentId, TARGET_DATE, 1), savedMember.getId()))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(RentErrorCode.RENT_JOIN_ALREADY_EXISTS.getMessage());
            }
        }

        @Nested
        @DisplayName("좌석이 초과된 경우")
        class Context_seats_exceeded {

            @Test
            @DisplayName("슬롯 초과 예외가 발생한다")
            void it_throws_slot_full() {
                assertThatThrownBy(() -> rentService.joinRent(
                                RentFixture.createRentJoinRequest(savedRentId, TARGET_DATE, 31), savedMember.getId()))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(RentErrorCode.SLOT_FULL.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("getRentDetail 테스트")
    class Describe_getRentDetail {

        private Long savedRentId;

        @BeforeEach
        void setUp() {
            savedRentId = rentService.registerRent(
                    RentFixture.createRentRegisterRequest(concertId, BOARDING_DATES), savedMember.getId());
        }

        @Nested
        @DisplayName("여러 탑승 날짜가 있는 경우")
        class Context_multiple_dates {

            @Test
            @DisplayName("모든 탑승 날짜가 반환된다")
            void it_returns_all_boarding_dates() {
                RentDetailResponse detail = rentService.getRentDetail(savedRentId);
                assertThat(detail.boardingDates()).hasSize(2);
            }
        }

        @Nested
        @DisplayName("존재하지 않는 차대절 조회 시")
        class Context_not_found {

            @Test
            @DisplayName("NOT_FOUND 예외가 발생한다")
            void it_throws_not_found() {
                assertThatThrownBy(() -> rentService.getRentDetail(999L))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(RentErrorCode.RENT_NOT_FOUND.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("getRentSummaries 테스트")
    class Describe_getRentSummaries {

        @Test
        @DisplayName("마감된 차대절은 목록에 포함되지 않는다")
        void it_excludes_closed_rents() {
            Long rentId = rentService.registerRent(
                    RentFixture.createRentRegisterRequest(concertId, BOARDING_DATES), savedMember.getId());
            rentService.closeRent(RentFixture.createRentIdRequest(rentId), savedMember.getId());

            List<RentSummaryResponse> summaries = rentService.getRentSummaries(null, SortType.LATEST, null, null, 10);
            assertThat(summaries).isEmpty();
        }
    }
}
