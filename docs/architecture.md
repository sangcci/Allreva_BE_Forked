# 멀티모듈 아키텍처 설계 기록

## 1. 아키텍처 설계 의도

이 문서는 현재 Allreva 백엔드가 왜 멀티모듈 구조로 이동했는지, 그리고 앞으로 어떤 기준으로 모듈과 패키지를 나눌지 기록하기 위해 작성한다.

출발점은 단순하다. 구조에 대한 심리적 거부감이나 선호만으로 아키텍처를 정하면 안 된다고 봤다. “레이어드 아키텍처가 익숙하니까”, “DDD가 좋아 보이니까”, “멀티모듈이 멋있어 보이니까” 같은 이유만으로는 오래 버티기 어렵다. 구조를 바꾸려면 그만한 근거가 있어야 하고, 나중에 팀원이 봐도 납득할 수 있는 판단 기준이 필요하다.

코드는 실행되는 문서에 가깝다. 신규 입사자나 다른 팀원이 코드를 읽었을 때, 어느 정도는 문서를 보지 않아도 비즈니스 흐름을 따라갈 수 있어야 한다. 물론 별도 문서도 필요하지만, 문서는 코드와 같이 변하지 않으면 금방 낡는다. 한 번 버전 관리가 어긋나기 시작하면 계속 어긋나고, 결국 아무도 신뢰하지 않는 문서가 된다. 그래서 핵심 비즈니스 흐름은 코드 구조 자체에 드러나는 편이 낫다고 판단했다.

단순 CRUD 프로젝트라면 전형적인 layered architecture로 충분하다. 유지보수할 일이 거의 없고, 업무 규칙도 단순하다면 Controller-Service-Repository 구조만으로도 이해하기 쉽다. 심지어 JPA Entity를 중심으로 transaction script에 가깝게 작성해도 큰 문제가 없을 수 있다. 사람이 머릿속에 업무 규칙을 다 담을 수 있는 수준이면, 추상화를 많이 넣는 것이 오히려 비용이 된다.

하지만 Allreva는 계속 요구사항이 붙고, 도메인별로 정책이 늘어날 가능성이 있다. 이런 상황에서 Service 코드가 비즈니스 로직을 담기보다 구현 절차만 길게 나열하는 형태가 되면, 기능을 고치는 일이 점점 어려워진다. 어디까지가 업무 규칙이고 어디부터가 기술 구현인지 흐려진다. 그래서 application과 domain 사이에 더 작은 행위자 단위를 두고, usecase가 어떤 업무를 조합하는지 읽히게 만들 필요가 있었다.

이 구조가 모든 문제를 해결한다고 보지는 않는다. 멀티모듈도 은탄환은 아니다. 모듈을 나누면 의존성 방향을 강제하기 쉬워지고, 기술 교체 지점을 분리하기 좋아진다. 반대로 설정이 늘고, 빌드가 복잡해지고, 처음 읽는 사람에게는 진입 장벽이 생길 수 있다. 그래서 처음부터 모든 것을 잘게 쪼개기보다, 임계점이 왔을 때 레고 블록처럼 분리하는 방향을 잡는다.

프레임워크 의존성도 현실적으로 본다. domain을 완전히 순수하게 유지하는 것이 항상 정답은 아니라고 본다. `spring-core`, `transaction`, `context` 같은 기본 기능을 가볍게 사용하는 편이 보일러플레이트를 줄이고 코드 의도를 더 잘 드러낼 때가 있다. 목표는 “프레임워크 종속성 0”이 아니다. 스프링의 생명주기와 DI가 주는 이점을 받아들이되, 비즈니스 요구사항이 기술 구현에 묻히지 않게 하는 것이다.

결국 아키텍처는 소프트웨어가 왜 생겼는지에서 출발해야 한다. 소프트웨어는 현실의 병목을 줄이고, 사람이 반복하던 일을 자동화하고, 사용자에게 편의와 안전을 제공하기 위해 존재한다. 이유가 명확하면 어떤 업무가 중요한지도 보인다. 그 중요도가 코드 구조에 반영되어야 한다.

비즈니스 언어도 꼭 기획자나 도메인 전문가만 쓰는 말로 한정하지 않는다. 개발자끼리 공유하는 문제도 유비쿼터스 언어가 될 수 있다. 예를 들어 “응답 시간 개선”, “인가 정책”, “검색 동기화” 같은 표현은 개발의 언어이면서 동시에 제품의 품질을 설명하는 언어다. 중요한 것은 팀이 같은 단어를 같은 뜻으로 쓰는 것이다.

현재 구조의 핵심 목표는 다음과 같다.

- 비즈니스 usecase 흐름을 application 코드에서 직관적으로 읽을 수 있게 한다.
- 업무 규칙은 domain 안에 두고, 기술 구현은 support 모듈로 밀어낸다.
- core 모듈은 api, batch, support 구현체를 모르게 한다.
- DB, cache, 외부 client, notification sender 같은 기술은 교체 가능한 adapter로 둔다.
- 도메인 복잡도에 따라 구조의 무게를 다르게 가져간다.

## 2. 모듈 및 패키지 다이어그램

### 2.1 전체 모듈 구조

