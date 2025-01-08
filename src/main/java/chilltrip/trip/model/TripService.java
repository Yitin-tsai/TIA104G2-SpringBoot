package chilltrip.trip.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import chilltrip.trip.repository.TripRepository;

@Service
public class TripService {
	
	private TripDAO dao;
	private TripRepository tripRepository;
	
	@Autowired
    public TripService(TripDAO dao, TripRepository tripRepository) {
        this.dao = dao;
        this.tripRepository = tripRepository;
    }
	
	public TripService() {
		dao = new TripDAOImplJDBC();
	}
	
	
	public TripVO addTrip(TripVO tripVO) {
		dao.insert(tripVO);
		return tripVO;
	}
	
	public TripVO updateTrip(TripVO tripVO) {
		dao.update(tripVO);
		return tripVO;
	}
	
	public void deleteTrip(Integer tripid) {
		dao.delete(tripid);
	}
	
	public List<TripVO> getAllTrip(){
		return dao.getAllTrip();
	}
	
	public List<Map<String, Object>> getAllTripPro(){
		return dao.getAllTripPro();
	}
	
	public List<Map<String, Object>> getByMemberId (Integer memberId){
		return dao.getByMemberId(memberId);
	}
	
	public void changeTripStatus(TripVO tripVO) {
		dao.changeTripStatus(tripVO);
	}
	
	public  List<Map<String, Object>> getMemberTripByStatus(Integer memberId, Integer status){
		return dao.getMemberTripByStatus(memberId, status);
	}
	
	public List<Map<String, Object>> getTripByLocation(String location_name){
		return dao.getTripByLocation(location_name);
	}
	
	public TripVO getById(Integer tripid) {
		return dao.getById(tripid);
	}
	
	public List<Map<String, Object>> getTripByName(String article_title){
		return dao.getTripByName(article_title);
	}
	
	@Transactional(readOnly = true)
	public Page<Map<String, Object>> getTrips(Pageable pageable) {
        Page<Object[]> tripsPage = tripRepository.findAllTripsWithDetails(pageable);
        
        return tripsPage.map(tripArray -> {
            // 獲取並處理標籤
            List<Object[]> tagArrays = tripRepository.findTripTags((Integer) tripArray[0]);
            List<String> tags = new ArrayList<>();
            
            for (Object[] tagArray : tagArrays) {
                String regionTag = (String) tagArray[0];
                String eventTag = (String) tagArray[1];
                
                // 判斷標籤是否為空
                if (regionTag != null && !regionTag.trim().isEmpty()) {
                    tags.add(regionTag);
                }
                if (eventTag != null && !eventTag.trim().isEmpty()) {
                    tags.add(eventTag);
                }
            }

            // 直接建立 Map
            Map<String, Object> tripMap = new HashMap<>();
            tripMap.put("id", tripArray[0]);
            
            // 標題判斷
            String title = (tripArray[1] == null || ((String)tripArray[1]).trim().isEmpty()) 
                ? "未命名行程" 
                : (String)tripArray[1];
            tripMap.put("title", title);
            
            // 描述判斷
            String description = (tripArray[2] == null || ((String)tripArray[2]).trim().isEmpty()) 
                ? "沒有行程簡介" 
                : (String)tripArray[2];
            tripMap.put("description", description);
            
            tripMap.put("views", tripArray[3]);
            tripMap.put("likes", tripArray[4]);
            tripMap.put("rating", tripArray[5]);
            
            // 作者判斷
            String author = (tripArray[6] == null || ((String)tripArray[6]).trim().isEmpty()) 
                ? "未知作者" 
                : (String)tripArray[6];
            tripMap.put("author", author);
            
            tripMap.put("image", tripArray[7]);
            tripMap.put("tags", tags);
            
            System.out.println(tripMap);
            return tripMap;
        });
    }

	public Object getPopularTrips(int i) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
