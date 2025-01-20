package chilltrip;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import chilltrip.member.model.MemberService;
import chilltrip.member.model.MemberVO;

@Controller
public class PageController {

	@Autowired
	private MemberService memberSvc;

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("contextPath", "/TIA104G2-SpringBoot");
		return "/frontend/index_logged_in";
	}

	@GetMapping("/home")
	public String indexLoggedIn(Model model) {
		model.addAttribute("contextPath", "/TIA104G2-SpringBoot");
		return "/frontend/index_logged_in";
	}

	// ------------修齊添加的url路徑（有些還在測試，只能用key的）---------------------------
	// Go行程
	@GetMapping("/go")
	public String goPage() {
		return "/frontend/go";
	}

	// 我的行程-->只有會員可以看到的「個人行程頁面」-->但允許會員邀請他人共編
	// 創建自己的新文章（非共編）

	// 我的行程頁面
	@GetMapping("/viewMyTrip")
	public String viewMyTrip(HttpServletRequest request, HttpSession session) {
		System.out.println("Accessing /viewMyTrip with session ID: " + session.getId());
		Integer memberId = (Integer) session.getAttribute("memberId");

		if (memberId == null) {
			System.out.println("No memberId in session, redirecting to login");
			return "redirect:/login";
		}

		System.out.println("User " + memberId + " accessing viewMyTrip");
		return "frontend/seal_mytrip";
	}

	// 新增文章路徑
	@GetMapping("/editor/create")
	public String showCreateEditor(HttpServletRequest request, HttpSession session) {

		System.out.println("Accessing /editor/create with session ID: " + session.getId());
		Integer memberId = (Integer) session.getAttribute("memberId");

		if (memberId == null) {
			System.out.println("No memberId in session, redirecting to login");
			return "redirect:/login";
		}

		System.out.println("User " + memberId + " accessing editor/create");
		return "frontend/seal_trip_editor";
	}

	// 單一行程文章
	@GetMapping("/tripArticle/{tripId}")
	public String viewTripArticle(@PathVariable Integer tripId) {
		return "/frontend/seal_trip_article";
	}

	// 會員行程主頁
	@GetMapping("/viewTrip/{memberId}")
	public String viewMemberTrip(@PathVariable Integer memberId) {
		return "/frontend/seal_membertrip";
	}


	// ---------------------------------------

	// 會員相關頁面
	@GetMapping("/register")
	public String registerPage(Model model) {
		return "/frontend/member_registration";
	}

	@GetMapping("/login")
	public String loginPage(Model model) {
		return "/frontend/member_login";
	}

	@GetMapping("/forgotPassword")
	public String forgotPasswordPage(Model model) {
		return "/frontend/member_forget_password";
	}

	@GetMapping("/resetPassword")
	public String changePasswordPage(Model model) {
		return "/frontend/member_change_password";
	}

	@GetMapping("/viewProfile")
	public String viewProfile(Model model) {
		return "/frontend/member_profile";
	}

	@GetMapping("/update")
	public String updateMember(Model model) {
		return "/frontend/member_profile_edit";
	}

	@GetMapping("/viewPublicProfile/{memberId}")
	public String viewProfileByMemberId(@PathVariable Integer memberId, Model model) {
		MemberVO memberVO = memberSvc.getMemberById(memberId);
		model.addAttribute("memberVO", memberVO);
		return "/frontend/public_member_profile";
	}

	@GetMapping("/tripComment/{tripId}")
	public String tripComment(@PathVariable int tripId, Model model) {
		model.addAttribute("tripId", tripId);
		return "/frontend/trip_comment";
	}

	// 管理員相關頁面
	@GetMapping("/adminLogin")
	public String adminLogin(Model model) {
		return "/frontend/admin_login";
	}

	@GetMapping("/adminRegister")
	public String adminRegister(Model model) {
		return "/frontend/admin_registration";
	}

	@GetMapping("/adminForgotPassword")
	public String adminForgotPasswordPage(Model model) {
		return "/frontend/admin_forget_password";
	}

	@GetMapping("/adminProfile")
	public String adminProfile(Model model) {
		return "/frontend/admin_profile";
	}

	@GetMapping("/adminUpdate")
	public String adminUpdate(Model model) {
		return "/frontend/admin_update";
	}

	@GetMapping("/adminList")
	public String adminLiset(Model model) {
		return "/frontend/admin_list";
	}

	@GetMapping("/announceList")
	public String announceList(Model model) {
		return "/frontend/announce_list";
	}

	@GetMapping("/announceListById")
	public String announceListById(Model model) {
		return "/frontend/announce_listById";
	}

	@GetMapping("/announceAdd")
	public String announceAdd(Model model) {
		return "/frontend/announce_add";
	}

	@GetMapping("/announce/{id}")
	public String announcePage(@PathVariable int id) {
		return "/frontend/activity_blog";
	}

	@GetMapping("/announceUpdate/{id}")
	public String announceUpdate(@PathVariable int id) {
		return "/frontend/announce_update";
	}

	@GetMapping("/announceCoedit/{id}")
	public String announceCoedit(@PathVariable int id) {
		return "/frontend/announce_coedit";
	}

	// 聊天室
	@GetMapping("/chatroom")
	public String chatroom(Model model) {
		return "/frontend/chat_vanilla copy";
	}
}
