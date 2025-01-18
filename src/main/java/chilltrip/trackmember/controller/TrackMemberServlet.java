package chilltrip.trackmember.controller;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import chilltrip.member.model.MemberService;
import chilltrip.member.model.MemberVO;
import chilltrip.trackmember.model.TrackMemberService;
import chilltrip.trackmember.model.TrackMemberVO;


@RestController
@RequestMapping("/trackMember")
public class TrackMemberServlet {

	@Autowired
	private TrackMemberService trackMemSvc;
	@Autowired
	private MemberService membersvc;

	@PostMapping("/track/{tid}")
	public ResponseEntity<String> trackMember(@PathVariable("tid") Integer trackId,HttpServletRequest req) {
		
		Integer fansId = (Integer) req.getSession().getAttribute("memberId");
		System.out.println(fansId);
		if(fansId == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("nologin");
		}
		
		MemberVO fans = membersvc.getOneMember(fansId);
		MemberVO tracker = membersvc.getOneMember(trackId);
		TrackMemberVO trackMembervo =new TrackMemberVO();
		trackMembervo.setFans(fans);
		trackMembervo.setTrackedMember(tracker);
		trackMemSvc.trackMember(trackMembervo);
		membersvc.updateMember(fans);
		membersvc.updateMember(tracker);
		return ResponseEntity.ok("success");
		
		
	}

	@PostMapping("/deleteTrack/{id}")
	public ResponseEntity<String> deleteTrack(@PathVariable("id") Integer id,HttpServletRequest req) {
		Integer fansId = (Integer) req.getSession().getAttribute("memberId");
		System.out.println(fansId);
		if(fansId == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("nologin");
		}
		MemberVO fans = membersvc.getOneMember(fansId);
		MemberVO tracker = membersvc.getOneMember(id);
	
		trackMemSvc.deleteTrack(fansId,id);
		membersvc.updateMember(fans);
		membersvc.updateMember(tracker);
		return ResponseEntity.ok("success");
	}

	@GetMapping("getAllFans/{id}")
	public List<MemberVO> getAllfans(@PathVariable("id") Integer memberId) {
		
		if (membersvc.getOneMember(memberId) != null) {
			List<MemberVO> list = trackMemSvc.getAllfans(memberId);
			return list;
		}else {
			List errormsg = new LinkedList();
			errormsg.add("failed id not found");
			return errormsg;
		}

	}
	@GetMapping("getAllTracks/{id}")
	public List<MemberVO>  getAllTracks(@PathVariable("id")Integer memberId) {

		if (membersvc.getOneMember(memberId) != null) {
			List<MemberVO> list = trackMemSvc.getAllTracks(memberId);
			return list;
		}else {
			List errormsg = new LinkedList();
			errormsg.add("failed id not found");
			return errormsg;
		}
	}
	
	@GetMapping("checkTrack/{tid}")
	public boolean checkTrack(HttpServletRequest req , @PathVariable("tid")Integer trackerId) {
		Integer memberId = (Integer) req.getSession().getAttribute("memberId");
		if(memberId ==  null) {
			return false;
		}
		return trackMemSvc.checkTrack(memberId, trackerId);
	}

}
