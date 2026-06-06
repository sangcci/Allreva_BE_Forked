package com.backend.allreva.concert.concert.kopis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.backend.allreva.concert.concert.domain.Concert;
import com.backend.allreva.concert.concert.domain.ConcertStatus;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("KopisConcertMapper 단위 테스트")
class KopisConcertMapperTest {

    private static final String CONCERT_DETAIL_XML = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <dbs>
                <db>
                    <mt20id>PF001</mt20id>
                    <prfnm>진행 중인 공연</prfnm>
                    <prfpdfrom>2026.01.01</prfpdfrom>
                    <prfpdto>2026.12.31</prfpdto>
                    <prfcast>홍길동, 김철수</prfcast>
                    <poster>http://example.com/poster.jpg</poster>
                    <pcseguidance>R석 150,000원</pcseguidance>
                    <prfstate>공연중</prfstate>
                    <dtguidance>토(19:00)</dtguidance>
                    <entrpsnmH>테스트 기획사</entrpsnmH>
                    <styurls>
                        <styurl>http://example.com/detail1.jpg</styurl>
                        <styurl>http://example.com/detail2.jpg</styurl>
                    </styurls>
                    <relates>
                        <relate>
                            <relatenm>인터파크</relatenm>
                            <relateurl>https://tickets.interpark.com</relateurl>
                        </relate>
                    </relates>
                </db>
            </dbs>
            """;

    private static final String CONCERT_DETAIL_WITHOUT_OPTIONAL_VALUES_XML = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <dbs>
                <db>
                    <mt20id>PF002</mt20id>
                    <prfnm>판매처 없는 공연</prfnm>
                    <prfpdfrom>2026.01.01</prfpdfrom>
                    <prfpdto>2026.12.31</prfpdto>
                    <poster>http://example.com/poster.jpg</poster>
                    <pcseguidance>무료</pcseguidance>
                    <prfstate>공연예정</prfstate>
                    <dtguidance>일(15:00)</dtguidance>
                    <entrpsnmH>테스트 기획사</entrpsnmH>
                </db>
            </dbs>
            """;

    private final XmlMapper xmlMapper = new XmlMapper();
    private final KopisConcertMapper mapper = new KopisConcertMapper();

    @Nested
    @DisplayName("toConcert 메서드는")
    class Describe_toConcert {

        @Test
        void KOPIS_공연_상세_응답을_공연_도메인으로_변환한다() throws Exception {
            KopisConcertDetailResponse response =
                    xmlMapper.readValue(CONCERT_DETAIL_XML, KopisConcertDetailResponse.class);

            Concert result = mapper.toConcert("FC001114-1", response);

            assertSoftly(softly -> {
                softly.assertThat(result.getConcertCode()).isEqualTo("PF001");
                softly.assertThat(result.getHallCode()).isEqualTo("FC001114-1");
                softly.assertThat(result.getConcertInfo().getTitle()).isEqualTo("진행 중인 공연");
                softly.assertThat(result.getConcertInfo().getHost()).isEqualTo("테스트 기획사");
                softly.assertThat(result.getConcertInfo().getPrice()).isEqualTo("R석 150,000원");
                softly.assertThat(result.getConcertInfo().getPerformStatus()).isEqualTo(ConcertStatus.IN_PROGRESS);
                softly.assertThat(result.getConcertInfo().getDateInfo().getStartDate())
                        .isEqualTo(LocalDate.of(2026, 1, 1));
                softly.assertThat(result.getConcertInfo().getDateInfo().getEndDate())
                        .isEqualTo(LocalDate.of(2026, 12, 31));
                softly.assertThat(result.getConcertInfo().getDateInfo().getTimeTable())
                        .isEqualTo("토(19:00)");
                softly.assertThat(result.getPoster().getUrl()).isEqualTo("http://example.com/poster.jpg");
                softly.assertThat(result.getDetailImages())
                        .extracting("url")
                        .containsExactly("http://example.com/detail1.jpg", "http://example.com/detail2.jpg");
                softly.assertThat(result.getSellers()).hasSize(1);
                softly.assertThat(result.getCastNames()).containsExactly("홍길동", "김철수");
            });
        }

        @Test
        void 소개이미지와_판매처와_출연진이_없으면_빈_컬렉션으로_변환한다() throws Exception {
            KopisConcertDetailResponse response =
                    xmlMapper.readValue(CONCERT_DETAIL_WITHOUT_OPTIONAL_VALUES_XML, KopisConcertDetailResponse.class);

            Concert result = mapper.toConcert("FC001114-1", response);

            assertThat(result.getDetailImages()).isEmpty();
            assertThat(result.getSellers()).isEmpty();
            assertThat(result.getCastNames()).isEmpty();
        }
    }
}
