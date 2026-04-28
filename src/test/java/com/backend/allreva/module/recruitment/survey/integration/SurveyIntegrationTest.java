package com.backend.allreva.module.recruitment.survey.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.domain.value.DateInfo;
import com.backend.allreva.module.concert.concert.fixture.ConcertFixture;
import com.backend.allreva.module.concert.concert.infra.jpa.ConcertJpaRepository;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.member.domain.MemberRepository;
import com.backend.allreva.module.member.fixture.MemberFixture;
import com.backend.allreva.module.recruitment.survey.application.SurveyService;
import com.backend.allreva.module.recruitment.survey.application.dto.JoinSurveyRequest;
import com.backend.allreva.module.recruitment.survey.application.dto.OpenSurveyRequest;
import com.backend.allreva.module.recruitment.survey.application.dto.SortType;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveyDetailResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveyIdRequest;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveySummaryResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.UpdateSurveyRequest;
import com.backend.allreva.module.recruitment.survey.domain.SurveyRepository;
import com.backend.allreva.module.recruitment.survey.domain.participant.SurveyParticipantRepository;
import com.backend.allreva.module.recruitment.survey.domain.value.Region;
import com.backend.allreva.module.recruitment.survey.exception.SurveyErrorCode;
import com.backend.allreva.module.recruitment.survey.fixture.SurveyFixture;
import com.backend.allreva.module.recruitment.survey.infra.jpa.SurveyJpaRepository;
import com.backend.allreva.module.recruitment.survey.infra.jpa.SurveyParticipantJpaRepository;
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

@DisplayName("Survey Integration 테스트")
class SurveyIntegrationTest extends IntegrationTestSupport {

    private static final List<LocalDate> BOARDING_DATES = List.of(LocalDate.of(2030, 12, 1), LocalDate.of(2030, 12, 2));

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private SurveyParticipantRepository surveyParticipantRepository;

    @Autowired
    private SurveyJpaRepository surveyJpaRepository;

    @Autowired
    private SurveyParticipantJpaRepository surveyParticipantJpaRepository;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member savedMember;
    private String concertCode;

    @BeforeEach
    void setUp() {
        savedMember = memberRepository.save(MemberFixture.createTestMember());
        Concert concert = concertJpaRepository.save(Instancio.of(ConcertFixture.inProgressConcertModel())
                .set(field(DateInfo.class, "startDate"), LocalDate.of(2030, 11, 1))
                .set(field(DateInfo.class, "endDate"), LocalDate.of(2030, 12, 31))
                .create());
        concertCode = concert.getConcertCode();
    }

    @AfterEach
    void tearDown() {
        surveyParticipantJpaRepository.deleteAll();
        surveyJpaRepository.deleteAll();
        concertJpaRepository.deleteAll();
        jdbcTemplate.execute("DELETE FROM member");
    }

    private OpenSurveyRequest buildOpenRequest() {
        return Instancio.of(SurveyFixture.openSurveyRequestModel())
                .set(field(OpenSurveyRequest.class, "concertCode"), concertCode)
                .set(field(OpenSurveyRequest.class, "boardingDates"), BOARDING_DATES)
                .create();
    }

    private JoinSurveyRequest buildJoinRequest(final Long surveyId, final LocalDate boardingDate) {
        return Instancio.of(SurveyFixture.joinSurveyRequestModel())
                .set(field(JoinSurveyRequest.class, "surveyId"), surveyId)
                .set(field(JoinSurveyRequest.class, "boardingDate"), boardingDate)
                .create();
    }

    @Nested
    @DisplayName("openSurvey 테스트")
    class Describe_openSurvey {

        @Nested
        @DisplayName("유효한 요청으로 수요조사 개설 시")
        class Context_valid_request {

            private Long savedSurveyId;
            private OpenSurveyRequest savedOpenRequest;

            @BeforeEach
            void setUp() {
                savedOpenRequest = buildOpenRequest();
                savedSurveyId = surveyService.openSurvey(savedMember.getId(), savedOpenRequest);
            }

            @Test
            @DisplayName("수요조사가 저장된다")
            void it_saves_survey() {
                // when
                var survey = surveyRepository.findById(savedSurveyId).orElseThrow();

                // then
                assertThat(survey.getTitle()).isEqualTo(savedOpenRequest.title());
            }

            @Test
            @DisplayName("탑승 날짜가 JSONB로 저장된다")
            void it_saves_boarding_dates_as_jsonb() {
                // when
                var survey = surveyRepository.findById(savedSurveyId).orElseThrow();

                // then
                assertThat(survey.getBoardingDates()).hasSize(2);
                assertThat(survey.getBoardingDates()).containsAll(BOARDING_DATES);
            }
        }
    }

