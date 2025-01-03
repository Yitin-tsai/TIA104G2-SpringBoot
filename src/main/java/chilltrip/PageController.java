package chilltrip;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {
	
	  @GetMapping("/")
	    public String index(Model model) {
	    	
	        return "/frontend/index"; 
	    }
	  
	  @GetMapping("/announce/{id}")
	  public String announcePage(@PathVariable int id) {
		  
		  return "/frontend/activity_blog"; 
	  }
	  
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
	        return "/frontend/profile";
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

	
	  
}