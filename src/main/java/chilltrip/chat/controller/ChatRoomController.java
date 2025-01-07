package chilltrip.chat.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import chilltrip.admin.model.AdminService;
import chilltrip.member.model.MemberService;
import chilltrip.message.model.ChatMessage;


@Controller
public class ChatRoomController extends TextWebSocketHandler {
	
	
	
    private static final Set<WebSocketSession> connectedSessions = Collections.synchronizedSet(new HashSet<>());
    private static final Set<String> onlineUsers = Collections.synchronizedSet(new HashSet<>());
    private ObjectMapper objectMapper = new ObjectMapper(); // 用來處理 JSON 與物件之間的轉換

    // 當有新客戶端連接時觸發
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    	String userName = (String) session.getAttributes().get("userName");
        connectedSessions.add(session);
        onlineUsers.add(userName);
        System.out.println("New session connected: " + session.getId() + " with username " + userName);
    }

    // 當接收到來自客戶端的消息時觸發
    @MessageMapping("/chat")  // 用來處理來自 /app/chat 的消息
    @SendTo("/topic/messages")
    protected void handleTextMessage( ChatMessage chatMessage) throws InterruptedException, IOException {
        
        System.out.println("Received message: " + chatMessage.getMessage());

        // 根據 receiver 發送訊息給指定的客戶端
        for (WebSocketSession s : connectedSessions) {
            if (s.isOpen() && s.getAttributes().get("userName").equals(chatMessage.getReceiver())) {
                // 只有發送給 receiver 的會話
                String response = objectMapper.writeValueAsString(chatMessage);
                s.sendMessage(new TextMessage(response)); // 發送消息
                System.out.println("Sent message to: " + chatMessage.getReceiver());
            }
        }
    }

    // 當連接被關閉時觸發
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    	String userName = (String) session.getAttributes().get("userName");
    	connectedSessions.remove(session);
        onlineUsers.remove(userName); // 當用戶斷開連接時將其從在線用戶列表中移除
        System.out.println("Session ID " + session.getId() + " disconnected");
    }

    // 處理錯誤
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("Error occurred: " + exception.getMessage());
    }
    public static Set<String> getOnlineUsers() {
        return new HashSet<>(onlineUsers); // 返回在線用戶列表
    }

	
}
