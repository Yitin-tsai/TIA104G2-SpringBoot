package chilltrip.member.model;

import java.util.List;
import java.util.Set;

import chilltrip.tripcomment.model.TripCommentVO;

public interface MemberDAO_interface {
	public void insert(MemberVO memberVO);
	public void update(MemberVO memberVO);
	public void delete(Integer memberId);
	public MemberVO findByPrimaryKey(Integer memberId);
	public List<MemberVO> getAll();
    // 查詢某會員的行程留言(一對多)(回傳 Set)
	public Set<TripCommentVO> getTripCommentByMember(Integer memberId);
	public MemberVO findByEmail(String email);
	public boolean isEmailExist(String email);
}
