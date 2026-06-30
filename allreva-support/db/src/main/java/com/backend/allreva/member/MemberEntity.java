package com.backend.allreva.member;

import com.backend.allreva.common.model.Email;
import com.backend.allreva.common.persistence.BaseEntity;
import com.backend.allreva.member.domain.LoginProvider;
import com.backend.allreva.member.domain.Member;
import com.backend.allreva.member.domain.MemberConstraints;
import com.backend.allreva.member.domain.MemberInfo;
import com.backend.allreva.member.domain.MemberRole;
import com.backend.allreva.member.domain.MemberStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity(name = "Member")
@Table(
        name = "member",
        uniqueConstraints =
                @UniqueConstraint(
                        name = MemberConstraints.UQ_EMAIL_PROVIDER,
                        columnNames = {"email", "provider"}))
@SQLDelete(sql = "UPDATE member SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
public class MemberEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private MemberRole memberRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemberStatus memberStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private LoginProvider loginProvider;

    @Column(nullable = false)
    private String nickname;

    private String introduce;

    @Column(name = "profile_image_url", nullable = false)
    private String profileImageUrl;

    @Convert(converter = RefundAccountVOConverter.class)
    @Column(name = "refund_account", nullable = false)
    private RefundAccountVO refundAccount;

    private MemberEntity(
            final Long id,
            final String email,
            final MemberRole memberRole,
            final MemberStatus memberStatus,
            final LoginProvider loginProvider,
            final MemberInfo memberInfo,
            final RefundAccountVO refundAccount) {
        this.id = id;
        this.email = email;
        this.memberRole = memberRole;
        this.memberStatus = memberStatus;
        this.loginProvider = loginProvider;
        if (memberInfo != null) {
            this.nickname = memberInfo.getNickname();
            this.introduce = memberInfo.getIntroduce();
            this.profileImageUrl = memberInfo.getProfileImageUrl();
        }
        this.refundAccount = refundAccount;
    }

    public static MemberEntity from(final Member member) {
        return new MemberEntity(
                member.getId(),
                member.getEmail() != null ? member.getEmail().getEmail() : null,
                member.getMemberRole(),
                member.getMemberStatus(),
                member.getLoginProvider(),
                member.getMemberInfo(),
                RefundAccountVO.from(member.getRefundAccount()));
    }

    public Member toDomain() {
        return Member.builder()
                .id(id)
                .email(email != null ? new Email(email) : null)
                .memberRole(memberRole)
                .memberStatus(memberStatus)
                .loginProvider(loginProvider)
                .memberInfo(MemberInfo.builder()
                        .nickname(nickname)
                        .introduce(introduce)
                        .profileImageUrl(profileImageUrl)
                        .build())
                .refundAccount(refundAccount != null ? refundAccount.toDomain() : null)
                .build();
    }
}
