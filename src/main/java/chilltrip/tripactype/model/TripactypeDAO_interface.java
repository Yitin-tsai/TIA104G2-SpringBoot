package chilltrip.tripactype.model;

import java.util.List;

import chilltrip.trip.model.TripVO;


public interface TripactypeDAO_interface {
	
	public void insert(TripactypeVO tripactypeVO);
	public void update(TripactypeVO tripactypeVO);
	public void delete(Integer eventtypeid);
	public List<TripactypeVO> getAll();
	public TripactypeVO getByid(Integer eventtypeid);
	
	// 查詢某個活動類型所對應的所有行程
	public List<TripVO> getTripsByEventType(String eventType);
}
