package com.backend.allreva.module.recruitment.rent.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import com.backend.allreva.common.storage.upload.StorageUploadService;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.fixture.ConcertFixture;
import com.backend.allreva.module.concert.concert.infra.jpa.ConcertJpaRepository;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.member.domain.MemberRepository;
import com.backend.allreva.module.member.fixture.MemberFixture;
import com.backend.allreva.module.recruitment.rent.application.RentService;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@DisplayName("Rent 동시성 테스트")
@SuppressWarnings("NonAsciiCharacters")
class RentConcurrencyTest extends IntegrationTestSupport {

    private static final int THREAD_COUNT = 5;
    private static final int RECRUITMENT_COUNT = 3;
    private static final LocalDate BOARDING_DATE = LocalDate.of(2030, 12, 1);

    @Autowired
    private RentService rentService;

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

    @MockBean
    private StorageUploadService storageUploadService;

    private List<Member> members;
    private Long rentId;

    @BeforeEach
    void setUp() {
        doNothing().when(storageUploadService).deleteImage(any());

        members = List.of(
                memberRepository.save(MemberFixture.createTestMember()),
                memberRepository.save(MemberFixture.createTestMember()),
                memberRepository.save(MemberFixture.createTestMember()),
                memberRepository.save(MemberFixture.createTestMember()),
                memberRepository.save(MemberFixture.createTestMember()));

        Concert concert = concertJpaRepository.save(ConcertFixture.createTestConcert());
        rentId = rentService.registerRent(
                RentFixture.createRentRegisterRequest(concert.getId(), List.of(BOARDING_DATE), RECRUITMENT_COUNT),
                members.get(0).getId());
    }

    @AfterEach
    void tearDown() {
        rentParticipantJpaRepository.deleteAll();
        rentBoardingSlotJpaRepository.deleteAll();
        rentJpaRepository.deleteAll();
        concertJpaRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("5명이 동시에 신청하면 정원(3명)만 성공해야 한다")
    void 동시_신청_시_정원_초과_방지() throws InterruptedException {
        // given
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch ready = new CountDownLatch(THREAD_COUNT);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(THREAD_COUNT);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < THREAD_COUNT; i++) {
            final Long memberId = members.get(i).getId();
            executor.submit(() -> {
                ready.countDown();
                try {
                    start.await(); // 모든 스레드가 동시에 출발
                    rentService.applyRent(RentFixture.createRentJoinRequest(rentId, BOARDING_DATE, 1), memberId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await(); // 모든 스레드 준비 대기
        start.countDown(); // 동시 출발
        done.await(); // 모든 스레드 완료 대기
        executor.shutdown();

        // then
        var slot = rentBoardingSlotJpaRepository
                .findByRentIdAndDate(rentId, BOARDING_DATE)
                .orElseThrow();

        System.out.println("failCount: " + failCount.get());
        System.out.println("successCount: " + successCount.get());

        assertThat(successCount.get()).isEqualTo(RECRUITMENT_COUNT);
        assertThat(slot.getPassengerCount()).isEqualTo(RECRUITMENT_COUNT);
    }
}
