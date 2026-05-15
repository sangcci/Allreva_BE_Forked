# batch-server 전략

## 현재 결정

- 초기 실행 모듈은 `api-server`, `batch-server` 두 개로 둡니다.
- `scheduler-server`는 바로 만들지 않고, `batch-server` 내부에 scheduler 진입점을 함께 둡니다.
- `@Scheduled`, Spring Batch Job, 이벤트 소비자는 모두 `batch-server`의 진입점 후보입니다.
- 실제 비즈니스 로직은 `allreva-application` 유스케이스를 호출하도록 유지합니다.

## why

- 아직 실제 소스가 멀티모듈로 완전히 이동하지 않은 상태라서 실행 모듈을 더 쪼개면 구조 검증보다 운영 구조 논의가 먼저 커집니다.
- 초기에는 배포 단위와 설정 수를 최소화하는 편이 안전합니다.
- 대신 패키지와 설정 경계를 먼저 나눠서, 나중에 `scheduler-server`로 승격하기 쉽게 준비합니다.

## batch-server 책임

- scheduler 진입점 (`@Scheduled`)
- batch job / step 진입점
- 이벤트 소비 진입점
- 배치 전용 설정, 락, 시간 추상화

## 분리 조건

아래 조건이 생기면 `scheduler-server` 분리를 검토합니다.

- 스케줄러와 배치 워커의 배포 주기가 다름
- 장애 격리 요구가 큼
- 인스턴스 수 / 자원 튜닝 기준이 다름
- cron, lock, queue, worker 설정이 크게 갈라짐

## 현재 코드 반영

- `BatchServerApplication`은 `com.backend.allreva` 전체를 스캔합니다.
- `batch-server`는 non-web 실행 모드로 고정합니다.
- scheduling 활성화는 `batch-server`에서 시작합니다.
- 기존 `@Scheduled` 진입점은 우선 `com.backend.allreva.batch.scheduler` 패키지로 이동합니다.
- 실제 소스가 modules/apps로 완전히 이동하기 전까지는 이 scheduler 패키지가 root `src/main/java`에 남을 수 있습니다.
- 기존 application 쪽 클래스는 scheduler 자체가 아니라 재사용 가능한 sync/closing 서비스로 유지합니다.
