package chilltrip.member.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
	public ResponseEntity<String> registerMember(@Valid @RequestPart("memberVO") MemberVO memberVO, @RequestPart(value = "photo", required = false) MultipartFile photoFile,
		    BindingResult result) {

		// 檢查驗證錯誤
		if (result.hasErrors()) {
			return ResponseEntity.badRequest().body("輸入資料有誤: " + result.getAllErrors().get(0).getDefaultMessage());
		}

		// 驗證信箱是否已存在
		if (memberSvc.checkEmailExists(memberVO.getEmail())) {
			System.out.println("此 email 已經註冊過，請使用其他 email");
			return ResponseEntity.badRequest().body("此 email 已經註冊過，請使用其他 email");
		}

		// 設定註冊時間與預設狀態與帳號
		memberVO.setAccount(memberVO.getEmail());
		memberVO.setCreateTime(new Timestamp(System.currentTimeMillis()));
		memberVO.setStatus(0); // 預設為一般狀態
		System.out.println("註冊時間與狀態設定完成，時間: " + memberVO.getCreateTime());

		if (photoFile != null && !photoFile.isEmpty()) {
	        try {
	            byte[] photo = photoFile.getBytes();
	            memberVO.setPhoto(photo);
	        } catch (IOException e) {
	            return ResponseEntity.badRequest().body("圖片上傳失敗: " + e.getMessage());
	        }
	    } else {
	    	memberVO.setPhoto(memberSvc.getDefaultAvatar());
	    }
		
		MemberVO savedMember = memberSvc.addMember(memberVO);
	    if (savedMember == null) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("註冊失敗，請稍後再試");
	    }
	    System.out.println("註冊成功，請登入");
	    return ResponseEntity.ok("註冊成功，請登入");

	}

	// 會員登入
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password, HttpSession session){
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
        
        if(memberVO == null) {
        	System.out.println("帳號或密碼錯誤");
        	return ResponseEntity.badRequest().body("帳號或密碼錯誤");
        }
        
        // 登入成功，將 memberId 放入 session 中
        session.setAttribute("memberId", memberVO.getMemberId());
        System.out.println("使用者:" + email + "登入成功");

        // 登入成功，跳轉到個人頁面
        return ResponseEntity.ok("登入成功，跳轉至個人頁面");
	}
	
	@GetMapping("/viewProfile")
	public ResponseEntity<Map<String, Object>> viewProfile(HttpSession session){
		Integer memberId = (Integer)session.getAttribute("memberId");
		
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
        response.put("photoBase64", photoBase64);
        
        System.out.println("成功取得會員資料");
        System.out.println("會員ID:" + memberVO.getMemberId() + ", " +
                "會員mail:" + memberVO.getEmail() + ", " +
                "會員帳號:" + memberVO.getAccount() + ", " +
                "會員密碼:" + memberVO.getPassword() + ", " +
                "會員姓名:" + memberVO.getName() + ", " +
                "會員電話:" + memberVO.getPhone() + ", " +
                "會員狀態:" + memberVO.getStatus() + ", " +
                "會員創立時間:" + memberVO.getCreateTime() + ", " +
                "會員暱稱:" + memberVO.getNickName() + ", " +
                "會員性別:" + genderStr + ", " +
                "會員生日:" + memberVO.getBirthday() + ", " +
                "會員公司統編:" + memberVO.getCompanyId() + ", " +
                "會員手機載具:" + memberVO.getEreceiptCarrier() + ", " +
                "會員信用卡號:" + memberVO.getCreditCard() + ", " +
                "會員追蹤數:" + memberVO.getTrackingNumber() + ", " +
                "會員粉絲數:" + memberVO.getFansNumber() + ", " +
                "會員照片: (已轉換為Base64)");
        
        // 返回會員資料
        return ResponseEntity.ok(response);
	}
	
	@PostMapping("/logout")
	public ResponseEntity<String> logout(HttpSession session){
		// 取得當前 session
		if(session != null) {
			session.invalidate();
		}
		
		System.out.println("登出成功");
		return ResponseEntity.ok("登出成功");
	}
	
	@PutMapping("/update")
	public ResponseEntity<String> updateMember(@RequestBody MemberVO memberVO, HttpSession session){
		Integer memberId = (Integer)session.getAttribute("memberId");
		
		// 確認用戶是否登入
		if(memberId == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("請先登入");
		}
		
		// 從資料庫取得原始會員資料，避免修改不可更改的欄位
		MemberVO originalMemberVO = memberSvc.getOneMember(memberId);
		if(originalMemberVO == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("會員資料不存在");
		}
		
		// 不可更改的欄位保留原始值
		memberVO.setMemberId(memberId);
	    memberVO.setEmail(originalMemberVO.getEmail());  // 保留原始email
	    memberVO.setAccount(originalMemberVO.getAccount());  // 保留原始account
	    memberVO.setCreateTime(originalMemberVO.getCreateTime());  // 保留創建時間
	    memberVO.setName(originalMemberVO.getName());  // 保留原始name
	    memberVO.setStatus(originalMemberVO.getStatus());  // 保留原始狀態
	    memberVO.setGender(originalMemberVO.getGender());  // 保留原始性別
	    memberVO.setBirthday(originalMemberVO.getBirthday());  // 保留原始生日
	    memberVO.setTrackingNumber(originalMemberVO.getTrackingNumber());  // 保留原始追蹤數
	    memberVO.setFansNumber(originalMemberVO.getFansNumber());  // 保留原始粉絲數
	    
	    if (memberVO.getPassword() == null) {
	        memberVO.setPassword(originalMemberVO.getPassword());  // 若使用者未修改保留原始密碼
	    }
	    
	    if (memberVO.getPhone() == null) {
	        memberVO.setPhone(originalMemberVO.getPhone());  // 若使用者未修改保留原始電話
	    }

	    if (memberVO.getNickName() == null) {
	        memberVO.setNickName(originalMemberVO.getNickName());  // 若使用者未修改保留原始暱稱
	    }

	    if (memberVO.getEreceiptCarrier() == null) {
	        memberVO.setEreceiptCarrier(originalMemberVO.getEreceiptCarrier());  // 若使用者未修改保留原始載具
	    }
	    
	    if (memberVO.getCompanyId() == null) {
	        memberVO.setCompanyId(originalMemberVO.getCompanyId());  // 若使用者未修改保留公司統編
	    }

	    if (memberVO.getCreditCard() == null) {
	        memberVO.setCreditCard(originalMemberVO.getCreditCard());  // 若使用者未修改保留原始信用卡號
	    }

	    if (memberVO.getPhoto() == null) {
	        memberVO.setPhoto(originalMemberVO.getPhoto());  // 若使用者未修改保留原始照片
	    }
		
        MemberVO updateMemberVO = memberSvc.updateMember(memberVO);
        if(updateMemberVO == null) {
        	System.out.println("更新資料失敗，請再試一次");
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新資料失敗，請再試一次");
        }
        
        // 更新成功
        System.out.println("會員資料更新成功");
        System.out.println(updateMemberVO);
        return ResponseEntity.ok("會員資料更新成功");
		
	}
	
	@PostMapping("/forgotPassword")
	public ResponseEntity<String> forgotPassword(@RequestParam String email){
		System.out.println("收到忘記密碼請求，Email: " + email);
		
		if(!memberSvc.checkEmailExists(email)) {
			return ResponseEntity.badRequest().body("該信箱未註冊");
		}
		
		String token = UUID.randomUUID().toString();
		int expirationTime = 3600; // 1 小時內有效
		memberSvc.setTempPassword(email, expirationTime, token);
		
		String resetLink = ServletUriComponentsBuilder.fromCurrentContextPath()
			    .path("/member/resetPassword")
			    .queryParam("token", token)
			    .queryParam("email", email)
			    .toUriString();
		try {
			memberSvc.sendEmail(email, "重設密碼連結", "點擊以下連結以重設密碼: \n" + resetLink + "\n" + "*請留意連結有效期限為 1 小時");
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		System.out.println("已發送重設密碼連結至信箱:" + email);
		
		return ResponseEntity.ok("重設密碼連結已發送至您的信箱");
	}
	
	@GetMapping("/resetPassword")
	public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String email){
		System.out.println("收到驗證Token和Email的請求，Email: " + email + ", Token: " + token);
		
		String redisToken = memberSvc.getTempPassword(email);
		if(redisToken == null || !redisToken.equals(token)) {
			return ResponseEntity.badRequest().body("無效或過期的重設密碼連結");
		}
		
		return ResponseEntity.ok("驗證成功，請繼續進行密碼重設");
	}
	
	@PostMapping("/newPassword")
	public ResponseEntity<String> newPassword(@RequestParam String email, @RequestParam String token, @RequestParam String newPassword, @RequestParam String confirmPassword){
		System.out.println("收到更新密碼請求，Email: " + email);
		
		if(!newPassword.equals(confirmPassword)) {
			return ResponseEntity.badRequest().body("兩次輸入的密碼不一致");
		}
		
		String passwordReg = "^[(a-zA-Z0-9_)]{5,15}$";
		if(!newPassword.matches(passwordReg)) {
			return ResponseEntity.badRequest().body("密碼只能是英文字母、數字和_，長度需在5~15之間");
		}
		
		String redisToken = memberSvc.getTempPassword(email);
		if(redisToken == null || !redisToken.equals(token)) {
			return ResponseEntity.badRequest().body("無效或過期的重設密碼連結");
		}
		
		boolean isUpdated = memberSvc.updatePassword(email, newPassword);
		if(!isUpdated) {
			return ResponseEntity.internalServerError().body("更新密碼失敗，請稍後再試");
		}
		
		memberSvc.deleteTempPassword(email);
		System.out.println("密碼更新成功，Email: " + email);
		
		return ResponseEntity.ok("密碼重設成功，請使用新密碼登入");
	}
}