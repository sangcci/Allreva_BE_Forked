package com.backend.allreva.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.backend.allreva.common.model.Email;
import com.backend.allreva.member.domain.Member;
import com.backend.allreva.member.fixture.MemberFixture;
import com.backend.allreva.support.DataJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import(MemberRepositoryImpl.class)
@DisplayName("MemberRepositoryImpl 테스트")
class MemberRepositoryImplTest extends DataJpaTestSupport {

    @Autowired
    private MemberRepositoryImpl memberRepository;

    @Test
    @DisplayName("회원을 저장하고 id로 조회한다")
    void save_and_find_by_id() {
        // given
        Member member = MemberFixture.createMember();

        // when
        Member saved = memberRepository.save(member);
        entityManager.flush();
        entityManager.clear();
        Member found = memberRepository.findById(saved.getId()).orElseThrow();

        // then
        assertSoftly(softly -> {
            softly.assertThat(found.getId()).isEqualTo(saved.getId());
            softly.assertThat(found.getEmail().getEmail()).isEqualTo(MemberFixture.EMAIL);
            softly.assertThat(found.getMemberInfo().getNickname()).isEqualTo(MemberFixture.NICKNAME);
            softly.assertThat(found.getRefundAccount().getBank()).isEqualTo("은행");
        });
    }

    @Test
    @DisplayName("email과 provider로 회원을 조회한다")
    void find_by_email_and_login_provider() {
        // given
        memberRepository.save(MemberFixture.createMember());
        entityManager.flush();
        entityManager.clear();

        // when
        Member found = memberRepository
                .findByEmailAndLoginProvider(new Email(MemberFixture.EMAIL), MemberFixture.PROVIDER)
                .orElseThrow();

        // then
        assertThat(found.getEmail().getEmail()).isEqualTo(MemberFixture.EMAIL);
    }
}
