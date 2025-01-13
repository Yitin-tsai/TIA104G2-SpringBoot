package chilltrip.sub_trip.model;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import chilltrip.sub_trip.dao.SubtripDAO;


@Service
public class SubtripService {

	private SubtripDAO dao;

	@Autowired
	public SubtripService(SubtripDAO dao) {
		this.dao = dao;
	}

	public SubtripVO addSubtrip(SubtripVO subtripVO) {
		dao.insert(subtripVO);
		return subtripVO;
	}

	public SubtripVO getBySubtripId(Integer subtrip) {
		return dao.getBySubtripId(subtrip);

	}

	public SubtripVO updateSubtrip(SubtripVO subtripVO) {
		dao.update(subtripVO);
		return subtripVO;
	}

	public void deleteSubtrip(Integer subtripid) {
		dao.delete(subtripid);
	}

	public List<SubtripVO> getAllSubtripById() {
		return dao.getallsubtrip();
	}

	public List<Map<String, Object>> getByTripId(Integer tripid) {
		return dao.getByTripId(tripid);
	}

	
}
