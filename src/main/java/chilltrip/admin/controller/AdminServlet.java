package chilltrip.admin.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import chilltrip.admin.model.AdminService;
import chilltrip.admin.model.AdminVO;



@RestController
@RequestMapping("/admin")
public class AdminServlet {

	@Autowired
	AdminService adminSvc;
	
	private Gson gson = new Gson();
	
	@GetMapping("getOne/{id}")
	public String getOneAdmin(@PathVariable("id") Integer adminid) {
		Map<String, String> errorMsgs = new HashMap<String, String>();
		
		// 查詢資料
	
		AdminVO adminVO = adminSvc.getOneAdmin(adminid);
		if (adminVO == null) {
			errorMsgs.put("adminid","查無此管理員，請重新確認id");
		}
		if (!errorMsgs.isEmpty()) {
			String errorMsGson = gson.toJson(errorMsgs);
			return errorMsGson;
		}
		
		String adminjson = gson.toJson(adminVO);
		return adminjson;

	}


	@PostMapping("update")
	public String update(@RequestBody AdminVO adminvo) {
		Map<String, String> errorMsgs = new HashMap<String, String>();

		// 輸入格式的錯誤處理
		Integer adminid = adminvo.getAdminid();
		AdminVO check = adminSvc.getOneAdmin(adminid);
		if(check ==null) {
			errorMsgs.put("adminid","查無此管理員請再確認編號");
		}
		String adminaccount = adminvo.getAdminaccount();
		String adminacReg = "^[(a-zA-Z0-9_)]{5,20}$";
		if (adminaccount == null || adminaccount.trim().length() == 0) {
			errorMsgs.put("adminaccount","管理員帳號請勿空白");
		} else if (!adminaccount.trim().matches(adminacReg)) {
			errorMsgs.put("adminaccount","管理員帳號只能是英文字母數字和_，長度需在5~20之間");
		}

		String adminpassword = adminvo.getAdminpassword();
		String adminpsReg = "^[(a-zA-Z0-9_)]{5,15}$";
		if (adminpassword == null || adminpassword.trim().length() == 0) {
			errorMsgs.put("adminpassword","管理員密碼請勿空白");
		} else if (!adminpassword.trim().matches(adminpsReg)) {
			errorMsgs.put("adminpassword","管理員密碼只能是英文字母數字和_，長度需在5~20之間");
		}

		String email =adminvo.getEmail();
		String emailReg = "^\\w{1,63}@[a-zA-Z0-9]{2,63}\\.[a-zA-Z]{2,63}(\\.[a-zA-Z]{5,50})?$";
		if (email == null || email.trim().length() == 0) {
			errorMsgs.put("adminemail","信箱請勿空白");
		} else if (!email.trim().matches(emailReg)) {
			errorMsgs.put("adminemail","管理員信箱格式不符");
		}

		String adminname = adminvo.getAdminname();
		String nameReg = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,20}$";
		if (adminname == null || adminname.trim().length() == 0) {
			errorMsgs.put("adminname","管理員名稱請勿空白");
		} else if (!adminname.trim().matches(nameReg)) {
			errorMsgs.put("adminname","管理員名稱只能是中、英文字母、數字和_ , 且長度必需在2到20之間");
		}

		String phone = adminvo.getPhone();
		String phoneReg = "^[((0-9_)]{9,13}$";
		if (phone == null || phone.trim().length() == 0) {
			errorMsgs.put("phone","電話號碼請勿空白");
		} else if (!phone.trim().matches(phoneReg)) {
			errorMsgs.put("phone","電話號碼只能是數字, 且長度必需是9到13之間");
		}

		Integer status = null;
		status = adminvo.getStatus();
		if(status == null) {
			errorMsgs.put("status","請輸入狀態");
		}else if (status < 0 || status > 2) {
			errorMsgs.put("status","請填入0,1,2 三種狀態");
		}
		

