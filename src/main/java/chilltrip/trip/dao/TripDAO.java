package chilltrip.trip.dao;

import java.util.List;
import java.util.Map;

import chilltrip.trip.model.TripVO;

public interface TripDAO {
	
	public void insert(TripVO tripVO);
	public void update(TripVO tripVO);
	public void delete(Integer tripid);
	public List<TripVO> getAllTrip();
	public List<Map<String, Object>> getAllTripPro();
	public TripVO getById(Integer tripid);
	public List<Map<String, Object>> getByMemberId(Integer memberId);
	//根據文章標題查詢文章
	public List<Map<String, Object>> getTripByName(String article_title);
	// 拿到某用戶的私人/公開/下架文章
	public  List<Map<String, Object>> getMemberTripByStatus(Integer memberId, Integer status);
	// 更改文章狀態 Ex:文章下架-->某用戶刪除文章或是文章被檢舉遭到管理員下架
	public void changeTripStatus(TripVO tripVO);
	// 透過景點搜尋到相關文章
	public List<Map<String, Object>> getTripByLocation(String location_name);
	
	
}
