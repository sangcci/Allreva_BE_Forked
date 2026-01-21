package com.backend.allreva.common.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.backend.allreva.module.auth.application.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    public static final String WEB_SOCKET_ENDPOINT = "/ws-chat";
    public static final String TOPIC = "/room";
    public static final String PREFIX = "/chat";

    private final JwtService jwtService;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(WEB_SOCKET_ENDPOINT)
                .setAllowedOriginPatterns("*")
                .withSockJS(); // 배포 전 수정 필요
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(TOPIC);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        log.info("registration.interceptors 이전");
        registration.interceptors(new org.springframework.messaging.support.ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                log.info("STOMP CONNECT 요청 수신");

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // CONNECT 시 Authorization 헤더를 통해 토큰 추출
                    List<String> authHeaders = accessor.getNativeHeader("Authorization");
                    if (authHeaders != null && !authHeaders.isEmpty()) {
                        String token = authHeaders.get(0);
                        jwtService.validateToken(token);
                        String memberId = jwtService.extractMemberId(token);
                        accessor.getSessionAttributes().put("memberId", memberId);
                        log.info("세션 설정 완료");
                    }
                }
                return message;
            }
        });
    }
}
