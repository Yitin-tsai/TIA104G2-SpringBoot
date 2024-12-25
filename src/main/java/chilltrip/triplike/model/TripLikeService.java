package chilltrip.triplike.model;

import java.util.List;

public class TripLikeService {

	private TripLikeDAO dao;
	
	public TripLikeService() {
		dao = new TripLikeDAOimpl();
	}
	
	public TripLikeVO addTripLike(TripLikeVO tripLikeVO) {
		dao.insert(tripLikeVO);
		return tripLikeVO;
	}
	
	public void deleTripLike(Integer tripLikeId) {
		dao.delete(tripLikeId);
	}
	
	public List<TripLikeVO> getByTrip(Integer tripId) {
		return dao.getByTrip(tripId);
	}
	public List<TripLikeVO> getByMember(Integer memberId) {
		return dao.getByMember(memberId);
	}
	
	
}
