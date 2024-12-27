package chilltrip.tripactype.controller;

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

import chilltrip.trip.model.TripService;
import chilltrip.trip.model.TripVO;
import chilltrip.tripactype.model.TripactypeService;
import chilltrip.tripactype.model.TripactypeVO;
import chilltrip.tripactyperela.model.TripactyperelaService;

@WebServlet("/tripactype")
public class TripactypeServlet extends HttpServlet {

	private TripactypeService tripactypeSvc;
	private TripactyperelaService tripactyperelaSvc;
	private TripService tripSvc;

	public void init() {
		tripactypeSvc = new TripactypeService();
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
		
		// 不允許使用者新增活動類型故先註解
//		if ("addTripactype".equals(action)) { // 增加行程活動類型請求
//			String eventcontent = req.getParameter("eventcontent");
//			TripactypeVO tripactypeVO = new TripactypeVO();
//			tripactypeVO.setEventcontent(eventcontent);
//
//			System.out.println("新增操作: " + eventcontent);
//			boolean result = tripactypeSvc.addTripactype(tripactypeVO);
//			System.out.println(result ? "新增成功" : "新增失敗");
//			res.getWriter().write(result ? "新增成功" : "新增失敗");
//		}

		if ("updateTripactype".equals(action)) { // 用戶編輯行程時更新與活動類型的關聯
			Integer tripId = Integer.valueOf(req.getParameter("tripId"));
			String[] eventTypeIds = req.getParameterValues("eventTypeIds"); // 獲取行程選擇的活動類型ID

			if (eventTypeIds != null) {
				// 更新行程與活動類型的關聯
				List<Integer> eventTypeList = new ArrayList<>();
				for (String eventTypeId : eventTypeIds) {
					eventTypeList.add(Integer.valueOf(eventTypeId)); // 將選擇的活動類型ID轉換成整數並加入列表
				}

				// 調用 TripactyperelaServlet 的方法來更新關聯
				boolean result = tripactyperelaSvc.updateTripactyperela(tripId, eventTypeList);
				if (result) {
					res.getWriter().write("行程與活動類型關聯更新成功");
					System.out.println("行程與活動類型關聯更新成功");
				} else {
					res.getWriter().write("行程與活動類型關聯更新失敗");
					System.out.println("行程與活動類型關聯更新失敗");
				}
			} else {
				res.getWriter().write("未選擇任何活動類型");
				System.out.println("未選擇任何活動類型");
			}

		}

		if ("deleteTripactype".equals(action)) { // 刪除行程活動類型請求
			Integer tripId = Integer.valueOf(req.getParameter("tripId"));
			Integer eventTypeId  = Integer.valueOf(req.getParameter("eventTypeId"));
			System.out.println("刪除tripId:" + tripId);
			System.out.println("刪除eventTypeId:" + eventTypeId);

			// 刪除行程與活動類型的關聯
            boolean result = tripactyperelaSvc.deleteRelationByTripAndEventType(tripId, eventTypeId);
            res.getWriter().write(result ? "刪除成功" : "刪除失敗");
		}

        if ("getAllTripActypes".equals(action)) {  // 顯示所有活動類型到下拉選單
            try {
                List<TripactypeVO> allTripactypes = tripactypeSvc.findAllTripactypes();

                // 提取所有活動類型名稱，並返回給前端顯示
                Set<String> types = new HashSet<>();
                for (TripactypeVO tripactype : allTripactypes) {
                    types.add(tripactype.getEventcontent()); // 活動類型名稱
                }

                List<String> typeList = new ArrayList<>(types);
                req.setAttribute("typeList", typeList);

                // 根據頁面進行轉發
                String targetPage = req.getParameter("targetPage");
                if ("search".equals(targetPage)) {
                    // 行程搜尋頁面
                    RequestDispatcher successView = req.getRequestDispatcher("/frontend/go.html");
                    successView.forward(req, res);
                } else if ("edit".equals(targetPage)) {
                    // 行程編輯頁面
                    RequestDispatcher successView = req.getRequestDispatcher("/frontend/go_single_editor3.0.html");
                    successView.forward(req, res);
                }
                System.out.println("所有行程活動類型已經顯示在頁面上: " + typeList);

            } catch (Exception e) {
                String targetPage = req.getParameter("targetPage");
                req.setAttribute("errorMessage", "無法取得所有行程活動類型資料：" + e.getMessage());

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
        
        if ("getTripByTripActype".equals(action)) {    // 查詢指定行程活動類型的所有行程卡片

    		// 取得前端選擇的活動類型名稱
            String eventType = req.getParameter("eventType");
            System.out.println("eventType = " + eventType);
            
            // 根據選擇的活動類型查詢行程資料
            List<TripVO> tripList = tripactypeSvc.findTripsByEventType(eventType);

            if (tripList != null && !tripList.isEmpty()) {
                System.out.println("找到的行程列表：");
                for (TripVO tripVO : tripList) {
                    System.out.println("行程標題：" + tripVO.getArticle_title());
                    System.out.println("行程摘要：" + tripVO.getTrip_abstract());
                    System.out.println("-------------------------------");
                }
            } else {
                System.out.println("沒有找到相關行程");
            }

            req.setAttribute("tripList", tripList);

            // 返回對應的頁面，顯示行程卡片
            RequestDispatcher successView = req.getRequestDispatcher("/frontend/go.html");
            successView.forward(req, res);
            System.out.println("查詢成功轉跳篩選後頁面");
        }
	}
}
