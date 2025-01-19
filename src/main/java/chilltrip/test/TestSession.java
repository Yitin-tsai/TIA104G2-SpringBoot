package chilltrip.test;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;

public class TestSession {
	
	@GetMapping("/test-session")
	public String testSessionTool (HttpSession session) {
	    // 設置一個測試值
	    session.setAttribute("test", "test-value");
	    
	    // 讀取並打印所有 session 屬性
	    Enumeration<String> attributeNames = session.getAttributeNames();
	    while (attributeNames.hasMoreElements()) {
	        String name = attributeNames.nextElement();
	        System.out.println(name + ": " + session.getAttribute(name));
	    }
	    
	    return "Session test complete";
	}

}
