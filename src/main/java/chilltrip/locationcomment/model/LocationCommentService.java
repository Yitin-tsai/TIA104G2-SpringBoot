package chilltrip.locationcomment.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
public class LocationCommentService {

	private LocationCommentDAO dao;

	@Autowired
	public LocationCommentService(LocationCommentDAOimpl dao) {
		this.dao = dao;
	}

	public LocationCommentVO addLocationComment(LocationCommentVO locationCommentVO) {

		dao.insert(locationCommentVO);
		return locationCommentVO;
	}

	public LocationCommentVO updateLocationComment(LocationCommentVO locationCommentVO) {

		dao.update(locationCommentVO);
		return locationCommentVO;
	}

	public boolean deleteLocationComment(Integer id) {
		LocationCommentVO locationCommentVO = dao.getById(id);
		if (locationCommentVO == null) {
			return false;
		}return dao.delete(id);
	}

	public List<LocationCommentVO> getLocationCommentByLocation(Integer locationId) {

		return dao.getByLocation(locationId);

	}

	public List<LocationCommentVO> getLocationCommentByLMember(Integer memberId) {

		return dao.getByMember(memberId);

	}
}
