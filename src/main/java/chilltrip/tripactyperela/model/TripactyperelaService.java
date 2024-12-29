package chilltrip.tripactyperela.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chilltrip.trip.model.TripVO;
import chilltrip.tripactype.model.TripactypeVO;

@Service
public class TripactyperelaService {

	private TripactyperelaDAO_interface dao;
	
	@Autowired
	public TripactyperelaService() {
		dao = new TripactyperelaDAO();
	}
	
	public List<TripactyperelaVO> findAllRelations(){
		return dao.getAll();
	}
	
	public List<TripactyperelaVO> findAllRelationsByTripId(Integer tripId){
		return dao.getByTripId(tripId);
	}
	
	public boolean addRelations(Integer tripId, List<Integer> eventTypeIds) {
		try {
	        for (Integer eventTypeId : eventTypeIds) {
	            // 生成新的關聯資料
	            TripactyperelaVO tripactyperelaVO = new TripactyperelaVO();
	            TripVO tripVO = new TripVO();
	            tripVO.setTrip_id(tripId);  // 設定行程 ID
	            
	            TripactypeVO tripactypeVO = new TripactypeVO();
	            tripactypeVO.setEventtypeid(eventTypeId);  // 設定活動類型 ID
	            
	            tripactyperelaVO.setTripid(tripVO);
	            tripactyperelaVO.setEventtypeid(tripactypeVO);
	            
	            // 存入關聯表
	            dao.insert(tripactyperelaVO);
	        }
	        return true;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public boolean deleteRelation(Integer relationId) {
        try {
            dao.delete(relationId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
	
	public boolean deleteRelationByTripAndEventType(Integer tripId, Integer eventTypeId) {
	    // 根據 tripId 和 eventTypeId 刪除關聯
	    return dao.deleteRelationByTripAndEventType(tripId, eventTypeId);
	}

	
	public boolean updateTripactyperela(Integer tripId, List<Integer> eventTypeIds) {
	    try {
	        // 先刪除原本的所有關聯資料
	    	dao.deleteByTripId(tripId);

	        // 再新增新的關聯資料
	        return addRelations(tripId, eventTypeIds);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}

}
