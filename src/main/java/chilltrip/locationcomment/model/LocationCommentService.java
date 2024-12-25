package chilltrip.locationcomment.model;

import java.util.List;

public class LocationCommentService {
	
	private LocationCommentDAO dao;
	
	public LocationCommentService() {
		dao = new LocationCommentDAOimpl();
	}
	
	public LocationCommentVO addLocationComment(LocationCommentVO locationCommentVO) {
		
		dao.insert(locationCommentVO);
		return locationCommentVO;
	}
	public LocationCommentVO updateLocationComment(LocationCommentVO locationCommentVO) {
		
		dao.update(locationCommentVO);
		return locationCommentVO;
	}
	public void deleteLocationComment(Integer id ) {
		
		dao.delete(id);
		
	}
	public List<LocationCommentVO> getLocationCommentByLocation(Integer locationId){
		
		return dao.getByLocation(locationId);
		
	}
	public List<LocationCommentVO> getLocationCommentByLMember(Integer memberId){
		
		return dao.getByLocation(memberId);
		
	}
}
