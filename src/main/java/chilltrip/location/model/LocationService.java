package chilltrip.location.model;

import java.util.List;
import java.util.Map;

import chilltrip.location.dao.LocationDAO;
import chilltrip.location.dao.LocationDAOImplJDBC;

public class LocationService {
	
	
	private LocationDAO dao;
	
	public LocationService() {
		dao = new LocationDAOImplJDBC();
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
	
	
	 
	

}
