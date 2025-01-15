package chilltrip.tripcollection.model;

import java.util.List;

public interface TripCollectionDAO {
	
	public void insert(TripCollectionVO tripCollectionVO);
	public void delete(TripCollectionVO tripCollectionVO);
	public List<TripCollectionVO> getByTrip(Integer tripid);
	public List<TripCollectionVO> getByMember(Integer memberid  ,Integer currentPage);
	long getTotalByMember(Integer memberid);
	public boolean getOne(Integer memberId, Integer tripId );
	public TripCollectionVO getone(Integer memberId, Integer tripId );
}
