package chilltrip.sub_trip.model;

import java.util.List;
import java.util.Map;

import chilltrip.sub_trip.dao.SubtripDAO;
import chilltrip.sub_trip.dao.SubtripDAOImplJDBC;

public class SubtripService {
	
	private SubtripDAO dao;
	
	public SubtripService() {
		dao = new SubtripDAOImplJDBC();
	}
	
	
	public SubtripVO addSubtrip (SubtripVO subtripVO) {
		dao.insert(subtripVO);
		return subtripVO;
	}
	
	public SubtripVO getBySubtripId(Integer subtrip) {
		return dao.getBySubtripId(subtrip);
		
	}
	
	
	public SubtripVO updateSubtrip (SubtripVO subtripVO) {
		dao.update(subtripVO);
		return subtripVO;
	}
	
	public void deleteSubtrip (Integer subtripid) {
		dao.delete(subtripid);
	}
	
	public List<SubtripVO> getAllSubtripById(){
		return dao.getallsubtrip();
	}
	
	
	public List<Map<String, Object>> getByTripId(Integer tripid){
		return dao.getByTripId(tripid);
	}
	
	
	
	

}
