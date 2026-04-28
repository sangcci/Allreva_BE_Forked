package com.backend.allreva.module.concert.concert.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.instancio.Select.field;

import com.backend.allreva.module.concert.concert.application.ConcertSyncScheduler;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.domain.ConcertRepository;
import com.backend.allreva.module.concert.concert.domain.value.ConcertInfo;
import com.backend.allreva.module.concert.concert.fixture.ConcertFixture;
import com.backend.allreva.module.concert.place.application.ConcertHallSyncScheduler;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import com.backend.allreva.module.concert.place.fixture.ConcertHallFixture;
import com.backend.allreva.support.IntegrationTestSupport;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.time.LocalDate;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("Concert Sync 통합 테스트")
class ConcertSyncIntegrationTest extends IntegrationTestSupport {

    private static final String HALL_ID = "FC001114-1";
    private static final String FACILITY_CODE = "FC001114";

    private static final String CONCERT_CODE_LIST_XML = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <dbs>
                <db>
                    <mt20id>%s</mt20id>
                    <prfstate>%s</prfstate>
                </db>
            </dbs>
            """;

    private static final String CONCERT_DETAIL_XML = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <dbs>
                <db>
                    <mt20id>%s</mt20id>
                    <prfnm>%s</prfnm>
                    <prfpdfrom>2026.01.01</prfpdfrom>
                    <prfpdto>2026.12.31</prfpdto>
                    <poster>http://example.com/poster.jpg</poster>
                    <pcseguidance>R석 150,000원</pcseguidance>
                    <prfstate>공연중</prfstate>
                    <dtguidance>토(19:00)</dtguidance>
                    <entrpsnmH>테스트 기획사</entrpsnmH>
                    <styurls>
                        <styurl>http://example.com/detail1.jpg</styurl>
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

    private static final String CONCERT_HALL_DETAIL_XML = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <dbs>
                <db>
                    <fcltynm>KSPO돔</fcltynm>
                    <adres>서울특별시 송파구 올림픽로 424</adres>
                    <la>37.518486</la>
                    <lo>127.013079</lo>
                    <restaurant>Y</restaurant>
                    <cafe>Y</cafe>
                    <store>N</store>
                    <parkbarrier>N</parkbarrier>
                    <restbarrier>N</restbarrier>
                    <runwbarrier>N</runwbarrier>
                    <elevbarrier>N</elevbarrier>
                    <parkinglot>Y</parkinglot>
                    <mt13s>
                        <mt13>
                            <prfplcnm>KSPO돔</prfplcnm>
                            <mt13id>FC001114-1</mt13id>
                            <seatscale>15000</seatscale>
                        </mt13>
                    </mt13s>
                </db>
            </dbs>
            """;

    @Autowired
    private ConcertSyncScheduler concertSyncScheduler;

    @Autowired
    private ConcertHallSyncScheduler concertHallSyncScheduler;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertHallRepository concertHallRepository;

    @AfterEach
    void tearDown() {
        concertRepository.deleteAll();
        concertHallRepository.deleteAll();
        WireMock.reset();
    }

    @Nested
    @DisplayName("공연 정보 동기화")
    class Describe_공연_정보_동기화 {

        @Nested
        @DisplayName("DB에 없는 신규 공연이 API에서 조회될 때")
        class Context_신규_공연 {

            @Test
            @DisplayName("공연 정보를 DB에 저장한다")
            void savesNewConcertToDb() {
                // given
                String concertCode = "PF001";
                concertHallRepository.save(Instancio.of(ConcertHallFixture.concertHallModel())
                        .set(field(ConcertHall.class, "hallCode"), HALL_ID)
                        .create());

                stubFor(get(urlPathEqualTo("/openApi/restful/pblprfr"))
                        .withQueryParam("prfplccd", equalTo(HALL_ID))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/xml;charset=UTF-8")
                                .withBody(CONCERT_CODE_LIST_XML.formatted(concertCode, "공연중"))));

                stubFor(get(urlPathMatching("/openApi/restful/pblprfr/" + concertCode + ".*"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/xml;charset=UTF-8")
                                .withBody(CONCERT_DETAIL_XML.formatted(concertCode, "진행 중인 공연"))));

                // when
                concertSyncScheduler.fetchDailyConcertInfoList(LocalDate.of(2026, 4, 24));

                // then
                Concert saved = concertRepository.findById(concertCode).orElseThrow();
                assertSoftly(softly -> {
                    softly.assertThat(saved.getConcertInfo().getTitle()).isEqualTo("진행 중인 공연");
                    softly.assertThat(saved.getConcertCode()).isEqualTo(concertCode);
                });
            }
        }

