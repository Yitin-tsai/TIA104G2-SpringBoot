package chilltrip.member.controller;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import chilltrip.member.model.MemberService;
import chilltrip.member.model.MemberVO;
import redis.clients.jedis.Jedis;

@RestController
@RequestMapping("/member")
public class MemberController {

	@Autowired
	private MemberService memberSvc;

	// 使用 Redis
	private Jedis jedis = new Jedis("localhost", 6379);

	public MemberController() {
		// 指定 Redis 資料庫 db5
		jedis.select(5);
	}

	// 發送驗證碼
	@PostMapping("/randomcode")
	public ResponseEntity<String> sendCode(@RequestParam String email) {
		// 檢查 email 是否已註冊
		if (memberSvc.checkEmailExists(email)) {
			return ResponseEntity.badRequest().body("此 email 已經註冊過，請使用其他 email");
		}
		// 生成驗證碼
		String verificationCode = memberSvc.generateCode();

		// 將驗證碼存到 Redis，並設置過期時間為 5 分鐘
		jedis.setex("verificationCode:" + email, 300, verificationCode);

		// 發送驗證碼至 email
		try {
			memberSvc.sendVerificationEmail(email, verificationCode);
			System.out.println("已發送驗證碼至 " + email);
			return ResponseEntity.ok("驗證碼已發送至您的信箱");
		} catch (MessagingException e) {
			System.out.println("發送郵件時發生錯誤: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("發送郵件時發生錯誤");
		}
	}

	// 確認信箱驗證碼
	@PostMapping("/checkrandomcode")
	public ResponseEntity<String> checkVerificationCode(@RequestParam String email, @RequestParam String emailCode) {
		String storedCode = jedis.get("verificationCode:" + email);

		if (storedCode == null || !storedCode.trim().equalsIgnoreCase(emailCode.trim())) {
			return ResponseEntity.badRequest().body("無效的驗證碼，請重新驗證");
		}
		return ResponseEntity.ok("驗證碼確認成功！");
	}

	// 會員註冊
	@PostMapping("/register")
	public ResponseEntity<String> registerMember(@RequestParam Map<String, String> paramMap, // 接收前端傳遞的 Map
			@RequestParam(name = "photo", required = false) MultipartFile photoFile) {

		// 將 Map 轉換為 MemberVO
		MemberVO memberVO = new MemberVO();
		try {
			memberVO.setNickName(paramMap.get("nickName"));
			memberVO.setName(paramMap.get("name"));
			memberVO.setPhone(paramMap.get("phone"));
			memberVO.setEmail(paramMap.get("email"));
			memberVO.setPassword(paramMap.get("password"));
			memberVO.setGender("男性".equals(paramMap.get("gender")) ? 0 : 1); // 性別轉換
			memberVO.setBirthday(Date.valueOf(paramMap.get("birthday"))); // 生日轉換為 java.sql.Date
			memberVO.setCompanyId(paramMap.get("companyId"));
			memberVO.setEreceiptCarrier(paramMap.get("ereceiptCarrier"));
			memberVO.setCreditCard(paramMap.get("creditCard"));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("資料格式錯誤，請重新提交");
		}

		// 驗證信箱是否已存在
		if (memberSvc.checkEmailExists(memberVO.getEmail())) {
			return ResponseEntity.badRequest().body("此 email 已經註冊過，請使用其他 email");
		}

		// 設定註冊時間與預設狀態與帳號
		memberVO.setCreateTime(new Timestamp(System.currentTimeMillis()));
		memberVO.setAccount(memberVO.getEmail()); // 使用 email 作為帳號
		memberVO.setStatus(0); // 預設為一般狀態
		memberVO.setTrackingNumber(0); // 預設追蹤數為0
		memberVO.setFansNumber(0); // 預設粉絲數為0

		// 處理圖片上傳
		if (photoFile != null && !photoFile.isEmpty()) {
			try {
				byte[] photo = photoFile.getBytes();
				memberVO.setPhoto(photo);
			} catch (IOException e) {
				return ResponseEntity.badRequest().body("圖片上傳失敗，請稍後再試");
			}
		} else {
			memberVO.setPhoto(memberSvc.getDefaultAvatar()); // 若無上傳圖片，設為預設頭像
		}

		// 儲存資料
		MemberVO savedMember = memberSvc.addMember(memberVO);
		if (savedMember == null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("註冊失敗，請稍後再試");
		}

		return ResponseEntity.ok("註冊成功，請登入");
	}

	// 會員登入
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody Map<String, String> payload, HttpSession session) {
		System.out.println("payload是: " + payload); // 確認是否收到資料

		String email = payload.get("email");
		String password = payload.get("password");

		System.out.println("登入請求 信箱: " + email + ", 密碼: " + password);

		// 驗證信箱與密碼是否為空
		if (email == null || email.trim().isEmpty()) {
			System.out.println("請輸入信箱");
			return ResponseEntity.badRequest().body("請輸入信箱");
		}

		if (password == null || password.trim().isEmpty()) {
			System.out.println("請輸入密碼");
			return ResponseEntity.badRequest().body("請輸入密碼");
		}

		// 驗證帳號密碼是否正確
		MemberVO memberVO = memberSvc.login(email, password);

		if (memberVO == null) {
			System.out.println("帳號或密碼錯誤");
			return ResponseEntity.badRequest().body("帳號或密碼錯誤");
		}

		// 登入成功，將 memberId 放入 session 中
		session.setAttribute("memberId", memberVO.getMemberId());
		System.out.println("使用者: " + email + " 登入成功");

		return ResponseEntity.ok("登入成功，跳轉至個人頁面");
	}

