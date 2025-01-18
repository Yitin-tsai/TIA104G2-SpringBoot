package chilltrip.chat.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{
	
	
	@Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.enableSimpleBroker("/user");
        config.setApplicationDestinationPrefixes("/app");
        config.setCacheLimit(30720000);
        
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
    	
        registry
        .addEndpoint("/chat")
        .setAllowedOriginPatterns("*")
        .addInterceptors(new MyHandshakeInterceptor())
        .withSockJS();
        registry
        .addEndpoint("/coedit")
        .setAllowedOriginPatterns("*")
        .addInterceptors(new MyHandshakeInterceptor())
        .withSockJS();
        registry
        .addEndpoint("/notice")
        .setAllowedOriginPatterns("*")
        .addInterceptors(new MyHandshakeInterceptor())
        .withSockJS();
        registry
        .addEndpoint("/onlineUsers")
        .setAllowedOriginPatterns("*")
        .addInterceptors(new MyHandshakeInterceptor())
        .withSockJS();
    }

    @Bean
    public ServletServerContainerFactoryBean createServletServerContainerFactoryBean() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(32768000);
        container.setMaxBinaryMessageBufferSize(32768000);
        System.out.println("Websocket factory returned");
        return container;
    }
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
    	registry.setMessageSizeLimit(1000*1024);
    }
   
}
