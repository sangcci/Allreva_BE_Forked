package com.backend.allreva.concert.place.kopis;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.backend.allreva.concert.place.domain.ConcertHall;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("KopisHallMapper 단위 테스트")
class KopisHallMapperTest {

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
                            <seatscale>15,000</seatscale>
                        </mt13>
                        <mt13>
                            <prfplcnm>보조홀</prfplcnm>
                            <mt13id>FC001114-2</mt13id>
                            <seatscale>500</seatscale>
                        </mt13>
                    </mt13s>
                </db>
            </dbs>
            """;

    private final XmlMapper xmlMapper = new XmlMapper();
    private final KopisHallMapper mapper = new KopisHallMapper();

    @Nested
    @DisplayName("toHalls 메서드는")
    class Describe_toHalls {

        @Test
        void KOPIS_공연장_상세_응답을_공연장_도메인_목록으로_변환한다() throws Exception {
            KopisHallDetailResponse response =
                    xmlMapper.readValue(CONCERT_HALL_DETAIL_XML, KopisHallDetailResponse.class);

            List<ConcertHall> result = mapper.toHalls(response);

            assertSoftly(softly -> {
                softly.assertThat(result).hasSize(2);
                softly.assertThat(result.get(0).getHallCode()).isEqualTo("FC001114-1");
                softly.assertThat(result.get(0).getName()).isEqualTo("KSPO돔");
                softly.assertThat(result.get(0).getSeatScale()).isEqualTo(15000);
                softly.assertThat(result.get(0).getConvenienceInfo().isHasRestaurant())
                        .isTrue();
                softly.assertThat(result.get(0).getConvenienceInfo().isHasCafe())
                        .isTrue();
                softly.assertThat(result.get(0).getConvenienceInfo().isHasStore())
                        .isFalse();
                softly.assertThat(result.get(0).getConvenienceInfo().isHasParkingLot())
                        .isTrue();
                softly.assertThat(result.get(0).getConvenienceInfo().isHasDisabledParking())
                        .isFalse();
                softly.assertThat(result.get(0).getConvenienceInfo().isHasDisabledToilet())
                        .isFalse();
                softly.assertThat(result.get(0).getConvenienceInfo().isHasElevator())
                        .isFalse();
                softly.assertThat(result.get(0).getConvenienceInfo().isHasRunway())
                        .isFalse();
                softly.assertThat(result.get(0).getLocation().getAddress()).isEqualTo("서울특별시 송파구 올림픽로 424");
                softly.assertThat(result.get(0).getLocation().getLatitude()).isEqualTo(37.518486);
                softly.assertThat(result.get(0).getLocation().getLongitude()).isEqualTo(127.013079);
                softly.assertThat(result.get(1).getHallCode()).isEqualTo("FC001114-2");
                softly.assertThat(result.get(1).getName()).isEqualTo("KSPO돔 보조홀");
                softly.assertThat(result.get(1).getSeatScale()).isEqualTo(500);
            });
        }
    }
}
