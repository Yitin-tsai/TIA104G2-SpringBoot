package chilltrip.tripcomment.model;

import java.sql.Timestamp;
import java.util.List;

public class TripCommentService {

	private TripCommentDAO_interface dao;

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

		TripCommentVO tripCommentVO = new TripCommentVO();

		tripCommentVO.setTripCommentId(tripCommentId);
		tripCommentVO.setMemberId(memberId);
		tripCommentVO.setTripId(tripId);
		tripCommentVO.setScore(score);
		tripCommentVO.setPhoto(photo);
		tripCommentVO.setCreateTime(createTime);
		tripCommentVO.setContent(content);
		
		dao.update(tripCommentVO);

		return dao.findByPrimaryKey(tripCommentId);
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
}
