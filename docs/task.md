# Refactor Task Ledger

## Current objective

Prepare API cleanup after support module boundaries and configuration ownership are stabilized in `refactor/#94-multi-module-migration`.

## Completed in current branch

### Core/application boundary

- Notification command flow split from generic message sending.
- Rent/survey notification listeners moved into application layer.
- Notification target reader/writer ports split.
- Notification sender dispatch changed from class-name detection to explicit target type.
- Query-side repository ports renamed to `*FinderPort` and db adapters to `*FinderAdapter`.
- Command repositories narrowed by removing query-only methods.
- Member command usecases split into orchestration and smaller actors.
- Auth command output package renamed and auth member lookup moved to member domain reader.
- Refresh token port/adapter renamed to storage naming.
- Concert sync split into period, registry, candidate selector, refresher, writer.
- Popular keyword command flow split into counter, rank reader/writer, calculator, refresher.

### Support/db

- Command repository adapters and query finder adapters separated.
- Entity embedded value objects flattened to table-column-like persistence fields.
- DB module repository/finder adapter tests added with `DataJpaTestSupport`.
- `db.yml` owns DB/JPA/Flyway settings only.
- Redis and S3 settings moved out of db config.
- DB support tests use annotation properties and `DynamicPropertySource`; no support test yml.

### Support/oauth-client

- Kakao OAuth config/properties moved into oauth-client module.
- `KakaoOAuthIdentityVerifier` kept as OAuth identity port adapter.
- `KakaoOAuthMemberMapper` added for Kakao response to core `OAuthMember` mapping.
- `oauth-client.yml` added.
- `OAuthClientTestSupport` uses narrow context, WireMock extension, `DynamicPropertySource`; no test yml.

### Support/kopis-client

- KOPIS response-to-domain mapping extracted from XML response DTOs into mapper components.
- `KopisProperties` added.
- `KopisFeignConfig` no longer uses direct `@Value` for service key.
- `kopis-client.yml` added.
- Mapper and Feign interceptor tests added.

### Support/push-notification

- FCM package moved under `notification/fcm`.
- `FcmProperties` and `FcmPushNotificationConfig` added.
- `FcmSender` no longer uses direct `@Value` for project id.
- `push-notification.yml` added.
- `FcmSenderTest` and `PushNotificationTestSupport` added.

### Support/storage

- `storage.yml` added.
- `StorageProperties` added.
- `S3Config` and `S3StorageAdapter` no longer use direct `@Value`.
- `StoragePropertiesTest` added.

### Support/global-cache

- `global-cache.yml` added.
- Redis config moved out of db config.
- `GlobalCacheTestSupport` added with Redis Testcontainers and `DynamicPropertySource`.
- `RefreshTokenStorageAdapter`, `FcmTargetStorage`, `PopularKeywordRepositoryImpl`, and `SearchFinderAdapter` tests added.

### Config ownership rule

- Support modules own their runtime config as one `{module}.yml` file in each resources root and split profile differences with `spring.config.activate.on-profile`.
- dev profile keeps local constants where safe; public-repo unsafe keys and stag/prod values use environment placeholders and are overridden by one external `secret.yml` imported through `SPRING_CONFIG_IMPORT` in deployment.
- Support modules do not keep test `application.yml` or `application-test.yml`.
- Support unit/slice tests inject properties through support classes or test annotations.
- `allreva-api` and `allreva-batch` import only the support configs they need.
- `allreva-test` keeps its own integration-test `application.yml` because it wires the whole application.
- Swagger interfaces keep documentation concerns only; runtime validation annotations live on controller implementations and API request DTOs.

## Redis test plan

Create `allreva-support:global-cache` tests before or alongside API cleanup if Redis behavior changes.

Proposed structure:

```text
allreva-support/global-cache/src/test/java/com/backend/allreva/support/GlobalCacheTestSupport.java
allreva-support/global-cache/src/test/java/com/backend/allreva/auth/RefreshTokenStorageAdapterTest.java
allreva-support/global-cache/src/test/java/com/backend/allreva/notification/fcm/FcmTargetStorageTest.java
allreva-support/global-cache/src/test/java/com/backend/allreva/search/redis/PopularKeywordRepositoryImplTest.java
allreva-support/global-cache/src/test/java/com/backend/allreva/search/redis/SearchFinderAdapterTest.java
```

Test support design:

- Use Redis Testcontainers with `redis:7-alpine`.
- Use narrow Spring context: `RedisConfig` plus target adapter beans only.
- Set `spring.data.redis.host` and `spring.data.redis.port` via `@DynamicPropertySource`.
- Flush Redis before each test to isolate keys.
- Do not create test yml.

Coverage priorities:

1. `RefreshTokenStorageAdapter`
   - save stores token→member and member→token.
   - find by token and by member id returns same `RefreshToken`.
   - delete removes both keys.
   - deleteAll removes all refresh-token keys.
   - TTL exists for both keys.
2. `FcmTargetStorage`
   - save/get/delete device token.
   - getAll returns targets for member ids in request order.
3. `PopularKeywordRepositoryImpl`
   - recordSearch increments ZSet score.
   - getTop10Keywords returns reverse-score order and max 10.
   - updateRank/getPopularKeywordRank round-trips `PopularKeywordRanks`.
   - decreaseAllKeywordCount halves existing scores and handles empty key.
4. `SearchFinderAdapter`
   - maps saved popular keyword rank domain items to query results.
   - returns empty list when no rank snapshot exists.

