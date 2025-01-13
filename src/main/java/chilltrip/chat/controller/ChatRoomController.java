package chilltrip.chat.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import chilltrip.admin.model.AdminService;
import chilltrip.admin.model.AdminVO;
import chilltrip.announce.model.AnnounceVO;
import chilltrip.chat.jedis.JedisHandleMessage;
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
			System.out.println("New session connected member: " + memberid + " with username " + userName);
		}
		if (payload.get("adminId") != null) {
			Integer adminid = Integer.valueOf(payload.get("adminId"));
			AdminVO admin = adminsvc.getOneAdmin(adminid);
			String userName = admin.getAdminnickname();
			connectedSessions.put(adminid,userName);
			System.out.println("New session connected admin: " + adminid + " with username " + userName);
		}
		
	}
	@MessageMapping("/chat.getHistory")
	public void getHistory(ChatMessage chatMessage) {
		String sender =chatMessage.getSender();
		String receiver = chatMessage.getReceiver();
		Gson gson = new Gson();
		List<String> historyData = JedisHandleMessage.getHistoryMsg(sender, receiver);
		String historyMsg = gson.toJson(historyData);
		chatMessage.setMessage(historyMsg);
		messagingTemplate.convertAndSend("/user/"+chatMessage.getSender()+"/queue/history", chatMessage);
            System.out.println("這是得到歷史訊息" +chatMessage);
	}
	// 當連接被關閉時觸發
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		String userName = (String) session.getAttributes().get("userName");
		Integer userId = (Integer) session.getAttributes().get("useaId");
		connectedSessions.remove(userId,userName);// 當用戶斷開連接時將其從在線用戶列表中移除
		System.out.println("Session ID " + session.getId() + " disconnected");
	}
	
    @MessageMapping("/chat")  // 用來處理來自 /app/chat 的消息
    protected void handleTextMessage(ChatMessage chatMessage) throws InterruptedException, IOException {
        
        System.out.println(chatMessage);
        Map<Integer,String>  connectedSessions = ChatRoomController.getConnectedSession();
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 根據 receiver 發送訊息給指定的客戶端
        for (Entry<Integer, String> s : connectedSessions.entrySet()) {
            if (s.getValue().equals(chatMessage.getReceiver())) {
                // 只有發送給 receiver 的會話
                String response = objectMapper.writeValueAsString(chatMessage);
                System.out.println("訊息字串 = " + response);              
                messagingTemplate.convertAndSend("/user/"+chatMessage.getSender()+"/queue/messages", response);
                messagingTemplate.convertAndSend("/user/"+chatMessage.getReceiver()+"/queue/messages", response);
                JedisHandleMessage.saveChatMessage(chatMessage.getSender(), chatMessage.getReceiver(), response);
                System.out.println("/user/"+chatMessage.getReceiver()+"/queue/messages");
                System.out.println("Sent message to: " + chatMessage.getReceiver());
            }if(chatMessage.getReceiver() == null) {
            	System.out.println("no recevicer send to all");
            	
            }
        }
    }
	
    @MessageMapping("/coedit")  // 用來處理來自 /app/chat 的消息
    protected void coEdit(AnnounceVO announce) throws InterruptedException, IOException {
    	
    	System.out.println(announce);
    	Map<Integer,String>  connectedSessions = ChatRoomController.getConnectedSession();
    	ObjectMapper objectMapper = new ObjectMapper();
    	
    	// 根據 receiver 發送訊息給指定的客戶端
    
    			String response = objectMapper.writeValueAsString(announce);
    			System.out.println("訊息字串 = " + response);
    			
    			messagingTemplate.convertAndSend("/user/"+ announce.getAnnounceid() +"/queue/coedit", response);
//    			JedisHandleMessage.saveChatMessage(chatMessage.getSender(), chatMessage.getReceiver(), response);
    			System.out.println("/user/"+announce.getAnnounceid()+"/queue/coedit");
    			
    			
    		}
    			
    		
    	
    
   

	public static Map<Integer,String> getConnectedSession() {
		return new HashMap<>(connectedSessions);
	}







}
