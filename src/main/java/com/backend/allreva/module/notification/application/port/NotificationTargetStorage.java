package com.backend.allreva.module.notification.application.port;

import java.util.List;

public interface NotificationTargetStorage {

    /**
     * 회원 ID 목록에 해당하는 알림 대상 식별자 목록 조회
     *
     * @param memberIds 회원 ID 목록
     * @return 알림 대상 식별자 목록 (대상이 없는 회원은 null 포함 가능)
     */
    List<String> findTargetsByMemberIds(List<Long> memberIds);

    /**
     * 회원의 알림 대상 식별자 저장
     *
     * @param memberId 회원 ID
     * @param target 알림 대상 식별자 (FCM Token, APNS Token 등)
     */
    void save(Long memberId, String target);

    /**
     * 회원의 알림 대상 식별자 삭제
     *
     * @param memberId 회원 ID
     */
    void delete(Long memberId);
}
