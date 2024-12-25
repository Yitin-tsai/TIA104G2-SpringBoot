package chilltrip.trackmember.model;

import java.util.List;

import chilltrip.member.model.MemberVO;

public class TrackMemberService {

	private TrackMemberDAO dao;

	public TrackMemberService() {
		dao = new TrackMemberDAOimpl();
	}

	public TrackMemberVO trackMember(TrackMemberVO trackMemberVO) {
		dao.insert(trackMemberVO);
		return trackMemberVO;
	}

	public void deleteTrack(Integer trackMemberId) {
		dao.delete(trackMemberId);

	}

	public List<MemberVO> getAllfans(Integer memberId) {
		return dao.getAllfans(memberId);
	}
	public List<TrackMemberVO> getAllTracks(Integer memberId) {
		return dao.getAllTracks(memberId);
	}
	public Long getFanQty(Integer memberId) {
		return dao.getFansQty(memberId);
	}
	public Long getTracksQty(Integer memberId) {
		return dao.getTracksQty(memberId);
	}
	
	
}