```text
allreva
├── allreva-core
│   ├── auth
│   ├── member
│   ├── concert
│   │   ├── concert
│   │   └── place
│   ├── recruitment
│   │   ├── rent
│   │   └── survey
│   ├── notification
│   ├── search
│   └── common
│
├── allreva-api
│   ├── ApiServerApplication
│   ├── */*Controller
│   ├── auth/security
│   ├── notification/sse
│   └── common/config, web, logging
│
├── allreva-batch
│   ├── BatchServerApplication
│   └── batch/scheduler
│
├── allreva-support
│   ├── db
│   ├── storage
│   ├── observability
│   ├── local-cache
│   ├── global-cache
│   ├── oauth-client
│   ├── kopis-client
│   └── push-notification
│
└── allreva-test
    └── integration test support
```

### 2.2 의존성 방향

```text
                 ┌──────────────────┐
                 │   allreva-api    │
                 │ REST, SSE, Auth  │
                 └────────┬─────────┘
                          │
                          ▼
                 ┌──────────────────┐
                 │  allreva-core    │
                 │ usecase, domain  │
                 └────────▲─────────┘
                          │
        implements ports  │  depends on port interfaces
                          │
┌─────────────────────────┴─────────────────────────┐
│                    allreva-support                 │
│ db, redis, s3, oauth, kopis, fcm, cache, logging   │
└───────────────────────────────────────────────────┘

                 ┌──────────────────┐
                 │  allreva-batch   │
                 │ scheduler, jobs  │
                 └────────┬─────────┘
                          │
                          ▼
                 ┌──────────────────┐
                 │  allreva-core    │
                 └──────────────────┘
```

규칙은 단순하다. `core`는 다른 모듈을 몰라야 한다. `api`, `batch`, `support`는 `core`를 사용할 수 있지만, `core`가 이들을 참조하면 안 된다. 의존성 방향이 흔들리면 어느 순간 비즈니스 코드가 DB, Redis, FCM, OAuth2 같은 기술 이름을 직접 알게 되고, 그때부터 변경 비용이 커진다.

### 2.3 core 내부 패키지 구조

```text
{domain-group}/{bounded-context}
├── command
│   ├── application
│   │   └── *Service.java
│   ├── implementation
│   │   ├── *Reader.java
│   │   ├── *Writer.java
│   │   ├── *Register.java
│   │   ├── *Updater.java
│   │   ├── *Canceller.java
│   │   └── *Port.java / interface
│   ├── input
│   │   └── *Command.java
│   └── event
│       └── *Event.java
│
├── query
│   ├── application
│   │   └── *Finder.java
│   ├── implementation
│   │   └── *FinderPort.java
│   └── model
│       └── *Result.java / *Summary.java / *Detail.java
│
└── domain
    ├── Aggregate.java
    ├── ValueObject.java
    ├── *Repository.java
    ├── *ErrorCode.java
    └── enum / policy object
```

예시는 `recruitment/rent`에서 가장 잘 보인다.

```text
recruitment/rent
├── command/application/RentService.java
├── command/implementation/RentReader.java
├── command/implementation/RentWriter.java
├── command/implementation/RentRegister.java
├── command/implementation/RentJoiner.java
├── command/implementation/RentParticipantCanceller.java
├── command/input/RentRegisterCommand.java
├── command/event/RentRegisteredEvent.java
├── query/application/RentFinder.java
├── query/implementation/RentFinderPort.java
├── query/model/RentDetailResult.java
└── domain/Rent.java, RentParticipant.java, RentRepository.java
```

`RentService`는 usecase 오케스트레이션을 맡는다. 예를 들어 `register`는 `RentRegister`로 도메인 객체를 만들고, `RentWriter`로 저장하고, 저장 후 `RentRegisteredEvent`를 발행한다. 이 흐름을 보면 “대절 모집 등록”이라는 업무가 어떤 단계로 진행되는지 코드만으로도 어느 정도 읽힌다.

### 2.4 support 내부 구조

```text
allreva-support
├── db
│   ├── *Entity.java
│   ├── *JpaRepository.java
│   ├── *JdbcRepository.java
│   ├── *QueryDslFinder.java
│   ├── *RepositoryImpl.java
│   ├── *FinderAdapter.java
│   └── db-config/application-{dev,stag,prod}.yml
│
├── global-cache
│   ├── RedisConfig.java
│   ├── RefreshTokenStorageAdapter.java
│   ├── FcmTargetStorage.java
│   ├── PopularKeywordRepositoryImpl.java
│   ├── SearchFinderAdapter.java
│   └── global-cache-config/application-{dev,stag,prod}.yml
│
├── local-cache
│   └── CacheConfig.java
│
├── storage
│   ├── S3Config.java
│   ├── StorageProperties.java
│   ├── S3StorageAdapter.java
│   └── storage-config/application-{dev,stag,prod}.yml
│
├── oauth-client
│   ├── kakao/*Client.java
│   ├── KakaoOAuthProperties.java
│   ├── KakaoOAuthIdentityVerifier.java
│   ├── KakaoOAuthMemberMapper.java
│   └── oauth-client-config/application-{dev,stag,prod}.yml
│
├── kopis-client
│   ├── KopisProperties.java
│   ├── KopisFeignConfig.java
│   ├── *Client.java
│   ├── *DataSync.java
│   ├── *Response.java
│   ├── *Mapper.java
│   └── kopis-client-config/application-{dev,stag,prod}.yml
│
├── push-notification
│   ├── fcm/FcmProperties.java
│   ├── fcm/FcmPushNotificationConfig.java
│   ├── fcm/FcmInitializer.java
│   ├── fcm/FcmClient.java
│   ├── fcm/FcmSender.java
│   └── push-notification-config/application-{dev,stag,prod}.yml
│
└── observability
    ├── logback-spring.xml
    └── observability-config/application-{dev,stag,prod}.yml
```

