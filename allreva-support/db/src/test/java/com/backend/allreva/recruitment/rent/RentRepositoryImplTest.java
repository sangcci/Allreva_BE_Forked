package com.backend.allreva.recruitment.rent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.backend.allreva.recruitment.rent.domain.BusSize;
import com.backend.allreva.recruitment.rent.domain.Rent;
import com.backend.allreva.recruitment.rent.domain.RentParticipant;
import com.backend.allreva.recruitment.rent.fixture.RentFixture;
import com.backend.allreva.support.DataJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({RentRepositoryImpl.class, RentParticipantRepositoryImpl.class})
@DisplayName("RentRepositoryImpl 테스트")
class RentRepositoryImplTest extends DataJpaTestSupport {

    @Autowired
    private RentRepositoryImpl rentRepository;

    @Autowired
    private RentParticipantRepositoryImpl rentParticipantRepository;

    @Test
    @DisplayName("차대절을 저장하고 탑승 슬롯과 함께 조회한다")
    void save_and_find_by_id_with_boarding_slots() {
        // given
        Rent rent = RentFixture.createRent();

        // when
        Rent saved = rentRepository.save(rent);
        entityManager.flush();
        entityManager.clear();
        Rent found = rentRepository.findById(saved.getId()).orElseThrow();

        // then
        assertSoftly(softly -> {
            softly.assertThat(found.getTitle()).isEqualTo(RentFixture.TITLE);
            softly.assertThat(found.getImage().getUrl()).isEqualTo(RentFixture.IMAGE_URL);
            softly.assertThat(found.getUpRoute().getBoardingArea()).isEqualTo("서울역");
            softly.assertThat(found.getDownRoute().getDropOffArea()).isEqualTo("서울역");
            softly.assertThat(found.getBus().getBusSize()).isEqualTo(BusSize.LARGE);
            softly.assertThat(found.getBoardingSlots()).hasSize(1);
        });
    }

    @Test
    @DisplayName("차대절 참가자를 저장하고 id로 조회한다")
    void save_and_find_participant_by_id() {
        // given
        Rent savedRent = rentRepository.save(RentFixture.createRent());
        RentParticipant participant = RentFixture.createRentParticipant(savedRent.getId());

        // when
        RentParticipant saved = rentParticipantRepository.save(participant);
        entityManager.flush();
        entityManager.clear();
        RentParticipant found =
                rentParticipantRepository.findById(saved.getId()).orElseThrow();

        // then
        assertSoftly(softly -> {
            softly.assertThat(found.getRentId()).isEqualTo(savedRent.getId());
            softly.assertThat(found.getDepositor().getDepositorName()).isEqualTo("입금자");
            softly.assertThat(found.getDepositor().getPhone()).isEqualTo("010-1234-5678");
            softly.assertThat(found.getPassengerNum()).isEqualTo(2);
        });
    }

    @Test
    @DisplayName("차대절 참가자 존재 여부를 조회한다")
    void exists_participant() {
        // given
        Rent savedRent = rentRepository.save(RentFixture.createRent());
        RentParticipant saved = rentParticipantRepository.save(RentFixture.createRentParticipant(savedRent.getId()));
        entityManager.flush();
        entityManager.clear();

        // when
        boolean exists = rentParticipantRepository.exists(2L, savedRent.getId(), saved.getBoardingDate());

        // then
        assertThat(exists).isTrue();
    }
}
