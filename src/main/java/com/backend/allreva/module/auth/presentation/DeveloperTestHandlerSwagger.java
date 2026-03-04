package com.backend.allreva.module.auth.presentation;

import com.backend.allreva.common.web.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "신이 되고 싶은가?")
public interface DeveloperTestHandlerSwagger {

    @Operation(summary = "전지전능한 개발자 모드 생성", description = """
            [다크로드](https://img.insight.co.kr/static/2020/07/29/700/drd1a625yy440q7bluvg.jpg)

            게발자가 되고 싶은 자 나에게로..
            """)
    Response<String> getAdminToken();
}
