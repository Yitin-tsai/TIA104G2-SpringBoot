package chilltrip.triplocationrelation.model;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TriplocationrelationService {
	
	private TriplocationrelationDAO dao;
	
	@Autowired
	public TriplocationrelationService(TriplocationrelationDAO dao) {
		this.dao = dao;
	}
	
	public TriplocationrelationVO addTriplocationrelation (TriplocationrelationVO triplocationrelationVO) {
		dao.insert(triplocationrelationVO);
		return triplocationrelationVO;
	}
	
	public TriplocationrelationVO updateTriplocaionrelation (TriplocationrelationVO triplocationrelationVO) {
		dao.update(triplocationrelationVO);
		return triplocationrelationVO;
	}
	
	public void delete (Integer trip_location_relation_id) {
		dao.delete(trip_location_relation_id);
	}
	
	public List<TriplocationrelationVO> getAllTriplocationrelationByTrip(){
		return dao.getAllTriplocationrelationByTrip();
	}
	
	public List<Map<String, Object>> getAllTriplocationrelationByTripPro(){
		return dao.getAllTriplocationrelationByTripPro();
	}
	
	public TriplocationrelationVO getTriplocationrelationById(Integer trip_location_relation_id) {
		return dao.getTriplocationrelationById(trip_location_relation_id);
	}

}

