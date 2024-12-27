package chilltrip.triparea.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import chillchip.trip.model.TripVO;
import chilltrip.triparea.model.TripAreaService;
import chilltrip.triparea.model.TripAreaVO;

@WebServlet("/triparea")
public class TripAreaServlet extends HttpServlet {

	private TripAreaService tripAreaSvc;

	public void init() {
		tripAreaSvc = new TripAreaService();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		res.setContentType("text/html; charset=UTF-8");
		String action = req.getParameter("action");

		if ("getTripBytripArea".equals(action)) { // 接收請求找尋行程地區的行程
			// 取得前端選擇的行程地區名稱
			String regioncontent = req.getParameter("regioncontent");
			System.out.println("選擇的行程地區：" + regioncontent);

			// 根據選擇的地區查詢行程資料
			List<TripAreaVO> tripList = tripAreaSvc.getTripBytripArea(regioncontent);

			if (tripList != null && !tripList.isEmpty()) {
				System.out.println("找到的行程列表：");
				for (TripAreaVO tripArea : tripList) {
					System.out.println("行程地區：" + tripArea.getRegioncontent());
					System.out.println("行程標題：" + tripArea.getTripid().getArticle_title());
					System.out.println("行程摘要：" + tripArea.getTripid().getTrip_abstract());
					System.out.println("遊客數：" + tripArea.getTripid().getVisitors_number());
					System.out.println("喜歡數：" + tripArea.getTripid().getLikes());
					System.out.println("-------------------------------");
				}
			} else {
				System.out.println("沒有找到相關行程");
			}

			req.setAttribute("tripList", tripList);

			RequestDispatcher successView = req.getRequestDispatcher("/frontend/go.html");
			successView.forward(req, res);
			System.out.println("查詢成功轉跳篩選後頁面");
		}

		if ("addTripAreaToTrip".equals(action)) { // 新增地區標註到行程
			Integer tripid = Integer.valueOf(req.getParameter("tripId"));
			String regionContent = req.getParameter("regionContent");

			System.out.println("新增行程標籤 - 行程 ID: " + tripid + ", 地區: " + regionContent);

			// 建立 TripVO 物件
			TripVO tripVO = new TripVO();
			tripVO.setTrip_id(tripid);

			try {
				tripAreaSvc.addTripAreaToTrip(tripid, regionContent);

				req.setAttribute("successMessage", "行程地區標註新增成功！");
				RequestDispatcher dispatcher = req.getRequestDispatcher("/frontend/go_single_editor3.0.html");
				dispatcher.forward(req, res);
				System.out.println("新增成功！");

			} catch (Exception e) {
				req.setAttribute("errorMessage", "新增行程地區標註失敗：" + e.getMessage());
				RequestDispatcher dispatcher = req.getRequestDispatcher("/frontend/go_single_editor3.0.html");
				dispatcher.forward(req, res);
				System.out.println("新增行程標註失敗");
			}
		}
		
		// 目前應該不會有移除行程地區標註的功能，因為每個行程都必須最少有一個行程地區標籤
//		if ("removeTripAreaFromTrip".equals(action)) { // 移除行程的地區標註
//			
//			Integer tripid = Integer.valueOf(req.getParameter("tripId"));
//            String regionContent = req.getParameter("regionContent");
//            System.out.println("移除行程標籤 - 行程 ID: " + tripid + ", 地區: " + regionContent);
//            
//			try {
//				tripAreaSvc.removeTripAreaFromTrip(tripid, regionContent);
//
//				req.setAttribute("successMessage", "行程地區標註移除成功！");
//				System.out.println("行程 ID: " + tripid + " 地區: " + regionContent + " 移除成功！");
//
//			} catch (Exception e) {
//				req.setAttribute("errorMessage", "移除行程地區標註失敗：" + e.getMessage());
//				System.out.println("移除行程標註失敗");
//			}
//			
//			RequestDispatcher dispatcher = req.getRequestDispatcher("/frontend/go_single_editor3.0.html");
//			dispatcher.forward(req, res);
//		}
		
		if("updateTripArea".equals(action)) {   // 修改行程的地區標註
			Integer tripid = Integer.valueOf(req.getParameter("tripId"));
			String oldRegionContent = req.getParameter("oldRegionContent");
            String newRegionContent = req.getParameter("newRegionContent");
            
            System.out.println("修改行程標籤 - 行程 ID: " + tripid + ", 舊地區: " + oldRegionContent + ", 新地區: " + newRegionContent);
			
            try {
            	tripAreaSvc.updateTripArea(tripid, oldRegionContent, newRegionContent);
            	System.out.println("行程 ID: " + tripid + " 地區從 " + oldRegionContent + " 更新為 " + newRegionContent);
                System.out.println("更新成功！");
            }catch(Exception e) {
            	req.setAttribute("errorMessage", "更新行程地區標註失敗：" + e.getMessage());
            	System.out.println("更新行程標註失敗");
            }
            
            RequestDispatcher dispatcher = req.getRequestDispatcher("/frontend/go_single_editor3.0.html");
            dispatcher.forward(req, res);
		}
		
		if("getAllTripArea".equals(action)) {  // 顯示下拉選單所有行程地區
			try {
				List<TripAreaVO> allTripAreas = tripAreaSvc.getAll();
				
				Set<String> areas = new HashSet<>();
				for(TripAreaVO tripArea : allTripAreas) {
					areas.add(tripArea.getRegioncontent());  // 將地區名稱加入 HashSet，自動去除重復
				}
				
				List<String> areaList = new ArrayList<>(areas);
				req.setAttribute("areaList", areaList);
				
				// 根據頁面進行轉發
				String targetPage = req.getParameter("targetPage");
				if("search".equals(targetPage)) {
					// 行程搜尋頁面
					RequestDispatcher successView = req.getRequestDispatcher("/frontend/go.html");
					successView.forward(req, res);
				}else if("edit".equals(targetPage)) {
					// 行程編輯頁面
                    RequestDispatcher successView = req.getRequestDispatcher("/frontend/go_single_editor3.0.html");
                    successView.forward(req, res);
				}
				System.out.println("所有不重復的行程地區已經顯示在頁面上: " + areaList);
				
			}catch(Exception e){
				String targetPage = req.getParameter("targetPage");
                req.setAttribute("errorMessage", "無法取得所有行程地區資料：" + e.getMessage());

                if ("search".equals(targetPage)) {
                    // 行程搜尋頁面
                    RequestDispatcher failureView = req.getRequestDispatcher("/frontend/go.html");
                    failureView.forward(req, res);
                } else if ("edit".equals(targetPage)) {
                    // 行程編輯頁面
                    RequestDispatcher failureView = req.getRequestDispatcher("/frontend/go_single_editor3.0.html");
                    failureView.forward(req, res);
                }
			}
			
		}

	}
}
