package chilltrip.tripphoto.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.json.JSONArray;
import org.json.JSONObject;

import chilltrip.tripphoto.model.TripphotoService;
import chilltrip.tripphoto.model.TripphotoVO;

@WebServlet("/tripphoto/tripphoto.do")
@MultipartConfig
public class TripphotoServlet extends HttpServlet{
	
	private TripphotoService tripphotoSvc;
	
	public void init() {
		tripphotoSvc = new TripphotoService();
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
		
		case "addTripphoto":
			System.out.println(action);
			addTripphoto(req,res);
			System.out.println(action);
			break;
		case "updateTripphoto":
			updateTripphoto(req,res);
			break;
		case "deleteTripphoto":
			deleteTripphoto(req,res);
			break;
		case "getOneTripPhotoByType":
			getOneTripPhotoByType(req,res);
			break;
		}
		
	}
	
	
	//新增
	private String addTripphoto (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Integer tripid = Integer.valueOf(req.getParameter("trip_id"));
		Part photo = req.getPart("photo");
		Integer phototype = Integer.valueOf(req.getParameter("photo_type"));
		System.out.println(tripid);
		System.out.println(photo);
		
		//將Part轉成 byte array 方式上傳 -->效率比較好
		if (photo != null && photo.getSize() > 0) {
            try {
                // 轉換為 byte array
                InputStream photoInputStream = photo.getInputStream();
                byte[] photoBytes = photoInputStream.readAllBytes();
                tripphotoSvc.determineFileType(photoBytes);
                
                TripphotoVO tripphotoVO = new TripphotoVO();
                tripphotoVO.setTrip_id(tripid);
                tripphotoVO.setPhoto(photoBytes);
                tripphotoVO.setPhoto_type(phototype);
                tripphotoSvc.addTripphoto(tripphotoVO);
                
                // 返回成功訊息
                res.setContentType("application/json");
                res.getWriter().write("{\"status\": \"success\"}");
                res.getWriter().write("照片檔案為："+ photoBytes);
                
            } catch (Exception e) {
                res.setContentType("application/json");
                res.getWriter().write("{\"status\": \"error\", \"message\": \"" + e.getMessage() + "\"}");
            }
        }
		return "新增成功！";
	}
	
	//修改
	private String updateTripphoto (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Integer tripphotoid = Integer.valueOf(req.getParameter("trip_photo_id"));
		Integer tripid = Integer.valueOf(req.getParameter("trip_id"));
		Part photo = req.getPart("photo");
		Integer phototype = Integer.valueOf(req.getParameter("photo_type"));
		System.out.println(tripid);
		
		//將Part轉成 byte array 方式上傳 -->效率比較好
				if (photo != null && photo.getSize() > 0) {
		            try {
		                // 轉換為 byte array
		                InputStream photoInputStream = photo.getInputStream();
		                byte[] photoBytes = photoInputStream.readAllBytes();
		                tripphotoSvc.determineFileType(photoBytes);
		                System.out.println(photoBytes);
		                
		                TripphotoVO tripphotoVO = tripphotoSvc.getTripphotoById(tripphotoid);
		                System.out.println(tripphotoVO);
		                tripphotoVO.setTrip_id(tripid);
		                tripphotoVO.setPhoto(photoBytes);
		                tripphotoVO.setPhoto_type(phototype);
		                tripphotoSvc.updateTripphoto(tripphotoVO);
		                
		                // 返回成功訊息
		                res.setContentType("application/json");
		                res.getWriter().write("{\"status\": \"success\"}");
		                res.getWriter().write("照片檔案為："+ photoBytes);
		                
		            } catch (Exception e) {
		                res.setContentType("application/json");
		                res.getWriter().write("{\"status\": \"error\", \"message\": \"" + e.getMessage() + "\"}");
		            }
		        }
		return "修改成功！";
	}
	
	//刪除
	private String deleteTripphoto (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Integer tripphotoid = Integer.valueOf(req.getParameter("trip_photo_id"));
		tripphotoSvc.deleteTripphoto(tripphotoid);
		
		res.setContentType("text/plain");
		res.setCharacterEncoding("UTF-8");

		// 回傳成功訊息給前端
		res.getWriter().write("success");
		
		return "刪除成功！";
	}
	
	//拿到某行程中的所有（封面／內文）照片
	private void getOneTripPhotoByType (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Integer tripid = Integer.valueOf(req.getParameter("trip_id"));
		Integer phototype = Integer.valueOf(req.getParameter("photo_type"));

		List<Map<String, Object>> tripphotolist = tripphotoSvc.getOneTripPhotoByTypeAsBase64(tripid, phototype);

		JSONArray jsonArray = new JSONArray();

		for (Map<String, Object> trip : tripphotolist) {
			JSONObject jsonRes = new JSONObject();
			for (String key : trip.keySet()) {
				jsonRes.put(key, trip.get(key));
			}
			jsonArray.put(jsonRes);
		}
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write(jsonArray.toString());
		
	}
	
	
	
	
}
