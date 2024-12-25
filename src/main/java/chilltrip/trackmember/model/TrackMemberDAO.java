package chilltrip.trackmember.model;

import java.util.List;

import chilltrip.member.model.MemberVO;

public interface TrackMemberDAO {

	public void insert(TrackMemberVO trackMemberVO);
	
	public void delete(Integer trackMemberId);
	
	public List<MemberVO> getAllfans(Integer memberId);

	public List<TrackMemberVO> getAllTracks(Integer fansId);
	
	public long getFansQty(Integer memberId);
	
	public long getTracksQty(Integer fansId);
	
	
	
	
}
