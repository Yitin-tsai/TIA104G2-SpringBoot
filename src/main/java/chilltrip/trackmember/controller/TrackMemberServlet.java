package chilltrip.trackmember.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import chilltrip.member.model.MemberVO;
import chilltrip.trackmember.model.TrackMemberDAO;
import chilltrip.trackmember.model.TrackMemberService;
import chilltrip.trackmember.model.TrackMemberVO;

public class TrackMemberServlet {
	private TrackMemberService trackMemSvc;

	public void init() {
		trackMemSvc = new TrackMemberService();
	}

	public void trackMember(/* @RequsetBody */ TrackMemberVO trackMembervo) {
		trackMemSvc.trackMember(trackMembervo);
	}

	public void deleteTrack(/* @PathVariable("id") */ Integer id) {
		trackMemSvc.deleteTrack(id);
	}

	public void getAllfans(Integer memberId, HttpServletResponse res) {

		List<MemberVO> list = trackMemSvc.getAllfans(memberId);
		JSONArray jsonArray = new JSONArray();
		for (MemberVO fans : list) {
			JSONObject jsonRes = new JSONObject();
			jsonRes.put("memberVO", fans);

			jsonArray.put(jsonRes);
		}
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		try {
			res.getWriter().write(jsonArray.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getAllTracks(Integer memberId, HttpServletResponse res) {

		List<TrackMemberVO> list = trackMemSvc.getAllTracks(memberId);
		JSONArray jsonArray = new JSONArray();
		for (TrackMemberVO tracks : list) {
			JSONObject jsonRes = new JSONObject();
			jsonRes.put("trackMemberId", tracks.getTrackMemberId());
			jsonRes.put("member", tracks.getFans());
			jsonRes.put("trackedMember", tracks.getTrackedMember());
			jsonArray.put(jsonRes);
		}
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		try {
			res.getWriter().write(jsonArray.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Long getFansQty(Integer memberId) {
		return trackMemSvc.getFanQty(memberId);
	}
	public Long getTracksQty(Integer memberId) {
		return trackMemSvc.getTracksQty(memberId);
	}
}
