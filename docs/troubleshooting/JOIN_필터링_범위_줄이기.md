---
created_at: 2026-04-30T15:07:00
updated_at:
---
# 개요

INDEX를 추가해도 필터링 범위를 줄이지 못했다.

계속 쿼리를 시도해보니, `date` 로 JOIN하는 부분에서 병목이 많이 발생하는 것을 발견하고, `date`의 선택도가 매우 낮음을 확인

- 다른 View 조회 쿼리도 rent_participants를 확인하는 레코드가 매우 많음을 인지

### **테스트 환경:**

- **CPU**: 13th Gen Intel(R) Core(TM) i5-1340P
- **RAM**: 16GB
- **PostgreSQL**: 16
- **데이터 개수**:
    - `rent`: 10,000개
    - `rent_boarding_slots`: 30,000개
    - `rent_participants`: 1,020,000개
- **테스트 기준:** `rent.id = 500`
- **실행 계획**: `EXPLAIN ANALYZE`

### index 없고 ON절 생략한 실행계획

```sql
SELECT
    r1_0.id,
    rbs1_0.date,
    rbs1_0.recruitment_count,
    rbs1_0.passenger_count,
    rp1_0.id,
    rp1_0.passenger_num
FROM
    rent r1_0
        JOIN rent_boarding_slots rbs1_0
            ON r1_0.id=rbs1_0.rent_id
        JOIN rent_participants rp1_0
            ON rbs1_0.date=rp1_0.boarding_date
WHERE
    rp1_0.member_id=?;
```

```text
Hash Join  (cost=1112.00..40452.31 rows=18000 width=28) (actual time=96.412..1324.618 rows=18175 loops=1)
  Hash Cond: (rp1_0.boarding_date = rbs1_0.date)
  ->  Seq Scan on rent_participants rp1_0  (cost=0.00..38883.00 rows=10168 width=20) (actual time=8.104..681.532 rows=216 loops=1)
        Filter: (member_id = 500)
        Rows Removed by Filter: 1019784
  ->  Hash  (cost=737.00..737.00 rows=30000 width=16) (actual time=96.011..96.014 rows=30000 loops=1)
        ->  Hash Join  (cost=280.00..737.00 rows=30000 width=16) (actual time=2.942..89.415 rows=30000 loops=1)
              Hash Cond: (rbs1_0.rent_id = r1_0.id)
              ->  Seq Scan on rent_boarding_slots rbs1_0  (cost=0.00..378.00 rows=30000 width=12) (actual time=0.021..18.721 rows=30000 loops=1)
              ->  Hash  (cost=155.00..155.00 rows=10000 width=8) (actual time=2.848..2.849 rows=10000 loops=1)
                    ->  Seq Scan on rent r1_0  (cost=0.00..155.00 rows=10000 width=8) (actual time=0.015..1.581 rows=10000 loops=1)
Planning Time: 0.533 ms
Execution Time: 1325.104 ms
```

1. `rent_participants`를 `member_id = 500`으로 filtering
2. `rent`를 먼저 조회하고 `rent_boarding_slots`와 nested loop join 실행
3. 두 테이블을 `date`별로 hash join

제일 먼저 full seq scan이 눈에 띔

member_id에 index를 추가하면 rent_participants 필터링을 효율적으로 할 수 있을 것이라 생각

### rent_participants `member_id` index 추가

```text
Hash Join  (cost=1010.48..1689.92 rows=68458 width=28) (actual time=2.821..37.318 rows=20049 loops=1)
  Hash Cond: (rbs1_0.date = rp1_0.boarding_date)
  ->  Seq Scan on rent_boarding_slots rbs1_0  (cost=0.00..378.00 rows=30000 width=16) (actual time=0.044..22.200 rows=30000 loops=1)
        Filter: (rent_id IS NOT NULL)
  ->  Hash  (cost=1007.47..1007.47 rows=241 width=20) (actual time=2.632..2.633 rows=241 loops=1)
        ->  Bitmap Heap Scan on rent_participants rp1_0  (cost=6.29..1007.47 rows=241 width=20) (actual time=0.765..2.630 rows=241 loops=1)
              Recheck Cond: (member_id = 500)
              ->  Bitmap Index Scan on idx_rent_participants_member_id  (cost=0.00..6.23 rows=241 width=0) (actual time=0.421..0.421 rows=241 loops=1)
                    Index Cond: (member_id = 500)
Planning Time: 0.474 ms
Execution Time: 65.600 ms
```

`rent_participants`를 member_id 필터링 과정에서 효율적으로 cost를 낮춤

하지만 `date`별로 JOIN하는 과정에서 매우 많은 cost 발생.

여기서 한참 해맴. 무엇이 문제였는지 계속 확인.

`date` 데이터 특성 상 중복값이 많을 수 밖에 없음을 인지.

- date 대신 `rent_boarding_slots_id`로 JOIN 조건을 걸면?
- `rent`와 `rent_boarding_slots` 테이블의 key들을 저장하는 테이블 생성 → 해당 테이블 PK를 저장?

