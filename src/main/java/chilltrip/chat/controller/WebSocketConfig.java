package chilltrip.chat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{
	
	
	@Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
        
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
    	
        registry
        .addEndpoint("/chat")
        .setAllowedOriginPatterns("*")
        .addInterceptors(new MyHandshakeInterceptor())
        .withSockJS();
    }
//    @Override
//    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
//        // 配置 WebSocket 傳輸，這裡可以調整網絡設置等
//    	registry.addDecoratorFactory( new MyWebSocketHandlerDecoratorFactory());
//    }
    
   
}
