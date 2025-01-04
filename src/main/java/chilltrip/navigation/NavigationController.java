package chilltrip.navigation;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class NavigationController {

	@GetMapping("/navigation-paths")
    public Map<String, String> getNavigationPaths() {
        Map<String, String> paths = new HashMap<>();
        paths.put("goPath", "/go");           
        paths.put("productPath", "/product");
        paths.put("profilePath", "/profile");
        paths.put("guidePath", "/guide");
        paths.put("basicInfoPath", "/basic-info");
        paths.put("couponsPath", "/coupons");
        paths.put("ordersPath", "/orders");
        paths.put("supportPath", "/support");
        paths.put("cartPath", "/cart");
        
        return paths;

	}
}
