# Multi-module migration plan after #84

## current state

- Gradle multi-module skeleton already exists.
- `apps:api-server`, `apps:batch-server`, `modules:allreva-api`, `allreva-application`, `allreva-domain`, `allreva-infra`, `allreva-events` are created.
- Architectural policy is mostly stabilized through `#77` ~ `#84`.
- However, most production code still lives under root `src/main/java` and is compiled through `apps:api-server` source set override.
- `apps:batch-server` is partially activated as a non-web scheduler entrypoint, but reusable business code is still in root `src`.

## why the next step should be physical migration

The project has already validated:

- CQRS split direction (`rent`, `survey`, `concert`)
- event package split
- query port / infra query placement
- validation responsibility in application service
- batch/scheduler entrypoint separation

So the next bottleneck is no longer naming or policy. It is the fact that the physical source layout still does not match the logical modules.

As long as `apps:api-server` compiles root `src/main/java`, the module graph is only a skeleton. The next parent step for `#76` should explicitly include **moving remaining root sources into real modules/apps and retiring the root source-set bridge**.

## migration constraints

### 1. keep imports stable
Move files physically without changing package names first.

This gives the safest path:
- imports stay the same
- behavioral changes stay small
- compile failures mostly come from Gradle dependency gaps, not Java package churn

### 2. remove the bridge last
`apps:api-server` currently depends on root `src/main/java` and `src/test/java`.
Do not remove that bridge first.

Safer order:
1. move files gradually into `modules/*` and `apps/*`
2. keep compile green during each slice
3. remove root source-set override only after root code becomes empty or near-empty

### 3. migrate by layer slice, not whole feature rewrite
The architecture policy is already settled. The remaining work is mainly physical relocation plus dependency cleanup.

## recommended phased plan

### phase 1. module dependency wiring
Goal: make each `modules/*` build file express real dependencies before large moves.

Recommended target:

- `modules:allreva-events`
  - pure java-library
- `modules:allreva-domain`
  - depends on `allreva-events` only if domain events are referenced
- `modules:allreva-application`
  - depends on `allreva-domain`, `allreva-events`
  - includes validation / transaction-facing application code
- `modules:allreva-infra`
  - depends on `allreva-application`, `allreva-domain`, `allreva-events`
  - contains JPA, Redis, external client adapters, storage adapters
- `modules:allreva-api`
  - depends on `allreva-application`, `allreva-domain`, `allreva-infra`, `allreva-events` only where truly needed
  - contains controller / web response / security / advice / web config

This phase is mostly build-script work.

### phase 2. easiest physical moves first
Goal: move low-risk shared code with clear destination.

Recommended order:
1. `com.backend.allreva.events` -> `modules/allreva-events`
2. `com.backend.allreva.batch.scheduler` -> `apps/batch-server`
3. `common/web/**` -> `modules/allreva-api`
4. `common/pagination`, shared application-facing exceptions if still common -> target module agreed by policy

Why first:
- these packages already have strong destination clarity
- they reduce root-src usage fast
- they flush out Gradle dependency gaps early

### phase 3. infrastructure move
Goal: move adapter-heavy code out of root source tree.

Move candidates:
- `infra` packages under business modules
- JPA repositories and Querydsl impls
- Redis adapters
- external API clients
- storage upload / presigned adapters
- config needed only for infra runtime

Why before full api move:
- infrastructure depends on the widest set of libraries
- once infra is stabilized, api/application moves become simpler

### phase 4. application move
Goal: move usecase orchestration into `modules/allreva-application`.

Move candidates:
- `module/*/application/**`
- command/query services
- application DTOs
- application query ports
- sync/closing services extracted in recent refactors

This is the phase where the CQRS work from `#79` ~ `#84` pays off.
Because classes are already split by role, physical movement becomes much safer.

### phase 5. domain move
Goal: move domain model, repository contracts, enums, domain events.

Move candidates:
- `module/*/domain/**`
- repository interfaces that are true domain contracts
- domain event classes near each bounded context

Note:
- packages can stay identical
- this phase mostly checks whether any domain code still leaks web/infra concerns

### phase 6. api move
Goal: make `apps:api-server` depend on actual modules instead of root source.

Move candidates:
- `presentation/**`
- auth/security/web concerns
- controller advice
- swagger contracts
- HTTP logging / servlet adapters
- API-only resources

At the end of this phase, `apps:api-server` should no longer need root `src/main/java`.

### phase 7. root resource retirement
Goal: remove remaining root app artifacts.

Candidates to relocate:
- `src/main/resources/logback-spring.xml`
- root static assets needed by api
- db migration ownership decision
  - likely app runtime keeps Flyway execution
  - but migration files need one clear home

### phase 8. remove root source-set bridge
Final gate:
- `apps:api-server` no longer compiles `../../src/main/java`
- tests no longer compile `../../src/test/java`
- root `src` is deleted or reduced to intentionally shared leftovers with explicit ownership

## suggested issue slicing after #84

### parent issue #76 should now explicitly include
- actual physical migration from root `src` to `modules/*` and `apps/*`
- removal of `apps:api-server` root source-set override
- final retirement of legacy root source tree

### recommended next child issues
1. `events/common/web/shared package physical move`
2. `infra adapter physical move`
3. `application layer physical move`
4. `domain layer physical move`
5. `api-server physical move + root source-set removal`
6. `test source relocation and cleanup`

## risk notes

- biggest risk is not Java imports but Gradle dependency gaps between modules
- second biggest risk is mixed ownership in `common/**`
- third biggest risk is test source coupling because current tests still assume root `src/test/java`

## success criteria

The migration is done when:

- root `src/main/java` is empty or intentionally minimized
- `apps:api-server` and `apps:batch-server` compile from their own source roots plus real modules
- `modules/*` contain actual production code, not just placeholders
- `#76` can close without saying “skeleton only”