## Remaining before API cleanup

- Review `global-cache/common/config` package naming if common config should move closer to `redis` package.

## API cleanup scope

- Keep controllers focused on request validation, command conversion, response conversion.
- Keep core command/input objects free of Bean Validation annotations.
- Keep response DTO assembly in API layer.
- Keep authentication principal conversion at API boundary.
- Keep SSE and HTTP connection concerns in API adapter code.
- Review Swagger interface vs controller implementation responsibilities.
- Review common response, exception handler, security config, and thread pool config as execution-module concerns.

## API validation migration plan

Goal: move HTTP input-format validation to API request DTOs and keep core command/input models free of Bean Validation.

Progress:

- Architecture rule added: Bean Validation belongs to API request DTOs, while core keeps business/domain validation.
- Current inventory shows no Bean Validation annotations in `allreva-core/src/main/java`.
- Member command request DTOs now carry validation annotations and controller request bodies use `@Valid`.
- Rent command API no longer uses core command input records as request body models for id/join-update flows.
- `RentIdRequest`, `RentJoinIdRequest`, and `RentJoinUpdateRequest` added in API layer and convert to core commands.
- Survey request validation tests moved from core service validation expectation to API request DTO validation.
- Numeric request fields that need null/basic value checks use wrapper types in API request DTOs.
- Root test dependency on `jakarta.validation-api` removed so validation dependency stays with API module.
- KOPIS Feign client scan narrowed to KOPIS clients to avoid duplicate Feign client specifications during full integration context startup.

Steps:

1. Inventory current validation annotations.
   - Run `rg "jakarta.validation|javax.validation|@Valid|@Not|@Size|@Pattern|@Email" allreva-core allreva-api`.
   - Split findings into API request DTOs, controller parameters, and core command/input/domain models.
2. Move format validation to API request DTOs.
   - Add missing field annotations to `allreva-api/**/request/*` DTOs.
   - Add nested `@Valid` where request DTO contains another request DTO/list.
   - Ensure controller `@RequestBody` parameters use `@Valid`.
3. Remove Bean Validation from core command/input.
   - Remove validation imports/annotations from `allreva-core/**/command/input/*` and other core input models.
   - Keep constructors/records simple unless they enforce real domain invariants.
4. Preserve business validation in core.
   - Existence checks, ownership checks, role checks, state transitions, and domain invariants remain in application/domain code.
   - Use `CustomException` + domain `*ErrorCode` for business failures.
5. Update tests.
   - API/controller/integration tests should expect request validation failures at API boundary.
   - Core tests should no longer depend on Bean Validation exceptions.
6. Verify.
   - Minimum: `./gradlew spotlessApply spotlessCheck :allreva-core:compileJava :allreva-api:compileJava :allreva-test:compileTestJava`.
   - Run affected integration tests by domain after inventory.

## Out of scope

- Remove rent/survey registered domain events entirely.
- Remove unused SSE sender/controller code unless API cleanup directly touches it. SSE remains for now.

## Decisions

- FCM device token storage remains Redis-backed for now.
  - Push delivery target is best-effort and not a required durable business record.
  - If Redis loses token values, clients can re-register tokens on revisit/login.
  - Durable notification history remains DB-backed through notification records.
  - Revisit DB-backed token storage only if push delivery becomes business-critical for long-idle users.

## Checks run recently

- `./gradlew spotlessApply spotlessCheck :allreva-support:oauth-client:test :allreva-support:push-notification:test :allreva-support:kopis-client:test :allreva-support:db:compileTestJava :allreva-api:compileJava :allreva-batch:compileJava :allreva-test:compileTestJava` passed.
- `./gradlew spotlessApply spotlessCheck :allreva-support:storage:test :allreva-support:kopis-client:test :allreva-api:compileJava :allreva-batch:compileJava` passed.
- `./gradlew spotlessApply spotlessCheck :allreva-support:global-cache:test` passed.
- `./gradlew spotlessApply spotlessCheck :allreva-core:compileJava :allreva-api:compileJava :allreva-test:compileTestJava :allreva-support:kopis-client:test` passed after API validation request split.
- `./gradlew :allreva-test:test --tests '*MemberIntegrationTest' --tests '*RentCommandIntegrationTest'` passed after API validation request split.
- `./gradlew spotlessApply spotlessCheck :allreva-core:compileJava :allreva-api:test --tests '*RentRequestValidationTest' --tests '*RentJoinRequestValidationTest' --tests '*SurveyRequestValidationTest' :allreva-api:compileJava :allreva-test:compileTestJava` passed after request DTO validation migration.
- `./gradlew :allreva-api:bootJar :allreva-batch:bootJar` passed after executable jar configuration split.
- PR #96 CI failed in `:allreva-test:test` with `Failed to find merged annotation for @BootstrapWith(SpringBootTestContextBootstrapper.class)` across integration tests; `allreva-api` plain jar is being re-enabled so project dependency resolution has a normal Java artifact while `bootJar` still produces `app.jar`.

## Recent commits

- `9ce558e refactor(support): 외부 설정 프로퍼티 바인딩 정리`
- `56e136e refactor(support): 외부 클라이언트 설정 모듈화`
- `517607b refactor(support): KOPIS 응답 매핑 책임 분리`
- `dd8a6b3 refactor(support): 카카오 OAuth 클라이언트 설정 정리`
