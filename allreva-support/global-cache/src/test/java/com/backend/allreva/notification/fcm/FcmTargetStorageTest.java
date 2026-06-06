package com.backend.allreva.notification.fcm;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.allreva.support.GlobalCacheTestSupport;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@SuppressWarnings("NonAsciiCharacters")
@ContextConfiguration(classes = {GlobalCacheTestSupport.StringRedisTemplateConfig.class, FcmTargetStorage.class})
@DisplayName("FcmTargetStorage 테스트")
class FcmTargetStorageTest extends GlobalCacheTestSupport {

    @Autowired
    private FcmTargetStorage fcmTargetStorage;

    @Test
    void FCM_target을_저장하고_조회한다() {
        fcmTargetStorage.save(1L, "device-token");

        assertThat(fcmTargetStorage.get(1L)).contains("device-token");
    }

    @Test
    void member_id_순서대로_FCM_target을_조회한다() {
        fcmTargetStorage.save(1L, "device-token-1");
        fcmTargetStorage.save(2L, "device-token-2");

        List<String> result = fcmTargetStorage.getAll(List.of(2L, 1L));

        assertThat(result).containsExactly("device-token-2", "device-token-1");
    }

    @Test
    void FCM_target을_삭제한다() {
        fcmTargetStorage.save(1L, "device-token");

        fcmTargetStorage.delete(1L);

        assertThat(fcmTargetStorage.get(1L)).isEmpty();
    }
}
