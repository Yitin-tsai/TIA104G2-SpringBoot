package chilltrip.tripcollection.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
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
import chilltrip.tripcollection.model.TripCollectionService;
import chilltrip.tripcollection.model.TripCollectionVO;

@RestController
@RequestMapping("/tripCollection")
public class TripCollectionServlet extends HttpServlet {

	@Autowired
	private TripCollectionService tripColSvc;
	@Autowired
	private MemberService memberSvc;
	@Autowired
	private TripService tripSvc;

	@PostMapping("/add/{tid}")
	public ResponseEntity<String>  addTripCollection(@PathVariable("tid") Integer tripId, HttpServletRequest request) {
		Integer memberId = (Integer) request.getSession().getAttribute("memberId");
		
		if (memberId == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("nologin");
        }
	
		TripVO trip = tripSvc.getById(tripId);
		MemberVO member = memberSvc.getOneMember(memberId);
		TripCollectionVO tripCollectionVO = new TripCollectionVO();
		tripCollectionVO.setMembervo(member);
		tripCollectionVO.setTripvo(trip);
		tripColSvc.addTripCollection(tripCollectionVO);
		return ResponseEntity.ok("success");
	}

	@PostMapping("/delete/{tid}")
	public void deleteTripCollection(@PathVariable("tid") Integer tripid,HttpServletRequest req) {
		Integer memberid = (Integer) req.getSession().getAttribute("memberId");
		tripColSvc.deleTripCollection(memberid,tripid);
	}

	@GetMapping("getByTrip/{id}")
	public List<TripCollectionVO> getByTrip(@PathVariable("id") Integer tripId) throws IOException {
		
		if (tripSvc.getById(tripId) != null) {
			List<TripCollectionVO> list = tripColSvc.getByTrip(tripId);
			return list;
		} else {
			List errormsg = new LinkedList();
			errormsg.add("failed id not found");
			return errormsg;
		}
	}

	@GetMapping("getByMember/{id}")
	public ResponseEntity<Map<String, Object>> getByMember(@PathVariable("id")Integer memberId, Integer page) {
		int currentPage = (page == null) ? 1 : page;
		
		Map<String, Object> responseMap = new HashMap<>();
		if (memberSvc.getOneMember(memberId) != null) {
			List<TripCollectionVO> list = tripColSvc.getByMember(memberId, currentPage);

			int tripColPageQty = tripColSvc.getTotalPage(memberId);
			Map<String, Integer> pageMap = new HashMap<String, Integer>();
			pageMap.put("tripColPageQty", tripColPageQty);
			pageMap.put("currentPage", currentPage);
			
			responseMap.put("success", true);
	        responseMap.put("tripCollections", list);  
	        responseMap.put("pageInfo", pageMap);      

	        return ResponseEntity.ok(responseMap);

		}
		responseMap.put("failed", true);
	    responseMap.put("message", "Failed to find member or data");
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
	}
	
	@GetMapping("checkCollection/{tid}")
	public boolean checkCollection( @PathVariable("tid")Integer tripId,HttpServletRequest req) {
		Integer memberId = (Integer) req.getSession().getAttribute("memberId");
		if(memberId ==  null) {
			return false;
		}
		return tripColSvc.checkCollection(memberId, tripId);
	}

}
