package chilltrip.navigation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/navigation")
public class NavigationController {

    @GetMapping("/config")
    public Map<String, Object> getNavigationConfig() {
        Map<String, Object> config = new HashMap<>();
        
        // 基本路徑配置
        Map<String, String> paths = new HashMap<>();
        paths.put("home", "/home");
        paths.put("go", "/go");
        paths.put("myTrip", "/viewMyTrip");
        paths.put("login", "/login");
        paths.put("profile", "/viewProfile");
        paths.put("chat", "/chatroom");
        paths.put("editor", "/editor/create");
        paths.put("article", "/tripArticle");
        paths.put("memberTrip", "/viewTrip");
        paths.put("logout", "/member/logout");
        config.put("paths", paths);

        // 需要登入驗證的路徑
        List<String> protectedPaths = Arrays.asList(
            "myTrip",
            "profile",
            "chat",
            "editor"
        );
        config.put("protectedPaths", protectedPaths);

        // 路徑參數模板
        Map<String, String> pathTemplates = new HashMap<>();
        pathTemplates.put("article", "/tripArticle/{tripId}");
        pathTemplates.put("memberTrip", "/viewTrip/{memberId}");
        pathTemplates.put("editor", "/editor/create");
        pathTemplates.put("editArticle", "/editor/{articleId}");
        config.put("pathTemplates", pathTemplates);

        // 重定向規則
        Map<String, String> redirectRules = new HashMap<>();
        redirectRules.put("unauthorized", "/login");
        redirectRules.put("notFound", "/error/404");
        redirectRules.put("home", "/home");
        config.put("redirectRules", redirectRules);

        return config;
    }

    // 可選：提供路徑驗證服務
    @GetMapping("/validate")
    public Map<String, Boolean> validatePath(String path) {
        Map<String, Boolean> result = new HashMap<>();
        result.put("valid", true); // 實際邏輯需要根據具體需求實現
        result.put("requiresAuth", isProtectedPath(path));
        return result;
    }

    private boolean isProtectedPath(String path) {
        List<String> protectedPaths = Arrays.asList(
            "/viewMyTrip",
            "/viewProfile",
            "/chatroom",
            "/editor"
        );
        return protectedPaths.contains(path);
    }
}


    // 客服中心通知接口
//    @GetMapping("/notifications")
//    public ResponseEntity<?> getNotifications(HttpSession session) {
//        Integer memberId = (Integer) session.getAttribute("memberId");
//        if (memberId == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        try {
//            // TODO: 從資料庫獲取通知訊息
//            List<Map<String, Object>> notifications = new ArrayList<>();
//            // 示例通知格式
//            Map<String, Object> notification = new HashMap<>();
//            notification.put("id", 1);
//            notification.put("type", "like");  // like, comment, follow 等
//            notification.put("message", "某某用戶收藏了你的文章");
//            notification.put("timestamp", new Date());
//            notification.put("isRead", false);
//            notifications.add(notification);
//
//            return ResponseEntity.ok(notifications);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

    // 標記通知為已讀
//    @PostMapping("/notifications/{id}/read")
//    public ResponseEntity<?> markAsRead(
//            @PathVariable Long id,
//            HttpSession session) {
//        Integer memberId = (Integer) session.getAttribute("memberId");
//        if (memberId == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        try {
//            // TODO: 更新通知狀態為已讀
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

