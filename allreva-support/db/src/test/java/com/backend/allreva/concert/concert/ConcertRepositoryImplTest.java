package com.backend.allreva.concert.concert;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.concert.concert.domain.Concert;
import com.backend.allreva.concert.concert.domain.Seller;
import com.backend.allreva.concert.concert.fixture.ConcertFixture;
import com.backend.allreva.support.DataJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import(ConcertRepositoryImpl.class)
@DisplayName("ConcertRepositoryImpl 테스트")
class ConcertRepositoryImplTest extends DataJpaTestSupport {

    @Autowired
    private ConcertRepositoryImpl concertRepository;

    @Test
    @DisplayName("공연을 저장하고 id로 조회한다")
    void save_and_find_by_id() {
        // given
        Concert concert = ConcertFixture.createConcert();

        // when
        concertRepository.save(concert);
        entityManager.flush();
        entityManager.clear();
        Concert found = concertRepository.findById(ConcertFixture.CONCERT_CODE).orElseThrow();

        // then
        assertSoftly(softly -> {
            softly.assertThat(found.getConcertCode()).isEqualTo(ConcertFixture.CONCERT_CODE);
            softly.assertThat(found.getConcertInfo().getTitle()).isEqualTo(ConcertFixture.TITLE);
            softly.assertThat(found.getPoster().getUrl()).isEqualTo(ConcertFixture.POSTER_URL);
            softly.assertThat(found.getDetailImages()).extracting(Image::getUrl).containsExactly("detail-1.jpg");
            softly.assertThat(found.getSellers()).extracting(Seller::getName).containsExactly("예매처");
        });
    }
}
