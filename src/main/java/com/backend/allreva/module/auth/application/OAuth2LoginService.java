package com.backend.allreva.module.auth.application;

import com.backend.allreva.module.auth.application.dto.UserInfo;

public interface OAuth2LoginService {

    UserInfo getUserInfo(String authorizationCode);
}
