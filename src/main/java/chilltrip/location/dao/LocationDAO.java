package chilltrip.location.dao;

import java.util.List;
import java.util.Map;

import chilltrip.location.model.LocationVO;

public interface LocationDAO {

	public void insert(LocationVO locationVO);
	public void update(LocationVO locationVO);
	public void delete(Integer locationid);
	public List<LocationVO> getAll();
	public List<Map<String, Object>> getAllPro();
	public LocationVO getById(Integer locationid);
	public List<Map<String, Object>> getLocationByName(String location_name);
	LocationVO findByGooglePlaceId(String googlePlaceId);
	void insertOrUpdate(LocationVO locationVO);

}