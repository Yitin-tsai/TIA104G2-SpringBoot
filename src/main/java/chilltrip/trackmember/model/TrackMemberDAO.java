package chilltrip.trackmember.model;

import java.util.List;

import chilltrip.member.model.MemberVO;

public interface TrackMemberDAO {

	public void insert(TrackMemberVO trackMemberVO);
	
	public void delete(TrackMemberVO trackMemberVO);
	
	public List<MemberVO> getAllfans(Integer memberId);

	public List<MemberVO> getAllTracks(Integer fansId);
	
	public Integer getFansQty(Integer memberId);
	
	public Integer getTracksQty(Integer fansId);
	
	public boolean getOne(Integer memberId, Integer trackerId );

	public TrackMemberVO getone(Integer memberId, Integer trackerId);
	
}
