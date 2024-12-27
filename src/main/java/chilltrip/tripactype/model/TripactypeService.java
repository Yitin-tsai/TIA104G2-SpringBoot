package chilltrip.tripactype.model;

import java.util.List;

import chillchip.trip.model.TripVO;

public class TripactypeService {
	private TripactypeDAO_interface dao;
	
	public TripactypeService() {
		dao = new TripactypeDAO();
	}
	
	// 查詢所有活動類型
	public List<TripactypeVO> findAllTripactypes(){
		return dao.getAll();
	}
	
	// 根據ID查詢活動類型
	public TripactypeVO findTripactypeById(Integer eventtypeid) {
		return dao.getByid(eventtypeid);
	}
	
	// 新增活動類型
	public boolean addTripactype(TripactypeVO tripactypeVO) {
		try {
            dao.insert(tripactypeVO);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
	}
	
	// 更新活動類型
	public boolean updateTripactype(TripactypeVO tripactypeVO) {
		try {
            dao.update(tripactypeVO);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
	}
	
	// 刪除活動類型
	public boolean deleteTripactype(Integer eventtypeid) {
		try {
	        dao.delete(eventtypeid);
	        return true;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public List<TripVO> findTripsByEventType(String eventType) {
	    // 根據 eventType 來查詢所有相關的行程
	    return dao.getTripsByEventType(eventType);
	}
	
}
