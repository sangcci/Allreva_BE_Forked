# Main Sequences

## OAuth Login

클라이언트가 OAuth authorization code를 전달하면 서버가 provider와 직접 통신해 사용자 정보를 조회합니다. 이후 로컬 회원을 식별하고 JWT access token과 refresh token을 발급합니다.

```mermaid
sequenceDiagram
    autonumber
    participant Client
    participant API
    participant OAuth as OAuth Provider
    participant DB as PostgreSQL
    participant Redis

    Client->>API: OAuth authorization code 전달
    API->>OAuth: access token 요청
    OAuth-->>API: access token 응답
    API->>OAuth: 사용자 프로필 요청
    OAuth-->>API: provider user info
    API->>DB: 회원 조회 또는 생성
    API->>API: JWT 발급
    API->>Redis: refresh token 저장
    API-->>Client: access token + refresh cookie 응답
```

## Rent Participation

차대절 참여 요청은 회원, 모집, 중복 참여 여부를 검증한 뒤 좌석 수를 갱신합니다. 정원 초과를 막기 위해 잔여 좌석 검증과 증가를 하나의 atomic update로 처리합니다.

```mermaid
sequenceDiagram
    autonumber
    participant Client
    participant API
    participant DB as PostgreSQL
    participant Event as Event Listener
    participant Push as FCM

    Client->>API: 차대절 참여 요청
    API->>DB: 회원/모집/중복 참여 검증
    API->>DB: Atomic UPDATE로 잔여 좌석 검증 및 증가
    alt 정원 초과
        API-->>Client: 참여 실패
    else 참여 가능
        API->>DB: 참여자 저장
        API-->>Client: 참여 완료
        API->>Event: 참여 완료 이벤트 발행
        Event->>Push: 주최자 알림 전송
    end
```

## KOPIS Concert Sync

스케줄러가 KOPIS API에서 공연장별 공연 목록을 조회하고, 신규 공연 또는 상태가 변경된 공연만 상세 조회해 저장합니다. 공연 상세 조회 성능을 위해 캐시 갱신 흐름도 함께 관리합니다.

```mermaid
sequenceDiagram
    autonumber
    participant Scheduler
    participant KOPIS
    participant DB as PostgreSQL
    participant Cache as Caffeine

    Scheduler->>DB: 공연장 코드 목록 조회
    loop hallCode별 순회
        Scheduler->>KOPIS: 공연 목록 조회
        KOPIS-->>Scheduler: 공연 코드와 상태 응답
        loop 신규 또는 변경 공연
            Scheduler->>KOPIS: 공연 상세 조회
            KOPIS-->>Scheduler: 공연 상세 정보
            Scheduler->>DB: 공연 저장 또는 갱신
        end
    end
    Scheduler->>Cache: 공연 상세 캐시 갱신/무효화
```