        @Nested
        @DisplayName("기존 COMPLETED 공연이 API에서 조회될 때")
        class Context_COMPLETED_공연_스킵 {

            @Test
            @DisplayName("상세를 호출하지 않고 스킵한다")
            void skipsCompletedConcert() {
                // given
                String concertCode = "PF003";
                concertHallRepository.save(Instancio.of(ConcertHallFixture.concertHallModel())
                        .set(field(ConcertHall.class, "hallCode"), HALL_ID)
                        .create());
                concertRepository.save(Instancio.of(ConcertFixture.completedConcertModel())
                        .set(field(Concert.class, "concertCode"), concertCode)
                        .set(field(ConcertInfo.class, "title"), "종료된 공연")
                        .create());

                stubFor(get(urlPathEqualTo("/openApi/restful/pblprfr"))
                        .withQueryParam("prfplccd", equalTo(HALL_ID))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/xml;charset=UTF-8")
                                .withBody(CONCERT_CODE_LIST_XML.formatted(concertCode, "공연완료"))));

                // when
                concertSyncScheduler.fetchDailyConcertInfoList(LocalDate.of(2026, 4, 24));

                // then
                verify(0, getRequestedFor(urlPathMatching("/openApi/restful/pblprfr/" + concertCode + ".*")));
                Concert result = concertRepository.findById(concertCode).orElseThrow();
                assertThat(result.getConcertInfo().getTitle()).isEqualTo("종료된 공연"); // unchanged
            }
        }

        @Nested
        @DisplayName("기존 SCHEDULED 공연이 API에서 IN_PROGRESS로 변경되어 조회될 때")
        class Context_SCHEDULED_에서_IN_PROGRESS로_상태전이 {

            @Test
            @DisplayName("상세를 호출하고 업데이트한다")
            void callsDetailAndUpdatesOnStatusTransition() {
                // given
                String concertCode = "PF004";
                concertHallRepository.save(Instancio.of(ConcertHallFixture.concertHallModel())
                        .set(field(ConcertHall.class, "hallCode"), HALL_ID)
                        .create());
                concertRepository.save(Instancio.of(ConcertFixture.scheduledConcertModel())
                        .set(field(Concert.class, "concertCode"), concertCode)
                        .create());

                stubFor(get(urlPathEqualTo("/openApi/restful/pblprfr"))
                        .withQueryParam("prfplccd", equalTo(HALL_ID))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/xml;charset=UTF-8")
                                .withBody(CONCERT_CODE_LIST_XML.formatted(concertCode, "공연중"))));

                stubFor(get(urlPathMatching("/openApi/restful/pblprfr/" + concertCode + ".*"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/xml;charset=UTF-8")
                                .withBody(CONCERT_DETAIL_XML.formatted(concertCode, "공연중"))));

                // when
                concertSyncScheduler.fetchDailyConcertInfoList(LocalDate.of(2026, 4, 24));

                // then
                verify(1, getRequestedFor(urlPathMatching("/openApi/restful/pblprfr/" + concertCode + ".*")));
                Concert result = concertRepository.findById(concertCode).orElseThrow();
                assertThat(result.getConcertInfo().getTitle()).isEqualTo("공연중");
            }
        }

        @Nested
        @DisplayName("DB에 공연장이 없을 때")
        class Context_공연장_없음 {