llm에게 힌트를 얻을 수 있었음

단순히 `date` 1개 만으로 JOIN을 시도할 경우

|rent_boarding_slots_id|rent_id|date|
|---|---|---|
|1|1|2025-03-17|
|2|1|2025-03-18|
|3|1|2025-03-19|
|4|2|2025-03-17|

|rent_participants_id|member_id|rent_id|boarding_date|
|---|---|---|---|
|1|500|1|2025-03-17|
|2|500|2|2025-03-17|

JOIN 결과:

|rent_boarding_slots_id|rent_id|**date**|**boarding_date**|rent_participants_id|member_id|rent_id|
|---|---|---|---|---|---|---|
|1|1|2025-03-17|2025-03-17|1|500|1|
|1|1|2025-03-17|2025-03-17|2|500|2|
|4|2|2025-03-17|2025-03-17|1|500|1|
|4|2|2025-03-17|2025-03-17|2|500|2|

불필요한 JOIN 데이터가 포함되는 것을 확인할 수 있다.

이는 제대로 된 필터링 조건을 주지 않았기 때문.

rent는 단순히 `date`만으로 구분되는 것이 아닌, `id`와 `date`가 같이 있어야 제대로 테이블을 JOIN시킬 수 있음을 알았다.

### ON절 포함한 실행 계획

```sql
SELECT
    r1_0.id,
    rbs1_0.date,
    rbs1_0.recruitment_count,
    rbs1_0.passenger_count,
    rp1_0.id,
    rp1_0.passenger_num
FROM
    rent r1_0
        JOIN rent_boarding_slots rbs1_0
            ON r1_0.id=rbs1_0.rent_id
        JOIN rent_participants rp1_0
             ON rbs1_0.rent_id = rp1_0.rent_id -- 필터링 조건 포함
                   AND rbs1_0.date=rp1_0.boarding_date
WHERE
    rp1_0.member_id=?;
```

실제로 쿼리 실행시간은 별로 차이나지 않았다..!

```text
Nested Loop  (cost=6.58..352.22 rows=70 width=28) (actual time=0.688..5.330 rows=242 loops=1)
  ->  Nested Loop  (cost=6.30..328.00 rows=70 width=32) (actual time=0.677..4.780 rows=242 loops=1)
        ->  Bitmap Heap Scan on rent_participants rp1_0  (cost=6.01..96.30 rows=241 width=20) (actual time=0.642..2.140 rows=241 loops=1)
              Recheck Cond: (member_id = 500)
              ->  Bitmap Index Scan on idx_rent_participants_member_id  (cost=0.00..5.95 rows=241 width=0) (actual time=0.318..0.318 rows=241 loops=1)
                    Index Cond: (member_id = 500)
        ->  Index Scan using idx_rent_boarding_slots_rent_date on rent_boarding_slots rbs1_0  (cost=0.29..0.96 rows=1 width=16) (actual time=0.009..0.010 rows=1 loops=241)
              Index Cond: ((rent_id = rp1_0.rent_id) AND (date = rp1_0.boarding_date))
  ->  Index Only Scan using rent_pkey on rent r1_0  (cost=0.29..0.35 rows=1 width=8) (actual time=0.001..0.001 rows=1 loops=242)
        Index Cond: (id = rp1_0.rent_id)
        Heap Fetches: 0
Planning Time: 0.512 ms
Execution Time: 5.410 ms
```

이전과 달리, `rent_boarding_slots`에서 `rent_participants`의 rent_id로 먼저 FK를 이용해서 필터링을 거친 후 `date`를 적용시킨 것을 확인할 수 있다. 즉, rent_id와 date를 적용해서 선택도를 매우 높였음을 확인할 수 있다.

이에 후속 처리 과정도 미리 rows를 확연하게 줄였으므로 순조롭게 필터링이 진행된다.

### rent_participants에 (rent_id, date) 인덱스 적용 후 실행계획 → 필터링 범위 줄이기, 단일 개체 인식

복합 키로 인덱스를 적용시키면

```text
Nested Loop  (cost=6.58..360.00 rows=70 width=28) (actual time=0.542..4.790 rows=242 loops=1)
  ->  Nested Loop  (cost=6.30..335.20 rows=70 width=32) (actual time=0.534..4.250 rows=242 loops=1)
        ->  Bitmap Heap Scan on rent_participants rp1_0  (cost=6.01..103.50 rows=241 width=20) (actual time=0.507..1.670 rows=241 loops=1)
              Recheck Cond: (member_id = 500)
              ->  Bitmap Index Scan on idx_rent_participants_member_id  (cost=0.00..5.95 rows=241 width=0) (actual time=0.302..0.302 rows=241 loops=1)
                    Index Cond: (member_id = 500)
        ->  Index Scan using idx_rent_boarding_slots_rent_date on rent_boarding_slots rbs1_0  (cost=0.29..0.96 rows=1 width=16) (actual time=0.009..0.010 rows=1 loops=241)
              Index Cond: ((rent_id = rp1_0.rent_id) AND (date = rp1_0.boarding_date))
  ->  Index Only Scan using rent_pkey on rent r1_0  (cost=0.29..0.35 rows=1 width=8) (actual time=0.001..0.001 rows=1 loops=242)
        Index Cond: (id = rp1_0.rent_id)
        Heap Fetches: 0
Planning Time: 0.489 ms
Execution Time: 4.870 ms
```

