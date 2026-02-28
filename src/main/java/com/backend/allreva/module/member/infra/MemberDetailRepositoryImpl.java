package com.backend.allreva.module.member.infra;

import static com.backend.allreva.module.concert.artist.domain.QArtist.artist;
import static com.backend.allreva.module.member.domain.QMember.member;
import static com.backend.allreva.module.member.domain.artist.QMemberArtist.memberArtist;

import com.backend.allreva.module.member.application.dto.MemberDetailResponse;
import com.backend.allreva.module.member.application.dto.MemberDetailResponse.MemberArtistDetail;
import com.backend.allreva.module.member.application.port.MemberDetailRepository;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberDetailRepositoryImpl implements MemberDetailRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public MemberDetailResponse findById(final Long id) {
        return queryFactory
                .select(memberDetailProjections())
                .from(member)
                .leftJoin(memberArtist)
                .on(memberArtist.memberId.eq(member.id))
                .leftJoin(artist)
                .on(artist.id.eq(memberArtist.artistId))
                .where(member.id.eq(id))
                .fetchFirst();
    }

    private ConstructorExpression<MemberDetailResponse> memberDetailProjections() {
        return Projections.constructor(
                MemberDetailResponse.class,
                member.email.email,
                member.memberInfo.nickname,
                member.memberInfo.introduce,
                member.memberInfo.profileImageUrl,
                Projections.list(Projections.constructor(MemberArtistDetail.class, memberArtist.artistId, artist.name)),
                member.refundAccount);
    }
}
