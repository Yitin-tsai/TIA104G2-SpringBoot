package chilltrip.sub_trip.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import chilltrip.location.model.LocationVO;
import chilltrip.sub_trip.dao.SubtripDAO;
import chilltrip.sub_trip.dao.SubtripDAOImplJDBC;
import chilltrip.sub_trip.model.SubtripService;
import chilltrip.sub_trip.model.SubtripVO;

@WebServlet("/subtrip/subtrip.do")
public class SubtripServlet extends HttpServlet {
	

	private SubtripService subtripSvc;

	public void init() {
		subtripSvc = new SubtripService(null);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		res.setContentType("text/html; charset=UTF-8");
		String action = req.getParameter("action");
		System.out.println(action);

		switch (action) {

		case "getAllSubtripById":
			getAllSubtripById(req, res);
			break;
		case "getByTripId":
			getByTripId(req, res);
			break;
		case "addSubtrip":
			try {
				addSubtrip(req, res);
			} catch (SerialException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "getBySubtripId":
			getBySubtripId(req,res);
			break;
		case "updateSubtrip":
			try {
				updateSubtrip(req,res);
			} catch (ServletException | IOException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "deleteSubtrip":
			try {
				deleteSubtrip(req,res);
			} catch (ServletException | IOException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			
		}
	}

	// 拿到子行程列表（雖然想不到可以用在哪，但先全部列出來）
	private void getAllSubtripById(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		List<SubtripVO> subtriplist = subtripSvc.getAllSubtripById();

		JSONArray jsonArray = new JSONArray();

		for (SubtripVO subtrip : subtriplist) {
			JSONObject jsonRes = new JSONObject();
			jsonRes.put("subtripid", subtrip.getSubtripid());
			jsonRes.put("tripid", subtrip.getTripid());
			jsonRes.put("index", subtrip.getIndex());
			jsonRes.put("content", subtrip.getContent());

			jsonArray.put(jsonRes);

		}
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write(jsonArray.toString());
	}

	// 每一個行程中的子行程按順序排列
	private void getByTripId(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Integer tripid = Integer.valueOf(req.getParameter("trip_id"));
		System.out.println(tripid);
		List<Map<String, Object>> subtriplist = subtripSvc.getByTripId(tripid);

		JSONArray jsonArray = new JSONArray();

		for (Map<String, Object> subtrip : subtriplist) {
			JSONObject jsonRes = new JSONObject();
			for (String key : subtrip.keySet()) {
				jsonRes.put(key, subtrip.get(key));
			}
			// jsonRes.put("tripid", subtrip.getTripid());
			// jsonRes.put("index", subtrip.getIndex());
			// jsonRes.put("content", subtrip.getContent());
			// jsonRes.put("subtripid", subtrip.getSubtripid());
			jsonArray.put(jsonRes);

		}
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write(jsonArray.toString());
	}
	
	//新增子行程（但這裡應該是新增行程的時候要自動化對應新增子行程）
	private String addSubtrip(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, SerialException, SQLException {
		
		Integer tripid = Integer.valueOf(req.getParameter("trip_id"));
		Integer index = Integer.valueOf(req.getParameter("subtrip_index"));
		String content = req.getParameter("subtrip_content");
		
		SubtripVO subtripVO = new SubtripVO();
		
		subtripVO.setTripid(tripid);
		subtripVO.setIndex(index);
		subtripVO.setContent(content);
		subtripSvc.addSubtrip(subtripVO);
		
		return "新增成功";
	}
	
	//單一查詢：用ID查詢
	private void getBySubtripId (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		Integer subtripid = Integer.valueOf(req.getParameter("subtrip_id"));
		SubtripVO subtripVO = new SubtripVO();
		subtripVO = subtripSvc.getBySubtripId(subtripid);
		JSONObject jsonRes = new JSONObject();
		jsonRes.put("tripid",subtripVO.getTripid());
		jsonRes.put("index", subtripVO.getIndex());
		jsonRes.put("content", subtripVO.getContent());
		
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write(jsonRes.toString());
	}
		
	
	//修改子行程
	private String updateSubtrip (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, SerialException, SQLException {
		
		Integer subtripid = Integer.valueOf(req.getParameter("subtrip_id"));
//		System.out.println(subtripid);
		Integer tripid = Integer.valueOf(req.getParameter("trip_id"));
//		System.out.println(tripid);
		Integer index = Integer.valueOf(req.getParameter("subtrip_index"));
//		System.out.println(index);
		String content = req.getParameter("subtrip_content");
//		System.out.println(content);
		
		SubtripDAO dao = new SubtripDAOImplJDBC();
		SubtripVO subtripVO = dao.getBySubtripId(subtripid);
		
		subtripVO.setTripid(tripid);
		subtripVO.setIndex(index);
		subtripVO.setContent(content);
		
		subtripSvc.updateSubtrip(subtripVO);
		
		return "修改成功！";
	}
	
	//刪除子行程-->但應該不太會用到，而是刪除行程或更改行程狀態，會直接無法看到子行程
	private String deleteSubtrip (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, SerialException, SQLException {
		
		Integer subtripid = Integer.valueOf(req.getParameter("subtrip_id"));
		subtripSvc.deleteSubtrip(subtripid);
		
		res.setContentType("text/plain");
		res.setCharacterEncoding("UTF-8");

		// 回傳成功訊息給前端
		res.getWriter().write("success");
		
		return "刪除成功";
	}
	

}
