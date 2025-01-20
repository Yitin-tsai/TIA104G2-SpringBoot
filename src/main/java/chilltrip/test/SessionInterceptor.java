package chilltrip.test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SessionInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(SessionInterceptor.class);
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
        
        String requestURI = request.getRequestURI();
        logger.info("Intercepting request to: {}", requestURI);
        
        // 對於某些路徑不需要檢查 session
        if (isPublicPath(requestURI)) {
            return true;
        }
        
        HttpSession session = request.getSession(false);
        if (session == null) {
            logger.warn("No session found for request: {}", requestURI);
            handleUnauthorized(request, response);
            return false;
        }
        
        Object memberId = session.getAttribute("memberId");
        if (memberId == null) {
            logger.warn("No memberId in session for request: {}", requestURI);
            handleUnauthorized(request, response);
            return false;
        }
        
        logger.info("Session validation passed for memberId: {} on path: {}", memberId, requestURI);
        return true;
    }
    
    private boolean isPublicPath(String path) {
        return path.startsWith("/login") ||
               path.startsWith("/static") ||
               path.startsWith("/public") ||
               path.startsWith("/api/test-session");
    }
    
    private void handleUnauthorized(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        if (isAjaxRequest(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
    
    private boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }
}