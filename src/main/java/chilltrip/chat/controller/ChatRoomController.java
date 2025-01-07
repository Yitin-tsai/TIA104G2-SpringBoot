package chilltrip.chat.controller;

import java.util.Collections;

import java.util.HashSet;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import chilltrip.admin.model.AdminService;
import chilltrip.admin.model.AdminVO;
import chilltrip.member.model.MemberService;
import chilltrip.member.model.MemberVO;
import chilltrip.message.model.ChatMessage;

public class ChatRoomController implements WebSocketHandler {

	@Autowired
	private MemberService membersvc;
	@Autowired
	private AdminService adminsvc;

	private static final Set<WebSocketSession> connectedSessions = Collections.synchronizedSet(new HashSet<>());
	private static final Set<String> onlineUsers = Collections.synchronizedSet(new HashSet<>());

	// 當有新客戶端連接時觸發
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		System.out.println("hereis connect");
		if (session.getAttributes().get("memberId") != null) {
			MemberVO member = membersvc.getMemberById((Integer) session.getAttributes().get("memberId"));
			String userName = member.getNickName();
			connectedSessions.add(session);
			onlineUsers.add(userName);
			System.out.println("New session connected: " + session.getId() + " with username " + userName);
		}
		if (session.getAttributes().get("adminId") != null) {
			AdminVO admin = adminsvc.getOneAdmin((Integer) session.getAttributes().get("adminId"));
			String userName = admin.getAdminnickname();
			connectedSessions.add(session);
			onlineUsers.add(userName);
			System.out.println("New session connected: " + session.getId() + " with username " + userName);
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

	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		System.out.println("Error occurred: " + exception.getMessage());
	}

	public static Set<String> getOnlineUsers() {
		return new HashSet<>(onlineUsers); // 返回在線用戶列表
	}

	public static Set<WebSocketSession> getConnectedSession() {
		return new HashSet<>(connectedSessions);
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean supportsPartialMessages() {
		// TODO Auto-generated method stub
		return false;
	}





}
