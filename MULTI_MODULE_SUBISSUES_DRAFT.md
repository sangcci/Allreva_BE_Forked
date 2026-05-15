# Multi-module sub issue drafts after #84

## 1. [REFACTOR] shared / low-risk 패키지 멀티모듈 물리 이동

### 기능 요약
`events`, `common/web`, `pagination`, batch scheduler 진입점처럼 목적지가 비교적 분명한 저위험 패키지를 root `src`에서 실제 `modules/*`, `apps/*`로 물리 이동합니다.

### 배경 및 목적
멀티모듈 전환의 다음 단계는 root `src` 의존을 줄이는 것입니다. 다만 처음부터 infra나 domain 전체를 옮기면 Gradle 의존성과 테스트 영향이 커집니다. 그래서 우선 목적지가 명확하고 의존 폭이 작은 패키지부터 이동해, 실제 멀티모듈 전환 과정에서 생기는 build gap을 먼저 드러내는 것이 목적입니다.

### 구현 방향
- [ ] `com.backend.allreva.events`를 `modules/allreva-events`로 이동합니다.
- [ ] `common/web/**`, `common/web/response/**`, `common/web/exception/**`를 `modules/allreva-api`로 이동합니다.
- [ ] `common/pagination`의 최종 소속을 정하고 이동합니다.
- [ ] `com.backend.allreva.batch.scheduler/**`를 `apps:batch-server` 소스 루트 기준으로 정리합니다.
- [ ] 이동 후 `apps:api-server`, `apps:batch-server` compile이 유지되는지 확인합니다.

---

## 2. [REFACTOR] infra adapter 멀티모듈 물리 이동

### 기능 요약
JPA, Querydsl, Redis, 외부 API client, storage adapter 등 infra 성격의 코드를 `modules/allreva-infra`로 실제 이동합니다.

### 배경 및 목적
현재 구조에서 가장 라이브러리 의존이 크고, 물리 이동 시 compile 영향이 큰 영역은 infra입니다. 이 레이어를 먼저 실제 모듈로 옮겨야 이후 application / domain 이동에서 의존 방향이 분명해집니다.

### 구현 방향
- [ ] 각 비즈니스 모듈의 `infra/**` 패키지를 실제 `modules/allreva-infra` 소스 루트로 이동합니다.
- [ ] JPA repository / Querydsl 구현 / Redis 구현 / 외부 API client / storage adapter를 포함합니다.
- [ ] `modules/allreva-infra` build.gradle에 필요한 기술 의존을 반영합니다.
- [ ] root `src`에 남아 있는 infra 관련 config의 소속을 정리합니다.
- [ ] 주요 integration test가 유지되는지 확인합니다.

---

## 3. [REFACTOR] application 레이어 멀티모듈 물리 이동

### 기능 요약
command/query service, application DTO, query port, sync/closing service 등 application 레이어 코드를 `modules/allreva-application`으로 실제 이동합니다.

### 배경 및 목적
`#79` ~ `#84`를 거치며 application 레이어의 역할이 많이 정리되었습니다. 이제 이 코드를 물리적으로 분리하면 `api`는 진입점, `application`은 유스케이스라는 구조가 빌드 레벨에서도 보이게 됩니다.

### 구현 방향
- [ ] `module/*/application/**`를 `modules/allreva-application`으로 이동합니다.
- [ ] command/query service와 application DTO를 함께 이동합니다.
- [ ] query port와 application service validation 책임도 같이 유지합니다.
- [ ] `modules/allreva-application`이 `allreva-domain`, `allreva-events`를 재사용하도록 build.gradle을 보강합니다.
- [ ] rent / survey / concert 중심 compile/test를 검증합니다.

---

## 4. [REFACTOR] domain 레이어 멀티모듈 물리 이동

### 기능 요약
도메인 모델, repository contract, enum, value object, 도메인 이벤트를 `modules/allreva-domain`으로 실제 이동합니다.

### 배경 및 목적
도메인 레이어는 가장 오랫동안 유지될 핵심 모델입니다. application / infra 정리가 어느 정도 끝난 뒤 domain을 물리적으로 떼어내야 의존 역전이 없는 구조를 확실히 만들 수 있습니다.

### 구현 방향
- [ ] `module/*/domain/**` 패키지를 `modules/allreva-domain`으로 이동합니다.
- [ ] repository interface 중 진짜 도메인 계약인 것들을 함께 이동합니다.
- [ ] enum / value object / aggregate / domain event를 함께 정리합니다.
- [ ] domain에서 web/infra 의존이 남아 있지 않은지 점검합니다.
- [ ] 이동 후 application 계층 compile이 유지되는지 확인합니다.

---

## 5. [REFACTOR] api-server 물리 이동 및 웹 진입점 정리

### 기능 요약
controller, security, advice, swagger, web config 등 API 전용 코드를 `modules/allreva-api`와 `apps:api-server` 기준으로 실제 이동합니다.

### 배경 및 목적
현재 API 관련 코드는 여전히 root `src`에 크게 의존합니다. 이 작업이 끝나야 `apps:api-server`가 더 이상 root source tree에 기대지 않고 실제 모듈 조합으로 동작하게 됩니다.

### 구현 방향
- [ ] `presentation/**`를 실제 `modules/allreva-api` 소스 루트로 이동합니다.
- [ ] controller advice, response wrapper, swagger, web/security config의 소속을 정리합니다.
- [ ] API 전용 static/resource의 위치를 정리합니다.
- [ ] `apps:api-server`가 필요한 모듈만 의존하도록 build.gradle을 보강합니다.
- [ ] 대표 API integration test를 검증합니다.

---

## 6. [REFACTOR] root source-set bridge 제거 및 테스트/리소스 정리

### 기능 요약
`apps:api-server`의 root source-set bridge(`../../src/main/java`, `../../src/test/java`)를 제거하고, 남아 있는 root `src`와 리소스를 정리합니다.

### 배경 및 목적
멀티모듈 전환이 진짜 끝났다고 말하려면 `apps:api-server`가 더 이상 legacy root source tree를 컴파일하지 않아야 합니다. 이 이슈는 그 마지막 정리 단계입니다.

### 구현 방향
- [ ] `apps:api-server/build.gradle`의 root source-set override를 제거합니다.
- [ ] `src/test/java` 테스트를 적절한 app/module 테스트 소스 루트로 이동합니다.
- [ ] `src/main/resources`의 남은 리소스(`logback-spring.xml`, static, migration ownership 등)를 정리합니다.
- [ ] root `src/main/java`가 비거나, 의도된 최소 잔여만 남도록 정리합니다.
- [ ] 최종적으로 app/module만으로 compile/test가 통과하는지 검증합니다.
