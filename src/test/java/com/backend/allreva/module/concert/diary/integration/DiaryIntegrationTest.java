package com.backend.allreva.module.concert.diary.integration;

import static com.backend.allreva.module.concert.concert.fixture.ConcertFixture.createTestConcert;

import static com.backend.allreva.module.member.fixture.MemberFixture.createTestMember;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.model.Image;
import com.backend.allreva.common.storage.upload.StorageUploadService;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.infra.ConcertRepository;
import com.backend.allreva.module.concert.diary.application.DiaryService;
import com.backend.allreva.module.concert.diary.application.dto.UpdateDiaryRequest;
import com.backend.allreva.module.concert.diary.domain.ConcertDiary;
import com.backend.allreva.module.concert.diary.domain.DiaryRepository;
import com.backend.allreva.module.concert.diary.exception.DiaryErrorCode;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.member.domain.MemberRepository;
import com.backend.allreva.module.member.domain.value.LoginProvider;
import com.backend.allreva.module.member.domain.value.MemberRole;
import com.backend.allreva.support.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("Diary 통합 테스트")
class DiaryIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @MockBean
    private StorageUploadService storageUploadService;

    @AfterEach
    void tearDown() {
        diaryRepository.deleteAll();
        memberRepository.deleteAll();
        concertRepository.deleteAll();
    }

    @Nested
    @DisplayName("공연 기록 수정")
    class Describe_공연_기록_수정 {

        @Nested
        @DisplayName("존재하지 않는 공연 기록을 수정하려고 할 때")
        class Context_존재하지_않는_공연_기록 {

            @Test
            @DisplayName("예외가 발생한다")
            void 예외가_발생한다() {
                // given
                Member member = memberRepository.save(createTestMember());
                Concert concert = concertRepository.save(createTestConcert());
                UpdateDiaryRequest request = new UpdateDiaryRequest(
                        999L,
                        concert.getId(),
                        LocalDate.now(),
                        "episode1",
                        "내용",
                        "A열 1번",
                        List.of(new Image("image1")));

                // when & then
                assertThatThrownBy(() -> diaryService.update(request, member.getId()))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(DiaryErrorCode.DIARY_NOT_FOUND.getMessage());
            }
        }

        @Nested
        @DisplayName("작성자가 아닌 사용자가 수정하려고 할 때")
        class Context_작성자가_아닌_사용자 {

            @Test
            @DisplayName("예외가 발생한다")
            void 예외가_발생한다() {
                // given
                Member owner = memberRepository.save(createTestMember());
                Member other = memberRepository.save(createOtherMember());
                Concert concert = concertRepository.save(createTestConcert());
                ConcertDiary diary = createDiary(owner.getId(), concert.getId());
                ConcertDiary savedDiary = diaryRepository.save(diary);

                UpdateDiaryRequest request = new UpdateDiaryRequest(
                        savedDiary.getId(),
                        concert.getId(),
                        LocalDate.now(),
                        "episode2",
                        "수정된 내용",
                        "B열 2번",
                        List.of(new Image("image2")));

                // when & then
                assertThatThrownBy(() -> diaryService.update(request, other.getId()))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(DiaryErrorCode.DIARY_NOT_WRITER.getMessage());
            }
        }

        @Nested
        @DisplayName("이미지와 함께 수정할 때")
        class Context_이미지_수정 {

            @Test
            @DisplayName("이미지가 정상적으로 교체된다")
            void 이미지가_정상적으로_교체된다() {
                // given
                Member member = memberRepository.save(createTestMember());
                Concert concert = concertRepository.save(createTestConcert());
                ConcertDiary diary = createDiary(member.getId(), concert.getId());
                diary.addImages(List.of(new Image("old-image1"), new Image("old-image2")));
                ConcertDiary savedDiary = diaryRepository.save(diary);

                List<Image> newImages = List.of(new Image("new-image1"), new Image("new-image2"));
                UpdateDiaryRequest request = new UpdateDiaryRequest(
                        savedDiary.getId(),
                        concert.getId(),
                        LocalDate.now(),
                        "episode2",
                        "수정된 내용",
                        "B열 2번",
                        newImages);

                // when
                diaryService.update(request, member.getId());

                // then
                ConcertDiary updatedDiary = diaryRepository.findById(savedDiary.getId()).get();
                assertSoftly(softly -> {
                    softly.assertThat(updatedDiary.getDiaryImages()).hasSize(2);
                    softly.assertThat(updatedDiary.getDiaryImages()).containsAll(newImages);
                    softly.assertThat(updatedDiary.getDiaryImages()).doesNotContain(new Image("old-image1"), new Image("old-image2"));
                });
            }

            @Test
            @DisplayName("이미지를 null로 전달하면 모든 이미지가 제거된다")
            void 이미지를_null로_전달하면_모든_이미지가_제거된다() {
                // given
                Member member = memberRepository.save(createTestMember());
                Concert concert = concertRepository.save(createTestConcert());
                ConcertDiary diary = createDiary(member.getId(), concert.getId());
                diary.addImages(List.of(new Image("image1"), new Image("image2")));
                ConcertDiary savedDiary = diaryRepository.save(diary);

                UpdateDiaryRequest request = new UpdateDiaryRequest(
                        savedDiary.getId(),
                        concert.getId(),
                        LocalDate.now(),
                        "episode2",
                        "수정된 내용",
                        "B열 2번",
                        null);

                // when
                diaryService.update(request, member.getId());

                // then
                ConcertDiary updatedDiary = diaryRepository.findById(savedDiary.getId()).get();
                assertThat(updatedDiary.getDiaryImages()).isEmpty();
            }
        }

        @Nested
        @DisplayName("모든 필드를 수정할 때")
        class Context_모든_필드_수정 {

            @Test
            @DisplayName("공연 기록의 모든 필드가 정상적으로 업데이트된다")
            void 모든_필드가_정상적으로_업데이트된다() {
                // given
                Member member = memberRepository.save(createTestMember());
                Concert oldConcert = concertRepository.save(createTestConcert());
                Concert newConcert = concertRepository.save(createTestConcert());

                ConcertDiary diary = ConcertDiary.builder()
                        .memberId(member.getId())
                        .concertId(oldConcert.getId())
                        .episode("episode1")
                        .diaryDate(LocalDate.of(2024, 1, 1))
                        .content("첫 번째 공연")
                        .seatName("A열 1번")
                        .build();
                ConcertDiary savedDiary = diaryRepository.save(diary);

                LocalDate newDate = LocalDate.of(2024, 12, 31);
                List<Image> newImages = List.of(new Image("new-image"));
                UpdateDiaryRequest request = new UpdateDiaryRequest(
                        savedDiary.getId(),
                        newConcert.getId(),
                        newDate,
                        "episode2",
                        "두 번째 공연",
                        "B열 2번",
                        newImages);

                // when
                diaryService.update(request, member.getId());

                // then
                ConcertDiary updatedDiary = diaryRepository.findById(savedDiary.getId()).get();
                assertSoftly(softly -> {
                    softly.assertThat(updatedDiary.getConcertId()).isEqualTo(newConcert.getId());
                    softly.assertThat(updatedDiary.getDiaryDate()).isEqualTo(newDate);
                    softly.assertThat(updatedDiary.getEpisode()).isEqualTo("episode2");
                    softly.assertThat(updatedDiary.getContent()).isEqualTo("두 번째 공연");
                    softly.assertThat(updatedDiary.getSeatName()).isEqualTo("B열 2번");
                    softly.assertThat(updatedDiary.getDiaryImages()).hasSize(1);
                });
            }
        }
    }

    @Nested
    @DisplayName("공연 기록 삭제")
    class Describe_공연_기록_삭제 {

        @Nested
        @DisplayName("존재하지 않는 공연 기록을 삭제하려고 할 때")
        class Context_존재하지_않는_공연_기록 {

            @Test
            @DisplayName("예외가 발생한다")
            void 예외가_발생한다() {
                // given
                Member member = memberRepository.save(createTestMember());

                // when & then
                assertThatThrownBy(() -> diaryService.delete(999L, member.getId()))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(DiaryErrorCode.DIARY_NOT_FOUND.getMessage());
            }
        }

        @Nested
        @DisplayName("작성자가 아닌 사용자가 삭제하려고 할 때")
        class Context_작성자가_아닌_사용자 {

            @Test
            @DisplayName("예외가 발생한다")
            void 예외가_발생한다() {
                // given
                Member owner = memberRepository.save(createTestMember());
                Member other = memberRepository.save(createOtherMember());
                Concert concert = concertRepository.save(createTestConcert());
                ConcertDiary diary = createDiary(owner.getId(), concert.getId());
                ConcertDiary savedDiary = diaryRepository.save(diary);

                // when & then
                assertThatThrownBy(() -> diaryService.delete(savedDiary.getId(), other.getId()))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(DiaryErrorCode.DIARY_NOT_WRITER.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("공연 기록 이미지 추가")
    class Describe_이미지_추가 {

        @Nested
        @DisplayName("존재하지 않는 공연 기록에 이미지를 추가하려고 할 때")
        class Context_존재하지_않는_공연_기록 {

            @Test
            @DisplayName("예외가 발생한다")
            void 예외가_발생한다() {
                // given
                List<Image> images = List.of(new Image("image1"), new Image("image2"));

                // when & then
                assertThatThrownBy(() -> diaryService.addImagesById(999L, images))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(DiaryErrorCode.DIARY_NOT_FOUND.getMessage());
            }
        }

        @Nested
        @DisplayName("공연 기록에 이미지를 추가할 때")
        class Context_이미지_추가 {

            @Test
            @DisplayName("이미지가 정상적으로 저장된다")
            void 이미지가_정상적으로_저장된다() {
                // given
                Member member = memberRepository.save(createTestMember());
                Concert concert = concertRepository.save(createTestConcert());
                ConcertDiary diary = createDiary(member.getId(), concert.getId());
                ConcertDiary savedDiary = diaryRepository.save(diary);
                List<Image> images = List.of(new Image("image1"), new Image("image2"));

                // when
                diaryService.addImagesById(savedDiary.getId(), images);

                // then
                ConcertDiary updatedDiary = diaryRepository.findById(savedDiary.getId()).get();
                assertSoftly(softly -> {
                    softly.assertThat(updatedDiary.getDiaryImages()).hasSize(2);
                    softly.assertThat(updatedDiary.getDiaryImages()).containsAll(images);
                });
            }

            @Test
            @DisplayName("null 이미지 리스트를 추가해도 예외가 발생하지 않는다")
            void null_이미지_리스트를_추가해도_예외가_발생하지_않는다() {
                // given
                Member member = memberRepository.save(createTestMember());
                Concert concert = concertRepository.save(createTestConcert());
                ConcertDiary diary = createDiary(member.getId(), concert.getId());
                ConcertDiary savedDiary = diaryRepository.save(diary);

                // when
                diaryService.addImagesById(savedDiary.getId(), null);

                // then
                ConcertDiary updatedDiary = diaryRepository.findById(savedDiary.getId()).get();
                assertThat(updatedDiary.getDiaryImages()).isEmpty();
            }
        }
    }

    private ConcertDiary createDiary(Long memberId, Long concertId) {
        return ConcertDiary.builder()
                .memberId(memberId)
                .concertId(concertId)
                .episode("episode1")
                .diaryDate(LocalDate.now())
                .content("내용")
                .seatName("A열 1번")
                .build();
    }

    private Member createOtherMember() {
        return Member.builder()
                .email(new com.backend.allreva.common.model.Email("other@example.com"))
                .memberRole(MemberRole.USER)
                .loginProvider(LoginProvider.GOOGLE)
                .nickname("OtherUser")
                .introduce("Hello")
                .profileImageUrl("http://example.com/other.jpg")
                .build();
    }
}
