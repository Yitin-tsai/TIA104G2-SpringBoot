package chilltrip.trackmember.controller;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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

	@PostMapping("/track/{fid}/{tid}")
	public String trackMember(@PathVariable("fid")Integer fansId,@PathVariable("tid") Integer trackId) {
		
	
		MemberVO fans = membersvc.getOneMember(fansId);
		MemberVO tracker = membersvc.getOneMember(trackId);
		TrackMemberVO trackMembervo =new TrackMemberVO();
		trackMembervo.setFans(fans);
		trackMembervo.setTrackedMember(tracker);
		trackMemSvc.trackMember(trackMembervo);
		membersvc.updateMember(fans);
		membersvc.updateMember(tracker);
		return "sucess";
	}

	@PostMapping("/deleteTrack/{id}")
	public void deleteTrack(@PathVariable("id") Integer id) {
		trackMemSvc.deleteTrack(id);
	}

	@GetMapping("getAllFans/{id}")
	public List<MemberVO> getAllfans(@PathVariable("id") Integer memberId) {
		MemberService memberSvc = new MemberService();
		if (memberSvc.getOneMember(memberId) != null) {
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

		MemberService memberSvc = new MemberService();
		if (memberSvc.getOneMember(memberId) != null) {
			List<MemberVO> list = trackMemSvc.getAllTracks(memberId);
			return list;
		}else {
			List errormsg = new LinkedList();
			errormsg.add("failed id not found");
			return errormsg;
		}
	}


}
