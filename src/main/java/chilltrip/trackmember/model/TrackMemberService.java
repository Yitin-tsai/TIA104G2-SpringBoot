package chilltrip.trackmember.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chilltrip.member.model.MemberVO;
@Service
public class TrackMemberService {
	
	@Autowired
	private TrackMemberDAO dao;

	public TrackMemberService(TrackMemberDAOimpl dao) {
		this.dao = dao;
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
	public List<MemberVO> getAllTracks(Integer memberId) {
		return dao.getAllTracks(memberId);
	}
	public Integer getFanQty(Integer memberId) {
		return dao.getFansQty(memberId);
	}
	public Integer getTracksQty(Integer memberId) {
		return dao.getTracksQty(memberId);
	}
	
	public boolean checkTrack(Integer memberId, Integer trakcerId) {
		return dao.getOne(memberId, trakcerId);
		
	}
	
	
}