`support`는 구현체의 모음이다. DB가 JPA인지 JDBC인지 Querydsl인지, 캐시가 Redis인지 local cache인지, 외부 API가 KOPIS인지 Kakao인지, push가 FCM인지 같은 결정은 여기로 모은다.

모듈 이름은 기술 이름보다 역할 이름을 우선한다. 다만 특정 기술이 충분히 비대해지거나 독립적으로 관리할 필요가 생기면 기술 이름을 모듈명으로 올릴 수 있다. 예를 들어 `push-notification`은 역할 이름이고, 내부 패키지에서 `fcm`이라는 벤더/기술 이름을 사용한다. FCM 코드가 더 커지고 다른 push provider가 추가되면 그때 별도 모듈 분리를 다시 판단한다.

support 모듈은 Allreva 내부 인프라 모듈이다. 외부 범용 라이브러리처럼 모든 설정을 실행 모듈에 떠넘기지 않는다. 각 support 모듈은 자신이 쓰는 설정 prefix와 profile별 기본 설정 파일을 가진다. 실행 모듈은 필요한 support config를 명시적으로 import한다.

규칙은 다음과 같다.

- main resource에는 `application.yml`을 직접 두지 않는다.
- profile 설정은 `{module}-config/application-{dev,stag,prod}.yml`에 둔다.
- secret 값은 support config에 하드코딩하지 않고 `${ENV_NAME}` placeholder로 둔다.
- test 전용 `application.yml`이나 `application-test.yml`은 support 모듈에 두지 않는다.
- support 단위 테스트에서 필요한 property는 테스트 support class의 `@DynamicPropertySource`, test annotation `properties`, 또는 수동 생성자로 직접 주입한다.
- `allreva-test`는 여러 모듈을 조합하는 통합 테스트 실행 모듈이므로 자체 `src/test/resources/application.yml`을 유지한다.

### 2.5 db 모듈 테스트 기준

`allreva-support:db`는 core보다 데이터베이스에 더 가까운 계층으로 본다. JPA Entity는 domain value object를 그대로 품는 모델이 아니라, 실제 테이블 컬럼과 최대한 비슷한 persistence model로 둔다. domain 객체와 DB 컬럼 사이의 변환은 `Entity.from(domain)`과 `toDomain()`에서 처리한다.

DB 모듈 테스트는 이 기준을 검증하는 쪽으로 둔다.

- `DataJpaTestSupport`를 db 모듈 테스트 공통 기반으로 사용한다.
- 테스트 설정은 별도 yml이 아니라 `DataJpaTestSupport`의 annotation property와 `DynamicPropertySource`로 주입한다.
- PostgreSQL Testcontainers를 사용해 실제 PostgreSQL 컬럼 타입, enum, jsonb, Flyway migration 영향을 확인한다.
- Entity 단독 매핑 테스트보다 repository adapter 테스트를 우선한다.
  - command repository adapter는 aggregate 저장/복원 흐름을 검증한다.
  - query finder adapter는 조회 usecase로 보고 projection, join, 검색 조건, 정렬, cursor 같은 쿼리 의미를 검증한다.
- 테스트 클래스는 도메인 패키지별로 둔다.
- 테스트 구조는 DCI 중첩 구조를 쓰지 않고 flat하게 작성한다.
- 테스트 본문은 `given / when / then` 흐름을 유지한다.
- assertion이 여러 개면 `SoftAssertions.assertSoftly`를 사용한다.

즉 db 테스트의 핵심은 “컬럼 하나가 잘 들어갔다”보다 “support/db adapter가 core port 계약에 맞는 aggregate 또는 query model을 복원한다”에 둔다. 복잡한 Querydsl은 단순 매핑이 아니라 조회 usecase의 구현이므로, 실패 비용이 큰 projection과 paging부터 테스트한다.

### 2.6 notification 현재 구조와 리팩토링 포인트

```text
notification
├── core
│   ├── command/application/NotificationService.java
│   ├── command/implementation/NotificationSender.java
│   ├── command/implementation/NotificationTargetStorage.java
│   ├── command/event/NotificationEvent.java
│   ├── query/application/NotificationFinder.java
│   └── domain/Notification.java, NotificationRepository.java
│
├── api
│   ├── NotificationCommandController.java
│   ├── NotificationQueryController.java
│   ├── NotificationSseController.java
│   └── sse/SseSender.java, SseConnectionManager.java
│
└── support
    ├── db/NotificationRepositoryImpl.java
    ├── global-cache/FcmTargetStorage.java
    └── push-notification/FcmSender.java
```

현재 notification은 멀티모듈 경계를 확인하기 좋은 지점이다. `NotificationSender`, `NotificationTargetStorage`, `NotificationRepository`는 core에 있는 port이고, SSE/FCM/Redis/DB 구현체는 api 또는 support에 있다. 이 방향은 맞다.

다만 `NotificationService` 안에 아직 개선 여지가 있다.

- `@Async("notificationExecutor")`와 `@TransactionalEventListener`는 Spring 실행 방식에 가깝다.
- `isFcmSender`가 클래스 이름에 `Fcm`이 들어가는지 확인한다.
- 알림 제목/메시지 포맷팅, 전송 대상 조회, sender별 전송, 이력 저장이 한 클래스에 모여 있다.

