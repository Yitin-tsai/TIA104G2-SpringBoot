package chilltrip.location.controller;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import chilltrip.location.dao.LocationDAO;
import chilltrip.location.dao.LocationDAOImplJDBC;
import chilltrip.location.model.LocationService;
import chilltrip.location.model.LocationVO;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

@WebServlet("/location/location.do")
public class LocationServlet extends HttpServlet {
	@Autowired
	private LocationService locationSvc;

//	public void init() {
//		locationSvc = new LocationService();
//	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		res.setContentType("text/html; charset=UTF-8");
		String action = req.getParameter("action");
//		System.out.println(action);

		switch (action) {
		case "getAllLocation":
			getAllLocation(req, res);
			break;
		case "getLocationById":
//			System.out.println(action);
			getLocationById(req, res);
			break;
		case "getLocationByName":
			getLocationByName(req, res);
			break;
		case "addLocation":
			addLocation(req, res);
			break;
		case "updateLocation":
			updateLocation(req, res);
			break;
		case "deleteLocation":
			deleteLocation(req, res);
			break;
		}

	}

	// 拿到景點列表，按照pk（ID）排序
	private void getAllLocation(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		List<LocationVO> locationlist = locationSvc.getAllLocation();

		JSONArray jsonArray = new JSONArray();

		for (LocationVO location : locationlist) {
			JSONObject jsonRes = new JSONObject();
			jsonRes.put("locationid", location.getLocationId());
			jsonRes.put("address", location.getAddress());
			jsonRes.put("create_time", location.getCreate_time());
			jsonRes.put("comments_number", location.getComments_number());
			jsonRes.put("score", location.getScore());
			jsonRes.put("location_name", location.getLocation_name());

			jsonArray.put(jsonRes);

		}
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write(jsonArray.toString());
	}

	// 單一查詢（用ID查詢）
	private void getLocationById(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Integer locationid = Integer.valueOf(req.getParameter("location_id"));
//		System.out.println(locationid);
		LocationVO locationVO = new LocationVO();
		locationVO = locationSvc.getLocationById(locationid);
		JSONObject jsonRes = new JSONObject();
		jsonRes.put("locationid", locationVO.getLocationId());
		jsonRes.put("address", locationVO.getAddress());
		jsonRes.put("create_time", locationVO.getCreate_time());
		jsonRes.put("comments_number", locationVO.getComments_number());
		jsonRes.put("score", locationVO.getScore());
		jsonRes.put("location_name", locationVO.getLocation_name());
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write(jsonRes.toString());
	}

	// 景點名字查詢
	private void getLocationByName(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		String locationname = req.getParameter("location_name");
		List<Map<String, Object>> locationlist = locationSvc.getLocationByName(locationname.trim());
		JSONObject jsonRes = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		for (Map<String, Object> location : locationlist) {
			JSONObject jsonLocation = new JSONObject();
			jsonLocation.put("locationid", location.get("locationid"));
			jsonLocation.put("address", location.get("address"));
			jsonLocation.put("create_time", location.get("create_time"));
			jsonLocation.put("comments_number", location.get("comments_number"));
			jsonLocation.put("score", location.get("score"));
			jsonLocation.put("location_name", location.get("location_name"));
			jsonArray.put(jsonLocation);
		}

		// 添加數據到響應對象
		jsonRes.put("total", locationlist.size()); // 總記錄數
		jsonRes.put("locations", jsonArray); // 位置列表

		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write(jsonRes.toString());
	}

	// 新增景點
	private String addLocation(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		String locationaddress = req.getParameter("location_address");
		String timeStr = req.getParameter("location_create_time");
		Integer locationcommentsnumber = Integer.valueOf(req.getParameter("location_comments_number"));
		Float locationscore = Float.valueOf(req.getParameter("location_score"));
		String locationname = req.getParameter("location_name");
		Timestamp locationcreatetime = null;

		LocationVO locationVO = new LocationVO();

		locationVO.setAddress(locationaddress);
		locationVO.setCreate_time(locationcreatetime);
		locationVO.setComments_number(locationcommentsnumber);
		locationVO.setScore(locationscore);

		// 處理時間格式
		try {
			if (timeStr != null && !timeStr.trim().isEmpty()) {
//				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				Date parsedDate = (Date) dateFormat.parse(timeStr);
				locationcreatetime = Timestamp.valueOf(timeStr);
//				locationcreatetime = new Timestamp(parsedDate.getTime());
			}
		} catch (IllegalArgumentException e) {
			// 處理時間格式錯誤
			System.out.println("時間格式錯誤：" + e.getMessage());
		}
		locationVO.setCreate_time(locationcreatetime);
		locationVO.setLocation_name(locationname);
		locationSvc.addlocation(locationVO);

		return "新增成功!";
	}

	// 編輯景點
	private String updateLocation(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		Integer locationid = Integer.valueOf(req.getParameter("location_id"));
		String locationaddress = req.getParameter("location_address");
		String timeStr = req.getParameter("location_create_time");
		Integer locationcommentsnumber = Integer.valueOf(req.getParameter("location_comments_number"));
		Float locationscore = Float.valueOf(req.getParameter("location_score"));
		String locationname = req.getParameter("location_name");
		Timestamp locationcreatetime = null;

		LocationDAO dao = new LocationDAOImplJDBC();
		LocationVO locationVO = dao.getById(locationid);

		locationVO.setAddress(locationaddress);
		locationVO.setCreate_time(locationcreatetime);
		locationVO.setComments_number(locationcommentsnumber);
		locationVO.setScore(locationscore);
		locationVO.setLocation_name(locationname);
		locationSvc.updatelocation(locationVO);

		return "修改成功！";
	}

	// 刪除景點
	private String deleteLocation(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		Integer locationid = Integer.valueOf(req.getParameter("location_id"));
		locationSvc.deletelocation(locationid);

		res.setContentType("text/plain");
		res.setCharacterEncoding("UTF-8");

		// 回傳成功訊息給前端
		res.getWriter().write("success");
		
		return "刪除成功";

	}

}
