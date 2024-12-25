package chilltrip.admin.controller;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import chilltrip.admin.model.AdminService;
import chilltrip.admin.model.AdminVO;



@RestController
@RequestMapping("/admin")
public class AdminServlet {

	@GetMapping("getOne/{id}")
	public String getOneAdmin(@PathVariable("id") Integer adminid) {
		List<String> errorMsgs = new LinkedList<String>();
		
		// 查詢資料
		AdminService adminSvc = new AdminService();
		AdminVO adminVO = adminSvc.getOneAdmin(adminid);
		Gson gson = new Gson();
		if (adminVO == null) {
			errorMsgs.add("查無資料");
		}
		if (!errorMsgs.isEmpty()) {
			String errorMsGson = gson.toJson(errorMsgs);
			return errorMsGson;
		}
		
		String adminjson = gson.toJson(adminVO);
		return adminjson;

	}


	@PostMapping("update")
	public String update(@Valid AdminVO adminvo,BindingResult result, ModelMap model) {
		List<String> errorMsgs = new LinkedList<String>();

		// 輸入格式的錯誤處理
		Integer adminid = adminvo.getAdminid();

		String adminaccount = adminvo.getAdminaccount();
		String adminacReg = "^[(a-zA-Z0-9_)]{5,20}$";
		if (adminaccount == null || adminaccount.trim().length() == 0) {
			errorMsgs.add("管理員帳號請勿空白");
		} else if (!adminaccount.trim().matches(adminacReg)) {
			errorMsgs.add("管理員帳號只能是英文字母數字和_，長度需在5~20之間");
		}

		String adminpassword = adminvo.getAdminpassword();
		String adminpsReg = "^[(a-zA-Z0-9_)]{5,15}$";
		if (adminpassword == null || adminpassword.trim().length() == 0) {
			errorMsgs.add("管理員密碼請勿空白");
		} else if (!adminpassword.trim().matches(adminpsReg)) {
			errorMsgs.add("管理員密碼只能是英文字母數字和_，長度需在5~20之間");
		}

		String email =adminvo.getEmail();
		String emailReg = "^\\w{1,63}@[a-zA-Z0-9]{2,63}\\.[a-zA-Z]{2,63}(\\.[a-zA-Z]{5,50})?$";
		if (email == null || email.trim().length() == 0) {
			errorMsgs.add("信箱請勿空白");
		} else if (!email.trim().matches(emailReg)) {
			errorMsgs.add("管理員信箱格式不符");
		}

		String adminname = adminvo.getAdminname();
		String nameReg = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,20}$";
		if (adminname == null || adminname.trim().length() == 0) {
			errorMsgs.add("管理員名稱請勿空白");
		} else if (!adminname.trim().matches(nameReg)) {
			errorMsgs.add("管理員名稱只能是中、英文字母、數字和_ , 且長度必需在2到20之間");
		}

		String phone = adminvo.getPhone();
		String phoneReg = "^[((0-9_)]{9,13}$";
		if (phone == null || phone.trim().length() == 0) {
			errorMsgs.add("電話號碼請勿空白");
		} else if (!phone.trim().matches(phoneReg)) {
			errorMsgs.add("電話號碼只能是數字, 且長度必需是9到13之間");
		}

		Integer status = null;
		status = adminvo.getStatus();

		if (status < 0 || status > 2) {
			errorMsgs.add("請填入0,1,2 三種狀態");
		}
		try {
			status = Integer.valueOf(adminvo.getStatus());
		} catch (NumberFormatException e) {
			status = 0;
			errorMsgs.add("狀態請填入0,1,2 三種狀態");
		}

