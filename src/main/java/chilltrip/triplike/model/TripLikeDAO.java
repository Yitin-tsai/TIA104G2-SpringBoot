package chilltrip.triplike.model;

import java.util.List;



public interface TripLikeDAO {

	public void insert(TripLikeVO tripLikeVO);

	public void delete(Integer tripLikeId);

	public List<TripLikeVO> getByTrip(Integer tripid);

	public List<TripLikeVO> getByMember(Integer memberid);

	

}
