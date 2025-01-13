package chilltrip.location.model;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import chilltrip.location.dao.LocationDAO;

@Service
public class LocationService {
	
	
	private LocationDAO dao;
	
	
	@Autowired
	public LocationService(LocationDAO dao) {
		this.dao = dao;
	}
	
	
	public LocationVO addlocation(LocationVO locationVO) {
		
		dao.insert(locationVO);
		return locationVO;
	}
	
	
	public LocationVO updatelocation(LocationVO locationVO) {
		
		dao.update(locationVO);
		return locationVO;
	}
	
	
	public void deletelocation(Integer locationid) {
		dao.delete(locationid);
	}
	
	public List<LocationVO> getAllLocation () {
		return dao.getAll();
		
	}
	

	public LocationVO getLocationById(Integer locationid) {
		return dao.getById(locationid);
	
	}
	
	public List<Map<String, Object>> getLocationByName(String locationname) {
		return dao.getLocationByName(locationname);
		
	}
	
	public LocationVO findByGooglePlaceId(String googlePlaceId) {
	    return dao.findByGooglePlaceId(googlePlaceId);
	}
	
	
	 
	

}
