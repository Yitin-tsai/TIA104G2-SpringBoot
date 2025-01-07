package chilltrip;

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
        return "/frontend/index";
    }
    
    @GetMapping("/loggedin")
    public String indexLoggedIn(Model model) {
    	model.addAttribute("contextPath", "/TIA104G2-SpringBoot");
    	return "/frontend/index_logged_in";
    }

    @GetMapping("/announce/{id}")
    public String announcePage(@PathVariable int id) {
        return "/frontend/activity_blog";
    }

    // 原有的頁面導航
    @GetMapping("/go")
    public String goPage() {
        return "/frontend/go";
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
    //管理員相關頁面
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
    
    //聊天室
    @GetMapping("/chatroom")
    public String chatroom(Model model) {
    	return "/frontend/chat_vanilla copy";
    }
    
}
