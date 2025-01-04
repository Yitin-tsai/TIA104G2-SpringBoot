package chilltrip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import chilltrip.member.model.MemberService;
import chilltrip.member.model.MemberVO;

@Controller
public class PageController {
	@Autowired
	private MemberService memberSvc;

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("contextPath", "/TIA104G2-SpringBoot");
		return "/frontend/index";
	}

	@GetMapping("/announce/{id}")
	public String announcePage(@PathVariable int id) {

		return "/frontend/activity_blog";
	}

	// 註冊頁面路徑
	@GetMapping("/register")
	public String registerPage(Model model) {
		return "frontend/member_registration"; // 會員註冊頁面
	}

	// 登入頁面路徑
	@GetMapping("/login")
	public String loginPage(Model model) {
		return "frontend/member_login"; // 會員登入頁面
	}

	// 會員忘記密碼頁面路徑
	@GetMapping("/forgotPassword")
	public String forgotPasswordPage(Model model) {
		return "/frontend/member_forget_password"; // 會員忘記密碼頁面
	}

	// 會員重設密碼頁面
	@GetMapping("/resetPassword")
	public String changePasswordPage(Model model) {
		return "frontend/member_change_password"; // 會員重設密碼頁面
	}

	// 會員個人頁面
	@GetMapping("/viewProfile")
	public String viewProfile(Model model) {
		return "frontend/member_profile"; // 會員個人頁面
	}

	// 會員個人編輯頁面
	@GetMapping("/update")
	public String updateMember(Model model) {
		return "frontend/member_profile_edit"; // 會員個人編輯頁面
	}

	// 另一個會員的公開個人頁面
	@GetMapping("/viewPublicProfile/{memberId}")
	public String viewProfileByMemberId(@PathVariable Integer memberId, Model model) {
	    // 使用 MemberService 來獲取會員資訊
	    MemberVO memberVO = memberSvc.getMemberById(memberId);

	    // 將資料傳遞到前端頁面
	    model.addAttribute("memberVO", memberVO);
	    return "frontend/public_member_profile"; // 另一個會員的公開個人頁面
	}
	
	// 行程留言頁面
	@GetMapping("/tripComment/{tripId}")
	public String tripComment(@PathVariable int tripId, Model model) {
	    model.addAttribute("tripId", tripId);
	    return "frontend/trip_comment"; // 行程留言頁面
	}
}
