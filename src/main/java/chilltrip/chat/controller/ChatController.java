package chilltrip.chat.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import chilltrip.admin.model.AdminService;
import chilltrip.member.model.MemberService;

@RestController
@RequestMapping("/chat")
public class ChatController {
	@Autowired
	private MemberService memberSvc;
	
	@Autowired
	private AdminService adminSvc;
	
	@GetMapping("/go")
	public Map<String, Object> chatPage(HttpSession session) {
        // 從 session 取得 memberId
		Map<String, Object> response = new HashMap<>();
		if(session.getAttribute("memberId")!=null) {
        Integer memberId =  (Integer) session.getAttribute("memberId");  
        String userName = memberSvc.getMemberById(memberId).getNickName();
        // 把用戶名稱傳遞到前端
        response.put("message", "success");
        response.put("userName", userName);
        return response;  // 返回聊天頁面
		}
		if(session.getAttribute("adminId")!=null) {
	        Integer adminId =  (Integer) session.getAttribute("adminId");  
	        String userName = adminSvc.getOneAdmin(adminId).getAdminnickname();
	        // 把用戶名稱傳遞到前端
	        response.put("message", "success");
	        response.put("userName", userName);
	        return response;  // 返回聊天頁面
		}
		response.put("message", "false");
       return response ;
       
    }
	// 獲取在線用戶列表的 HTTP API
    @GetMapping("/onlineUsers")
    public Set<String> getOnlineUsers() {
    	System.out.println(ChatRoomController.getOnlineUsers() +"我是誰");
        return ChatRoomController.getOnlineUsers(); // 調用 WebSocketController 提供的在線用戶列表
    }
}
