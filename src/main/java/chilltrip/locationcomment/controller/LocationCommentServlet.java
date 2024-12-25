package chilltrip.locationcomment.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import chilltrip.locationcomment.model.LocationCommentService;
import chilltrip.locationcomment.model.LocationCommentVO;

@WebServlet("/LocationCommment")
public class LocationCommentServlet {

	private LocationCommentService commentSvc;

	public void init() {
		commentSvc = new LocationCommentService();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		res.setContentType("text/html; charset=UTF-8");
		String action = req.getParameter("action");

		switch (action) {
		case "getLocationCommemtByMember":
			getByMember(req, res);
			break;
		case "getLocationCommentByLocation":
			getByLocation(req, res);
			break;
		}

	}

	private void getByLocation(HttpServletRequest req, HttpServletResponse res) throws IOException {
		// TODO Auto-generated method stub
		Integer locationid = Integer.valueOf(req.getParameter("locationid"));
		List<LocationCommentVO> commentList = commentSvc.getLocationCommentByLocation(locationid);
		JSONArray jsonArray = new JSONArray();

		for (LocationCommentVO comment : commentList) {
			JSONObject jsonRes = new JSONObject();
			jsonRes.put("locationCommentId", comment.getLocationCommitId());
			jsonRes.put("member", comment.getMembervo());
			jsonRes.put("location", comment.getLocationvo());
			jsonRes.put("score", comment.getScore());
			jsonRes.put("content", comment.getContent());
			jsonRes.put("createTime", comment.getCreateTime());
			jsonRes.put("photo", Base64.getEncoder().encodeToString(comment.getPhoto()));

			jsonArray.put(jsonRes);
		}

		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write(jsonArray.toString());

	}

	public void getByMember(HttpServletRequest req, HttpServletResponse res) throws IOException {
		Integer memberid = Integer.valueOf(req.getParameter("locationid"));
		List<LocationCommentVO> commentList = commentSvc.getLocationCommentByLocation(memberid);
		JSONArray jsonArray = new JSONArray();

		for (LocationCommentVO comment : commentList) {
			JSONObject jsonRes = new JSONObject();
			jsonRes.put("locationCommentId", comment.getLocationCommitId());
			jsonRes.put("member", comment.getMembervo());
			jsonRes.put("location", comment.getLocationvo());
			jsonRes.put("score", comment.getScore());
			jsonRes.put("content", comment.getContent());
			jsonRes.put("createTime", comment.getCreateTime());
			jsonRes.put("photo", Base64.getEncoder().encodeToString(comment.getPhoto()));

			jsonArray.put(jsonRes);
		}

		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write(jsonArray.toString());

	}
	/*@PutMapping("/update/{id}")*/
	private void updateLocationComment(/*@PathVariable("id")*/ Integer id, /*@RequestBody*/ LocationCommentVO locationCommentVO) {
		// TODO Auto-generated method stub
		locationCommentVO.setLocationCommitId(id);
		commentSvc.updateLocationComment(locationCommentVO);
	}
	/*@PutMapping("/add")*/
	private void addLocationComment(/*@RequestBody*/LocationCommentVO locationCommentVO) {
		// TODO Auto-generated method stub
		commentSvc.addLocationComment(locationCommentVO);

	}
	/*@PutMapping("/delete")/{id}*/
	private void deleteLocationComment(/*@PathVariabl("id")*/Integer id ) {
		// TODO Auto-generated method stub
		commentSvc.deleteLocationComment(id);
	}

}