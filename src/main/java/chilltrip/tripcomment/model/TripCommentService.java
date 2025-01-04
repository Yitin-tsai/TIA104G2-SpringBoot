package chilltrip.tripcomment.model;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TripCommentService {

	private TripCommentDAO_interface dao;

	@Autowired
	public TripCommentService() {
		dao = new TripCommentJDBCDAO();
	}

	
	public TripCommentVO addTripComment(Integer memberId, Integer tripId,
			Integer score, byte[] photo, java.sql.Timestamp createTime, String content) {

		TripCommentVO tripCommentVO = new TripCommentVO();

		tripCommentVO.setMemberId(memberId);
		tripCommentVO.setTripId(tripId);
		tripCommentVO.setScore(score);
		tripCommentVO.setPhoto(photo);
		tripCommentVO.setCreateTime(createTime);
		tripCommentVO.setContent(content);
		
		dao.insert(tripCommentVO);

		return tripCommentVO;
	}

	public TripCommentVO updateTripComment(Integer tripCommentId, Integer memberId, Integer tripId,
	        Integer score, byte[] photo, java.sql.Timestamp createTime, String content) {

	    // 先查詢留言是否存在，且該留言是否是該使用者的
	    TripCommentVO tripCommentVO = dao.findByPrimaryKey(tripCommentId);
	    if (tripCommentVO == null || !tripCommentVO.getMemberId().equals(memberId)) {
	        throw new IllegalArgumentException("無法修改他人的留言");
	    }

	    tripCommentVO.setScore(score);
	    tripCommentVO.setPhoto(photo);
	    tripCommentVO.setCreateTime(createTime);
	    tripCommentVO.setContent(content);

	    dao.update(tripCommentVO);

	    return dao.findByPrimaryKey(tripCommentId);  // 返回更新後的留言
	}


	public TripCommentVO getTripComment(Integer tripCommentId) {
		return dao.findByPrimaryKey(tripCommentId);
	}

	public List<TripCommentVO> getAll() {
		return dao.getAll();
	}
	
	public void deleteTripComment(Integer tripCommentId) {
		dao.delete(tripCommentId);
	}
	
	public List<TripCommentVO> getCommentsByTripId(Integer tripId) {
	    return dao.findByTripId(tripId);
	}
	
	public List<Map<String, Object>> getCommentsWithMemberInfo(Integer tripId) {
	    return dao.getCommentsWithMemberInfo(tripId);
	}
	
	public void deleteTripComment(Integer tripCommentId, Integer memberId) {
	    TripCommentVO tripCommentVO = dao.findByPrimaryKey(tripCommentId);
	    if (tripCommentVO == null || !tripCommentVO.getMemberId().equals(memberId)) {
	        throw new IllegalArgumentException("無法刪除他人的留言");
	    }

	    dao.delete(tripCommentId);
	}
}
