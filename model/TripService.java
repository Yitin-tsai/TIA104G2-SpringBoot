package chillchip.trip.model;

import java.util.List;
import java.util.Map;

import chillchip.trip.dao.TripDAO;
import chillchip.trip.dao.TripDAOImplJDBC;

public class TripService {
	
	private TripDAO dao;
	
	public TripService() {
		dao = new TripDAOImplJDBC();
	}
	
	
	public TripVO addTrip(TripVO tripVO) {
		dao.insert(tripVO);
		return tripVO;
	}
	
	public TripVO updateTrip(TripVO tripVO) {
		dao.update(tripVO);
		return tripVO;
	}
	
	public void deleteTrip(Integer tripid) {
		dao.delete(tripid);
	}
	
	public List<TripVO> getAllTrip(){
		return dao.getAllTrip();
	}
	
	public List<Map<String, Object>> getAllTripPro(){
		return dao.getAllTripPro();
	}
	
	public List<Map<String, Object>> getByMemberId (Integer memberId){
		return dao.getByMemberId(memberId);
	}
	
	public void changeTripStatus(TripVO tripVO) {
		dao.changeTripStatus(tripVO);
	}
	
	public  List<Map<String, Object>> getMemberTripByStatus(Integer memberId, Integer status){
		return dao.getMemberTripByStatus(memberId, status);
	}
	
	public List<Map<String, Object>> getTripByLocation(String location_name){
		return dao.getTripByLocation(location_name);
	}
	
	public TripVO getById(Integer tripid) {
		return dao.getById(tripid);
	}
	
	public List<Map<String, Object>> getTripByName(String article_title){
		return dao.getTripByName(article_title);
	}
	

}