    @Nested
    @DisplayName("updateSurvey 테스트")
    class Describe_updateSurvey {

        private Long savedSurveyId;

        @BeforeEach
        void setUp() {
            savedSurveyId = surveyService.openSurvey(savedMember.getId(), buildOpenRequest());
        }

        @Nested
        @DisplayName("본인 수요조사 수정 시")
        class Context_own_survey {

            @Test
            @DisplayName("수요조사가 수정된다")
            void it_updates_survey() {
                // given
                UpdateSurveyRequest updateRequest = Instancio.of(SurveyFixture.updateSurveyRequestModel())
                        .set(field(UpdateSurveyRequest.class, "surveyId"), savedSurveyId)
                        .set(field(UpdateSurveyRequest.class, "boardingDates"), BOARDING_DATES)
                        .create();

                // when
                surveyService.updateSurvey(savedMember.getId(), updateRequest);

                // then
                var survey = surveyRepository.findById(savedSurveyId).orElseThrow();
                assertThat(survey.getTitle()).isEqualTo(updateRequest.title());
            }
        }

        @Nested
        @DisplayName("타인의 수요조사 수정 시")
        class Context_other_member {

            @Test
            @DisplayName("접근 거부 예외가 발생한다")
            void it_throws_access_denied() {
                // given
                Member otherMember = memberRepository.save(MemberFixture.createOtherTestMember());
                UpdateSurveyRequest updateRequest = Instancio.of(SurveyFixture.updateSurveyRequestModel())
                        .set(field(UpdateSurveyRequest.class, "surveyId"), savedSurveyId)
                        .set(field(UpdateSurveyRequest.class, "boardingDates"), BOARDING_DATES)
                        .create();

                // when & then
                assertThatThrownBy(() -> surveyService.updateSurvey(otherMember.getId(), updateRequest))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(SurveyErrorCode.SURVEY_NOT_WRITER.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("removeSurvey 테스트")
    class Describe_deleteSurvey {

        private Long savedSurveyId;

        @BeforeEach
        void setUp() {
            savedSurveyId = surveyService.openSurvey(savedMember.getId(), buildOpenRequest());
        }

        @Nested
        @DisplayName("본인 수요조사 삭제 시")
        class Context_own_survey {

            @Test
            @DisplayName("수요조사가 소프트 삭제된다")
            void it_soft_deletes_survey() {
                // given
                SurveyIdRequest idRequest = Instancio.of(SurveyFixture.surveyIdRequestModel())
                        .set(field(SurveyIdRequest.class, "surveyId"), savedSurveyId)
                        .create();

                // when
                surveyService.removeSurvey(savedMember.getId(), idRequest);

                // then
                assertThat(surveyRepository.findById(savedSurveyId)).isEmpty();
            }
        }

        @Nested
        @DisplayName("타인의 수요조사 삭제 시")
        class Context_other_member {

            @Test
            @DisplayName("접근 거부 예외가 발생한다")
            void it_throws_access_denied() {
                // given
                Member otherMember = memberRepository.save(MemberFixture.createOtherTestMember());
                SurveyIdRequest idRequest = Instancio.of(SurveyFixture.surveyIdRequestModel())
                        .set(field(SurveyIdRequest.class, "surveyId"), savedSurveyId)
                        .create();

                // when & then
                assertThatThrownBy(() -> surveyService.removeSurvey(otherMember.getId(), idRequest))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(SurveyErrorCode.SURVEY_NOT_WRITER.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("joinSurvey 테스트")
    class Describe_joinSurvey {

        private Long savedSurveyId;
        private static final LocalDate TARGET_DATE = LocalDate.of(2030, 12, 1);

        @BeforeEach
        void setUp() {
            savedSurveyId = surveyService.openSurvey(savedMember.getId(), buildOpenRequest());
        }

        @Nested
        @DisplayName("유효한 참여 요청 시")
        class Context_valid_join {

            @Test
            @DisplayName("참여자가 저장된다")
            void it_saves_participant() {
                // when
                Long participantId =
                        surveyService.joinSurvey(savedMember.getId(), buildJoinRequest(savedSurveyId, TARGET_DATE));

                // then
                assertThat(participantId).isNotNull();
            }
        }

        @Nested
        @DisplayName("중복 참여 시")
        class Context_duplicate_join {

            @BeforeEach
            void setUp() {
                surveyService.joinSurvey(savedMember.getId(), buildJoinRequest(savedSurveyId, TARGET_DATE));
            }

            @Test
            @DisplayName("중복 참여 예외가 발생한다")
            void it_throws_already_exists() {
                // when & then
                assertThatThrownBy(() -> surveyService.joinSurvey(
                                savedMember.getId(), buildJoinRequest(savedSurveyId, TARGET_DATE)))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(SurveyErrorCode.SURVEY_JOIN_ALREADY_EXISTS.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("cancelJoin 테스트")
    class Describe_cancelSurveyJoin {

        private Long savedSurveyId;
        private static final LocalDate TARGET_DATE = LocalDate.of(2030, 12, 1);

        @BeforeEach
        void setUp() {
            savedSurveyId = surveyService.openSurvey(savedMember.getId(), buildOpenRequest());
        }

        @Nested
        @DisplayName("본인 참여 취소 시")
        class Context_own_participation {

            @Test
            @DisplayName("참여자가 삭제된다")
            void it_deletes_participant() {
                // given
                Long participantId =
                        surveyService.joinSurvey(savedMember.getId(), buildJoinRequest(savedSurveyId, TARGET_DATE));

                // when
                surveyService.cancelJoin(savedMember.getId(), participantId);

                // then
                assertThat(surveyParticipantRepository.findById(participantId)).isEmpty();
            }
        }

        @Nested
        @DisplayName("타인의 참여 취소 시")
        class Context_other_member {

            @Test
            @DisplayName("접근 거부 예외가 발생한다")
            void it_throws_access_denied() {
                // given
                Long participantId =
                        surveyService.joinSurvey(savedMember.getId(), buildJoinRequest(savedSurveyId, TARGET_DATE));
                Member otherMember = memberRepository.save(MemberFixture.createOtherTestMember());

                // when & then
                assertThatThrownBy(() -> surveyService.cancelJoin(otherMember.getId(), participantId))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(SurveyErrorCode.SURVEY_JOIN_ACCESS_DENIED.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("getSurveyDetail 테스트")
    class Describe_getSurveyDetail {

        private Long savedSurveyId;

        @BeforeEach
        void setUp() {
            savedSurveyId = surveyService.openSurvey(savedMember.getId(), buildOpenRequest());
        }

        @Nested
        @DisplayName("존재하는 수요조사 조회 시")
        class Context_exists {

            @Test
            @DisplayName("상세 정보가 반환된다")
            void it_returns_detail() {
                // when
                SurveyDetailResponse detail = surveyService.findSurveyDetail(savedSurveyId);

                // then
                assertThat(detail).isNotNull();
            }
        }

        @Nested
        @DisplayName("존재하지 않는 수요조사 조회 시")
        class Context_not_found {

            @Test
            @DisplayName("NOT_FOUND 예외가 발생한다")
            void it_throws_not_found() {
                // when & then
                assertThatThrownBy(() -> surveyService.findSurveyDetail(999L))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(SurveyErrorCode.SURVEY_NOT_FOUND.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("getSurveyList 테스트")
    class Describe_getSurveyList {

        @BeforeEach
        void setUp() {
            surveyService.openSurvey(savedMember.getId(), buildOpenRequest());
        }

        @Test
        @DisplayName("지역 필터링이 동작한다")
        void it_filters_by_region() {
            // when
            List<SurveySummaryResponse> seoulResults =
                    surveyService.findSurveyList(Region.서울, SortType.LATEST, null, null, 10);
            List<SurveySummaryResponse> busanResults =
                    surveyService.findSurveyList(Region.부산, SortType.LATEST, null, null, 10);

            // then
            assertThat(seoulResults).isNotEmpty();
            assertThat(busanResults).isEmpty();
        }

        @Test
        @DisplayName("최신순 정렬로 목록이 반환된다")
        void it_sorts_by_latest() {
            // when
            List<SurveySummaryResponse> results = surveyService.findSurveyList(null, SortType.LATEST, null, null, 10);

            // then
            assertThat(results).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("getCreatedSurveyList 테스트")
    class Describe_getCreatedSurveyList {

        @BeforeEach
        void setUp() {
            surveyService.openSurvey(savedMember.getId(), buildOpenRequest());
        }

        @Test
        @DisplayName("개설자의 수요조사 목록이 반환된다")
        void it_returns_own_surveys() {
            // given
            Member otherMember = memberRepository.save(MemberFixture.createOtherTestMember());

            // when
            var ownSurveys = surveyService.findCreatedSurveyList(savedMember.getId(), null, null, 10);
            var otherSurveys = surveyService.findCreatedSurveyList(otherMember.getId(), null, null, 10);

            // then
            assertThat(ownSurveys).isNotEmpty();
            assertThat(otherSurveys).isEmpty();
        }
    }
}
