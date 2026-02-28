package com.backend.allreva.support;

import com.backend.allreva.common.event.Event;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

/**
 * 비동기 이벤트 처리를 테스트에서 동기적으로 기다릴 수 있게 하는 헬퍼 클래스
 *
 * <p>도메인 이벤트({@link Event})가 발행되면 자동으로 감지하여 카운트를 감소시킵니다. 이벤트 핸들러가 {@code @Async}로 비동기 처리되더라도 모든 처리가
 * 완료될 때까지 대기할 수 있습니다. 사용 예시:
 *
 * <pre>
 * asyncAspect.init(2);  // 2개의 이벤트 완료를 기다림
 * Events.raise(someEvent);
 * asyncAspect.await();  // 이벤트 핸들러 완료까지 대기
 * </pre>
 */
@Slf4j
@TestComponent
public class AsyncAspect {

    private static final int DEFAULT_TIMEOUT_SECONDS = 5;

    private CountDownLatch latch;
    private volatile int expectedCount;
    private volatile int actualCount;

    /** 하나의 비동기 이벤트 완료를 기다리도록 초기화 */
    public void init() {
        init(1);
    }

    /**
     * 지정된 개수의 비동기 이벤트 완료를 기다리도록 초기화
     *
     * @param count 기다릴 이벤트 개수
     */
    public void init(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive: " + count);
        }
        this.latch = new CountDownLatch(count);
        this.expectedCount = count;
        this.actualCount = 0;
        log.debug("AsyncAspect initialized with count: {}", count);
    }

    /**
     * 도메인 이벤트를 자동으로 감지하여 카운트 감소
     *
     * <p>이 메서드는 {@link Event}를 상속한 모든 이벤트에 대해 자동으로 호출됩니다. 이벤트 핸들러보다 늦게 실행되도록 {@code @Order} 설정이
     * 필요합니다.
     *
     * @param event 발행된 도메인 이벤트
     */
    @EventListener
    @Order(Integer.MAX_VALUE) // 다른 이벤트 리스너들이 모두 실행된 후에 실행
    @Async // 비동기로 실행하여 이벤트 핸들러 완료 후 카운트 감소
    public void onDomainEvent(Event event) {
        if (latch != null) {
            actualCount++;
            long remaining = latch.getCount();
            latch.countDown();
            log.debug(
                    "Domain event processed: {} - Remaining: {}/{}",
                    event.getClass().getSimpleName(),
                    remaining - 1,
                    expectedCount);
        }
    }

    /**
     * 모든 비동기 이벤트가 완료될 때까지 대기 (기본 타임아웃: 5초)
     *
     * @throws InterruptedException 대기 중 인터럽트 발생 시
     * @throws IllegalStateException 타임아웃 발생 시
     */
    public void await() throws InterruptedException {
        await(DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * 모든 비동기 이벤트가 완료될 때까지 지정된 시간만큼 대기
     *
     * @param timeoutSeconds 타임아웃 (초)
     * @throws InterruptedException 대기 중 인터럽트 발생 시
     * @throws IllegalStateException 타임아웃 발생 시
     */
    public void await(int timeoutSeconds) throws InterruptedException {
        if (latch == null) {
            throw new IllegalStateException("AsyncAspect not initialized. Call init() first.");
        }

        boolean completed = latch.await(timeoutSeconds, TimeUnit.SECONDS);

        if (!completed) {
            String errorMsg = String.format(
                    "Timeout waiting for async events. Expected: %d, Completed: %d, Remaining: %d",
                    expectedCount, actualCount, latch.getCount());
            log.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }

        log.debug("All {} async events completed successfully", expectedCount);
    }

    /**
     * 현재 남은 이벤트 개수 반환 (디버깅용)
     *
     * @return 남은 이벤트 개수
     */
    public long getRemainingCount() {
        return latch != null ? latch.getCount() : 0;
    }

    /** AsyncAspect 상태 초기화 */
    public void reset() {
        this.latch = null;
        this.expectedCount = 0;
        this.actualCount = 0;
        log.debug("AsyncAspect reset");
    }
}
