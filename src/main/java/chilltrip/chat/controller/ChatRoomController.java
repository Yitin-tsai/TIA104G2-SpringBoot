package chilltrip.chat.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import chilltrip.admin.model.AdminService;
import chilltrip.admin.model.AdminVO;
import chilltrip.member.model.MemberService;
import chilltrip.member.model.MemberVO;
import chilltrip.message.model.ChatMessage;

@Controller
public class ChatRoomController  {
	
	@Autowired
    private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private MemberService membersvc;
	@Autowired
	private AdminService adminsvc;

	private static final Map<Integer,String> connectedSessions = Collections.synchronizedMap(new HashMap<>());
	

	@MessageMapping("/chat.addUser")
	public void afterConnectionEstablished(@Payload Map<String, String> payload) throws Exception {
		 System.out.println("Received payload: " + payload);
		 System.out.println(payload.get("memberId"));
		 System.out.println(payload.get("adminId"));
		if (payload.get("memberId") != null) {
			Integer memberid = Integer.valueOf(payload.get("memberId"));
			MemberVO member = membersvc.getMemberById(memberid);
			String userName = member.getNickName();
			connectedSessions.put(memberid,userName);
			System.out.println("New session connected: " + memberid + " with username " + userName);
		}
		if (payload.get("adminId") != null) {
			Integer adminid = Integer.valueOf(payload.get("adminId"));
			AdminVO admin = adminsvc.getOneAdmin(adminid);
			String userName = admin.getAdminnickname();
			connectedSessions.put(adminid,userName);
			System.out.println("New session connected: " + adminid + " with username " + userName);
		}else
			System.out.println("empty");
		
	}

	// 當連接被關閉時觸發
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		String userName = (String) session.getAttributes().get("userName");
		Integer userId = (Integer) session.getAttributes().get("useaId");
		connectedSessions.remove(userId,userName);// 當用戶斷開連接時將其從在線用戶列表中移除
		System.out.println("Session ID " + session.getId() + " disconnected");
	}

	// 處理錯誤

	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		System.out.println("Error occurred: " + exception.getMessage());
	}
	
	 // 透過 STOMP 發送消息
    public void sendMessageToAll(String message) {
        messagingTemplate.convertAndSend("/topic/messages", message);  // 發送消息到所有訂閱該主題的客戶端
    }
	
    @MessageMapping("/chat")  // 用來處理來自 /app/chat 的消息
    @SendTo("/topic/messages")
    protected void handleTextMessage( ChatMessage chatMessage) throws InterruptedException, IOException {
        
        System.out.println("Received message: " + chatMessage.getMessage());
        Map<Integer,String>  connectedSessions = ChatRoomController.getConnectedSession();
        ObjectMapper objectMapper = new ObjectMapper();
        // 根據 receiver 發送訊息給指定的客戶端
        for (Entry<Integer, String> s : connectedSessions.entrySet()) {
            if (s.getValue().equals(chatMessage.getReceiver())) {
                // 只有發送給 receiver 的會話
                String response = objectMapper.writeValueAsString(chatMessage);
//                 s.getKey().sendMessage(new TextMessage(response)); // 發送消息
                System.out.println("Sent message to: " + chatMessage.getReceiver());
            }if(chatMessage.getReceiver() == null) {
            	System.out.println("no recevicer send to all");
            	
            }
        }
    }
	

	public static Map<Integer,String> getConnectedSession() {
		return new HashMap<>(connectedSessions);
	}







}
