package chilltrip.triplike.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import chilltrip.tripcollection.model.TripCollectionVO;
import chilltrip.triplike.model.TripLikeService;
import chilltrip.triplike.model.TripLikeVO;

public class TripLikeServlet {
	private TripLikeService tripLikeSvc;
	
	public void init() {
		tripLikeSvc = new TripLikeService();
	}
	
	public void addTripLike(/*@RequsetBody*/ TripLikeVO tripLikevo) {
		tripLikeSvc.addTripLike(tripLikevo);
	}
	
	public void deleTripLike(/*@PathVariable("id") */ Integer id ) {
		tripLikeSvc.deleTripLike(id);
	}
	
	public void getByTrip(Integer tripId, HttpServletResponse res) {

		List<TripLikeVO> list = tripLikeSvc.getByTrip(tripId);
		JSONArray jsonArray = new JSONArray();
		for (TripLikeVO tripLike : list) {
			JSONObject jsonRes = new JSONObject();
			jsonRes.put("tripLikeId", tripLike.getTripLikeId());
			jsonRes.put("tripvo", tripLike.getTripvo());
			jsonRes.put("membervo", tripLike.getMembervo());

			jsonArray.put(jsonRes);
		}

		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		try {
			res.getWriter().write(jsonArray.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void getByMember(Integer memberId, HttpServletResponse res) throws IOException {
		
		List<TripLikeVO> list = tripLikeSvc.getByTrip(memberId);
		JSONArray jsonArray = new JSONArray();
		for (TripLikeVO tripLike : list) {
			JSONObject jsonRes = new JSONObject();
			jsonRes.put("tripLikeId", tripLike.getTripLikeId());
			jsonRes.put("tripvo", tripLike.getTripvo());
			jsonRes.put("membervo", tripLike.getMembervo());
			
			jsonArray.put(jsonRes);
		}
		
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write(jsonArray.toString());
		
	}
	
}