		String adminnickname = adminvo.getAdminnickname();
		String adminnicknameReg = "^[(\\u4e00-\\u9fa5)(a-zA-Z0-9_)]{2,20}$";
		if (adminnickname == null || adminnickname.trim().length() == 0) {
			errorMsgs.put("nickname","管理員暱稱請勿空白");
		} else if (!adminnickname.trim().matches(adminnicknameReg)) {
			errorMsgs.put("nickname","管理員暱稱只能是中、英文字母、數字和_ , 且長度必需在2到20之間");
		}

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
			String errorMsGson = gson.toJson(errorMsgs);
			return errorMsGson; // 程式中斷
		}
		// 開始修改資料
		AdminService adminSvc = new AdminService();
		adminVO = adminSvc.updateAdmin(adminid, email, adminaccount, adminpassword, adminname, phone, status,
				adminnickname);
		// 修改完成準備轉交
		return "/admin/listOneAdmin.jsp";
		

	}

	@PostMapping("insert")
	public String insert(@RequestBody AdminVO adminvo) {
		Map<String, String> errorMsgs = new HashMap<String, String>();

		// 輸入格式的錯誤處理

		String adminaccount = adminvo.getAdminaccount();
		String adminacReg = "^[(a-zA-Z0-9_)]{5,20}$";
		if (adminaccount == null || adminaccount.trim().length() == 0) {
			errorMsgs.put("adminaccount","管理員帳號請勿空白");
		} else if (!adminaccount.trim().matches(adminacReg)) {
			errorMsgs.put("adminaccount","管理員帳號只能是英文字母數字和_，長度需在5~20之間");
		}

		String adminpassword = adminvo.getAdminpassword();
		String adminpsReg = "^[(a-zA-Z0-9_)]{5,15}$";
		if (adminpassword == null || adminpassword.trim().length() == 0) {
			errorMsgs.put("adminpassword","管理員密碼請勿空白");
		} else if (!adminpassword.trim().matches(adminpsReg)) {
			errorMsgs.put("adminpassword","管理員密碼只能是英文字母數字和_，長度需在5~20之間");
		}

		String email =adminvo.getEmail();
		String emailReg = "^\\w{1,63}@[a-zA-Z0-9]{2,63}\\.[a-zA-Z]{2,63}(\\.[a-zA-Z]{5,50})?$";
		if (email == null || email.trim().length() == 0) {
			errorMsgs.put("adminemail","信箱請勿空白");
		} else if (!email.trim().matches(emailReg)) {
			errorMsgs.put("adminemail","管理員信箱格式不符");
		}

		String adminname = adminvo.getAdminname();
		String nameReg = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,20}$";
		if (adminname == null || adminname.trim().length() == 0) {
			errorMsgs.put("adminname","管理員名稱請勿空白");
		} else if (!adminname.trim().matches(nameReg)) {
			errorMsgs.put("adminname","管理員名稱只能是中、英文字母、數字和_ , 且長度必需在2到20之間");
		}

		String phone = adminvo.getPhone();
		String phoneReg = "^[((0-9_)]{9,13}$";
		if (phone == null || phone.trim().length() == 0) {
			errorMsgs.put("phone","電話號碼請勿空白");
		} else if (!phone.trim().matches(phoneReg)) {
			errorMsgs.put("phone","電話號碼只能是數字, 且長度必需是9到13之間");
		}

		
		Integer status = adminvo.getStatus();
	
		

		String adminnickname = adminvo.getAdminnickname();
		String adminnicknameReg = "^[(\\u4e00-\\u9fa5)(a-zA-Z0-9_)]{2,20}$";
		if (adminnickname == null || adminnickname.trim().length() == 0) {
			errorMsgs.put("nickname","管理員暱稱請勿空白");
		} else if (!adminnickname.trim().matches(adminnicknameReg)) {
			errorMsgs.put("nickname","管理員暱稱只能是中、英文字母、數字和_ , 且長度必需在2到20之間");
		}


		AdminVO adminVO = new AdminVO();

		adminVO.setAdminaccount(adminaccount);
		adminVO.setAdminpassword(adminpassword);
		adminVO.setEmail(email);
		adminVO.setAdminname(adminname);
		adminVO.setAdminnickname(adminnickname);
		adminVO.setPhone(phone);
		adminVO.setStatus(status);
		
		if (!errorMsgs.isEmpty()) {
			String errorMsGson = gson.toJson(errorMsgs);
			return errorMsGson;
		
		}
		// 開始新增資料
		AdminService adminSvc = new AdminService();
		adminVO = adminSvc.addAdmin(email, adminaccount, adminpassword, adminname, phone, status, adminnickname);
		// 新增完成準備轉交

		String url = "/admin/listAllAdmin.jsp";
		return "sucess" + url ;

	}

	@PostMapping("delete/{id}")
	public String delete(@PathVariable("id") Integer adminid) {
	
		AdminService adminSvc = new AdminService();
		AdminVO adminvo = adminSvc.getOneAdmin(adminid);
		if(adminvo!= null) {
		adminSvc.deleteAdmin(adminid);
		}else{
			Map<String, String> errorMsgs = new HashMap<String, String>();
			errorMsgs.put("adminid","查無此管理員，請重新確認id");
			
			return gson.toJson(errorMsgs);
		}
		// 刪除完成 轉交頁面
		return  "delete succese";
		

	}

}
