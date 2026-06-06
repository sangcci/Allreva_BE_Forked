package com.backend.allreva.recruitment.rent.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

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
import com.backend.allreva.recruitment.rent.command.input.RentJoinIdCommand;
import com.backend.allreva.recruitment.rent.command.input.RentJoinUpdateCommand;
import com.backend.allreva.recruitment.rent.command.input.RentRegisterCommand;
import com.backend.allreva.recruitment.rent.command.input.RentUpdateCommand;
import com.backend.allreva.recruitment.rent.domain.RentBoardingSlot;
import com.backend.allreva.recruitment.rent.domain.RentErrorCode;
import com.backend.allreva.recruitment.rent.domain.RentRepository;
import com.backend.allreva.recruitment.rent.fixture.RentFixture;
import com.backend.allreva.support.IntegrationTestSupport;
import java.time.LocalDate;
import java.util.List;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("Rent Command Integration 테스트")
class RentCommandIntegrationTest extends IntegrationTestSupport {

    private static final List<LocalDate> BOARDING_DATES = List.of(LocalDate.of(2030, 12, 1), LocalDate.of(2030, 12, 2));

    @Autowired
    private RentService rentService;

    @Autowired
    private RentRepository rentRepository;

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

    private List<RentBoardingSlot> boardingSlots(final Long rentId) {
        return rentRepository.findById(rentId).orElseThrow().getBoardingSlots();
    }

    private RentBoardingSlot boardingSlot(final Long rentId, final LocalDate boardingDate) {
        return boardingSlots(rentId).stream()
                .filter(slot -> slot.getDate().equals(boardingDate))
                .findFirst()
                .orElseThrow();
    }

    @Nested
    @DisplayName("registerRent 테스트")
    class Describe_registerRent {

        @Nested
        @DisplayName("유효한 요청으로 차대절 등록 시")
        class Context_valid_request {

            private Long savedRentId;
            private RentRegisterCommand savedRequest;

            @BeforeEach
            void setUp() {
                savedRequest = Instancio.of(RentFixture.rentRegisterRequestModel())
                        .set(field(RentRegisterCommand.class, "concertCode"), concertCode)
                        .set(field(RentRegisterCommand.class, "rentBoardingDateRequests"), BOARDING_DATES)
                        .create();
                savedRentId = rentService.register(savedRequest, savedMember.getId());
            }

            @Test
            @DisplayName("차대절이 저장된다")
            void it_saves_rent() {
                // when
                var rent = rentRepository.findById(savedRentId).orElseThrow();

                // then
                assertThat(rent.getTitle()).isEqualTo(savedRequest.title());
                assertThat(rent.getMemberId()).isEqualTo(savedMember.getId());
                assertThat(rent.getConcertCode()).isEqualTo(concertCode);
            }