### rent_boarding_slots에 (rent_id, date) 인덱스 적용 후 실행계획

rent_boarding_slots가 rent_participants에 비해 레코드 수가 현저히 적음

또한 rent_participants는 passenger_num의 영향으로 unique 인덱스가 있을 경우 레코드 추가 과정에서 데드락이 발생할 수 있음(이는 unique index 참고)

앞선 실행계획에서도 rent_boarding_slots를 기준으로 table을 찾아내고 있음.

이제 rent와 rent_participants는 1대1로 JOIN이 가능하므로 먼저 진행 후,

rent_boarding_slots의 복합 인덱스를 거쳐서 효율적으로 JOIN이 가능해짐.

```text
Nested Loop  (cost=6.58..266.10 rows=241 width=28) (actual time=0.511..3.980 rows=242 loops=1)
  ->  Nested Loop  (cost=6.29..182.00 rows=241 width=24) (actual time=0.492..2.120 rows=241 loops=1)
        ->  Bitmap Heap Scan on rent_participants rp1_0  (cost=6.01..97.20 rows=241 width=20) (actual time=0.479..1.620 rows=241 loops=1)
              Recheck Cond: (member_id = 500)
              ->  Bitmap Index Scan on idx_rent_participants_member_id  (cost=0.00..5.95 rows=241 width=0) (actual time=0.295..0.295 rows=241 loops=1)
                    Index Cond: (member_id = 500)
        ->  Index Only Scan using rent_pkey on rent r1_0  (cost=0.29..0.35 rows=1 width=8) (actual time=0.001..0.002 rows=1 loops=241)
              Index Cond: (id = rp1_0.rent_id)
              Heap Fetches: 0
  ->  Index Scan using idx_rent_boarding_slots_rent_and_date on rent_boarding_slots rbs1_0  (cost=0.29..0.34 rows=1 width=16) (actual time=0.006..0.007 rows=1 loops=241)
        Index Cond: ((rent_id = rp1_0.rent_id) AND (date = rp1_0.boarding_date))
Planning Time: 0.438 ms
Execution Time: 4.060 ms
```

# 결론

- JOIN을 적용해야 할 때 생각해야 할 순서
    
    1. JOIN 쿼리를 짤 때, 일단 table 별로 몇개의 컬럼이 있는지 분포 확인
        
    2. 이후 JOIN의 순서를 중요하게 생각! (이게 중요)
        
        - `rent`와 `rent_boarding_slots`를 JOIN하고, 이후에 `rent_participants`와 JOIN 하는게 제일 좋음!
        - 만일 실행계획에서 엉뚱하게 JOIN하거나, 너무 많은 cost 및 실제 rows랑 실행계획 상 rows랑 많이 차이난다면 해당 JOIN에서 문제가 있음을 의식
    3. 이제 그 안에서 어떤 데이터와 JOIN을 이룰지 (ON절)를 고민해야 됨
        
        - rent, rent_boarding_slots에서는 `rent_id`로 했기 때문에 선택도가 높고, 인덱스 기반이기 때문에 빠른 JOIN 가능
        - 하지만 rent_boarding_slots와 rent_participants 사이에서 boarding_date 컬럼으로만 JOIN을 시도했기 때문에 date 타입 특성 상 선택도가 낮을 수 밖에 없어서(중복 데이터 많음) 제대로 된 JOIN이 안됨.
        - `rent_boarding_slots`에는 rent_id가 다르지만 date가 똑같은 컬럼이 많이 존재. 그럼 그 date 같은걸로 계속 돌리고 있다는 소리.
        
        → 결국 필터링 범위를 줄여야됨. 애초에 rent는 date 이외에 rent_id가 있어야 제대로 된 개체 구분이 가능한데, date만으로 했기 때문에 훨씬 많은 table이 형성됨. 거기서 rent의 id와 또 같은지 비교하기 때문에 성능은 나빠질 수 밖에 없다.
        
- 다른 대안도 존재한다.
    
    - Join할 때 식별자 기반 조인
    - 🚨 `date` 대신 `rent_boarding_slot_id`로 파악하게 하는 것도 unique하기 때문에 필터링 범위를 줄일 수 있는 하나의 방법이라 생각.
- 필터링 범위를 줄이는 데 집중하자. 특히 unique할 경우 인덱스 적용 시 쉽게 rows를 찾아서 조회할 수 있다는 점을 알게 되었다.
