package chilltrip.member.controller;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import chilltrip.member.model.MemberService;
import chilltrip.member.model.MemberVO;
import redis.clients.jedis.Jedis;


@WebServlet("/member")
@MultipartConfig
public class MemberServlet extends HttpServlet {

	private MemberService memberSvc;

	// 使用 Redis
	private Jedis jedis = new Jedis("localhost", 6379);

	public void init() {
		memberSvc = new MemberService();
		try {
			jedis.select(5); // 指定db5資料庫
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("無法連接到 Redis，請檢查伺服器設定。");
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		res.setContentType("text/html; charset=UTF-8");
		String action = req.getParameter("action");

		if ("randomcode".equals(action)) { // 傳送信箱驗證碼的請求
			List<String> errorMsgs = new LinkedList<String>(); // 儲存錯誤訊息的列表
			req.setAttribute("errorMsgs", errorMsgs);
			
			// 接收請求參數，輸入格式的錯誤處理
			String email = req.getParameter("email");
			String emailReg = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
			if (email == null || email.trim().length() == 0) {
				errorMsgs.add("信箱請勿空白");
			} else if (!email.trim().matches(emailReg)) {
				errorMsgs.add("信箱格式不符合，請輸入正確信箱格式");
			}

			if (memberSvc.checkEmailExists(email)) {
				errorMsgs.add("此 email 已經註冊過，請使用其他 email");
			}
			
			if (!errorMsgs.isEmpty()) {
			    req.setAttribute("errorMsgs", errorMsgs);
			    RequestDispatcher failureView = req.getRequestDispatcher("/frontend/member_registration.html");
			    failureView.forward(req, res);
			    return;
			}
			
			sendCode(email);
		}
		
		if("checkrandomcode".equals(action)) {  // 確認信箱驗證碼的請求
			List<String> errorMsgs = new LinkedList<String>(); // 儲存錯誤訊息的列表
			req.setAttribute("errorMsgs", errorMsgs);

			// 接收請求參數，驗證郵件驗證碼
			String emailCode = req.getParameter("emailCode");
			String email = req.getParameter("email").trim();  // 信箱去除前後空格
			
			if (emailCode == null || emailCode.trim().length() == 0) {
				errorMsgs.add("信箱驗證碼請勿空白");
			}
			
			// 從 Redis db5 中取出驗證碼
			String storedCode = jedis.get("verification_code:" + email);
			System.out.println("從 Redis 取得的驗證碼：" + storedCode);  // 確保儲存的驗證碼無誤
			System.out.println("用戶輸入的驗證碼：" + emailCode);  // 確保用戶輸入的驗證碼無誤

			if (storedCode == null || !storedCode.trim().equalsIgnoreCase(emailCode.trim())) {
				System.out.println("無效的驗證碼，請重新驗證");
			    errorMsgs.add("無效的驗證碼，請重新驗證");
			}
			
			if (!errorMsgs.isEmpty()) {
				System.out.println("驗證碼確認失敗！");
			    req.setAttribute("errorMsgs", errorMsgs);
			    RequestDispatcher failureView = req.getRequestDispatcher("/frontend/member_registration.html");
			    failureView.forward(req, res);
			    return;
			} else {
			    // 驗證成功，將結果傳遞到前端
			    req.setAttribute("successMessage", "驗證碼確認成功！");
			   
			    System.out.println("驗證碼確認成功！");
			    RequestDispatcher successView = req.getRequestDispatcher("/frontend/member_registration.html");
			    successView.forward(req, res);
			    return;
			}
		}

		if ("register".equals(action)) { // 註冊會員的請求
			
			System.out.println("開始註冊會員...");

			List<String> errorMsgs = new LinkedList<String>(); // 儲存錯誤訊息的列表
			req.setAttribute("errorMsgs", errorMsgs);

			 // 接收請求參數，處理信箱(將信箱作為帳號)
			String email = req.getParameter("email").trim();  // 信箱去除前後空格
			System.out.println("註冊的 email：" + email);  // 檢查 email 是否正確接收
			
			String emailReg = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
			if (email == null || email.trim().length() == 0) {
				errorMsgs.add("信箱請勿空白");
			} else if (!email.trim().matches(emailReg)) {
				errorMsgs.add("信箱格式不符合，請輸入正確信箱格式");
			}

			if (memberSvc.checkEmailExists(email)) {
				errorMsgs.add("此 email 已經註冊過，請使用其他 email");
			}

			String emailCode = req.getParameter("successMessage");

			String password = req.getParameter("password");
			System.out.println("註冊的密碼：" + password);  // 檢查密碼是否正確接收
			
			String passwordReg = "^[(a-zA-Z0-9_)]{5,15}$";
			if (password == null || password.trim().length() == 0) {
				errorMsgs.add("密碼請勿空白");
			} else if (!password.trim().matches(passwordReg)) {
				errorMsgs.add("密碼只能是英文字母數字和_，長度需在5~20之間");
			}

			String name = req.getParameter("name");
			System.out.println("註冊的姓名：" + name);  // 檢查姓名是否正確接收
			
			String nameReg = "^[\\u4E00-\\u9FFFa-zA-Z\\s]{2,20}$";
			if (name == null || name.trim().length() == 0) {
				errorMsgs.add("姓名請勿空白");
			} else if (!name.trim().matches(nameReg)) {
				errorMsgs.add("姓名可以包括中文、英文、空格，但不可是特殊符號，長度需在2~20之間，且不允許開頭或結尾有空格");
			}

			String phone = req.getParameter("phone");
			System.out.println("註冊的電話：" + phone);  // 檢查電話是否正確接收
			
			String phoneReg = "^(09[0-9]{8}|0[2-8][0-9]{7,8}|0[2-8]-[0-9]{6,8})$";
			if (phone == null || phone.trim().length() == 0) {
				errorMsgs.add("聯絡電話請勿空白");
			} else if (!phone.trim().matches(phoneReg)) {
				errorMsgs.add("請輸入正確聯絡電話格式，手機或市話皆可，ex: 0912345678 或 02-12345678");
			}

			Timestamp createTime = new Timestamp(System.currentTimeMillis()); // 生成當前時間
			System.out.println("註冊的時間：" + createTime);  // 檢查註冊時間是否正確接收

			String nickName = req.getParameter("nickname");
			System.out.println("註冊的暱稱：" + nickName);  // 檢查暱稱是否正確接收
			
			String nickNameReg = "^[\\u4E00-\\u9FFFa-zA-Z\\s]{2,20}$";
			if (nickName == null || nickName.trim().length() == 0) {
				errorMsgs.add("會員名稱請勿空白");
			} else if (!nickName.trim().matches(nickNameReg)) {
				errorMsgs.add("會員名稱可以包括中文、英文、空格，但不可是特殊符號，長度需在2~20之間，且不允許開頭或結尾有空格");
			}

			// 設置註冊後使用者的預設狀態為 0（一般狀態）
			Integer status = 0; // 0 表示一般狀態
			System.out.println("註冊的狀態：" + status);  // 檢查狀態是否正確接收

			// 接收前端傳來的性別選擇
			String genderStr = req.getParameter("gender");

			// 初始化 gender 變數
			Integer gender = null;

			// 根據前端傳來的性別值（字串），轉換為數字
			if (genderStr != null) {
				if ("男".equals(genderStr)) {
					gender = 0; // 男性對應 0
				} else if ("女".equals(genderStr)) {
					gender = 1; // 女性對應 1
				}
			}
			System.out.println("註冊的性別：" + gender);  // 檢查性別是否正確接收

			Date birthday = Date.valueOf(req.getParameter("birthday"));
			System.out.println("註冊的生日：" + birthday);  // 檢查生日是否正確接收
			

			String companyId = String.valueOf(req.getParameter("companyid"));
			System.out.println("註冊的公司統編：" + companyId);  // 檢查公司統編是否正確接收
			
			if (companyId != null && !companyId.trim().isEmpty()) {
				String companyIdReg = "^\\d{8}$";
				if (!companyId.trim().matches(companyIdReg)) {
					errorMsgs.add("公司統編為 8 位數字，且不能包含字母或特殊符號");					
				}
			}

			String ereceiptCarrier = req.getParameter("ereceiptcarrier");
			System.out.println("註冊的手機載具：" + ereceiptCarrier);  // 檢查手機載具是否正確接收
			
			if (ereceiptCarrier != null && !ereceiptCarrier.trim().isEmpty()) {
				String ereceiptCarrierReg = "^\\/[0-9A-Z.\\-\\+]{7}$";
				if (!ereceiptCarrier.trim().matches(ereceiptCarrierReg)) {
					errorMsgs.add("手機載具格式錯誤，總長度是 8 個字符，第一碼必須是\" / \"，後續接 7 個字符可以是數字(0-9)、大寫英文字母(A-Z)、以及特殊符號(.、-、+)");					
				}
			}

			String creditCard = req.getParameter("creditcard");
			System.out.println("註冊的信用卡號：" + creditCard);  // 檢查信用卡號是否正確接收
			
			if (creditCard != null && !creditCard.trim().isEmpty()) {
				String creditCardReg = "^(\\d{4}[-\\s]?){3}\\d{4}$|^\\d{13,19}$";
				if(!creditCard.trim().matches(creditCardReg)) {
					errorMsgs.add("信用卡號輸入格式不正確，請輸入 13 至 19 位的數字");
				}
			}
			
			// 註冊時預設追蹤數為 0
			Integer trackingNumber = 0;
			System.out.println("註冊的追蹤數：" + trackingNumber);  // 檢查追蹤數是否正確接收
			// 註冊時預設粉絲數為 0
			Integer fansNumber = 0;
			System.out.println("註冊的粉絲數：" + fansNumber);  // 檢查粉絲數是否正確接收

			byte[] photo = memberSvc.processPhoto(req);  // 調用 Service 層處理圖片邏輯
			
			if(photo == null) {
				System.out.println("圖片處理錯誤");  // 顯示圖片處理錯誤
				req.setAttribute("errorMsgs", "圖片處理錯誤");
				RequestDispatcher failureView = req.getRequestDispatcher("/frontend/member_registration.html");
				failureView.forward(req, res);
				return;
			}
			
			if (!errorMsgs.isEmpty()) {
				req.setAttribute("errorMsgs", errorMsgs); 
				RequestDispatcher failureView = req.getRequestDispatcher("/frontend/member_registration.html"); // 錯誤時導到原註冊頁面顯示錯誤頁面
				failureView.forward(req, res);
				return;
			}

			// 如果驗證成功，建立 MemberVO 物件並設置相應屬性
			MemberVO memberVO = new MemberVO();
			memberVO.setEmail(email);
			memberVO.setAccount(email);  // 帳號等於電子郵件
			memberVO.setPassword(password);
			memberVO.setName(name);
			memberVO.setPhone(phone);
			memberVO.setCreateTime(new Timestamp(System.currentTimeMillis())); // 創建時間為當前時間
			memberVO.setNickName(nickName);
			memberVO.setStatus(status);
			memberVO.setGender(gender);
			memberVO.setBirthday(birthday);
			memberVO.setCompanyId(companyId);
			memberVO.setEreceiptCarrier(ereceiptCarrier);
			memberVO.setCreditCard(creditCard);
			memberVO.setTrackingNumber(trackingNumber);
			memberVO.setFansNumber(fansNumber);
			memberVO.setPhoto(photo);
			System.out.println(memberVO);

			// 開始新增資料
			memberVO = memberSvc.addMember(memberVO);
			if (memberVO == null) {
			    System.out.println("資料庫新增失敗，請檢查 addMember 方法");
			} else {
			    System.out.println("會員資料已成功保存");
			}

			// 新增完成,準備轉交(Send the Success view)
			System.out.println("註冊成功，請登入");
			req.setAttribute("successMessage", "註冊成功，請登入");
			String url = "/frontend/member_login.html";
			RequestDispatcher successView = req.getRequestDispatcher(url); // 註冊成功時導到登入頁面
			successView.forward(req, res);

		}
		
		
		if("login".equals(action)) {  // 登入請求處理
			List<String> errorMsgs = new LinkedList<String>();  // 儲存錯誤訊息的列表
			req.setAttribute("errorMsgs", errorMsgs);
			
			// 接收請求參數
			// 取得登入表單中的帳號(信箱)和密碼
            String email = req.getParameter("email");
            String password = req.getParameter("password");

            // 驗證帳號和密碼
            if (email == null || email.trim().length() == 0) {
                errorMsgs.add("請輸入信箱");
            }
            if (password == null || password.trim().length() == 0) {
                errorMsgs.add("請輸入密碼");
            }
            
            if (!errorMsgs.isEmpty()) {
            	System.out.println("使用者:" + email + "登入失敗");
                RequestDispatcher failureView = req.getRequestDispatcher("/frontend/member_login.html");  // 錯誤時導到原登入頁面顯示錯誤頁面
                failureView.forward(req, res);
                return;
            }
			
			// 開始查詢資料
			MemberVO memberVO = memberSvc.login(email, password);
			
			if (memberVO == null) {
				System.out.println("使用者:" + email + "登入失敗");
                errorMsgs.add("帳號或密碼錯誤");
                RequestDispatcher failureView = req.getRequestDispatcher("/frontend/member_login.html");
                failureView.forward(req, res);
                return;
            }
			
			// 登入成功，將會員資料放入 session 中
            req.getSession().setAttribute("memberid", memberVO.getMemberId());
            System.out.println("使用者:" + email + "登入成功");
                      
            // 登入成功，導向到會員個人頁面
            res.sendRedirect(req.getContextPath() + "/member?cation=viewProfile");
            
		}
		
		if("viewProfile".equals(action)) {  // 登入後跳個人頁面查詢會員資料
			// 確認 session 中的使用者是否存在
			MemberVO memberid = (MemberVO)req.getSession().getAttribute("memberid");
			if(memberid == null) {
				// 如果沒有登入，導回登入頁面
				res.sendRedirect(req.getContextPath() + "/frontend/member_login.html");
				return;
			}
			
			// 取得 MemberVO 中的性別
			Integer gender = memberid.getGender();
			
			// 將性別從數字轉換成文字
			String genderStr = "";
			if(gender != null) {
				if(gender == 0){
					genderStr = "男";
				}else if(gender == 1) {
					genderStr = "女";
				}
			}
			
			// 把轉換後的性別文字傳遞到前端頁面
			req.setAttribute("gender", genderStr);
			
			// 將會員照片轉換為 Base64 字串
			String photoBase64 = memberSvc.encodePhotoToBase64(memberid);
			req.setAttribute("photoBase64", photoBase64);   // 將 Base64 字串傳遞給前端頁面
			
			try {
				// 直接使用 session 中的 memberVO 資料，不必再重新查詢
				req.setAttribute("memberVO", memberid); // 將會員資料傳遞到前端
				
				// 將資料轉發到會員中心頁面
				RequestDispatcher successView = req.getRequestDispatcher("/frontend/personal_homepage.html");
				successView.forward(req, res);
				System.out.println("成功取得會員資料");
				System.out.println("會員ID:" + memberid.getMemberId() + ",");
				System.out.println("會員mail:" + memberid.getEmail() + ",");
				System.out.println("會員帳號:" + memberid.getAccount() + ",");
				System.out.println("會員密碼:" + memberid.getPassword() + ",");
				System.out.println("會員姓名:" + memberid.getName() + ",");
				System.out.println("會員電話:" + memberid.getPhone() + ",");
				System.out.println("會員狀態:" + memberid.getStatus() + ",");
				System.out.println("會員創立時間:" + memberid.getCreateTime() + ",");
				System.out.println("會員暱稱:" + memberid.getNickName() + ",");
				System.out.println("會員性別:" + memberid.getGender() + ",");
				System.out.println("會員生日:" + memberid.getBirthday() + ",");
				System.out.println("會員公司統編:" + memberid.getCompanyId() + ",");
				System.out.println("會員手機載具:" + memberid.getEreceiptCarrier() + ",");
				System.out.println("會員信用卡號:" + memberid.getCreditCard() + ",");
				System.out.println("會員追蹤數:" + memberid.getTrackingNumber() + ",");
				System.out.println("會員粉絲數:" + memberid.getFansNumber() + ",");
				System.out.println("會員照片:" + memberid.getPhoto());
				

				}catch(Exception e) {
					System.out.println("無法取得會員資料");
					req.setAttribute("errorMessage", "無法取得會員資料：" + e.getMessage());
					RequestDispatcher failuserView = req.getRequestDispatcher("/frontend/personal_homepage.html");
					failuserView.forward(req, res);
			}
		}
		
		if("logout".equals(action)) {  // 登出請求處理
			
			// 取得當前session
			HttpSession session = req.getSession(false);  // false: 如果當前請求沒有session，則返回 null
			
			// 若 session 存在並且有 memberVO 資料，進行登出
			if(session != null) {
				// 移除 session 中的 memberVO 資料
				session.removeAttribute("memberVO");
				
				// 使 session 無效，確保完全登出
				session.invalidate();
			}
			
			// 導向未登入首頁
			res.sendRedirect(req.getContextPath() + "/frontend/index_nologin.html");
			System.out.println("登出成功");
		}
		
		if("update".equals(action)) {  // 更新會員資訊
			List<String> errorMsgs = new LinkedList<String>();
			req.setAttribute("errorMsgs", errorMsgs);
			
			// 取得 session 中的會員資料
			HttpSession session = req.getSession();
			MemberVO memberid = (MemberVO)session.getAttribute("memberid");
			
			if(memberid == null) {
				// 如果未登入，導回登入頁面
				res.sendRedirect(req.getContextPath() + "/frontend/member_login.html");
				return;
			}
			
			// 取得會員 ID，這是資料庫的主鍵
		    Integer memberId = memberid.getMemberId();
		    System.out.println("會員ID為:" + memberId);
			
		    // 使用 memberId 查詢會員資料，避免讓用戶修改不可更改的欄位
		    memberid = memberSvc.getOneMember(memberId);
		    
		    String email = memberid.getEmail();  // 不可更改的欄位
		    String account = memberid.getAccount();  // 不可更改的欄位
		    Timestamp createTime = memberid.getCreateTime();  // 不可更改的欄位
		    
			// 接收使用者輸入的資料
			String password = req.getParameter("password");
			System.out.println("修改的密碼：" + password);  // 檢查密碼是否正確接收
						
			String passwordReg = "^[(a-zA-Z0-9_)]{5,15}$";
			if (password == null || password.trim().length() == 0) {
					errorMsgs.add("密碼請勿空白");
			} else if (!password.trim().matches(passwordReg)) {
					errorMsgs.add("密碼只能是英文字母數字和_，長度需在5~20之間");
			}

			String name = memberid.getName();  // 不可更改的欄位
			
			String phone = req.getParameter("phone");
			System.out.println("修改的電話：" + phone);  // 檢查電話是否正確接收
						
			String phoneReg = "^(09[0-9]{8}|0[2-8][0-9]{7,8}|0[2-8]-[0-9]{6,8})$";
			if (phone == null || phone.trim().length() == 0) {
				errorMsgs.add("聯絡電話請勿空白");
			} else if (!phone.trim().matches(phoneReg)) {
				errorMsgs.add("請輸入正確聯絡電話格式，手機或市話皆可，ex: 0912345678 或 02-12345678");
			}
			
			String nickName = req.getParameter("nickname");
			System.out.println("修改的暱稱：" + nickName);  // 檢查暱稱是否正確接收
						
			String nickNameReg = "^[\\u4E00-\\u9FFFa-zA-Z\\s]{2,20}$";
			if (nickName == null || nickName.trim().length() == 0) {
				errorMsgs.add("會員名稱請勿空白");
			} else if (!nickName.trim().matches(nickNameReg)) {
				errorMsgs.add("會員名稱可以包括中文、英文、空格，但不可是特殊符號，長度需在2~20之間，且不允許開頭或結尾有空格");
			}
			
			// 狀態不需要更改
			Integer status = memberid.getStatus();

			// 性別不需要更改
			Integer gender = memberid.getGender();

			// 生日不需要更改
			Date birthday = memberid.getBirthday();

			String companyId = req.getParameter("companyid");
			System.out.println("修改的公司統編：" + companyId);  // 檢查公司統編是否正確接收
			
			if (companyId != null && !companyId.trim().isEmpty()) {
				String companyIdReg = "^\\d{8}$";
				if (!companyId.trim().matches(companyIdReg)) {
					errorMsgs.add("公司統編為 8 位數字，且不能包含字母或特殊符號");					
				}
			}

			String ereceiptCarrier = req.getParameter("ereceiptcarrier");
			System.out.println("修改的手機載具：" + ereceiptCarrier);  // 檢查手機載具是否正確接收
			
			if (ereceiptCarrier != null && !ereceiptCarrier.trim().isEmpty()) {
				String ereceiptCarrierReg = "^\\/[0-9A-Z.\\-\\+]{7}$";
				if (!ereceiptCarrier.trim().matches(ereceiptCarrierReg)) {
					errorMsgs.add("手機載具格式錯誤，總長度是 8 個字符，第一碼必須是\" / \"，後續接 7 個字符可以是數字(0-9)、大寫英文字母(A-Z)、以及特殊符號(.、-、+)");					
				}
			}

			String creditCard = req.getParameter("creditcard");
			System.out.println("修改的信用卡號：" + creditCard);  // 檢查信用卡號是否正確接收
			
			if (creditCard != null && !creditCard.trim().isEmpty()) {
				String creditCardReg = "^(\\d{4}[-\\s]?){3}\\d{4}$|^\\d{13,19}$";
				if(!creditCard.trim().matches(creditCardReg)) {
					errorMsgs.add("信用卡號輸入格式不正確，請輸入 13 至 19 位的數字");
				}
			}
			
			// 註冊時預設追蹤數為 0
			Integer trackingNumber = memberid.getTrackingNumber();
			System.out.println("註冊的追蹤數不可變更：" + trackingNumber);  // 檢查追蹤數是否正確接收
			
			// 註冊時預設粉絲數為 0
			Integer fansNumber = memberid.getFansNumber();
			System.out.println("註冊的粉絲數不可變更：" + fansNumber);  // 檢查粉絲數是否正確接收
			
			byte[] photo = memberSvc.processPhoto(req);  // 調用 Service 層處理圖片邏輯
						
			if(photo == null) {
				System.out.println("圖片處理錯誤");  // 顯示圖片處理錯誤
				req.setAttribute("errorMsgs", "圖片處理錯誤");
				RequestDispatcher failureView = req.getRequestDispatcher("/frontend/member_update.html");
				failureView.forward(req, res);
				return;
			}
						
			if (!errorMsgs.isEmpty()) {
				req.setAttribute("errorMsgs", errorMsgs); 
				RequestDispatcher failureView = req.getRequestDispatcher("/frontend/member_update.html"); // 錯誤時導到原更新資訊頁面顯示錯誤頁面
				failureView.forward(req, res);
				return;
			}

			// 如果驗證成功，建立 MemberVO 物件並設置相應屬性
			memberid = new MemberVO();
			memberid.setMemberId(memberId);
			memberid.setEmail(email);
			memberid.setAccount(account);  // 帳號等於電子郵件
			memberid.setPassword(password);
			memberid.setName(name);
			memberid.setPhone(phone);
			memberid.setCreateTime(createTime);
			memberid.setNickName(nickName);
			memberid.setStatus(status);
			memberid.setGender(gender);
			memberid.setBirthday(birthday);
			memberid.setCompanyId(companyId);
			memberid.setEreceiptCarrier(ereceiptCarrier);
			memberid.setCreditCard(creditCard);
			memberid.setTrackingNumber(trackingNumber);
			memberid.setFansNumber(fansNumber);
			memberid.setPhoto(photo);
			System.out.println(memberid);
			
			// 調用 Service 層更新資料
			MemberVO updatedMemberVO = memberSvc.updateMember(memberid);
			
			if(updatedMemberVO == null || email == null) {
				System.out.println("更新資料失敗，請再試一次");
				errorMsgs.add("更新資料失敗，請再試一次");
				RequestDispatcher failuserView = req.getRequestDispatcher("/frontend/member_update.html");
				failuserView.forward(req, res);
				return;
			}
			
			// 更新成功，將更新後的資料放回 session
			req.getSession().setAttribute("memberid", updatedMemberVO);
			
			// 更新成功，導向個人頁面
			System.out.println("會員資料更新成功");
			req.setAttribute("successMessage", "會員資料更新成功");
			RequestDispatcher successView = req.getRequestDispatcher("/frontend/personal_homepage.html");
			successView.forward(req, res);
		}
		if ("forgotPassword".equals(action)) { // 忘記密碼請求處理
			List<String> errorMsgs = new LinkedList<String>();
			req.setAttribute("errorMsgs", errorMsgs);

			// 接收信箱
			String email = req.getParameter("email");
			String emailReg = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
			if (email == null || email.trim().isEmpty()) {
				errorMsgs.add("請輸入信箱");
			} else if (!email.trim().matches(emailReg)) {
				errorMsgs.add("信箱格式不正確");
			}

			if (!errorMsgs.isEmpty()) {
				req.setAttribute("errorMsgs", errorMsgs);
				RequestDispatcher failureView = req.getRequestDispatcher("/frontend/member_forget_password.html");
				failureView.forward(req, res);
				return;
			}

			// 驗證信箱是否存在於資料庫
			if (!memberSvc.checkEmailExists(email)) {
				errorMsgs.add("該信箱未註冊");
			}

			if (!errorMsgs.isEmpty()) {
				req.setAttribute("errorMsgs", errorMsgs);
				RequestDispatcher failureView = req.getRequestDispatcher("/frontend/member_forget_password.html");
				failureView.forward(req, res);
				return;
			}

			// 生成 Token 並儲存到 Redis
			String token = UUID.randomUUID().toString();
			int expirationTime = 3600; // 1 小時內有效
			memberSvc.setTempPassword(email, expirationTime, token);

			// 發送重設密碼連結
			String resetLink = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort()
					+ req.getContextPath() + "/member?action=resetPassword&token=" + token + "&email=" + email;
			memberSvc.sendEmail(email, "重設密碼連結",
					"點擊以下連結以重設密碼: \n" + resetLink + "\n" + "*請留意連結有效期限為 1 小時");

			req.setAttribute("successMessage", "重設密碼連結已發送至您的信箱，請檢查郵件。");
			RequestDispatcher successView = req.getRequestDispatcher("/frontend/member_change_password.html");
			successView.forward(req, res);
		}

		if ("resetPassword".equals(action)) { // 轉跳改密碼驗證 token 及 email
			List<String> errorMsgs = new LinkedList<String>();
			req.setAttribute("errorMsgs", errorMsgs);

			// 接收 Token 和 Email
			String token = req.getParameter("token");
			String email = req.getParameter("email");

			// 驗證 token 和 email 是否有效
			if (token == null || email == null || token.trim().isEmpty() || email.trim().isEmpty()) {
				errorMsgs.add("無效的連結或信箱");
			}

			if (!errorMsgs.isEmpty()) {
				req.setAttribute("errorMsgs", errorMsgs);
				RequestDispatcher failureView = req.getRequestDispatcher("/frontend/member_change_password.html");
				failureView.forward(req, res);
				return;
			}

			// 驗證 Redis 中的 Token 是否有效
			String redisToken = memberSvc.getTempPassword(email);
			if (redisToken == null || !redisToken.equals(token)) {
				errorMsgs.add("無效或過期的重設密碼連結");
			}

			if (!errorMsgs.isEmpty()) {
				req.setAttribute("errorMsgs", errorMsgs);
				RequestDispatcher failureView = req.getRequestDispatcher("/frontend/member_change_password.html");
				failureView.forward(req, res);
				return;
			}

			// 如果 token 和 email 有效，顯示輸入新密碼的頁面
			RequestDispatcher successView = req.getRequestDispatcher("/frontend/member_change_password.html");
			successView.forward(req, res);
			return;
		}

		if ("newPassword".equals(action)) {  // 更新密碼的請求處理
			List<String> errorMsgs = new LinkedList<String>();
		    req.setAttribute("errorMsgs", errorMsgs);
		    
		    // 接收新密碼與確認密碼
		    String newPassword = req.getParameter("newPassword");
		    String confirmPassword = req.getParameter("confirmPassword");
		    String email = req.getParameter("email");
		    String token = req.getParameter("token");
			
		    // 驗證輸入欄位是否為空
		    if (newPassword == null || confirmPassword == null || newPassword.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
		        errorMsgs.add("密碼欄位不可為空");
		    }

		    // 驗證密碼是否一致
		    if (!newPassword.equals(confirmPassword)) {
		        errorMsgs.add("兩次輸入的密碼不一致");
		    }

		    // 密碼格式驗證
		    String passwordReg = "^[(a-zA-Z0-9_)]{5,15}$";
		    if (!newPassword.matches(passwordReg)) {
		        errorMsgs.add("密碼只能是英文字母、數字和_，長度需在5~15之間");
		    }

		    // 驗證 Redis 中的 Token 是否有效
		    String redisToken = memberSvc.getTempPassword(email);
		    if (redisToken == null || !redisToken.equals(token)) {
		        errorMsgs.add("無效或過期的重設密碼連結");
		    }

		    // 如果有錯誤，顯示錯誤訊息並返回
		    if (!errorMsgs.isEmpty()) {
		        req.setAttribute("errorMsgs", errorMsgs);
		        RequestDispatcher failureView = req.getRequestDispatcher("/frontend/reset_password.html");
		        failureView.forward(req, res);
		        return;
		    }

		    // 更新 MySQL 資料庫中的密碼
		    boolean isUpdated = memberSvc.updatePassword(email, newPassword);
		    if (!isUpdated) {
		        errorMsgs.add("更新密碼失敗，請稍後再試");
		        req.setAttribute("errorMsgs", errorMsgs);
		        RequestDispatcher failureView = req.getRequestDispatcher("/frontend/reset_password.html");
		        failureView.forward(req, res);
		        return;
		    }

		    // 刪除 Redis 中的 Token
		    memberSvc.deleteTempPassword(email);

		    req.setAttribute("successMessage", "密碼重設成功，請使用新密碼登入");
		    RequestDispatcher successView = req.getRequestDispatcher("/frontend/member_login.html");
		    successView.forward(req, res);
		}
	}

	// 發送驗證碼並保存至 Redis
	private void sendCode(String email) {
		try {
			String code = memberSvc.generateCode();
			jedis.setex("verification_code:" + email, 300, code); // 儲存驗證碼到 Redis，5 分鐘過期
			memberSvc.sendEmail(email, code); // 發送驗證碼
			System.out.println("驗證碼已存入 Redis 並發送郵件：" + code);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("驗證碼存入 Redis 失敗！");
		}
	}

	public void destroy() {
		if (jedis != null) {
			try {
				jedis.close();
				System.out.println("Jedis 連線已關閉。");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("關閉 Jedis 時發生錯誤！");
			}
		}

	}
}