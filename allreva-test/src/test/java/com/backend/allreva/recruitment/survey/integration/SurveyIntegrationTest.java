package com.backend.allreva.recruitment.survey.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.concert.concert.ConcertEntity;
import com.backend.allreva.concert.concert.ConcertJpaRepository;
import com.backend.allreva.concert.concert.domain.DateInfo;
import com.backend.allreva.concert.concert.fixture.ConcertFixture;
import com.backend.allreva.member.domain.Member;
import com.backend.allreva.member.domain.MemberRepository;
import com.backend.allreva.member.fixture.MemberFixture;
import com.backend.allreva.recruitment.survey.SurveyJpaRepository;
import com.backend.allreva.recruitment.survey.SurveyParticipantJpaRepository;
import com.backend.allreva.recruitment.survey.command.application.SurveyService;
import com.backend.allreva.recruitment.survey.command.input.JoinSurveyCommand;
import com.backend.allreva.recruitment.survey.command.input.OpenSurveyCommand;
import com.backend.allreva.recruitment.survey.command.input.SurveyIdCommand;
import com.backend.allreva.recruitment.survey.command.input.UpdateSurveyCommand;
import com.backend.allreva.recruitment.survey.domain.Region;
import com.backend.allreva.recruitment.survey.domain.SortType;
import com.backend.allreva.recruitment.survey.domain.SurveyErrorCode;
import com.backend.allreva.recruitment.survey.domain.SurveyParticipantRepository;
import com.backend.allreva.recruitment.survey.domain.SurveyRepository;
import com.backend.allreva.recruitment.survey.fixture.SurveyFixture;
import com.backend.allreva.recruitment.survey.query.application.SurveyFinder;
import com.backend.allreva.recruitment.survey.query.model.SurveyDetail;
import com.backend.allreva.recruitment.survey.query.model.SurveySummary;
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
    private SurveyService surveyCommandService;

    @Autowired
    private SurveyFinder surveyQueryService;

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
        var concert = concertJpaRepository.save(ConcertEntity.from(Instancio.of(ConcertFixture.inProgressConcertModel())
                .set(field(DateInfo.class, "startDate"), LocalDate.of(2030, 11, 1))
                .set(field(DateInfo.class, "endDate"), LocalDate.of(2030, 12, 31))
                .create()));
        concertCode = concert.getConcertCode();
    }

    @AfterEach
    void tearDown() {
        surveyParticipantJpaRepository.deleteAll();
        surveyJpaRepository.deleteAll();
        concertJpaRepository.deleteAll();
        jdbcTemplate.execute("DELETE FROM member");
    }

    private OpenSurveyCommand buildOpenRequest() {
        return Instancio.of(SurveyFixture.openSurveyRequestModel())
                .set(field(OpenSurveyCommand.class, "concertCode"), concertCode)
                .set(field(OpenSurveyCommand.class, "boardingDates"), BOARDING_DATES)
                .create();
    }

    private JoinSurveyCommand buildJoinRequest(final Long surveyId, final LocalDate boardingDate) {
        return Instancio.of(SurveyFixture.joinSurveyRequestModel())
                .set(field(JoinSurveyCommand.class, "surveyId"), surveyId)
                .set(field(JoinSurveyCommand.class, "boardingDate"), boardingDate)
                .create();
    }

    @Nested
    @DisplayName("openSurvey 테스트")
    class Describe_openSurvey {

        @Nested
        @DisplayName("유효한 요청으로 수요조사 개설 시")
        class Context_valid_request {

            private Long savedSurveyId;
            private OpenSurveyCommand savedOpenRequest;

            @BeforeEach
            void setUp() {
                savedOpenRequest = buildOpenRequest();
                savedSurveyId = surveyCommandService.open(savedOpenRequest, savedMember.getId());
            }

            @Test
            @DisplayName("수요조사가 저장된다")
            void it_saves_survey() {
                var survey = surveyRepository.findById(savedSurveyId).orElseThrow();
                assertThat(survey.getTitle()).isEqualTo(savedOpenRequest.title());
            }

            @Test
            @DisplayName("탑승 날짜가 JSONB로 저장된다")
            void it_saves_boarding_dates_as_jsonb() {
                var survey = surveyRepository.findById(savedSurveyId).orElseThrow();
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
            savedSurveyId = surveyCommandService.open(buildOpenRequest(), savedMember.getId());
        }

        @Nested
        @DisplayName("본인 수요조사 수정 시")
        class Context_own_survey {

            @Test
            @DisplayName("수요조사가 수정된다")
            void it_updates_survey() {
                UpdateSurveyCommand updateRequest = Instancio.of(SurveyFixture.updateSurveyRequestModel())
                        .set(field(UpdateSurveyCommand.class, "surveyId"), savedSurveyId)
                        .set(field(UpdateSurveyCommand.class, "boardingDates"), BOARDING_DATES)
                        .create();

                surveyCommandService.update(updateRequest, savedMember.getId());

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
                Member otherMember = memberRepository.save(MemberFixture.createOtherTestMember());
                UpdateSurveyCommand updateRequest = Instancio.of(SurveyFixture.updateSurveyRequestModel())
                        .set(field(UpdateSurveyCommand.class, "surveyId"), savedSurveyId)
                        .set(field(UpdateSurveyCommand.class, "boardingDates"), BOARDING_DATES)
                        .create();

                assertThatThrownBy(() -> surveyCommandService.update(updateRequest, otherMember.getId()))
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
            savedSurveyId = surveyCommandService.open(buildOpenRequest(), savedMember.getId());
        }

        @Nested
        @DisplayName("본인 수요조사 삭제 시")
        class Context_own_survey {

            @Test
            @DisplayName("수요조사가 소프트 삭제된다")
            void it_soft_deletes_survey() {
                SurveyIdCommand idRequest = Instancio.of(SurveyFixture.surveyIdRequestModel())
                        .set(field(SurveyIdCommand.class, "surveyId"), savedSurveyId)
                        .create();

                surveyCommandService.delete(idRequest, savedMember.getId());

                assertThat(surveyRepository.findById(savedSurveyId)).isEmpty();
            }
        }

        @Nested
        @DisplayName("타인의 수요조사 삭제 시")
        class Context_other_member {

            @Test
            @DisplayName("접근 거부 예외가 발생한다")
            void it_throws_access_denied() {
                Member otherMember = memberRepository.save(MemberFixture.createOtherTestMember());
                SurveyIdCommand idRequest = Instancio.of(SurveyFixture.surveyIdRequestModel())
                        .set(field(SurveyIdCommand.class, "surveyId"), savedSurveyId)
                        .create();

                assertThatThrownBy(() -> surveyCommandService.delete(idRequest, otherMember.getId()))
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
            savedSurveyId = surveyCommandService.open(buildOpenRequest(), savedMember.getId());
        }

        @Nested
        @DisplayName("유효한 참여 요청 시")
        class Context_valid_join {

            @Test
            @DisplayName("참여자가 저장된다")
            void it_saves_participant() {
                Long participantId =
                        surveyCommandService.join(buildJoinRequest(savedSurveyId, TARGET_DATE), savedMember.getId());

                assertThat(participantId).isNotNull();
            }
        }

        @Nested
        @DisplayName("중복 참여 시")
        class Context_duplicate_join {

            @BeforeEach
            void setUp() {
                surveyCommandService.join(buildJoinRequest(savedSurveyId, TARGET_DATE), savedMember.getId());
            }

            @Test
            @DisplayName("중복 참여 예외가 발생한다")
            void it_throws_already_exists() {
                assertThatThrownBy(() -> surveyCommandService.join(
                                buildJoinRequest(savedSurveyId, TARGET_DATE), savedMember.getId()))
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
            savedSurveyId = surveyCommandService.open(buildOpenRequest(), savedMember.getId());
        }

        @Nested
        @DisplayName("본인 참여 취소 시")
        class Context_own_participation {

            @Test
            @DisplayName("참여자가 삭제된다")
            void it_deletes_participant() {
                Long participantId =
                        surveyCommandService.join(buildJoinRequest(savedSurveyId, TARGET_DATE), savedMember.getId());

                surveyCommandService.cancelJoin(participantId, savedMember.getId());

                assertThat(surveyParticipantRepository.findById(participantId)).isEmpty();
            }
        }

        @Nested
        @DisplayName("타인의 참여 취소 시")
        class Context_other_member {

            @Test
            @DisplayName("접근 거부 예외가 발생한다")
            void it_throws_access_denied() {
                Long participantId =
                        surveyCommandService.join(buildJoinRequest(savedSurveyId, TARGET_DATE), savedMember.getId());
                Member otherMember = memberRepository.save(MemberFixture.createOtherTestMember());

                assertThatThrownBy(() -> surveyCommandService.cancelJoin(participantId, otherMember.getId()))
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
            savedSurveyId = surveyCommandService.open(buildOpenRequest(), savedMember.getId());
        }

        @Nested
        @DisplayName("존재하는 수요조사 조회 시")
        class Context_exists {

            @Test
            @DisplayName("상세 정보가 반환된다")
            void it_returns_detail() {
                SurveyDetail detail = surveyQueryService.findSurveyDetail(savedSurveyId);
                assertThat(detail).isNotNull();
            }
        }

        @Nested
        @DisplayName("존재하지 않는 수요조사 조회 시")
        class Context_not_found {

            @Test
            @DisplayName("NOT_FOUND 예외가 발생한다")
            void it_throws_not_found() {
                assertThatThrownBy(() -> surveyQueryService.findSurveyDetail(999L))
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
            surveyCommandService.open(buildOpenRequest(), savedMember.getId());
        }

        @Test
        @DisplayName("지역 필터링이 동작한다")
        void it_filters_by_region() {
            List<SurveySummary> seoulResults =
                    surveyQueryService.findSurveyList(Region.서울, SortType.LATEST, null, null, 10);
            List<SurveySummary> busanResults =
                    surveyQueryService.findSurveyList(Region.부산, SortType.LATEST, null, null, 10);

            assertThat(seoulResults).isNotEmpty();
            assertThat(busanResults).isEmpty();
        }

        @Test
        @DisplayName("최신순 정렬로 목록이 반환된다")
        void it_sorts_by_latest() {
            List<SurveySummary> results = surveyQueryService.findSurveyList(null, SortType.LATEST, null, null, 10);
            assertThat(results).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("getCreatedSurveyList 테스트")
    class Describe_getCreatedSurveyList {

        @BeforeEach
        void setUp() {
            surveyCommandService.open(buildOpenRequest(), savedMember.getId());
        }

        @Test
        @DisplayName("개설자의 수요조사 목록이 반환된다")
        void it_returns_own_surveys() {
            Member otherMember = memberRepository.save(MemberFixture.createOtherTestMember());

            var ownSurveys = surveyQueryService.findCreatedSurveyList(savedMember.getId(), null, null, 10);
            var otherSurveys = surveyQueryService.findCreatedSurveyList(otherMember.getId(), null, null, 10);

            assertThat(ownSurveys).isNotEmpty();
            assertThat(otherSurveys).isEmpty();
        }
    }
}
