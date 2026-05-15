# Allreva 모듈 · 패키지 정책

## 목적

멀티모듈 뼈대를 세운 뒤, `common` 패키지와 DTO / Event / Repository / Query의 위치 기준을 먼저 고정합니다.
이 문서는 이후 `rent`, `survey`, `concert` 리팩토링에서 공통 판단 기준으로 사용합니다.

## 최상위 원칙

1. `common`은 무조건 유지 대상이 아닙니다.
2. 여러 모듈이 함께 쓴다고 해서 자동으로 `common`으로 올리지 않습니다.
3. 기술 설정은 `infra` 또는 `implement`에 둡니다.
4. 조회 모델과 상태 변경 모델은 처음부터 분리합니다.
5. 내부 이벤트와 외부 공개 이벤트는 다르게 취급합니다.
6. 도메인은 개념 객체와 규칙 중심으로 유지하고, 요청/응답 DTO는 두지 않습니다.

## 레이어별 책임

### modules/allreva-domain
- 엔티티, 밸류 객체, 도메인 규칙
- 도메인 저장소 인터페이스
- 도메인 내부 이벤트
- 여러 도메인에서 재사용 가능한 순수 개념 타입

### modules/allreva-application
- command / query 유스케이스
- application 전용 입력 DTO, 조회 DTO
- 도메인 객체 조합과 흐름 제어

### modules/allreva-infra
- JPA, Querydsl, Redis, S3, 외부 API 구현
- 저장소 구현체, 조회 구현체
- 인프라 설정

### modules/allreva-api
- Controller, Swagger, HTTP 응답 포맷
- API 전용 요청/응답 DTO
- 웹 보안, 웹 엔드포인트, multipart 같은 진입점 설정
- HTTP logging adapter (`Filter`, `HandlerInterceptor`)

### modules/allreva-events
- 외부 공개 이벤트 계약
- 브로커/배치/다른 실행 모듈에 노출되는 integration event

### modules/allreva-observability
- logging / metrics / tracing 공통 지원
- AOP 기반 메서드 로깅
- MDC / trace helper
- 공통 annotation, helper, config
- web 비의존 observability 라이브러리

## common 재분류 기준

### 유지 가능한 공통 기반
아래 성격은 당장 공통 기반으로 둘 수 있습니다.

- `common/exception`: `CustomException`, `ErrorCode`, `GlobalErrorCode`
- `common/model`: `BaseEntity`, `Email`, `Image`
- `common/converter`: JPA converter 성격의 공통 변환기
- `common/pagination/SliceResponse`: 여러 query에서 재사용 가능한 공통 조회 래퍼

단, 이들도 최종적으로는 의미가 더 분명한 모듈 하위로 이동할 수 있습니다.
예를 들면 `BaseEntity`는 `domain`, `Response`는 `implement`가 더 자연스럽습니다.

### api로 가야 하는 것
- `common/web/response/Response`
- `common/web/exception/CustomControllerAdvice`
- `common/config/CorsConfig`
- `common/config/SecurityConfig`
- `common/config/SwaggerConfig`
- `common/config/MultipartJacksonConverter`
- `common/storage/presigned/PresignedUrlController`
- `common/storage/presigned/PresignedUrlControllerSwagger`
- `common/logging/HttpLogFilter`

### infra로 가야 하는 것
- `common/config/JpaAuditingConfig`
- `common/config/QuerydslConfig`
- `common/config/RedisConfig`
- `common/config/S3Config`
- `common/config/OpenFeignConfig`
- `common/config/KopisFeignConfig`
- `common/config/FcmInitializer`
- `common/config/CacheConfig`
- `common/storage/upload/StorageUploadService`
- `common/storage/presigned/PresignedUrlService`

### events 또는 도메인 근처로 가야 하는 것
- `common/event/Event`
- `common/event/Events`
- `common/event/EventsConfig`
- `common/event/KeywordSearchedEvent`

기준은 아래와 같습니다.

- 도메인 내부 후처리용 사건이면 각 도메인 모듈 가까이 둡니다.
- 여러 실행 모듈에 공개되는 계약이면 `allreva-events`로 올립니다.
- `KeywordSearchedEvent`처럼 특정 비즈니스 의미가 있는 이벤트는 generic common에 두지 않습니다.

## DTO 정책

## 1. api DTO
외부 HTTP 계약에 직접 노출되는 요청/응답 모델입니다.

예:
- `RentRegisterRequest`
- `RentUpdateRequest`
- `RentDetailResponse`

위치는 다음처럼 둡니다.

```text
modules/allreva-api
└─ rent
   ├─ command/dto
   └─ query/dto
```

## 2. application DTO
유스케이스 호출과 query 결과 조합에 쓰는 내부 전달 모델입니다.

