package com.backend.allreva.concert.concert.kopis;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("KopisCastnameParser 단위 테스트")
class KopisCastnameParserTest {

    @Nested
    @DisplayName("parseCastNames 메서드는")
    class Describe_parseCastNames {

        @Test
        void 콤마로_구분된_문자열을_리스트로_분리한다() {
            List<String> result = KopisCastnameParser.parseCastNames("홍길동, 김철수, 이영희");
            assertThat(result).containsExactly("홍길동", "김철수", "이영희");
        }

        @Test
        void 공백을_trim_처리한다() {
            List<String> result = KopisCastnameParser.parseCastNames("홍길동,  김철수,이영희");
            assertThat(result).containsExactly("홍길동", "김철수", "이영희");
        }

        @Test
        void 단독_출연자도_단일_원소_리스트로_반환한다() {
            List<String> result = KopisCastnameParser.parseCastNames("홍길동");
            assertThat(result).containsExactly("홍길동");
        }

        @Test
        void null_입력은_빈_리스트로_반환한다() {
            assertThat(KopisCastnameParser.parseCastNames(null)).isEmpty();
        }

        @Test
        void 빈_문자열_입력은_빈_리스트로_반환한다() {
            assertThat(KopisCastnameParser.parseCastNames("")).isEmpty();
            assertThat(KopisCastnameParser.parseCastNames("   ")).isEmpty();
        }

        @Test
        void 콤마만_있는_입력은_blank_원소를_제외한다() {
            assertThat(KopisCastnameParser.parseCastNames(",,,")).isEmpty();
            assertThat(KopisCastnameParser.parseCastNames("홍길동,,김철수")).containsExactly("홍길동", "김철수");
        }
    }
}
