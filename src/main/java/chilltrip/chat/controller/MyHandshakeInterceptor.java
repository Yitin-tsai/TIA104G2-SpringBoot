package chilltrip.chat.controller;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Controller
public class MyHandshakeInterceptor implements HandshakeInterceptor {

	@Override
	public boolean beforeHandshake(ServerHttpRequest req, ServerHttpResponse res, WebSocketHandler handler,
			Map<String, Object> attributes) throws Exception {
		// 從 HTTP headers 中獲取 userName
		
		ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) req;
		if (servletRequest.getServletRequest().getSession().getAttribute("memberId") != null) {
			Integer memberid = (Integer) servletRequest.getServletRequest().getSession().getAttribute("memberId");
			String membername =  (String) servletRequest.getServletRequest().getSession().getAttribute("memberName");
			attributes.put("memberId", memberid); // 存儲 userName 到 WebSocketSession
			attributes.put("memberName", membername); // 存儲 userName 到 WebSocketSession
			
			System.out.println("這是握手成功" + memberid);

			return true; // 返回 true 以允許 WebSocket 連接
		}
		if (servletRequest.getServletRequest().getSession().getAttribute("adminId") != null) {
			Integer adminid = (Integer) servletRequest.getServletRequest().getSession().getAttribute("adminId");
			String adminname = (String) servletRequest.getServletRequest().getSession().getAttribute("adminName");
			attributes.put("adminId", adminid); // 存儲 userName 到 WebSocketSession
			attributes.put("adminName", adminname); // 存儲 userName 到 WebSocketSession

			System.out.println("這是握手成功" + adminid);
			return true; // 返回 true 以允許 WebSocket 連接
		}

		return false; // 如果沒有 userName，則拒絕握手
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception ex) {

	}
}
