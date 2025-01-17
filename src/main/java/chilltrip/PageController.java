package chilltrip;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
    
    @GetMapping("/loggedin")
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
	@GetMapping("/editor/create")
	public String showCreateEditor(HttpServletRequest request) {
		Integer loginMemberId = (Integer) request.getAttribute("loginMemberId");
		if (loginMemberId == null) {
			return "redirect:/login";
		}
		return "frontend/seal_trip_editor";
	}

	// 第三人視角看「我的行程」-->未登入會員也可以看
	public String viewOtherTrip(@PathVariable Integer memberId, Model model) {
		MemberVO memberVO = memberSvc.getMemberById(memberId);
		if (memberVO == null) {
			return "error/404";
		}
		model.addAttribute("memberVO", memberVO);
		return "/frontend/sealfinal_profile_page";
	}

	// 新增的編輯器路徑
	@GetMapping("/viewMyTrip/{memberId}/editor")
	public String tripEditor(@PathVariable Integer memberId, HttpServletRequest request, Model model) {
		// 獲取登入會員 ID
		Integer loginMemberId = (Integer) request.getAttribute("loginMemberId");

		// 確保用戶只能訪問自己的編輯器
		if (!memberId.equals(loginMemberId)) {
			return "redirect:/";
		}

		return "/frontend/trip-editor";
	}

	// 編輯特定文章
	@GetMapping("/viewMyTrip/{memberId}/editor/{articleId}")
	public String editArticle(@PathVariable Integer memberId, @PathVariable Integer articleId,
			HttpServletRequest request, Model model) {
		Integer loginMemberId = (Integer) request.getAttribute("loginMemberId");

		// 驗證權限
		if (!memberId.equals(loginMemberId)) {
			return "redirect:/";
		}

		// TODO: 獲取文章數據
		// ArticleVO article = articleService.getArticleById(articleId);
		// model.addAttribute("article", article);

		return "/frontend/trip-editor";
	}

	@GetMapping("/tripeditor")
	public String editTripByMemberId(Model model) {
		return "/frontend/seal_trip_editor";
	}

	@GetMapping("/product")
	public String productPage() {
		return "/frontend/product";
	}

	@GetMapping("/profile")
	public String profilePage() {
		return "/frontend/member_profile";
	}

	@GetMapping("/guide")
	public String guidePage() {
		return "/frontend/guide";
	}

	@GetMapping("/basic-info")
	public String basicInfoPage() {
		return "/frontend/basic_info";
	}

	@GetMapping("/coupons")
	public String couponsPage() {
		return "/frontend/coupons";
	}

	@GetMapping("/orders")
	public String ordersPage() {
		return "/frontend/orders";
	}

	@GetMapping("/support")
	public String supportPage() {
		return "/frontend/support";
	}

	@GetMapping("/cart")
	public String cartPage() {
		return "/frontend/cart";
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
