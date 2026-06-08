# Project Overview

## Service

ALLREVA는 공연 팬들이 공연 정보, 차대절 모집, 수요조사를 한 곳에서 관리할 수 있도록 돕는 팬덤 기반 서비스입니다.

## Problem

공연 참여를 위한 차대절 정보는 X, 오픈채팅, 개인 폼 등에 흩어져 있습니다. 사용자는 모집 글을 직접 찾아야 하고, 주최자는 신청 인원과 마감 여부를 별도로 관리해야 합니다.

기존 팬덤 플랫폼은 특정 기획사나 공식 활동 중심으로 구성된 경우가 많아, 팬들이 직접 조직하는 차대절이나 비공식 활동을 충분히 지원하지 못합니다.

ALLREVA는 이런 흐름을 공연 기준으로 묶어, 모집 생성부터 참여 관리까지 일관된 경험을 제공합니다.

## Core Features

- 공연 정보 조회 및 검색
- 공연 상세 정보 캐싱
- 차대절 모집 생성 및 참여
- 수요조사 생성 및 참여
- KOPIS 공연 데이터 배치 수집
- 소셜 로그인 및 알림

## Users

- 공연 관람과 팬덤 활동에 적극적으로 참여하는 사용자
- 차대절을 직접 모집하거나 참여하려는 사용자
- 흩어진 팬 활동 정보를 한곳에서 관리하고 싶은 사용자

## Tech Stack

- Java 17
- Spring Boot 3.3
- Spring Data JPA, QueryDSL
- PostgreSQL, Redis, Caffeine
- Docker, GitHub Actions
- Cloudflare Tunnel, Prometheus, Grafana
