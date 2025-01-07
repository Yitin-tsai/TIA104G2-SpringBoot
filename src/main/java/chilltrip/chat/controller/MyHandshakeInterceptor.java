package chilltrip.chat.controller;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

public class MyHandshakeInterceptor implements HandshakeInterceptor{

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
    	// 從 HTTP headers 中獲取 userName
        String userName = request.getHeaders().getFirst("userName");
        System.out.println("Received handshake request with userName: " + userName); // 增加日誌
        if (userName != null) {
            attributes.put("userName", userName); // 存儲 userName 到 WebSocketSession
            System.out.println("這是握手成功");
            return true;  // 返回 true 以允許 WebSocket 連接
        }
        
        return false;  // 如果沒有 userName，則拒絕握手
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                WebSocketHandler wsHandler, Exception ex) {
        // 握手後的處理（如果有需要的話）
    }
}