다음 단계에서는 다음처럼 나눌 수 있다.

```text
NotificationService
├── markAsRead
├── registerTarget
└── deleteTarget

NotificationEventHandler
└── transaction commit 이후 event 수신, async 실행

NotificationMessageFormatter
└── NotificationEvent -> title/message

NotificationDispatcher
└── sender 목록으로 전송

NotificationHistoryWriter
└── 저장 대상 알림 이력 생성 및 저장
```

중요한 점은 “core에서 Spring을 완전히 제거하자”가 아니다. 비동기 실행, 트랜잭션 이벤트 처리, sender 선택 같은 실행 정책과 비즈니스 정책이 한 클래스에서 섞이지 않게 하는 것이다.

## 3. 클래스 및 메서드 네이밍 컨벤션

### 3.1 command application

`*Service`는 하나의 외부 usecase를 표현한다. Controller나 batch job이 직접 호출하는 진입점이다.

```text
RentService.register(command, memberId)
RentService.update(command, memberId)
RentService.join(command, memberId)
NotificationService.markAsRead(member, command)
```

규칙:

- 메서드 이름은 API 동사가 아니라 업무 행위로 짓는다.
- `create`보다 도메인에서 쓰는 `register`, `join`, `close`, `cancel`을 우선한다.
- Service는 세부 구현을 직접 길게 들고 있지 않고, 작은 행위자들을 조합한다.
- 트랜잭션은 기본값처럼 붙이지 않고 usecase별로 필요성을 판단한다.
- 여러 변경의 원자성, dirty checking, 동시성 제어, 일관된 조회 스냅샷이 필요한 경우에만 application service에 `@Transactional`을 둔다.
- 단일 저장/삭제처럼 repository 구현체의 트랜잭션으로 충분한 경우에는 application service에 트랜잭션을 두지 않을 수 있다.

### 3.2 command implementation

`implementation` 패키지는 application과 domain 사이에서 업무 단위 행위자를 둔다. 이름은 역할이 바로 보이게 짓는다.

```text
*Reader       엔티티 조회, not found 처리
*Writer       저장/삭제
*Register     등록에 필요한 도메인 객체 생성
*Updater      수정 정책 적용
*Joiner       참여 행위 처리
*Canceller    취소 행위 처리
*Refresher    외부 데이터 기준 갱신
*Registry     등록 후보 수집 및 저장 흐름
*Port         외부 시스템 호출 추상화
```

예시:

```java
Rent rent = rentReader.getById(command.rentId());
rent.validateMine(memberId);
rentUpdater.update(rent, command);
rentWriter.save(rent);
```

이 코드는 구현 세부보다 업무 흐름이 먼저 읽힌다. “대절을 가져오고, 내 글인지 확인하고, 수정하고, 저장한다”는 흐름이다.

### 3.3 domain

`domain`은 업무 규칙을 담는다.

```text
Rent.validateMine(memberId)
Rent.close()
Notification.read()
Survey.close()
```

규칙:

- Aggregate는 자기 상태를 바꾸는 규칙을 직접 가진다.
- 다른 aggregate를 객체로 직접 참조하지 않고 ID로 참조한다.
- repository interface는 domain에 둔다.
- 예외는 `CustomException`과 도메인별 `*ErrorCode`를 사용한다.
- 값이 여러 필드로 묶여 의미를 가지면 Value Object로 분리한다.

동시성 제어가 중요한 command는 예외적으로 repository에 원자적 update 메서드를 둘 수 있다. 예를 들어 탑승 슬롯의 인원 예약처럼 `read -> modify -> write`로 처리하면 경합 상황에서 정합성이 깨질 수 있는 경우, DB의 조건부 `UPDATE` 한 번으로 검증과 변경을 함께 수행하는 편이 안전하다. 이런 메서드는 별도 port로 과하게 분리하기보다 해당 aggregate repository에 두고, `implementation` 계층의 행위자(`*Joiner`, `*Closer`, `*Writer` 등)가 업무 행위로 감싸서 사용한다. 기술적 update 방식이 application 이상 계층으로 번지지 않게 막기 위한 트레이드오프다.

### 3.4 query

조회는 command와 분리한다. 조회 결과가 화면/API 요구사항에 가까우면 domain 객체를 그대로 노출하지 않고 query model을 둔다. command 쪽 `Repository`는 aggregate 복원과 저장을 위한 port로 한정하고, query 쪽은 `Repository` 이름을 쓰지 않는다. 이름부터 command 저장소와 query 조회 모델 접근을 분리한다.

```text
*Finder        조회 usecase 진입점
*FinderPort    조회 전용 port
*Result        API 응답으로 변환되기 전 결과 모델
*Summary       목록 요약 모델
*Detail        상세 모델
```

예시:

```text
ConcertFinder
ConcertFinderPort
ConcertSummary
ConcertDetailResult
```

조회는 성능 요구가 강한 경우가 많다. 그래서 command domain model을 억지로 재사용하기보다, Querydsl/JPA projection 등 조회에 맞는 모델을 따로 둘 수 있다. 외부로 노출되는 조회 usecase가 있는 도메인은 `*Finder`를 진입점으로 두고, DB adapter는 `*FinderPort`를 구현한다. 내부 검증이나 상태 변경을 위한 조회는 command 쪽 `*Reader`와 `Repository`를 사용한다.

