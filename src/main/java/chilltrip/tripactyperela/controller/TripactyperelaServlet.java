package chilltrip.tripactyperela.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import chillchip.trip.model.TripService;
import chillchip.trip.model.TripVO;
import chilltrip.tripactyperela.model.TripactyperelaService;
import chilltrip.tripactyperela.model.TripactyperelaVO;

@WebServlet("/tripactyperela")
public class TripactyperelaServlet extends HttpServlet {

	private TripactyperelaService tripactyperelaSvc;
	private TripService tripSvc;

	public void init() {
		tripactyperelaSvc = new TripactyperelaService();
		tripSvc = new TripService();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
			req.setCharacterEncoding("UTF-8");
			res.setContentType("text/html; charset=UTF-8");
			String action = req.getParameter("action");
			
			if ("addTripWithEventTypes".equals(action)) {  // 新增行程的同時也新增行程活動類型並建立關聯
				System.out.println("開始新增...");
				Integer tripId = Integer.valueOf(req.getParameter("tripId"));
		        String[] eventTypeIds = req.getParameterValues("eventTypeIds");

		        System.out.println("tripId為:" + tripId);
		        System.out.println("eventTypeIds為:" + eventTypeIds);

	            if (eventTypeIds == null || eventTypeIds.length == 0) {
	                res.getWriter().write("至少選擇一個活動類型");
	                return;
	            }

	            // 創建行程並獲取 tripId
//	            TripVO tripVO = new TripVO();
//	            tripVO.setArticle_title(tripTitle);
//	            tripVO.setTrip_abstract(tripDescription);
//	            
//	            TripVO addedTrip = tripSvc.addTrip(tripVO);
//	            if (addedTrip == null) {
//	                res.getWriter().write("行程創建失敗");
//	                return;
//	            }
	            
//	            Integer tripId = addedTrip.getTrip_id(); // 從 TripVO 中拿取 tripId

	            // 轉換活動類型ID為整數
	            List<Integer> eventTypeList = new ArrayList<>();
	            for (String eventTypeId : eventTypeIds) {
	                try {
	                    eventTypeList.add(Integer.valueOf(eventTypeId));
	                } catch (NumberFormatException e) {
	                    res.getWriter().write("活動類型 ID 格式錯誤");
	                    return;
	                }
	            }

	            // 創建行程和活動類型的關聯
	            boolean relationResult = tripactyperelaSvc.addRelations(tripId, eventTypeList);
	            if (relationResult) {
	                res.getWriter().write("行程和活動類型關聯成功");
	            } else {
	                // 如果關聯創建失敗，回滾行程的創建操作
	                tripSvc.deleteTrip(tripId);
	                res.getWriter().write("行程和活動類型關聯失敗，已回滾行程創建");
	            }
	        }
			
	        if ("addRelation".equals(action)) {  // 新增關聯資料 (將活動類型與行程關聯)
	            Integer tripId = Integer.valueOf(req.getParameter("tripId"));
	            String[] eventTypeIds = req.getParameterValues("eventTypeIds");  // 活動類型的 ID
	            
	            System.out.println("tripId: " + tripId);
	            
	            if (eventTypeIds != null) {
	            	List<Integer> eventTypeList = new ArrayList<>();
	            	
	            	for (String eventTypeId : eventTypeIds) {
	                    eventTypeList.add(Integer.valueOf(eventTypeId));  // 把 String 轉換成 Integer 並加入到 List 中
	                }
	            	
	                System.out.println("eventTypeIds: " + eventTypeList);
	                
	                boolean result = tripactyperelaSvc.addRelations(tripId, eventTypeList);
	                
	                System.out.println("新增結果: " + result);
	                res.getWriter().write(result ? "新增成功" : "新增失敗");
	                
	            } else {
	            	System.out.println("未選擇任何活動類型");
	                res.getWriter().write("未選擇任何活動類型");
	            }

	        } 
	        
	        
	        if ("deleteRelation".equals(action)) {  // 刪除行程與活動類型的關聯
	            Integer tripId = Integer.valueOf(req.getParameter("tripId"));
	            Integer eventTypeId = Integer.valueOf(req.getParameter("eventTypeId"));
	            System.out.println("刪除的tripId: " + tripId);
	            System.out.println("刪除的eventTypeId: " + eventTypeId);

	            // 執行刪除操作
	            boolean result = tripactyperelaSvc.deleteRelationByTripAndEventType(tripId, eventTypeId);
	            
	            res.getWriter().write(result ? "刪除成功" : "刪除失敗");
	            System.out.println("刪除結果: " + result);
	        }
	       

	    	if ("updateRelation".equals(action)) {   // 更新行程與活動類型的關聯
	    		Integer tripId = Integer.valueOf(req.getParameter("tripId"));
	            String[] eventTypeIds = req.getParameterValues("eventTypeIds");
	            System.out.println("tripId: " + tripId);

	            if (eventTypeIds != null) { 
	                List<Integer> eventTypeList = new ArrayList<>();
	                for (String eventTypeId : eventTypeIds) {
	                    eventTypeList.add(Integer.valueOf(eventTypeId));  // 把 String 轉換成 Integer 並加入到 List 中
	                }
	                
	                System.out.println("eventTypeIds: " + eventTypeList);  // 印出轉換後的活動類型 ID 列表
	                
	                boolean result = tripactyperelaSvc.updateTripactyperela(tripId, eventTypeList);
	                
	                System.out.println("更新結果: " + result);
	                res.getWriter().write(result ? "更新成功" : "更新失敗");
	            } else {
	                System.out.println("未選擇任何活動類型");
	                res.getWriter().write("未選擇任何活動類型");
	            }
	        } 

	        if ("findRelationsByTripId".equals(action)) {     // 查詢某個行程的所有關聯
	        	Integer tripId = Integer.valueOf(req.getParameter("tripId"));
	            System.out.println("查詢的行程id: " + tripId);
	            
	            List<TripactyperelaVO> relations = tripactyperelaSvc.findAllRelationsByTripId(tripId);
	            for (TripactyperelaVO tripactyperelaVO : relations) {
	                System.out.println("關聯 ID: " + tripactyperelaVO.getRelaid() +
	                        ", 行程 ID: " + tripactyperelaVO.getTripid().getTrip_id() + 
	                        ", 活動類型 ID: " + tripactyperelaVO.getEventtypeid().getEventtypeid());
	            }
	        }

//	        if ("findAllRelations".equals(action)) {    // 查詢所有關聯資料
//	            List<TripactyperelaVO> allRelations = tripactyperelaSvc.findAllRelations();
//	            
//	            for (TripactyperelaVO tripactyperelaVO : allRelations) {
//	                System.out.println("關聯 ID: " + tripactyperelaVO.getRelaid() +
//	                        ", 行程 ID: " + tripactyperelaVO.getTripid().getTrip_id() + 
//	                        ", 活動類型 ID: " + tripactyperelaVO.getEventtypeid().getEventtypeid());
//	            }
//	        } 
}}
