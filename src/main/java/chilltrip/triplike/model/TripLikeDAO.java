package chilltrip.triplike.model;

import java.util.List;



public interface TripLikeDAO {

	public void insert(TripLikeVO tripLikeVO);

	public void delete(TripLikeVO tripLikeVO);

	public List<TripLikeVO> getByTrip(Integer tripid);

	public List<TripLikeVO> getByMember(Integer memberid);

	public boolean getOne(Integer memberId, Integer tripId );
	public TripLikeVO getone(Integer memberId, Integer tripId );
	

}