### 3.5 support 구현체

support 구현체는 어떤 port를 구현하는지 이름에 드러낸다.

```text
ConcertRepositoryImpl        ConcertRepository 구현, command aggregate 저장소 adapter
ConcertFinderAdapter         ConcertFinderPort 구현, query read model 조회 adapter
FcmSender                    NotificationSender 구현
FcmTargetStorage             NotificationTargetStorage 구현
S3StorageAdapter             StorageWriter/Reader 구현
OAuthLoginKakaoImpl          OAuthLogin 구현
KopisConcertDataSync         ConcertDataSyncPort 구현
```

규칙:

- core port 이름을 기준으로 구현체 이름을 맞춘다.
- command 저장소 구현은 `*RepositoryImpl`, query 조회 port 구현은 `*FinderAdapter`를 사용한다.
- 벤더 이름은 구현체 또는 내부 패키지에 둔다.
- Entity/VO/Client/Response 같은 기술 객체는 support 안에 둔다.
- domain 객체와 persistence entity 변환은 support adapter 책임이다.

## 4. 매개변수 순서 컨벤션

매개변수 순서는 읽는 사람이 “무엇을 대상으로, 어떤 입력으로, 누가 수행하는지” 자연스럽게 이해할 수 있어야 한다. 현재 코드에는 `command, memberId`와 `member, command`가 섞여 있으므로, 앞으로는 아래 기준으로 맞춘다.

### 4.1 application service

```text
public Long register(final RentRegisterCommand command, final Long memberId)
public void update(final RentUpdateCommand command, final Long memberId)
public void markAsRead(final Member member, final NotificationIdCommand command)
```

권장 순서:

```text
command/request → actorId/memberId
```

Controller에서 이미 인증 객체를 받아 command로 변환하는 경우, core application에는 `Member` 전체보다 `memberId`를 넘기는 편을 우선한다. core usecase가 회원의 모든 상태를 필요로 하지 않는데 `Member` aggregate를 받으면 의존 범위가 커진다.

예외적으로 회원 상태 자체가 업무 판단에 필요하면 `Member`를 받을 수 있다.

### 4.2 domain method

```text
rent.validateMine(memberId)
participant.update(depositor, passengerNum, refundType, refundAccount, boardingDate)
```

권장 순서:

```text
대상 aggregate 내부 상태 기준 → 업무 판단에 필요한 값 → 부가 값
```

domain method는 이미 receiver가 대상이다. 그래서 첫 번째 매개변수에 다시 대상 ID를 넣기보다, 검증이나 변경에 필요한 값만 받는다.

### 4.3 port/interface

```text
findByIdAndRecipientId(id, recipientId)
save(memberId, target)
sendMessage(target, title, message)
```

권장 순서:

```text
식별자/대상 → 변경 또는 전달할 값 → option
```

조회 조건이 여러 개라면 더 좁은 식별자를 먼저 둔다. 전송 계열은 대상이 먼저 오고, 그다음 내용이 온다.

## 5. 각 모듈과 패키지 역할

### 5.1 allreva-core

비즈니스 중심 모듈이다. API 서버인지 batch 서버인지, DB가 무엇인지, Redis를 쓰는지, FCM을 쓰는지 몰라야 한다.

포함할 것:

- usecase service
- domain aggregate, value object, enum, policy
- repository interface
- external port interface
- command input model
- domain/application event
- query usecase 및 query port

포함하지 않을 것:

- Controller
- JPA Entity
- Redis repository
- Feign client
- S3 client
- Firebase SDK 직접 호출
- HTTP request/response 객체

### 5.2 allreva-api

HTTP API 서버 모듈이다. REST, security, SSE, web configuration처럼 API 서버 실행에 필요한 코드를 둔다.

역할:

- HTTP request DTO를 검증한다.
- request를 command로 변환한다.
- 인증 정보를 application usecase에 필요한 actor 정보로 변환한다.
- core usecase를 호출한다.
- response view를 만든다.
- SSE처럼 HTTP 연결 특성이 강한 adapter를 둔다.

Bean Validation 기준은 API layer에 둔다. `@NotBlank`, `@NotNull`, `@Size`, `@Pattern`, `@Email`, `@Valid` 같은 annotation은 HTTP 입력 형식을 검증하는 책임에 가깝다. 같은 core usecase라도 REST API, batch, event consumer의 입력 출처와 검증 방식이 달라질 수 있으므로 core command/input model이 Bean Validation에 의존하지 않게 한다.

권장 흐름은 다음과 같다.

```text
HTTP request DTO(@Valid) → controller → core command/input → application usecase → domain
```

규칙:

- controller implementation method의 request body에는 `@Valid`를 둔다.
- Swagger interface는 문서용 annotation 중심으로 두고 runtime validation annotation 중복을 피한다.
- request DTO field에는 형식 검증 annotation을 둔다.
- nested request DTO가 있으면 field에 `@Valid`를 둔다.
- core command/input model에는 Bean Validation annotation을 두지 않는다.
- domain이 반드시 지켜야 하는 비즈니스 불변식은 domain 생성자, factory, method에서 직접 검증한다.
- core application은 권한, 존재 여부, 상태 전이 같은 업무 검증을 담당한다.

예를 들어 이메일 형식, 문자열 길이, 필수 입력 여부는 API request DTO에서 검증한다. 이미 가입된 회원인지, 모집에 참여할 수 있는 상태인지, 소유자가 맞는지는 core usecase/domain에서 검증한다.

