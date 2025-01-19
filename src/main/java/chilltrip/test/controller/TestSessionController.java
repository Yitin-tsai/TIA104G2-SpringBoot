package chilltrip.test.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Enumeration;

@RestController
@RequestMapping("/api")
public class TestSessionController {
	
	 private static final Logger logger = LoggerFactory.getLogger(TestSessionController.class);
	    
	    @GetMapping("/test-session")
	    @ResponseBody
	    public Map<String, Object> testSession(HttpSession session) {
	        logger.info("=== TestSessionController: test-session endpoint accessed ===");
	        logger.info("Current Session ID: {}", session.getId());
	        
	        Map<String, Object> response = new HashMap<>();
	        try {
	            // 收集 session 資訊
	            Map<String, Object> sessionInfo = new HashMap<>();
	            Enumeration<String> attributeNames = session.getAttributeNames();
	            
	            while (attributeNames.hasMoreElements()) {
	                String name = attributeNames.nextElement();
	                Object value = session.getAttribute(name);
	                sessionInfo.put(name, value);
	                logger.info("Session attribute found - {}: {}", name, value);
	            }
	            
	            response.put("status", "success");
	            response.put("sessionId", session.getId());
	            response.put("sessionAttributes", sessionInfo);
	            response.put("message", "Session test completed successfully");
	            
	        } catch (Exception e) {
	            logger.error("Error during session test", e);
	            response.put("status", "error");
	            response.put("message", e.getMessage());
	        }
	        
	        return response;
	    }

}
