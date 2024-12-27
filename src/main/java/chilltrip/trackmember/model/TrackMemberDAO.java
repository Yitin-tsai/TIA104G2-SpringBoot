package chilltrip.trackmember.model;

import java.util.List;

import chilltrip.member.model.MemberVO;

public interface TrackMemberDAO {

	public void insert(TrackMemberVO trackMemberVO);
	
	public void delete(Integer trackMemberId);
	
	public List<MemberVO> getAllfans(Integer memberId);

	public List<TrackMemberVO> getAllTracks(Integer fansId);
	
	public Integer getFansQty(Integer memberId);
	
	public Integer getTracksQty(Integer fansId);
	
	
	
	
}
