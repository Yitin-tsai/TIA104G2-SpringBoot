package chilltrip.admin.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.logicalcobwebs.proxool.admin.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import chilltrip.admin.model.AdminService;
import chilltrip.admin.model.AdminService.LoginException;
import chilltrip.admin.model.AdminVO;
import chilltrip.member.model.MemberVO;
import oracle.jdbc.proxy.annotation.Post;

@RestController
@RequestMapping("/admin")
public class AdminServlet {

	@Autowired
	AdminService adminSvc;

	private Gson gson = new Gson();

	@GetMapping("getOne/{id}")
	public AdminVO getOneAdmin(@PathVariable("id") Integer adminid) {	
		
		AdminVO adminVO = adminSvc.getOneAdmin(adminid);

		return adminVO;

	}
	@GetMapping("getOneSession")
	public AdminVO getOneAdminBySession(HttpSession session) {	
		Integer adminid = (Integer) session.getAttribute("adminId");
		AdminVO adminVO = adminSvc.getOneAdmin(adminid);
		
		return adminVO;
		
	}
	
	@GetMapping("profile")
	public ResponseEntity<Map<String, Object>> getOneAdmin(HttpSession session) {	
		Integer adminId = (Integer) session.getAttribute("adminId");

		// 確認 session 中的使用者是否存在
		if (adminId == null) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("message", "未登入，請先登入");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
		}
		AdminVO adminVO = adminSvc.getOneAdmin(adminId);
		Map<String, Object> sucess = new HashMap<>();
		sucess.put("message", adminVO);
		return ResponseEntity.ok(sucess);		

	}
	@PostMapping("update")
	public  ResponseEntity<Map<String, String>> update(@RequestBody AdminVO adminvo , HttpSession session) {
		Map<String, String> errorMsgs = new HashMap<String, String>();

		// 輸入格式的錯誤處理
		Integer adminid = (Integer) session.getAttribute("adminId");
		AdminVO check = adminSvc.getOneAdmin(adminid);
		if (check == null) {
			errorMsgs.put("adminid", "查無此管理員請再確認編號");
		}
		String adminaccount = adminvo.getAdminaccount();
		String adminacReg = "^[(a-zA-Z0-9_)]{5,20}$";
		if (adminaccount == null || adminaccount.trim().length() == 0) {
			errorMsgs.put("adminaccount", "管理員帳號請勿空白");
		} else if (!adminaccount.trim().matches(adminacReg)) {
			errorMsgs.put("adminaccount", "管理員帳號只能是英文字母數字和_，長度需在5~20之間");
		}

		String adminpassword = adminvo.getAdminpassword();
		String adminpsReg = "^[(a-zA-Z0-9_)]{5,15}$";
		if (adminpassword == null || adminpassword.trim().length() == 0) {
			errorMsgs.put("adminpassword", "管理員密碼請勿空白");
		} else if (!adminpassword.trim().matches(adminpsReg)) {
			errorMsgs.put("adminpassword", "管理員密碼只能是英文字母數字和_，長度需在5~20之間");
		}

		String email = adminvo.getEmail();
		String emailReg = "^\\w{1,63}@[a-zA-Z0-9]{2,63}\\.[a-zA-Z]{2,63}(\\.[a-zA-Z]{5,50})?$";
		if (email == null || email.trim().length() == 0) {
			errorMsgs.put("email", "信箱請勿空白");
		} else if (!email.trim().matches(emailReg)) {
			errorMsgs.put("email", "管理員信箱格式不符");
		}

		String adminname = adminvo.getAdminname();
		String nameReg = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,20}$";
		if (adminname == null || adminname.trim().length() == 0) {
			errorMsgs.put("adminname", "管理員名稱請勿空白");
		} else if (!adminname.trim().matches(nameReg)) {
			errorMsgs.put("adminname", "管理員名稱只能是中、英文字母、數字和_ , 且長度必需在2到20之間");
		}

		String phone = adminvo.getPhone();
		String phoneReg = "^[((0-9_)]{9,13}$";
		if (phone == null || phone.trim().length() == 0) {
			errorMsgs.put("phone", "電話號碼請勿空白");
		} else if (!phone.trim().matches(phoneReg)) {
			errorMsgs.put("phone", "電話號碼只能是數字, 且長度必需是9到13之間");
		} 

		Integer status = null;
		status = adminvo.getStatus();
		if (status == null) {
			errorMsgs.put("status", "請輸入狀態");
		} else if (status < 0 || status > 2) {
			errorMsgs.put("status", "請填入0,1,2 三種狀態");
		}

		String adminnickname = adminvo.getAdminnickname();
		String adminnicknameReg = "^[(\\u4e00-\\u9fa5)(a-zA-Z0-9_)]{2,20}$";
		if (adminnickname == null || adminnickname.trim().length() == 0) {
			errorMsgs.put("nickname", "管理員暱稱請勿空白");
		} else if (!adminnickname.trim().matches(adminnicknameReg)) {
			errorMsgs.put("nickname", "管理員暱稱只能是中、英文字母、數字和_ , 且長度必需在2到20之間");
		}
		adminnickname += "管理員";
		AdminVO adminVO = new AdminVO();
		adminVO.setAdminaccount(adminaccount);
		adminVO.setAdminpassword(adminpassword);
		adminVO.setEmail(email);
		adminVO.setAdminname(adminname);
		adminVO.setAdminnickname(adminnickname);
		adminVO.setPhone(phone);
		adminVO.setStatus(status);
		adminVO.setAdminid(adminid);

		if (!errorMsgs.isEmpty()) {
			return ResponseEntity.badRequest().body(errorMsgs); // 程式中斷
		}
		// 開始修改資料
		adminVO = adminSvc.updateAdmin(adminid, email, adminaccount, adminpassword, adminname, phone, status,
				adminnickname);
		// 修改完成準備轉交
		// 新增完成準備轉交
				Map<String, String> result = new HashMap<>();
				result.put("message", "更新成功");
				result.put("message2", ""); // 這個才是前端要抓的

				return ResponseEntity.ok(result);

	}

	@PostMapping("insert")
	public ResponseEntity<Map<String, String>> insert(@RequestBody AdminVO adminvo) {
		Map<String, String> errorMsgs = new HashMap<String, String>();

		// 輸入格式的錯誤處理

		String adminaccount = adminvo.getAdminaccount();
		String adminacReg = "^[(a-zA-Z0-9_)]{5,20}$";
		if (adminaccount == null || adminaccount.trim().length() == 0) {
			errorMsgs.put("adminaccount", "管理員帳號請勿空白");
		} else if (adminSvc.checkAccount(adminaccount)) {
			errorMsgs.put("adminaccount", "管理員帳號已經存在請更換帳號");
		} else if (!adminaccount.trim().matches(adminacReg)) {
			errorMsgs.put("adminaccount", "管理員帳號只能是英文字母數字和_，長度需在5~20之間");
		}

		String adminpassword = adminvo.getAdminpassword();
		String adminpsReg = "^[(a-zA-Z0-9_)]{5,15}$";
		if (adminpassword == null || adminpassword.trim().length() == 0) {
			errorMsgs.put("adminpassword", "管理員密碼請勿空白");
		} else if (!adminpassword.trim().matches(adminpsReg)) {
			errorMsgs.put("adminpassword", "管理員密碼只能是英文字母數字和_，長度需在5~20之間");
		}

		String email = adminvo.getEmail();
		String emailReg = "^\\w{1,63}@[a-zA-Z0-9]{2,63}\\.[a-zA-Z]{2,63}(\\.[a-zA-Z]{5,50})?$";
		if (email == null || email.trim().length() == 0) {
			errorMsgs.put("email", "信箱請勿空白");
		} else if (!email.trim().matches(emailReg)) {
			errorMsgs.put("email", "管理員信箱格式不符");
		}

		String adminname = adminvo.getAdminname();
		String nameReg = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,20}$";
		if (adminname == null || adminname.trim().length() == 0) {
			errorMsgs.put("adminname", "管理員名稱請勿空白");
		} else if (!adminname.trim().matches(nameReg)) {
			errorMsgs.put("adminname", "管理員名稱只能是中、英文字母、數字和_ , 且長度必需在2到20之間");
		}

		String phone = adminvo.getPhone();
		String phoneReg = "^[((0-9_)]{9,13}$";
		if (phone == null || phone.trim().length() == 0) {
			errorMsgs.put("phone", "電話號碼請勿空白");
		} else if (!phone.trim().matches(phoneReg)) {
			errorMsgs.put("phone", "電話號碼只能是數字, 且長度必需是9到13之間");
		} else if (adminSvc.checkPhone(phone)) {
			errorMsgs.put("phone", "電話號碼已被註冊, 請再確認");
		}

		Integer status = adminvo.getStatus();

		String adminnickname = adminvo.getAdminnickname() ;
		String adminnicknameReg = "^[(\\u4e00-\\u9fa5)(a-zA-Z0-9_)]{2,20}$";
		if (adminnickname == null || adminnickname.trim().length() == 0) {
			errorMsgs.put("nickname", "管理員暱稱請勿空白");
		} else if (!adminnickname.trim().matches(adminnicknameReg)) {
			errorMsgs.put("nickname", "管理員暱稱只能是中、英文字母、數字和_ , 且長度必需在2到20之間");
		}
		adminnickname += "管理員";

		AdminVO adminVO = new AdminVO();

		adminVO.setAdminaccount(adminaccount);
		adminVO.setAdminpassword(adminpassword);
		adminVO.setEmail(email);
		adminVO.setAdminname(adminname);
		adminVO.setAdminnickname(adminnickname);
		adminVO.setPhone(phone);
		adminVO.setStatus(status);

		if (!errorMsgs.isEmpty()) {
			return ResponseEntity.badRequest().body(errorMsgs);

		}
		// 開始新增資料
		AdminService adminSvc = new AdminService();
		adminVO = adminSvc.addAdmin(email, adminaccount, adminpassword, adminname, phone, status, adminnickname);
		// 新增完成準備轉交
		Map<String, String> result = new HashMap<>();
		result.put("message", "註冊成功");
		result.put("message2", ""); // 這個才是前端要抓的

		return ResponseEntity.ok(result);

	}

	@PostMapping("delete/{id}")
	public String delete(@PathVariable("id") Integer adminid) {

		AdminService adminSvc = new AdminService();
		AdminVO adminvo = adminSvc.getOneAdmin(adminid);
		if (adminvo != null) {
			adminSvc.deleteAdmin(adminid);
		} else {
			Map<String, String> errorMsgs = new HashMap<String, String>();
			errorMsgs.put("adminid", "查無此管理員，請重新確認id");

			return gson.toJson(errorMsgs);
		}
		// 刪除完成 轉交頁面
		return "delete succese";

	}

	@PostMapping("/login")
	private ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> payload, HttpSession session) {
	    String account = payload.get("account");
	    String password = payload.get("password");

	    if (account == null || account.trim().isEmpty()) {
	        return ResponseEntity.badRequest().body(Map.of("message", "請輸入帳號"));
	    }
	    if (password == null || password.trim().isEmpty()) {
	        return ResponseEntity.badRequest().body(Map.of("message", "請輸入密碼"));
	    }

	    try {
	        // 呼叫 Service 進行登入
	        AdminVO adminVO = adminSvc.login(account, password);

	        // 登入成功，將 adminId 放入 server-side session
	        session.setAttribute("adminId", adminVO.getAdminid());
	        session.setAttribute("adminName", adminVO.getAdminnickname());
	        // 回傳 JSON 給前端
	        Map<String, Object> result = new HashMap<>();
	        result.put("message", "登入成功");
	        result.put("adminId", adminVO.getAdminid()); // 這個才是前端要抓的

	        return ResponseEntity.ok(result);

	    } catch (LoginException e) {
	        // 根據錯誤類型回傳不同的訊息
	        if ("ACCOUNT_NOT_FOUND".equals(e.getErrorType())) {
	            return ResponseEntity.badRequest().body(Map.of("message", "帳號不存在"));
	        } else if ("INVALID_PASSWORD".equals(e.getErrorType())) {
	            return ResponseEntity.badRequest().body(Map.of("message", "密碼錯誤"));
	        } else {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "未知錯誤"));
	        }
	    }
	}
	@GetMapping("getall")
	public List<AdminVO> getAll(){
		return adminSvc.getAll();
	}

}
