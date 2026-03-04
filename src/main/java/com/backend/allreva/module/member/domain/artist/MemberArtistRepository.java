package com.backend.allreva.module.member.domain.artist;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberArtistRepository extends JpaRepository<MemberArtist, Long> {
    List<MemberArtist> findByMemberId(final Long memberId);
}