	// 瀏覽個人頁面
	@GetMapping("/viewProfile")
	public ResponseEntity<Map<String, Object>> viewProfile(HttpSession session) {
		Integer memberId = (Integer) session.getAttribute("memberId");

		// 確認 session 中的使用者是否存在
		if (memberId == null) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("message", "未登入，請先登入");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
		}

		// 使用 memberId 查詢會員資料
		MemberVO memberVO = memberSvc.getOneMember(memberId);
		if (memberVO == null) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("message", "無法查詢到會員資料");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
		}

		// 取得性別
		Integer gender = memberVO.getGender();
		String genderStr = (gender != null && gender == 0) ? "男" : (gender != null && gender == 1) ? "女" : "";

		// 轉換照片為 Base64
		String photoBase64 = memberSvc.encodePhotoToBase64(memberVO);

		// 建立一個 Map 來傳遞資料
		Map<String, Object> response = new HashMap<>();
		response.put("memberId", memberVO.getMemberId());
		response.put("email", memberVO.getEmail());
		response.put("account", memberVO.getAccount());
		response.put("password", memberVO.getPassword());
		response.put("name", memberVO.getName());
		response.put("phone", memberVO.getPhone());
		response.put("status", memberVO.getStatus());
		response.put("createTime", memberVO.getCreateTime());
		response.put("nickName", memberVO.getNickName());
		response.put("gender", genderStr);
		response.put("birthday", memberVO.getBirthday());
		response.put("companyId", memberVO.getCompanyId());
		response.put("ereceiptCarrier", memberVO.getEreceiptCarrier());
		response.put("creditCard", memberVO.getCreditCard());
		response.put("trackingNumber", memberVO.getTrackingNumber());
		response.put("fansNumber", memberVO.getFansNumber());
		response.put("photo", photoBase64);

		System.out.println("成功取得會員資料");
		System.out.println("會員ID:" + memberVO.getMemberId() + ", " + "會員mail:" + memberVO.getEmail() + ", " + "會員帳號:"
				+ memberVO.getAccount() + ", " + "會員密碼:" + memberVO.getPassword() + ", " + "會員姓名:" + memberVO.getName()
				+ ", " + "會員電話:" + memberVO.getPhone() + ", " + "會員狀態:" + memberVO.getStatus() + ", " + "會員創立時間:"
				+ memberVO.getCreateTime() + ", " + "會員暱稱:" + memberVO.getNickName() + ", " + "會員性別:" + genderStr + ", "
				+ "會員生日:" + memberVO.getBirthday() + ", " + "會員公司統編:" + memberVO.getCompanyId() + ", " + "會員手機載具:"
				+ memberVO.getEreceiptCarrier() + ", " + "會員信用卡號:" + memberVO.getCreditCard() + ", " + "會員追蹤數:"
				+ memberVO.getTrackingNumber() + ", " + "會員粉絲數:" + memberVO.getFansNumber() + ", "
				+ "會員照片: (已轉換為Base64)");
		System.out.println("圖片 Base64 資料：" + photoBase64);

		// 返回會員資料
		return ResponseEntity.ok(response);
	}
	
	// 根據使用者ID獲取使用者的公開資訊
	@GetMapping("/viewPublicProfile/{memberId}")
	public ResponseEntity<Map<String, Object>> viewPublicProfile(@PathVariable("memberId") Integer memberId) {
	    // 使用 service 查詢會員資料
	    MemberVO memberVO = memberSvc.getMemberById(memberId);
	    
	    // 如果找不到會員資料，返回 404
	    if (memberVO == null) {
	        Map<String, Object> errorResponse = new HashMap<>();
	        errorResponse.put("message", "該會員不存在");
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	    }
	    
	    // 只返回公開資訊（如暱稱、追蹤數、粉絲數等）
	    Map<String, Object> response = new HashMap<>();
	    response.put("nickName", memberVO.getNickName());
	    response.put("trackingNumber", memberVO.getTrackingNumber());
	    response.put("fansNumber", memberVO.getFansNumber());
	    
	    // 照片轉為 Base64
	    String photoBase64 = memberSvc.encodePhotoToBase64(memberVO);
	    response.put("photo", photoBase64);
	    
	    System.out.println("成功查詢公開資訊：" + response);
	    
	    // 返回公開資訊
	    return ResponseEntity.ok(response);
	}
	
	// 會員登出
	@PostMapping("/logout")
	public ResponseEntity<String> logout(HttpSession session) {
		// 取得當前 session
		if (session != null) {
			session.invalidate();
		}

		System.out.println("登出成功");
		return ResponseEntity.ok("登出成功");
	}

	// 會員更新個人資訊
	@PutMapping("/update")
	public ResponseEntity<String> updateMember(@RequestParam("nickName") String nickName,
			@RequestParam("phone") String phone, @RequestParam("companyId") String companyId,
			@RequestParam("ereceiptCarrier") String ereceiptCarrier, @RequestParam("creditCard") String creditCard,
			@RequestParam(value = "password", required = false) String password,
			@RequestParam(value = "photo", required = false) MultipartFile photoFile, HttpSession session) {

		// 從 session 取得會員 ID
		Integer memberId = (Integer) session.getAttribute("memberId");

		if (memberId == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("請先登入");
		}

		// 根據 memberId 取得原始的會員資料
		MemberVO originalMemberVO = memberSvc.getOneMember(memberId);
		if (originalMemberVO == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("會員資料不存在");
		}

		// 新的 MemberVO 用來保存更新後的會員資料
		MemberVO memberVO = new MemberVO();
		try {
			// 從前端傳過來的資料設置到 MemberVO
			memberVO.setNickName(nickName);
			memberVO.setPhone(phone);
			memberVO.setCompanyId(companyId);
			memberVO.setEreceiptCarrier(ereceiptCarrier);
			memberVO.setCreditCard(creditCard);

			// 如果有提供密碼，才更新密碼
			if (password != null && !password.isEmpty()) {
				memberVO.setPassword(password);
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("資料格式錯誤，請重新提交");
		}

		// 保留原始資料中的不可更改欄位
		memberVO.setMemberId(memberId);
		memberVO.setEmail(originalMemberVO.getEmail()); // 保留原始email
		memberVO.setAccount(originalMemberVO.getAccount()); // 保留原始account
		memberVO.setCreateTime(originalMemberVO.getCreateTime()); // 保留創建時間
		memberVO.setName(originalMemberVO.getName()); // 保留原始name
		memberVO.setStatus(originalMemberVO.getStatus()); // 保留原始狀態
		memberVO.setGender(originalMemberVO.getGender()); // 保留原始性別
		memberVO.setBirthday(originalMemberVO.getBirthday()); // 保留原始生日
		memberVO.setTrackingNumber(originalMemberVO.getTrackingNumber()); // 保留原始追蹤數
		memberVO.setFansNumber(originalMemberVO.getFansNumber()); // 保留原始粉絲數

		// 若使用者未修改保留原始密碼、電話、暱稱等資料
		if (memberVO.getPassword() == null) {
			memberVO.setPassword(originalMemberVO.getPassword());
		}

		if (memberVO.getPhone() == null) {
			memberVO.setPhone(originalMemberVO.getPhone());
		}

		if (memberVO.getNickName() == null) {
			memberVO.setNickName(originalMemberVO.getNickName());
		}

		if (memberVO.getEreceiptCarrier() == null) {
			memberVO.setEreceiptCarrier(originalMemberVO.getEreceiptCarrier());
		}

		if (memberVO.getCompanyId() == null) {
			memberVO.setCompanyId(originalMemberVO.getCompanyId());
		}

		if (memberVO.getCreditCard() == null) {
			memberVO.setCreditCard(originalMemberVO.getCreditCard());
		}

		// 處理照片上傳（如果有）
		if (photoFile != null && !photoFile.isEmpty()) {
			try {
				byte[] photoBytes = photoFile.getBytes();
				memberVO.setPhoto(photoBytes); // 保存照片到 MemberVO
			} catch (IOException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("圖片上傳失敗");
			}
		} else {
			memberVO.setPhoto(originalMemberVO.getPhoto());
		}

		// 更新會員資料，這裡假設 updateMember 方法會返回更新後的 MemberVO
		MemberVO updatedMemberVO = memberSvc.updateMember(memberVO);

		// 檢查返回的 MemberVO 是否為 null，來判斷更新是否成功
		if (updatedMemberVO != null) {
			try {
				ObjectMapper objectMapper = new ObjectMapper();

				// 使用標準方式來初始化 HashMap
				HashMap<String, String> responseMap = new HashMap<>();
				responseMap.put("message", "會員資料更新成功");

				String responseBody = objectMapper.writeValueAsString(responseMap);

				return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.body(responseBody);

			} catch (JsonProcessingException e) {
				e.printStackTrace(); // 可以根據需求選擇處理這個異常，例如返回錯誤訊息
				return ResponseEntity.status(500).body("{\"message\": \"內部錯誤，無法處理請求\"}");
			}
		}

		// 若更新失敗，返回錯誤訊息
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("會員資料更新失敗");
	}

	// 會員忘記密碼傳送驗證信
	@PostMapping("/forgotPassword/sendCode")
	public ResponseEntity<String> sendForgotPasswordCode(@RequestParam String email) {
	    if (!memberSvc.checkEmailExists(email)) {
	        return ResponseEntity.badRequest().body("此 email 未註冊");
	    }

	    String code = memberSvc.generateCode();
	    memberSvc.setTempPassword("forgotPassword:" + email, 300, code); // Redis 紀錄

	    try {
	        memberSvc.sendVerificationEmail(email, code);
	        return ResponseEntity.ok("驗證碼已寄出，請檢查您的信箱");
	    } catch (MessagingException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("發送驗證碼失敗，請稍後再試");
	    }
	}

	// 驗證會員忘記密碼信箱驗證碼
	@PostMapping("/forgotPassword/verifyCode")
	public ResponseEntity<String> verifyForgotPasswordCode(@RequestParam String email, @RequestParam String code) {
	    String storedCode = memberSvc.getTempPassword("forgotPassword:" + email);

	    if (storedCode == null || !storedCode.equals(code)) {
	        return ResponseEntity.badRequest().body("驗證碼錯誤或已過期");
	    }

	    return ResponseEntity.ok("驗證成功");
	}


	// 驗證碼成功後讓會員重設密碼
	@PostMapping("/resetPassword")
	public ResponseEntity<String> resetPassword(@RequestParam String email,
	                                            @RequestParam String password
	                                       		) {
		
	    boolean success = memberSvc.updatePassword(email, password);
	    
	    if (!success) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("密碼更新失敗，請稍後再試");
	    }

	    return ResponseEntity.ok("密碼更新成功，請重新登入");
	}
}