예:
- `RegisterRentCommand`
- `UpdateRentCommand`
- `RentDetailView`
- `RentSummaryView`

현재 `application/dto`에 섞여 있는 요청/응답 클래스는 이후 점진적으로 아래 둘 중 하나로 정리합니다.

- api DTO
- application command/query DTO

## 3. domain 객체
도메인 개념 자체는 DTO로 취급하지 않습니다.

예:
- `Rent`
- `RentParticipant`
- `Bus`
- `Depositor`

## DTO 판단 규칙
- 컨트롤러 입력/출력 모양이면 `api`
- 유스케이스 전달 모델이면 `application`
- 핵심 개념과 상태 변화 규칙이면 `domain`
- 외부 API 응답 원본이면 `infra`

## Event 정책

### 내부 이벤트
- 도메인 행위 이후 같은 시스템 내부 후처리에 사용
- 도메인 모듈 가까이에 둠
- 예: `RentParticipatedEvent`

### 외부 공개 이벤트
- 다른 실행 모듈, 브로커 소비자, 외부 계약에 사용
- `allreva-events`에 둠
- 예: `RentParticipatedIntegrationEvent`

### 현재 공통 이벤트에 대한 처리 원칙
- `Events`, `EventsConfig`는 공통 이벤트 발행 메커니즘으로 유지 가능
- 하지만 개별 비즈니스 이벤트 클래스는 common에 계속 쌓지 않음

## Repository / Query 정책

## 1. 도메인 저장소
Aggregate 저장과 상태 변경에 필요한 인터페이스만 둡니다.

예:
- `RentRepository`
- `MemberRepository`
- `RefreshTokenRepository`

위치:
- 인터페이스: `domain`
- 구현체: `infra`

## 2. query 전용 저장소
조회 조합, projection, paging, join에 특화된 읽기 저장소입니다.

예:
- `RentSearchRepository`
- `ConcertSearchRepository`

정책:
- query 전용 계약은 `application/query/port`
- 구현은 `infra/query`
- `*SearchRepository`처럼 조회 목적이 분명한 이름을 우선 사용
- Aggregate 저장소와 같은 이름/역할로 섞지 않음

## 3. Spring Data JPA repository
JPA 기술 구현 세부사항이므로 `infra` 소속입니다.

예:
- `RentJpaRepository`
- `ConcertJpaRepository`

## Query 정책

- query는 domain이 아니라 application의 read usecase로 봅니다.
- 복잡한 SQL, Querydsl, projection은 `infra/query`
- query service는 `application/query`
- controller query endpoint는 `implement/query`

## Observability 정책

- logging / metrics / tracing 공통 지원은 `allreva-observability` 후보 모듈로 관리합니다.
- 이 모듈은 `spring-web` 의존 없이 유지합니다.
- 메서드 실행 로깅, 타이머, MDC, trace helper는 observability로 보냅니다.
- HTTP 요청/응답 로깅은 servlet/webmvc adapter가 필요하므로 `api` 책임으로 둡니다.
- 따라서 `HttpLogFilter`를 유지하더라도 최종 위치는 `api`가 더 자연스럽습니다.
- 이후 메서드 로깅은 AOP 기반으로 전환할 수 있습니다.

## 네이밍 정책

- 초기 CQRS 단계에서는 `*CommandService`, `*QueryService`를 우선 사용합니다.
- `Host`, `Participant` 같은 행위 축은 당장 클래스명으로 올리지 않고 메서드 책임으로 먼저 검증합니다.
- `Rule`, `Reader`, `Writer`, `DomainService`는 실제 복잡도가 확인된 뒤 후속 이슈에서 분리합니다.
- query 전용 협력 객체는 service와 같은 축에 두고(`application/query`, `infra/query`) 이름으로 역할을 드러냅니다.

## 1차 적용 대상

### rent
- `application/dto`에 섞인 request/response 정리
- `RentService` 분리 전 기준 문서로 사용
- `RentSearchRepository`의 위치와 역할 명확화

### common/storage/presigned
- controller는 `implement`
- service와 S3 연동은 `infra`
- request DTO는 api DTO로 이동 검토

### common/event
- 공통 이벤트 메커니즘과 비즈니스 이벤트 클래스 분리

## 이번 단계에서 하지 않는 것

- 모든 common 코드 즉시 이동
- 도메인 서비스 / Rule 세분화
- `HostCommandService`, `ParticipantCommandService` 같은 역할명 세분화
- batch-server / scheduler-server 실행 전략 확정

## 후속 이슈 연결

- `#79`: rent 파일럿 CQRS 분리
- `#81`: 이벤트 구조 분리
- observability 모듈 분리 이슈
- `#83`: 도메인 역할 및 네이밍 정제
- `#84`: batch-server / scheduler-server 전략
