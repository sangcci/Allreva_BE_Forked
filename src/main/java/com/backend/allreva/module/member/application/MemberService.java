package com.backend.allreva.module.member.application;

import com.backend.allreva.common.event.Events;
import com.backend.allreva.module.concert.artist.application.ArtistService;
import com.backend.allreva.module.concert.artist.application.dto.ArtistCreateRequest;
import com.backend.allreva.module.member.application.dto.MemberArtistRequest;
import com.backend.allreva.module.member.application.dto.MemberDetailResponse;
import com.backend.allreva.module.member.application.dto.MemberRegisterRequest;
import com.backend.allreva.module.member.application.dto.NicknameDuplication;
import com.backend.allreva.module.member.application.dto.RefundAccountRequest;
import com.backend.allreva.module.member.application.port.MemberDetailRepository;
import com.backend.allreva.module.member.domain.event.MemberRegisteredEvent;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.member.domain.artist.MemberArtist;
import com.backend.allreva.module.member.domain.artist.MemberArtistRepository;
import com.backend.allreva.module.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberArtistRepository memberArtistRepository;
    private final MemberDetailRepository memberDetailRepository;
    private final ArtistService artistService;

    @Transactional(readOnly = true)
    public MemberDetailResponse getById(final Long id) {
        return memberDetailRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public NicknameDuplication isDuplicatedNickname(final String nickname) {
        boolean exists = memberRepository.existsByMemberInfoNickname(nickname);
        return new NicknameDuplication(exists);
    }

    @Transactional
    public void registerMember(final MemberRegisterRequest memberRegisterRequest) {
        Member member = memberRegisterRequest.toEntity();
        Member registeredMember = memberRepository.save(member);

        updateMemberArtist(memberRegisterRequest.memberArtistRequests(), registeredMember);

        Events.raise(new MemberRegisteredEvent(registeredMember.getId()));
    }

    @Transactional
    public void updateMemberInfo(
            final MemberRegisterRequest memberRegisterRequest,
            final Member member
    ) {
        member.setMemberInfo(
                memberRegisterRequest.nickname(),
                memberRegisterRequest.introduce(),
                memberRegisterRequest.image().getUrl()
        );
        memberRepository.save(member);
        updateMemberArtist(memberRegisterRequest.memberArtistRequests(), member);
    }

    @Transactional
    public void registerRefundAccount(
            final RefundAccountRequest refundAccountRequest,
            final Member member
    ) {
        member.setRefundAccount(
                refundAccountRequest.bank(),
                refundAccountRequest.number()
        );
        memberRepository.save(member);
    }

    @Transactional
    public void deleteRefundAccount(final Member member) {
        member.setDefaultRefundAccount();
        memberRepository.save(member);
    }

    private void updateMemberArtist(
            final List<MemberArtistRequest> memberArtistRequests,
            final Member member
    ) {
        List<ArtistCreateRequest> artistCreateRequests = memberArtistRequests.stream()
                .map(req -> new ArtistCreateRequest(req.spotifyArtistId(), req.name()))
                .toList();
        artistService.saveIfNotExist(artistCreateRequests);
        List<MemberArtist> preMemberArtists = memberArtistRepository.findByMemberId(member.getId());

        List<MemberArtist> addMemberArtists = memberArtistRequests.stream()
                .filter(req -> isNewMemberArtists(req, preMemberArtists))
                .map(req -> artistService.getArtistById(req.spotifyArtistId()))
                .map(artist -> MemberArtist.builder()
                        .memberId(member.getId())
                        .artistId(artist.getId())
                        .build())
                .toList();
        memberArtistRepository.saveAll(addMemberArtists);

        List<MemberArtist> removeMemberArtists = preMemberArtists.stream()
                .filter(pre -> isRemoveMemberArtist(pre, memberArtistRequests))
                .toList();
        memberArtistRepository.deleteAll(removeMemberArtists);
    }

    private boolean isNewMemberArtists(final MemberArtistRequest req, final List<MemberArtist> preMemberArtists) {
        return preMemberArtists.stream()
                .noneMatch(pre -> pre.getArtistId().equals(req.spotifyArtistId()));
    }

    private boolean isRemoveMemberArtist(final MemberArtist pre, final List<MemberArtistRequest> memberArtistRequests) {
        return memberArtistRequests.stream()
                .noneMatch(req -> req.spotifyArtistId().equals(pre.getArtistId()));
    }
}