            @Test
            @DisplayName("KOPIS API를 호출하지 않는다")
            void doesNotCallApiWhenNoHalls() {
                // given: no halls in DB

                // when
                concertSyncScheduler.fetchDailyConcertInfoList(LocalDate.of(2026, 4, 24));

                // then
                verify(0, getRequestedFor(urlPathEqualTo("/openApi/restful/pblprfr")));
            }
        }
    }

    @Nested
    @DisplayName("공연장 정보 동기화")
    class Describe_공연장_정보_동기화 {

        @Nested
        @DisplayName("KOPIS API에서 공연장 정보가 조회될 때")
        class Context_공연장_정보_조회 {

            @Test
            @DisplayName("공연장 정보를 DB에 업데이트한다")
            void updatesConcertHallInfoInDb() {
                // given
                concertHallRepository.save(Instancio.of(ConcertHallFixture.concertHallModel())
                        .set(field(ConcertHall.class, "hallCode"), HALL_ID)
                        .create());

                String updatedHallXml = """
                        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                        <dbs>
                            <db>
                                <fcltynm>업데이트된 공연장</fcltynm>
                                <adres>서울특별시 송파구 올림픽로 424</adres>
                                <la>37.518486</la>
                                <lo>127.013079</lo>
                                <restaurant>Y</restaurant>
                                <cafe>Y</cafe>
                                <store>N</store>
                                <parkbarrier>N</parkbarrier>
                                <restbarrier>N</restbarrier>
                                <runwbarrier>N</runwbarrier>
                                <elevbarrier>N</elevbarrier>
                                <parkinglot>N</parkinglot>
                                <mt13s>
                                    <mt13>
                                        <prfplcnm>업데이트된 공연장</prfplcnm>
                                        <mt13id>FC001114-1</mt13id>
                                        <seatscale>20000</seatscale>
                                    </mt13>
                                </mt13s>
                            </db>
                        </dbs>
                        """;

                stubFor(get(urlPathMatching("/openApi/restful/prfplc/" + FACILITY_CODE + ".*"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/xml;charset=UTF-8")
                                .withBody(updatedHallXml)));

                // when
                concertHallSyncScheduler.fetchConcertHallInfoList();

                // then
                ConcertHall result = concertHallRepository.findById(HALL_ID).orElseThrow();
                assertSoftly(softly -> {
                    softly.assertThat(result.getName()).isEqualTo("업데이트된 공연장");
                    softly.assertThat(result.getSeatScale()).isEqualTo(20000);
                });
            }
        }

        @Nested
        @DisplayName("API가 hallId가 다른 공연장도 함께 반환할 때")
        class Context_타_공연장_필터링 {

            @Test
            @DisplayName("hallId가 일치하는 공연장만 저장한다")
            void savesOnlyMatchingHalls() {
                // given
                concertHallRepository.save(Instancio.of(ConcertHallFixture.concertHallModel())
                        .set(field(ConcertHall.class, "hallCode"), HALL_ID)
                        .create());

                String multiHallXml = """
                        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                        <dbs>
                            <db>
                                <fcltynm>KSPO돔</fcltynm>
                                <adres>서울특별시 송파구 올림픽로 424</adres>
                                <la>37.518486</la>
                                <lo>127.013079</lo>
                                <restaurant>Y</restaurant>
                                <cafe>Y</cafe>
                                <store>N</store>
                                <parkbarrier>Y</parkbarrier>
                                <restbarrier>N</restbarrier>
                                <runwbarrier>N</runwbarrier>
                                <elevbarrier>N</elevbarrier>
                                <parkinglot>Y</parkinglot>
                                <mt13s>
                                    <mt13>
                                        <prfplcnm>KSPO돔</prfplcnm>
                                        <mt13id>FC001114-1</mt13id>
                                        <seatscale>15000</seatscale>
                                    </mt13>
                                    <mt13>
                                        <prfplcnm>별관</prfplcnm>
                                        <mt13id>FC001114-2</mt13id>
                                        <seatscale>500</seatscale>
                                    </mt13>
                                </mt13s>
                            </db>
                        </dbs>
                        """;

                stubFor(get(urlPathMatching("/openApi/restful/prfplc/" + FACILITY_CODE + ".*"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/xml;charset=UTF-8")
                                .withBody(multiHallXml)));

                // when
                concertHallSyncScheduler.fetchConcertHallInfoList();

                // then
                assertThat(concertHallRepository.findById("FC001114-2")).isEmpty();
                assertThat(concertHallRepository.findById(HALL_ID)).isPresent();
            }
        }

        @Nested
        @DisplayName("DB에 공연장이 없을 때")
        class Context_공연장_없음 {

            @Test
            @DisplayName("KOPIS API를 호출하지 않는다")
            void doesNotCallApiWhenNoHalls() {
                // given: no halls in DB

                // when
                concertHallSyncScheduler.fetchConcertHallInfoList();

                // then
                verify(0, getRequestedFor(urlPathMatching("/openApi/restful/prfplc/.*")));
            }
        }
    }
}
