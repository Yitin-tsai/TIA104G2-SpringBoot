package chilltrip.trippage.viewarticle.controller;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import chilltrip.trippage.viewarticle.dto.TripDaysResponseDTO;
import chilltrip.trippage.viewarticle.service.ArticleService;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    
    private static final Logger log = LoggerFactory.getLogger(ArticleService.class);

    
    //基本信息渲染的端點
    @GetMapping("/{tripId}/basic")
    public ResponseEntity<Map<String, Object>> getBasicInfo(
            @PathVariable Integer tripId,
            HttpServletRequest request) {
            
        HttpSession session = request.getSession();
        Integer currentMemberId = (Integer) session.getAttribute("memberId");
        
        Map<String, Object> basicInfo = articleService.getBasicInfo(tripId, currentMemberId);
        return ResponseEntity.ok(basicInfo);
    }
    
 // 新增的天數信息端點
    @GetMapping("/{tripId}/days")
    public ResponseEntity<TripDaysResponseDTO> getTripDays(
            @PathVariable Integer tripId) {
        TripDaysResponseDTO daysInfo = articleService.getTripDaysInfo(tripId);
        return ResponseEntity.ok(daysInfo);
    }
    
    //留言區串接
    @Cacheable(value = "comments", key = "#tripId + '-' + #page")
    @Transactional(readOnly = true)
    @GetMapping("/{tripId}/comments")
    public ResponseEntity<?> getComments(
            @PathVariable Integer tripId,
            @RequestParam(defaultValue = "0") int page) {
        try {
            // 添加日誌
            log.info("Received comment request for tripId: {} page: {}", tripId, page);
            
            // 同步處理評論請求
            Map<String, Object> response = articleService.getComments(tripId, page);
            
            log.info("Successfully retrieved comments for tripId: {}", tripId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error processing comment request for tripId: " + tripId, e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to load comments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(errorResponse);
        }
    }
    
}