### 5.3 allreva-batch

batch/scheduler 실행 모듈이다. 현재는 `scheduler-server`를 따로 만들지 않고 `allreva-batch` 안에 scheduler 진입점을 함께 둔다.

역할:

- `@Scheduled` 진입점
- Spring Batch job/step 진입점
- 이벤트 소비 진입점
- 배치 전용 설정, 락, 시간 제어

나중에 scheduler와 batch worker의 배포 주기, 장애 격리, 자원 튜닝 기준이 달라지면 `scheduler-server` 분리를 검토한다.

### 5.4 allreva-support:db

영속성 구현 모듈이다.

역할:

- JPA Entity 정의
- Spring Data JPA repository
- JDBC batch repository
- Querydsl finder
- core command repository port 구현
- core query finder port 구현
- domain ↔ entity 변환

DB 스키마나 ORM 세부사항은 이 모듈 안에 둔다. db layer 안에서도 command와 query를 분리한다. `*RepositoryImpl`은 aggregate를 복원하고 저장하는 command adapter로 둔다. `*FinderAdapter`는 `*FinderPort`를 구현하고, 화면/API 조회에 필요한 read model projection을 반환한다. query adapter는 domain aggregate를 억지로 복원하지 않는다.

### 5.5 allreva-support:global-cache

Redis처럼 프로세스 밖에 있고 여러 인스턴스가 공유하는 cache/storage 구현 모듈이다.

역할:

- refresh token 저장소 구현
- notification target 저장소 구현
- popular keyword 저장소 구현
- search query cache/finder adapter 구현
- Redis config
- `global-cache-config/application-{dev,stag,prod}.yml` 소유

Redis 테스트는 실제 Redis 동작에 의존하는 adapter 계약을 검증한다. `GlobalCacheTestSupport`를 두고 Redis Testcontainers, `RedisConfig`, adapter bean만 올리는 좁은 context를 사용한다. 테스트마다 Redis key를 비워 prefix 충돌을 막는다.

우선순위는 다음과 같다.

1. `RefreshTokenStorageAdapter`: token/member 양방향 저장, 조회, 삭제, 전체 삭제, TTL 존재 여부.
2. `FcmTargetStorage`: 단건 저장/조회/삭제, 여러 member target 조회 순서.
3. `PopularKeywordRepositoryImpl`: 검색어 count 증가, top10 역순 조회, rank snapshot 저장/조회, count 반감.
4. `SearchFinderAdapter`: 저장된 domain rank를 query result로 변환.

### 5.6 allreva-support:local-cache

애플리케이션 프로세스 내부 cache 설정을 둔다. 여러 서버 간 강한 동기화가 필요 없고, 데이터 변경 주기가 낮은 조회 최적화에 사용한다.

### 5.7 allreva-support:storage

파일 저장소 adapter 모듈이다.

역할:

- S3 설정
- `StorageProperties` 바인딩
- presigned URL 생성
- storage port 구현
- `storage-config/application-{dev,stag,prod}.yml` 소유

### 5.8 allreva-support:oauth-client

OAuth2/OIDC provider 연동 모듈이다. OAuth2 자체는 기술 로직이다. 로그인 방식이나 provider가 바뀌어도 core 비즈니스 규칙은 크게 바뀌면 안 된다.

단, “인가된 사용자가 어떤 역할을 가진다”, “어떤 권한으로 어떤 업무를 수행할 수 있다”는 비즈니스에 가깝다. 인증 방식은 support/api에 두고, 인가 판단은 core의 usecase나 domain 규칙으로 끌어올리는 편이 낫다.

### 5.9 allreva-support:kopis-client

KOPIS 외부 API 연동 모듈이다.

역할:

- Feign client
- `KopisProperties` 바인딩
- KOPIS response model
- KOPIS response-to-domain mapper
- 공연/공연장 동기화 port 구현
- rate limit 제어
- `kopis-client-config/application-{dev,stag,prod}.yml` 소유

### 5.10 allreva-support:push-notification

Push notification 구현 모듈이다.

역할:

- Firebase 초기화
- `FcmProperties` 바인딩
- FCM message 생성
- FCM client 호출
- `NotificationSender` 구현
- `push-notification-config/application-{dev,stag,prod}.yml` 소유

`build.gradle`은 OpenFeign, Firebase SDK, Jackson annotation 의존성만 직접 선언한다. Spring Context/Web 의존성은 OpenFeign starter 전이 의존성으로 사용하며, core에는 `NotificationSender` port만 노출한다.

FCM device token은 `global-cache`의 Redis adapter에 저장한다. 현재 push delivery target은 best-effort 성격이며 필수 영구 비즈니스 데이터가 아니다. Redis 장애나 flush로 token이 사라져도 클라이언트가 재방문하거나 로그인할 때 token register API로 복구할 수 있다. 알림 이력은 notification DB record로 남기고, 장기간 미접속 사용자에게도 push delivery가 반드시 보장되어야 하는 요구가 생기면 DB-backed token storage로 전환을 재검토한다.

### 5.11 allreva-test

통합 테스트 모듈이다. 여러 실행/지원 모듈을 조합해 실제 wiring이 깨지지 않았는지 검증한다.

## 6. domain, implementation, domain service의 경계

