package com.backend.allreva.module.recruitment.rent.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.fixture.ConcertFixture;
import com.backend.allreva.module.concert.concert.infra.jpa.ConcertJpaRepository;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.member.domain.MemberRepository;
import com.backend.allreva.module.member.fixture.MemberFixture;
import com.backend.allreva.module.recruitment.rent.application.command.RentCommandService;
import com.backend.allreva.module.recruitment.rent.application.command.dto.RentJoinRequest;
import com.backend.allreva.module.recruitment.rent.application.command.dto.RentRegisterRequest;
import com.backend.allreva.module.recruitment.rent.fixture.RentFixture;
import com.backend.allreva.module.recruitment.rent.infra.jpa.RentBoardingSlotJpaRepository;
import com.backend.allreva.module.recruitment.rent.infra.jpa.RentJpaRepository;
import com.backend.allreva.module.recruitment.rent.infra.jpa.RentParticipantJpaRepository;
import com.backend.allreva.support.IntegrationTestSupport;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("Rent 동시성 테스트")
@SuppressWarnings("NonAsciiCharacters")
class RentConcurrencyTest extends IntegrationTestSupport {

    private static final int THREAD_COUNT = 5;
    private static final int RECRUITMENT_COUNT = 3;
    private static final LocalDate BOARDING_DATE = LocalDate.of(2030, 12, 1);

    @Autowired
    private RentCommandService rentCommandService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Autowired
    private RentJpaRepository rentJpaRepository;

    @Autowired
    private RentBoardingSlotJpaRepository rentBoardingSlotJpaRepository;

    @Autowired
    private RentParticipantJpaRepository rentParticipantJpaRepository;

    private List<Member> members;
    private Long rentId;

    @BeforeEach
    void setUp() {
        doNothing().when(storageUploadService).deleteImage(any());

        members = List.of(
                memberRepository.save(MemberFixture.createTestMemberWithIndex(1)),
                memberRepository.save(MemberFixture.createTestMemberWithIndex(2)),
                memberRepository.save(MemberFixture.createTestMemberWithIndex(3)),
                memberRepository.save(MemberFixture.createTestMemberWithIndex(4)),
                memberRepository.save(MemberFixture.createTestMemberWithIndex(5)));

        Concert concert = concertJpaRepository.save(
                Instancio.of(ConcertFixture.inProgressConcertModel()).create());
        rentId = rentCommandService.registerRent(
                Instancio.of(RentFixture.rentRegisterRequestModel())
                        .set(field(RentRegisterRequest.class, "concertCode"), concert.getConcertCode())
                        .set(field(RentRegisterRequest.class, "rentBoardingDateRequests"), List.of(BOARDING_DATE))
                        .set(field(RentRegisterRequest.class, "recruitmentCount"), RECRUITMENT_COUNT)
                        .create(),
                members.get(0).getId());
    }

    @AfterEach
    void tearDown() {
        rentParticipantJpaRepository.deleteAll();
        rentBoardingSlotJpaRepository.deleteAll();
        rentJpaRepository.deleteAll();
        concertJpaRepository.deleteAll();
        jdbcTemplate.execute("DELETE FROM member");
    }

    @Test
    @DisplayName("5명이 동시에 신청하면 3명만 성공해야 한다")
    void 동시_신청_시_정원_초과_방지() throws InterruptedException {
        // given
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch ready = new CountDownLatch(THREAD_COUNT);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(THREAD_COUNT);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicInteger unexpectedCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < THREAD_COUNT; i++) {
            final Long memberId = members.get(i).getId();
            executor.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    rentCommandService.joinRent(
                            Instancio.of(RentFixture.rentJoinRequestModel())
                                    .set(field(RentJoinRequest.class, "rentId"), rentId)
                                    .set(field(RentJoinRequest.class, "boardingDate"), BOARDING_DATE)
                                    .set(field(RentJoinRequest.class, "passengerNum"), 1)
                                    .create(),
                            memberId);
                    successCount.incrementAndGet();
                } catch (CustomException e) {
                    failCount.incrementAndGet();
                } catch (Exception e) {
                    unexpectedCount.incrementAndGet();
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await();
        start.countDown();
        done.await();
        executor.shutdown();

        // then
        var slot = rentBoardingSlotJpaRepository
                .findByRent_IdAndDate(rentId, BOARDING_DATE)
                .orElseThrow();

        assertThat(unexpectedCount.get()).isZero();
        assertSoftly(soft -> {
            soft.assertThat(successCount.get()).isEqualTo(RECRUITMENT_COUNT);
            soft.assertThat(failCount.get()).isEqualTo(THREAD_COUNT - RECRUITMENT_COUNT);
            soft.assertThat(slot.getPassengerCount()).isEqualTo(RECRUITMENT_COUNT);
        });
    }
}
