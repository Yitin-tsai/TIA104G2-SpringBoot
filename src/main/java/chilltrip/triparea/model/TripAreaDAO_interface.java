package chilltrip.triparea.model;

import java.util.List;

import chilltrip.trip.model.TripVO;

public interface TripAreaDAO_interface {
	public void insert(TripAreaVO tripAreaVO);
	public void update(TripAreaVO tripAreaVO);
	public void delete(Integer triplocationid);
	public TripAreaVO findByPrimaryKey(Integer triplocationid);
	public List<TripAreaVO> getAll();
	// 查詢某行程地區的行程
	public List<TripAreaVO> getTripBytripArea(String regioncontent);
	
	// 行程地區在行程的新增及刪除及修改
	public void addTripAreaToTrip(TripVO tripId, String regionContent);
	public void removeTripAreaFromTrip(TripVO tripVO, String regionContent);
	public void updateTripArea(Integer tripId, String oldRegionContent, String newRegionContent);
}
