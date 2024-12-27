package chilltrip.tripactyperela.model;

import java.util.List;
import java.util.Map;

public interface TripactyperelaDAO_interface {

	
	public void insert(TripactyperelaVO tripactyperelaVO);
	public void update(TripactyperelaVO tripactyperelaVO);
	public void delete(Integer eventtyperelaid);
	public List<TripactyperelaVO> getAll();
//	public List<TripactyperelaVO> getByCompositeQuery(Map<String, String> map);
//	public List<TripactyperelaVO> getAll(int currentPage);
	public List<TripactyperelaVO> getByeventtypeid(Integer eventtypeid);
	
	// 刪除某個行程所有的活動類型關聯
	public void deleteByTripId(Integer tripId);
	// 用 trip_id 查詢出所有與之關聯的活動類型資料
	public List<TripactyperelaVO> getByTripId(Integer tripId);
	// 刪除指定行程和活動類型之間的關聯
	public boolean deleteRelationByTripAndEventType(Integer tripId, Integer eventTypeId);
	
}
