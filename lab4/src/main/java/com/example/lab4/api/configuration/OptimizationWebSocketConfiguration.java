package com.example.lab4.api.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Arrays;

@Configuration
@EnableWebSocketMessageBroker
public class OptimizationWebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    private final ChannelInterceptor authenticationInterceptor;
    private final String[] allowedOrigins;

    public OptimizationWebSocketConfiguration(JwtDecoder decoder, JwtAuthenticationConverter converter, Environment environment,
                                              @Value("${app.websocket.allowed-origin-patterns:http://localhost:*,http://127.0.0.1:*}") String origins) {
        this.authenticationInterceptor = new StompJwtAuthenticationInterceptor(decoder, converter,
                environment.acceptsProfiles(Profiles.of("dev & !prod")));
        this.allowedOrigins = Arrays.stream(origins.split(",")).map(String::trim).filter(value -> !value.isEmpty()).toArray(String[]::new);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns(allowedOrigins);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authenticationInterceptor);
    }

    private static final class StompJwtAuthenticationInterceptor implements ChannelInterceptor {
        private static final String OPTIMIZATION_TOPIC_PREFIX = "/topic/optimization/";
        private final JwtDecoder decoder;
        private final JwtAuthenticationConverter converter;
        private final boolean devSecurityBypass;

        private StompJwtAuthenticationInterceptor(JwtDecoder decoder, JwtAuthenticationConverter converter, boolean devSecurityBypass) {
            this.decoder = decoder;
            this.converter = converter;
            this.devSecurityBypass = devSecurityBypass;
        }

        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
            if (accessor == null || accessor.getCommand() == null) return message;
            if (devSecurityBypass) return message;

            if (accessor.getCommand() == StompCommand.CONNECT || accessor.getCommand() == StompCommand.STOMP) {
                authenticate(accessor);
            } else if (accessor.getCommand() == StompCommand.SUBSCRIBE) {
                authorizeSubscription(accessor);
            } else if (accessor.getCommand() == StompCommand.MESSAGE) {
                throw new AccessDeniedException("Clients cannot publish to the optimization broker");
            }
            return message;
        }

        private void authenticate(StompHeaderAccessor accessor) {
            String authorization = accessor.getFirstNativeHeader("Authorization");
            if (authorization == null || !authorization.startsWith("Bearer ") || authorization.length() <= 7) {
                throw new AccessDeniedException("A Bearer access token is required in the STOMP CONNECT frame");
            }
            try {
                Jwt jwt = decoder.decode(authorization.substring(7).trim());
                AbstractAuthenticationToken authentication = converter.convert(jwt);
                if (authentication == null || !authentication.isAuthenticated()) {
                    throw new AccessDeniedException("Invalid WebSocket authentication");
                }
                accessor.setUser(authentication);
            } catch (RuntimeException exception) {
                if (exception instanceof AccessDeniedException denied) throw denied;
                throw new AccessDeniedException("Invalid WebSocket access token", exception);
            }
        }

        private void authorizeSubscription(StompHeaderAccessor accessor) {
            if (accessor.getUser() == null) throw new AccessDeniedException("Authenticate before subscribing");
            String destination = accessor.getDestination();
            if (destination == null || !destination.matches("/topic/optimization/[0-9a-fA-F-]{36}")) {
                throw new AccessDeniedException("Only per-job optimization topics may be subscribed to");
            }
        }
    }
}
