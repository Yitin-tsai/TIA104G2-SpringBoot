package chilltrip.triplike.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class TripLikeService {
	
	@Autowired
	private static TripLikeDAO dao;
	
	public TripLikeService(TripLikeDAOimpl dao) {
		this.dao = dao;
	}
	
	public TripLikeVO addTripLike(TripLikeVO tripLikeVO) {
		dao.insert(tripLikeVO);
		return tripLikeVO;
	}
	
	public void deleTripLike(Integer memberId , Integer tripid) {
		dao.delete(dao.getone(memberId, tripid));
	}
	
	public List<TripLikeVO> getByTrip(Integer tripId) {
		return dao.getByTrip(tripId);
	}
	public List<TripLikeVO> getByMember(Integer memberId) {
		return dao.getByMember(memberId);
	}
	
	
	public boolean checkLike(Integer memberId ,Integer tripId) {
		return dao.getOne(memberId, tripId); 
		}
}