이 구조에서 가장 헷갈리기 쉬운 부분은 domain과 implementation의 경계다. 둘 다 로직을 가질 수 있기 때문이다. 중요한 것은 “로직이 있느냐 없느냐”가 아니라, 그 로직이 어떤 종류의 로직이냐이다.

### 6.1 domain

`domain`은 도메인 객체가 스스로 지켜야 하는 규칙을 담는다.

```text
domain = 무엇이 맞고 틀린가
```

역할:

- 자기 상태를 바꾸는 행위
- 항상 지켜져야 하는 불변식
- 도메인 용어로 설명할 수 있는 판단
- 기술을 몰라도 수행할 수 있는 규칙

예시:

```java
rent.validateMine(memberId);
rent.close();
participant.cancel(memberId);
notification.read();
```

이런 코드는 domain에 두는 것이 자연스럽다. `Rent`가 닫힐 수 있는지, 이미 닫힌 모집인지, 작성자만 수정할 수 있는지 같은 판단은 `Rent`의 규칙이다. 이 규칙이 바깥으로 흩어지면 domain 객체는 상태만 가진 데이터 박스가 되고, 진짜 업무 규칙은 Service나 implementation에 숨어버린다.

나쁜 예시는 다음과 같다.

```java
public class RentCloser {
    public void close(Rent rent) {
        if (rent.getStatus() == CLOSED) {
            throw new CustomException(RentErrorCode.ALREADY_CLOSED);
        }
        rent.setStatus(CLOSED);
    }
}
```

이렇게 되면 `Rent`는 자기 상태가 언제 바뀌어도 되는지 모른다. 닫기 규칙을 아는 쪽은 `RentCloser`가 된다.

더 나은 방향은 다음과 같다.

```java
public class Rent {
    public void close() {
        validateClosable();
        this.status = CLOSED;
    }
}
```

`Rent`가 자기 규칙을 직접 지키고, 바깥에서는 그 행위를 호출한다.

### 6.2 implementation

`implementation`은 usecase를 완성하기 위한 작은 행위자다.

```text
implementation = 그 일을 하기 위해 무엇을 가져오고 누구를 호출하는가
```

역할:

- repository 조회/저장 감싸기
- not found 처리
- 여러 domain 객체 조합
- 외부 port 호출
- domain method 호출 전후 절차 구성
- transaction 범위가 작은 행위 단위로 필요할 때 경계 제공

예시:

```java
Rent rent = rentReader.getById(rentId);
rent.validateMine(memberId);
rent.close();
rentWriter.save(rent);
```

여기서 `rent.close()`는 domain 규칙이다. `rentReader.getById()`와 `rentWriter.save()`는 usecase를 진행하기 위한 행위다. 즉 implementation은 비즈니스 흐름에 참여하지만, domain 객체가 책임질 수 있는 규칙까지 빼앗아 오지는 않는다.

현재 Allreva의 `implementation` 패키지는 글에서 말하는 `Activity`에 가깝다. “비즈니스 구현체”라기보다 “usecase 행위자”로 이해하는 편이 좋다.

### 6.3 domain service / policy

`domain service` 또는 `policy`는 도메인 규칙이지만 특정 entity 하나에 넣기 애매할 때 사용한다.

```text
domain service / policy = 도메인 규칙인데, 한 aggregate의 책임으로 넣으면 어색한 것
```

사용할 수 있는 경우:

- 여러 aggregate를 함께 봐야 하는 규칙
- 외부에서 주어진 정책/계산기가 필요한 규칙
- 특정 entity 하나에 넣으면 책임이 과해지는 판단
- 기술 의존은 없지만, 단일 객체의 상태만으로 판단하기 어려운 규칙

예시:

```java
public class RentSeatReservationPolicy {
    public void validateReservable(
            final Rent rent,
            final List<RentParticipant> participants,
            final int requestedSeatCount
    ) {
        if (rent.remainingSeats(participants) < requestedSeatCount) {
            throw new CustomException(RentErrorCode.SEAT_NOT_ENOUGH);
        }
    }
}
```

좌석 예약 가능 여부가 `Rent` 하나만으로 판단되면 `Rent` 안에 두면 된다. 하지만 이미 참여한 사람 목록이나 다른 aggregate 상태까지 봐야 한다면 별도 policy가 더 자연스럽다. 그렇다고 application service에 조건문을 직접 박아두면 업무 규칙이 흐름 코드에 섞인다.

### 6.4 application service

`application service`는 사용자 요청 하나에 대응하는 usecase 흐름을 보여준다.

```text
application = 사용자 요청 하나를 어떤 순서로 처리하는가
```

역할:

- command/request 기준 usecase 시작
- reader/writer/행위자 조합
- transaction boundary 설정
- domain event 발행
- 외부 adapter에 직접 의존하지 않고 port/행위자를 통해 흐름 구성

예시:

```java
@Transactional
public Long join(final RentJoinCommand command, final Long memberId) {
    Rent rent = rentReader.getById(command.rentId());

    RentParticipant participant = rentJoiner.join(rent, command, memberId);
    RentParticipant saved = rentWriter.save(participant);

    Events.raise(RentJoinedEvent.from(rent, saved));
    return saved.getId();
}
```

이 코드는 “대절 참여” usecase가 어떤 순서로 진행되는지 보여준다. 하지만 좌석이 충분한지, 참여자가 취소 가능한지 같은 세부 규칙은 domain 또는 policy로 내려가는 편이 좋다.

### 6.5 판단 기준

경계가 애매할 때는 아래 기준으로 판단한다.