		String adminnickname = adminvo.getAdminnickname();
		String adminnicknameReg = "^[(\\u4e00-\\u9fa5)(a-zA-Z0-9_)]{2,20}$";
		if (adminnickname == null || adminnickname.trim().length() == 0) {
			errorMsgs.add("管理員暱稱請勿空白");
		} else if (!adminnickname.trim().matches(adminnicknameReg)) {
			errorMsgs.add("管理員暱稱只能是中、英文字母、數字和_ , 且長度必需在2到20之間");
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
		Gson gson = new Gson();
		
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
	public void insert(HttpServletRequest req, HttpServletResponse res) {
		List<String> errorMsgs = new LinkedList<String>();
		// Store this set in the request scope, in case we need to
		// send the ErrorPage view.
		req.setAttribute("errorMsgs", errorMsgs);

		String adminaccount = req.getParameter("adminaccount");
		String adminacReg = "^[(a-zA-Z0-9_)]{5,20}$";
		if (adminaccount == null || adminaccount.trim().length() == 0) {
			errorMsgs.add("管理員帳號請勿空白");
		} else if (!adminaccount.trim().matches(adminacReg)) {
			errorMsgs.add("管理員帳號只能是英文字母數字和_，長度需在5~20之間");
		}

		String adminpassword = req.getParameter("adminpassword");
		String adminpsReg = "^[(a-zA-Z0-9_)]{5,15}$";
		if (adminpassword == null || adminpassword.trim().length() == 0) {
			errorMsgs.add("管理員密碼請勿空白");
		} else if (!adminpassword.trim().matches(adminpsReg)) {
			errorMsgs.add("管理員密碼只能是英文字母數字和_，長度需在5~20之間");
		}

		String email = req.getParameter("email");
		String emailReg = "^\\w{1,63}@[a-zA-Z0-9]{2,63}\\.[a-zA-Z]{2,63}(\\.[a-zA-Z]{5,50})?$";
		if (email == null || email.trim().length() == 0) {
			errorMsgs.add("管理員信箱請勿空白");
		} else if (!email.trim().matches(emailReg)) {
			errorMsgs.add("管理員信箱格式不符");
		}

		String adminname = req.getParameter("adminname");
		String nameReg = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,20}$";
		if (adminname == null || adminname.trim().length() == 0) {
			errorMsgs.add("管理員名稱請勿空白");
		} else if (!adminname.trim().matches(nameReg)) {
			errorMsgs.add("管理員名稱只能是中、英文字母、數字和_ , 且長度必需在2到20之間");
		}

		String phone = req.getParameter("phone");
		String phoneReg = "^[((0-9_)]{9,13}$";
		if (phone == null || phone.trim().length() == 0) {
			errorMsgs.add("電話號碼請勿空白");
		} else if (!phone.trim().matches(phoneReg)) {
			errorMsgs.add("電話號碼只能是數字, 且長度必需是9到13之間");
		}

		Integer status = null;
		status = Integer.valueOf(req.getParameter("status").trim());

		if (status < 0 || status > 2) {
			errorMsgs.add("請填入0,1,2 三種狀態");
		}
		try {
			status = Integer.valueOf(req.getParameter("status").trim());
		} catch (NumberFormatException e) {
			status = 0;
			errorMsgs.add("狀態請填入0,1,2 三種狀態");
		}

		String adminnickname = req.getParameter("adminnickname");
		String adminnicknameReg = "^[(\\u4e00-\\u9fa5)(a-zA-Z0-9_)]{2,20}$";
		if (adminnickname == null || adminnickname.trim().length() == 0) {
			errorMsgs.add("管理員暱稱請勿空白");
		} else if (!adminnickname.trim().matches(adminnicknameReg)) {
			errorMsgs.add("管理員暱稱只能是中、英文字母、數字和_ , 且長度必需在2到20之間");
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
			req.setAttribute("adminVO", adminVO);
			RequestDispatcher failureView = req.getRequestDispatcher("/admin/addAdmin.jsp");
			try {
				failureView.forward(req, res);
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return; // 程式中斷
		}
		// 開始新增資料
		AdminService adminSvc = new AdminService();
		adminVO = adminSvc.addAdmin(email, adminaccount, adminpassword, adminname, phone, status, adminnickname);
		// 新增完成準備轉交

		String url = "/admin/listAllAdmin.jsp";
		RequestDispatcher successView = req.getRequestDispatcher(url);
		try {
			successView.forward(req, res);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@GetMapping("delete")
	public void delete(HttpServletRequest req, HttpServletResponse res) {
		List<String> errorMsgs = new LinkedList<String>();

		req.setAttribute("errorMsgs", errorMsgs);
		// 接受請求
		Integer adminid = Integer.valueOf(req.getParameter("adminid"));
		// 開始刪除
		AdminService adminSvc = new AdminService();
		adminSvc.deleteAdmin(adminid);
		;
		// 刪除完成 轉交頁面
		String url = "/admin/listAllAdmin.jsp";
		RequestDispatcher succesView = req.getRequestDispatcher(url);
		try {
			succesView.forward(req, res);
		} catch (ServletException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
