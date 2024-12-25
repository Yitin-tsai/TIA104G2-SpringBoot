package chilltrip.locationcomment.model;

import java.util.List;

import chilltrip.location.model.LocationVO;

public interface LocationCommentDAO {
	
	public void insert(LocationCommentVO locationCommentVO);
	public void update(LocationCommentVO locationCommentVO);
	public void delete(Integer locationCommentId);
	public List<LocationCommentVO> getByLocation(Integer locationId);
	public List<LocationCommentVO> getByMember(Integer memberId);
	
}