```text
if 조건이 도메인 상태/정책 판단이면 domain 또는 domain service로 둔다.
if 조건이 null/not found/외부 호출 실패 처리면 implementation으로 둔다.
repository에서 꺼내야 판단 가능하면 implementation이 가져오고, 판단 자체는 domain/domain service가 한다.
여러 aggregate를 봐야 하는 순수 업무 규칙이면 domain service/policy로 둔다.
트랜잭션과 이벤트 발행 순서라면 application 또는 implementation에 둔다.
```

예시는 다음과 같다.

```java
// implementation: 필요한 객체를 가져온다.
Rent rent = rentReader.getById(command.rentId());
List<RentParticipant> participants = rentParticipantReader.findByRentId(rent.getId());

// domain service / policy: 여러 객체를 보고 업무 규칙을 판단한다.
seatReservationPolicy.validateReservable(rent, participants, command.passengerNum());

// domain: 자기 상태를 바꾼다.
RentParticipant participant = rent.join(command.toDepositor(), memberId);
```

정리하면 다음과 같다.

```text
domain
- 자기 상태 변경
- 자기 불변식 보호
- 예: rent.close(), rent.validateMine()

domain service / policy
- 도메인 규칙인데 entity 하나에 넣기 애매함
- 여러 도메인 객체/정책 조합
- 기술 의존 없음
- 예: RentSeatReservationPolicy

implementation
- usecase 행위자
- 조회, 저장, not found, 외부 port 호출, domain method 조합
- 예: RentReader, RentWriter, RentJoiner

application service
- 외부 요청 하나에 대응하는 usecase 흐름
- 행위자 조합
- 이벤트 발행
- 예: RentService.register()
```

implementation 행위자 이름은 `{domain}{행위자}` 형태를 기본으로 한다. 도메인이 먼저 오고, 뒤에 역할을 붙인다. 예를 들어 `RentReader`, `RentWriter`, `RentJoiner`, `NotificationReader`, `NotificationMarker`, `NotificationNotifier`처럼 쓴다. `RentDomainReader`, `NotificationCommandHandler`, `NotificationUseCaseProcessor`처럼 계층명이나 흐름명을 섞어 길게 만들지 않는다.

implementation 메서드 이름은 가능한 한 행위만 짧게 드러낸다. 매개변수에 이미 조건이 드러나면 메서드명에 다시 풀어 쓰지 않는다.

```java
// 선호
notificationReader.get(notificationId, memberId);
notificationTargetReader.get(memberId);
rentReader.get(rentId);

// 지양
notificationReader.getByIdAndRecipientId(notificationId, memberId);
notificationTargetReader.findTargetByMemberId(memberId);
rentReader.getById(rentId);
```

다만 repository, query repository, Spring Data 파생 메서드는 예외다. 이들은 저장소 조회 조건을 메서드명으로 표현해야 하거나 프레임워크가 메서드명을 해석하기 때문이다. 예를 들어 `findByIdAndRecipientId`, `findAllByTitle`은 repository/query 계층에서는 허용한다.

## 7. 기술 로직과 비즈니스 로직 구분

기술 로직과 비즈니스 로직을 나누는 기준은 “기술이 바뀌었을 때 core가 변해야 하는가”다.

예시:

```text
OAuth2 authorization code 교환      기술 로직
Kakao user info 조회                기술 로직
JWT 파싱                            기술 로직
현재 사용자가 어떤 역할인지 판단     비즈니스/인가 로직
특정 회원이 모집글을 수정할 수 있는지 비즈니스 규칙
FCM token 저장                      기술 로직
알림을 누구에게 보내야 하는지        비즈니스 규칙
```

OAuth2 provider가 Kakao에서 다른 provider로 바뀌어도 회원 가입 정책이나 역할 부여 정책이 통째로 흔들리면 안 된다. FCM에서 APNS나 Web Push를 추가하더라도 “알림 이벤트를 만들고, 수신자를 결정하고, 이력을 저장한다”는 core 흐름은 유지되어야 한다.

이 기준을 계속 적용하면 모듈 경계가 더 선명해진다. 기술은 바뀔 수 있고, 비즈니스 규칙은 제품의 이유가 바뀔 때만 바뀌어야 한다.

## 8. 앞으로 지킬 규칙

- core는 api, batch, support를 참조하지 않는다.
- application은 usecase 흐름을 보여주고, 세부 행위는 implementation 행위자에게 맡긴다.
- domain은 업무 규칙과 상태 변경을 책임진다.
- repository interface와 external port는 core에 둔다.
- 구현체는 support 또는 api/batch adapter에 둔다.
- aggregate 간 직접 객체 참조는 피하고 ID로 참조한다.
- 예외는 `CustomException`과 도메인별 `*ErrorCode`를 사용한다.
- Service에 `@Transactional`을 무분별하게 붙이지 않는다. usecase가 여러 저장 작업의 원자성, dirty checking, 동시성 제어, 일관된 조회 스냅샷을 필요로 할 때만 트랜잭션을 둔다. 단일 저장/삭제처럼 repository 구현체의 트랜잭션으로 충분한 경우에는 application service에 트랜잭션을 두지 않을 수 있다.
- 모듈은 처음부터 과하게 쪼개지 않고, 변경 임계점이 왔을 때 분리한다.
- 기술 이름보다 역할 이름을 먼저 쓰고, 기술이 충분히 커졌을 때 별도 모듈로 승격한다.
