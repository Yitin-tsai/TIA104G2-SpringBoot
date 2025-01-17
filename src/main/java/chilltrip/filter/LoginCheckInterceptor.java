package chilltrip.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Integer memberId = (Integer) session.getAttribute("memberId");
        
        if (memberId == null) {
            // 判斷是否是 AJAX 請求
            String requestedWithHeader = request.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equals(requestedWithHeader)) {
                // AJAX 請求返回 401 狀態碼
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                // 一般請求重導向到登入頁面
                response.sendRedirect("/login");
            }
            return false;
        }

        // 將會員 ID 添加到請求屬性中，方便後續使用
        request.setAttribute("loginMemberId", memberId);
        return true;
    }
}