package chilltrip.triplike.controller;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import chilltrip.member.model.MemberService;
import chilltrip.member.model.MemberVO;
import chilltrip.trip.model.TripService;
import chilltrip.trip.model.TripVO;
import chilltrip.tripcollection.model.TripCollectionVO;
import chilltrip.triplike.model.TripLikeService;
import chilltrip.triplike.model.TripLikeVO;
import oracle.jdbc.proxy.annotation.Post;

@RestController
@RequestMapping("/tripLike")
public class TripLikeServlet {
	@Autowired
	private TripLikeService tripLikeSvc;
	@Autowired
	private MemberService memberSvc;
	
	
	@PostMapping("/add")
	public String addTripLike(@RequestParam Integer tripId, HttpServletRequest request) {
		Integer memberId = (Integer) request.getSession().getAttribute("memberId");
		TripService tripSvc = new TripService();
	
		if (memberId == null) {
            return "false not login";
        }
		TripVO trip = tripSvc.getById(tripId);
		MemberVO member = memberSvc.getOneMember(memberId);
		TripLikeVO triplikeVO = new TripLikeVO();
		triplikeVO.setMembervo(member);
		triplikeVO.setTripvo(trip);
		tripLikeSvc.addTripLike(triplikeVO);
		return "sucess";
	}
	@PostMapping("/delete/{id}")
	public void deleTripLike(@PathVariable("id") Integer id ) {
		tripLikeSvc.deleTripLike(id);
	}
	@GetMapping("getByTrip/{id}")
	public List<TripLikeVO> getByTrip(@PathVariable("id")Integer tripId) {

		TripService tripSvc = new TripService();
		if (tripSvc.getById(tripId) != null) {
			List<TripLikeVO> list = tripLikeSvc.getByTrip(tripId);
			return list;
		}else {
			List errormsg = new LinkedList();
			errormsg.add("failed id not found");
			return errormsg;
		}

	}
	@GetMapping("getByMember/{id}")
	public List<TripLikeVO> getByMember(@PathVariable("id")Integer memberId)  {
		

		TripService tripSvc = new TripService();
		if (tripSvc.getById(memberId) != null) {
			List<TripLikeVO> list = tripLikeSvc.getByMember(memberId);
			return list;
		}else {
			List errormsg = new LinkedList();
			errormsg.add("failed id not found");
			return errormsg;
		}
		
	}
	
}