            @Test
            @DisplayName("탑승 날짜 슬롯이 생성된다")
            void it_creates_boarding_slots() {
                // when
                var slots = boardingSlots(savedRentId);

                // then
                assertThat(slots).hasSize(2);
                slots.forEach(slot -> {
                    assertThat(slot.getRecruitmentCount()).isEqualTo(savedRequest.recruitmentCount());
                    assertThat(slot.getPassengerCount()).isZero();
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
            savedRentId = rentService.register(
                    Instancio.of(RentFixture.rentRegisterRequestModel())
                            .set(field(RentRegisterCommand.class, "concertCode"), concertCode)
                            .set(field(RentRegisterCommand.class, "rentBoardingDateRequests"), BOARDING_DATES)
                            .create(),
                    savedMember.getId());
        }

        @Nested
        @DisplayName("본인 차대절 수정 시")
        class Context_own_rent {

            private static final List<LocalDate> NEW_DATES = List.of(LocalDate.of(2030, 12, 1));
            private RentUpdateCommand updateRequest;

            @BeforeEach
            void setUp() {
                updateRequest = Instancio.of(RentFixture.rentUpdateRequestModel())
                        .set(field(RentUpdateCommand.class, "rentId"), savedRentId)
                        .set(field(RentUpdateCommand.class, "rentBoardingDateRequests"), NEW_DATES)
                        .create();
                rentService.update(updateRequest, savedMember.getId());
            }

            @Test
            @DisplayName("차대절 필드가 수정된다")
            void it_updates_rent_fields() {
                // when
                var rent = rentRepository.findById(savedRentId).orElseThrow();

                // then
                assertThat(rent.getUpRoute().getBoardingArea())
                        .isEqualTo(updateRequest.upRoute().getBoardingArea());
                assertThat(rent.getUpRoute().getTime())
                        .isEqualTo(updateRequest.upRoute().getTime());
            }

            @Test
            @DisplayName("탑승 날짜 슬롯이 교체된다")
            void it_replaces_boarding_slots() {
                // when
                var slots = boardingSlots(savedRentId);

                // then
                assertThat(slots).hasSize(1);
                assertThat(slots.get(0).getDate()).isEqualTo(LocalDate.of(2030, 12, 1));
                assertThat(slots.get(0).getRecruitmentCount()).isEqualTo(updateRequest.recruitmentCount());
            }
        }

        @Nested
        @DisplayName("타인의 차대절 수정 시")
        class Context_other_member {

            private Member otherMember;

            @BeforeEach
            void setUp() {
                otherMember = memberRepository.save(MemberFixture.createOtherTestMember());
            }

            @Test
            @DisplayName("접근 거부 예외가 발생한다")
            void it_throws_access_denied() {
                // given
                var request = Instancio.of(RentFixture.rentUpdateRequestModel())
                        .set(field(RentUpdateCommand.class, "rentId"), savedRentId)
                        .set(field(RentUpdateCommand.class, "rentBoardingDateRequests"), BOARDING_DATES)
                        .create();

                // when & then
                assertThatThrownBy(() -> rentService.update(request, otherMember.getId()))
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
            savedRentId = rentService.register(
                    Instancio.of(RentFixture.rentRegisterRequestModel())
                            .set(field(RentRegisterCommand.class, "concertCode"), concertCode)
                            .set(field(RentRegisterCommand.class, "rentBoardingDateRequests"), BOARDING_DATES)
                            .create(),
                    savedMember.getId());
        }

        @Nested
        @DisplayName("본인 차대절 마감 시")
        class Context_own_rent {

            @Test
            @DisplayName("차대절이 마감된다")
            void it_closes_rent() {
                // when
                rentService.close(
                        Instancio.of(RentFixture.rentIdRequestModel())
                                .set(field(RentIdCommand.class, "rentId"), savedRentId)
                                .create(),
                        savedMember.getId());

                // then
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
                // given
                Member otherMember = memberRepository.save(MemberFixture.createOtherTestMember());
                var request = Instancio.of(RentFixture.rentIdRequestModel())
                        .set(field(RentIdCommand.class, "rentId"), savedRentId)
                        .create();

                // when & then
                assertThatThrownBy(() -> rentService.close(request, otherMember.getId()))
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
            savedRentId = rentService.register(
                    Instancio.of(RentFixture.rentRegisterRequestModel())
                            .set(field(RentRegisterCommand.class, "concertCode"), concertCode)
                            .set(field(RentRegisterCommand.class, "rentBoardingDateRequests"), BOARDING_DATES)
                            .create(),
                    savedMember.getId());
        }

        @Nested
        @DisplayName("본인 차대절 삭제 시")
        class Context_own_rent {

            @Test
            @DisplayName("차대절이 소프트 삭제된다")
            void it_soft_deletes_rent() {
                // when
                rentService.delete(
                        Instancio.of(RentFixture.rentIdRequestModel())
                                .set(field(RentIdCommand.class, "rentId"), savedRentId)
                                .create(),
                        savedMember.getId());

                // then
                assertThat(rentRepository.findById(savedRentId)).isEmpty();
            }

            @Test
            @DisplayName("이미지 삭제가 호출된다")
            void it_deletes_image() {
                // when
                rentService.delete(
                        Instancio.of(RentFixture.rentIdRequestModel())
                                .set(field(RentIdCommand.class, "rentId"), savedRentId)
                                .create(),
                        savedMember.getId());

                // then
                verify(storageWriter).delete(any());
            }
        }

        @Nested
        @DisplayName("타인의 차대절 삭제 시")
        class Context_other_member {

            @Test
            @DisplayName("접근 거부 예외가 발생한다")
            void it_throws_access_denied() {
                // given
                Member otherMember = memberRepository.save(MemberFixture.createOtherTestMember());
                var request = Instancio.of(RentFixture.rentIdRequestModel())
                        .set(field(RentIdCommand.class, "rentId"), savedRentId)
                        .create();

                // when & then
                assertThatThrownBy(() -> rentService.delete(request, otherMember.getId()))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(RentErrorCode.RENT_ACCESS_DENIED.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("joinRent 테스트")
    class Describe_joinRent {

        private Long savedRentId;
        private Member participantMember;
        private static final LocalDate TARGET_DATE = LocalDate.of(2030, 12, 1);

        @BeforeEach
        void setUp() {
            participantMember = memberRepository.save(MemberFixture.createTestMemberWithIndex(101));
            savedRentId = rentService.register(
                    Instancio.of(RentFixture.rentRegisterRequestModel())
                            .set(field(RentRegisterCommand.class, "concertCode"), concertCode)
                            .set(field(RentRegisterCommand.class, "rentBoardingDateRequests"), BOARDING_DATES)
                            .set(field(RentRegisterCommand.class, "recruitmentCount"), 30)
                            .create(),
                    savedMember.getId());
        }

        @Nested
        @DisplayName("좌석이 남아 있는 경우")
        class Context_seats_available {

            @Test
            @DisplayName("참여자가 저장된다")
            void it_saves_participant() {
                // when
                Long participantId = rentService.join(
                        Instancio.of(RentFixture.rentJoinRequestModel())
                                .set(field(RentJoinCommand.class, "rentId"), savedRentId)
                                .set(field(RentJoinCommand.class, "boardingDate"), TARGET_DATE)
                                .set(field(RentJoinCommand.class, "passengerNum"), 2)
                                .create(),
                        participantMember.getId());

                // then
                assertThat(participantId).isNotNull();
            }

            @Test
            @DisplayName("슬롯의 탑승 인원이 증가한다")
            void it_increases_passenger_count() {
                // when
                rentService.join(
                        Instancio.of(RentFixture.rentJoinRequestModel())
                                .set(field(RentJoinCommand.class, "rentId"), savedRentId)
                                .set(field(RentJoinCommand.class, "boardingDate"), TARGET_DATE)
                                .set(field(RentJoinCommand.class, "passengerNum"), 3)
                                .create(),
                        participantMember.getId());

                // then
                var slot = boardingSlot(savedRentId, TARGET_DATE);
                assertThat(slot.getPassengerCount()).isEqualTo(3);
            }
        }

        @Nested
        @DisplayName("이미 신청한 경우")
        class Context_already_applied {

            @BeforeEach
            void setUp() {
                rentService.join(
                        Instancio.of(RentFixture.rentJoinRequestModel())
                                .set(field(RentJoinCommand.class, "rentId"), savedRentId)
                                .set(field(RentJoinCommand.class, "boardingDate"), TARGET_DATE)
                                .set(field(RentJoinCommand.class, "passengerNum"), 2)
                                .create(),
                        participantMember.getId());
            }

            @Test
            @DisplayName("중복 신청 예외가 발생한다")
            void it_throws_already_exists() {
                // given
                var request = Instancio.of(RentFixture.rentJoinRequestModel())
                        .set(field(RentJoinCommand.class, "rentId"), savedRentId)
                        .set(field(RentJoinCommand.class, "boardingDate"), TARGET_DATE)
                        .set(field(RentJoinCommand.class, "passengerNum"), 1)
                        .create();

                // when & then
                assertThatThrownBy(() -> rentService.join(request, participantMember.getId()))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(RentErrorCode.RENT_JOIN_ALREADY_EXISTS.getMessage());
            }
        }

        @Nested
        @DisplayName("주최자가 참석하는 경우")
        class Context_host_join {

            @Test
            @DisplayName("주최자 참석 제한 예외가 발생한다")
            void it_throws_host_cannot_join() {
                // given
                var request = Instancio.of(RentFixture.rentJoinRequestModel())
                        .set(field(RentJoinCommand.class, "rentId"), savedRentId)
                        .set(field(RentJoinCommand.class, "boardingDate"), TARGET_DATE)
                        .set(field(RentJoinCommand.class, "passengerNum"), 1)
                        .create();

                // when & then
                assertThatThrownBy(() -> rentService.join(request, savedMember.getId()))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(RentErrorCode.RENT_HOST_CANNOT_JOIN.getMessage());
            }
        }

        @Nested
        @DisplayName("좌석이 초과된 경우")
        class Context_seats_exceeded {

            @Test
            @DisplayName("슬롯 초과 예외가 발생한다")
            void it_throws_slot_full() {
                // given
                var request = Instancio.of(RentFixture.rentJoinRequestModel())
                        .set(field(RentJoinCommand.class, "rentId"), savedRentId)
                        .set(field(RentJoinCommand.class, "boardingDate"), TARGET_DATE)
                        .set(field(RentJoinCommand.class, "passengerNum"), 31)
                        .create();

                // when & then
                assertThatThrownBy(() -> rentService.join(request, participantMember.getId()))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(RentErrorCode.SLOT_FULL.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("updateRentJoin 테스트")
    class Describe_updateRentJoin {

        private Long savedRentId;
        private Long participantId;
        private Member participantMember;
        private static final LocalDate TARGET_DATE = LocalDate.of(2030, 12, 1);

        @BeforeEach
        void setUp() {
            participantMember = memberRepository.save(MemberFixture.createTestMemberWithIndex(201));
            savedRentId = rentService.register(
                    Instancio.of(RentFixture.rentRegisterRequestModel())
                            .set(field(RentRegisterCommand.class, "concertCode"), concertCode)
                            .set(field(RentRegisterCommand.class, "rentBoardingDateRequests"), BOARDING_DATES)
                            .create(),
                    savedMember.getId());
            participantId = rentService.join(
                    Instancio.of(RentFixture.rentJoinRequestModel())
                            .set(field(RentJoinCommand.class, "rentId"), savedRentId)
                            .set(field(RentJoinCommand.class, "boardingDate"), TARGET_DATE)
                            .set(field(RentJoinCommand.class, "passengerNum"), 2)
                            .create(),
                    participantMember.getId());
        }

        @Nested
        @DisplayName("본인 참여 내역 수정 시")
        class Context_own_participant {

            private RentJoinUpdateCommand updateRequest;

            @BeforeEach
            void setUp() {
                updateRequest = Instancio.of(RentFixture.rentJoinUpdateRequestModel())
                        .set(field(RentJoinUpdateCommand.class, "rentParticipantId"), participantId)
                        .set(field(RentJoinUpdateCommand.class, "boardingDate"), TARGET_DATE)
                        .create();
                rentService.updateJoin(updateRequest, participantMember.getId());
            }

            @Test
            @DisplayName("참여 정보가 수정된다")
            void it_updates_participant_fields() {
                // when
                var participant =
                        rentParticipantJpaRepository.findById(participantId).orElseThrow();

                // then
                assertThat(participant.getDepositorName()).isEqualTo(updateRequest.depositorName());
                assertThat(participant.getPassengerNum()).isEqualTo(updateRequest.passengerNum());
            }
        }

        @Nested
        @DisplayName("타인의 참여 내역 수정 시")
        class Context_other_member {

            private Member otherMember;

            @BeforeEach
            void setUp() {
                otherMember = memberRepository.save(MemberFixture.createOtherTestMember());
            }

            @Test
            @DisplayName("접근 거부 예외가 발생한다")
            void it_throws_access_denied() {
                // given
                var request = Instancio.of(RentFixture.rentJoinUpdateRequestModel())
                        .set(field(RentJoinUpdateCommand.class, "rentParticipantId"), participantId)
                        .set(field(RentJoinUpdateCommand.class, "boardingDate"), TARGET_DATE)
                        .create();

                // when & then
                assertThatThrownBy(() -> rentService.updateJoin(request, otherMember.getId()))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(RentErrorCode.RENT_PARTICIPANT_ACCESS_DENIED.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("cancelRentJoin 테스트")
    class Describe_cancelRentJoin {

        private Long savedRentId;
        private Long participantId;
        private Member participantMember;
        private static final LocalDate TARGET_DATE = LocalDate.of(2030, 12, 1);

        @BeforeEach
        void setUp() {
            participantMember = memberRepository.save(MemberFixture.createTestMemberWithIndex(202));
            savedRentId = rentService.register(
                    Instancio.of(RentFixture.rentRegisterRequestModel())
                            .set(field(RentRegisterCommand.class, "concertCode"), concertCode)
                            .set(field(RentRegisterCommand.class, "rentBoardingDateRequests"), BOARDING_DATES)
                            .create(),
                    savedMember.getId());
            participantId = rentService.join(
                    Instancio.of(RentFixture.rentJoinRequestModel())
                            .set(field(RentJoinCommand.class, "rentId"), savedRentId)
                            .set(field(RentJoinCommand.class, "boardingDate"), TARGET_DATE)
                            .set(field(RentJoinCommand.class, "passengerNum"), 2)
                            .create(),
                    participantMember.getId());
        }

        @Nested
        @DisplayName("본인 참여 취소 시")
        class Context_own_participant {

            @Test
            @DisplayName("참여자가 삭제된다")
            void it_deletes_participant() {
                // given
                var request = Instancio.of(RentFixture.rentJoinIdRequestModel())
                        .set(field(RentJoinIdCommand.class, "rentParticipantId"), participantId)
                        .create();

                // when
                rentService.cancelJoin(request, participantMember.getId());

                // then
                assertThat(rentParticipantJpaRepository.findById(participantId)).isEmpty();
            }

            @Test
            @DisplayName("탑승 슬롯의 passengerCount가 감소한다")
            void it_decreases_passenger_count() {
                // given
                var slotBefore = boardingSlot(savedRentId, TARGET_DATE);
                assertThat(slotBefore.getPassengerCount()).isEqualTo(2);
                var request = Instancio.of(RentFixture.rentJoinIdRequestModel())
                        .set(field(RentJoinIdCommand.class, "rentParticipantId"), participantId)
                        .create();

                // when
                rentService.cancelJoin(request, participantMember.getId());

                // then
                var slotAfter = boardingSlot(savedRentId, TARGET_DATE);
                assertThat(slotAfter.getPassengerCount()).isZero();
            }
        }

        @Nested
        @DisplayName("타인의 참여 취소 시")
        class Context_other_member {

            private Member otherMember;

            @BeforeEach
            void setUp() {
                otherMember = memberRepository.save(MemberFixture.createOtherTestMember());
            }

            @Test
            @DisplayName("접근 거부 예외가 발생한다")
            void it_throws_access_denied() {
                // given
                var request = Instancio.of(RentFixture.rentJoinIdRequestModel())
                        .set(field(RentJoinIdCommand.class, "rentParticipantId"), participantId)
                        .create();

                // when & then
                assertThatThrownBy(() -> rentService.cancelJoin(request, otherMember.getId()))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(RentErrorCode.RENT_PARTICIPANT_ACCESS_DENIED.getMessage());
            }
        }
    }
}
