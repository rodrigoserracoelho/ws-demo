package io.surisoft.demo.ws.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${capi.ws.name}")
    private String webSocketApplicationName;

    @Value("${capi.ws.topic.name}")
    private String webSocketTopicName;

    @Value("${capi.ws.root.context}")
    private String webSocketRootContext;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry) {
        messageBrokerRegistry.enableSimpleBroker(webSocketTopicName);
        messageBrokerRegistry.setApplicationDestinationPrefixes(webSocketApplicationName);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        stompEndpointRegistry.addEndpoint(webSocketRootContext).setAllowedOrigins("*").withSockJS();
    }
}
