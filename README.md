# ALLREVA Backend

> 부트캠프 팀 프로젝트로 시작한 ALLREVA 백엔드를 개인적으로 리뉴얼한 저장소입니다.  
> 기존 기능을 유지하면서 멀티 모듈 구조, 인프라, 캐싱, 검색, 배치 흐름을 다시 정리했습니다.

ALLREVA는 공연 팬들이 공연 정보, 차대절 모집, 수요조사를 한 곳에서 관리할 수 있도록 돕는 팬덤 기반 서비스입니다.

<br>

## Overview

기존 팬덤 플랫폼은 특정 기획사 중심으로 운영되는 경우가 많아, 팬들이 직접 준비하는 이동 수단이나 수요조사 같은 활동을 충분히 지원하지 못합니다.

ALLREVA는 공연 정보를 기반으로 팬들이 필요한 모집과 참여 흐름을 직접 만들 수 있도록 설계했습니다.

주요 기능:

- 공연 정보 조회 및 검색
- 공연 상세 정보 캐싱
- 차대절 모집 생성 및 참여
- 수요조사 생성 및 참여
- 소셜 로그인
- 알림
- KOPIS 공연 데이터 배치 수집

<br>

## Architecture

<img width="1280" height="618" alt="Pasted image 20260608175836" src="https://github.com/user-attachments/assets/55d82e99-b609-467d-a311-69751295a3fa" />

<br>

## Module Structure

<img width="1130" height="429" alt="Pasted image 20260608180209" src="https://github.com/user-attachments/assets/02579bb3-0238-4c57-a273-b003c0f18341" />

멀티 모듈 구조로 API, Core, Support 계층을 분리했습니다.

```text
allreva-api
allreva-batch
allreva-core
allreva-support
├── db
├── storage
├── observability
├── local-cache
├── global-cache
├── oauth-client
├── kopis-client
└── push-notification
```

<br>

## ERD

<img width="2015" height="1832" alt="image" src="https://github.com/user-attachments/assets/b10045bc-6cc3-42df-ba09-264ede803937" />


<br>

## Tech Stack

### Backend

![Java](https://img.shields.io/badge/Java_17-007396?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.3.5-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=flat-square&logo=spring&logoColor=white)
![QueryDSL](https://img.shields.io/badge/QueryDSL-0769AD?style=flat-square)
![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=flat-square&logo=flyway&logoColor=white)

### Database & Cache

![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white)
![Caffeine](https://img.shields.io/badge/Caffeine_Local_Cache-8B5E3C?style=flat-square)

### Infra & Tools

![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat-square&logo=githubactions&logoColor=white)
![Cloudflare](https://img.shields.io/badge/Cloudflare_Tunnel-F38020?style=flat-square&logo=cloudflare&logoColor=white)
![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?style=flat-square&logo=prometheus&logoColor=white)
![Grafana](https://img.shields.io/badge/Grafana-F46800?style=flat-square&logo=grafana&logoColor=white)
![AWS S3](https://img.shields.io/badge/AWS_S3-569A31?style=flat-square&logo=amazons3&logoColor=white)

<br>

## Key Features

### 공연 데이터 수집

KOPIS Open API에서 공연과 공연장 데이터를 수집하고, 서비스에 필요한 형태로 저장합니다.

### 검색 최적화

PostgreSQL `pg_trgm` 기반 GIN index를 활용해 공연명, 공연장 주소, 차대절/수요조사 제목 검색을 개선했습니다.

### 캐싱 전략

공연 상세 정보는 변경 빈도가 낮기 때문에 Caffeine 로컬 캐시를 적용했습니다. 배치 수집 이후 캐시를 갱신해 첫 조회에서도 빠르게 응답할 수 있도록 설계했습니다.

### 차대절 모집

공연별 차대절 게시글을 생성하고, 탑승 일자와 참여 인원 정보를 관리합니다.

### 수요조사

공연 참여 수요를 사전에 조사하고, 차대절 모집으로 이어질 수 있는 흐름을 제공합니다.

### 인증

OAuth provider와 직접 통신해 사용자 정보를 가져오고, JWT 기반 인증 흐름을 구성했습니다.

<br>

## Documents

- [Project Overview](./docs/overview.md)
- [Main Sequences](./docs/main-sequences.md)

<br>

## Getting Started

```bash
./gradlew clean build
```

```bash
./gradlew :allreva-api:bootRun
```
