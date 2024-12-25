package chilltrip.tripcollection.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import chilltrip.tripcollection.model.TripCollectionService;
import chilltrip.tripcollection.model.TripCollectionVO;


@WebServlet("/tripCollection/tripCollection.do")
public class TripCollectionServlet extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		Integer id = Integer.valueOf(req.getParameter("id"));
		String action = req.getParameter("action");
		
		if("getall".equals(action)) {
			getByMember(id, res, req, null);
		}
	}
	private TripCollectionService tripColSvc;

	public void init() {
		tripColSvc = new TripCollectionService();
	}

	public void addTripCollection(/* @RequestBody */ TripCollectionVO tripCollecrionvo) {
		tripColSvc.addTripCollection(tripCollecrionvo);
	}

	public void deleteTripCollection(/* @PathVariabl("id") */Integer id) {
		tripColSvc.deleTripCollection(id);
	}

	public void getByTrip(Integer tripId, HttpServletResponse res) throws IOException {

		List<TripCollectionVO> list = tripColSvc.getByTrip(tripId);
		JSONArray jsonArray = new JSONArray();
		for (TripCollectionVO tripCollection : list) {
			JSONObject jsonRes = new JSONObject();
			jsonRes.put("tripCollectionId", tripCollection.getTripCollectionId());
			jsonRes.put("tripvo", tripCollection.getTripvo());
			jsonRes.put("membervo", tripCollection.getMembervo());

			jsonArray.put(jsonRes);
		}

		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write(jsonArray.toString());

	}

	public String getByMember(Integer memberId, HttpServletResponse res, HttpServletRequest req, Integer page) throws IOException {
		int currentPage = (page == null) ? 1 : page;

		List<TripCollectionVO> list = tripColSvc.getByMember(memberId, currentPage);

		int tripColPageQty = tripColSvc.getTotalPage(memberId);
		
		Map<String, Integer> pageMap = new HashMap<String, Integer>();
		pageMap.put("tripColPageQty", tripColPageQty);
		pageMap.put("currentPage", currentPage);
		
		JSONArray jsonArray = new JSONArray();
		jsonArray.put(pageMap);
		
		for (TripCollectionVO tripCollection : list) {
			JSONObject jsonRes = new JSONObject();
			jsonRes.put("tripCollectionId", tripCollection.getTripCollectionId());
			jsonRes.put("tripvo", tripCollection.getTripvo());
			jsonRes.put("membervo", tripCollection.getMembervo());
			jsonArray.put(jsonRes);
		}
		
		
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write(jsonArray.toString());
		
		return "peko";
		
	}

}
