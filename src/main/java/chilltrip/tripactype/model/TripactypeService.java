package chilltrip.tripactype.model;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chilltrip.member.model.MemberDAO_interface;
import chilltrip.member.model.MemberJDBCDAO;
import chilltrip.member.model.MemberVO;
import chilltrip.trip.model.TripVO;
import chilltrip.tripphoto.dao.TripphotoDAO;
import chilltrip.tripphoto.dao.TripphotoDAOImplJDBC;
import chilltrip.tripphoto.model.TripphotoService;
import chilltrip.tripphoto.model.TripphotoVO;

@Service
public class TripactypeService {

	private TripactypeDAO_interface dao;
	private MemberDAO_interface memberDao;
	private TripphotoDAO tripPhotoDao;
	private TripphotoService tripphotoService;

	@Autowired
	public TripactypeService() {
		dao = new TripactypeDAO();
		memberDao = new MemberJDBCDAO();
		tripPhotoDao = new TripphotoDAOImplJDBC();
		tripphotoService = new TripphotoService(tripPhotoDao);
	}

	// 查詢所有活動類型
	public List<TripactypeVO> findAllTripactypes() {
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

	// 查詢行程活動並顯示封面照片
	public List<Map<String, Object>> findTripsByEventType(String eventType) {
		List<TripVO> trips = dao.getTripsByEventType(eventType);
		List<Map<String, Object>> tripDataList = new ArrayList<>();

		for (TripVO trip : trips) {
			Map<String, Object> tripData = new HashMap<>();
			tripData.put("trip_id", trip.getTrip_id());
			tripData.put("article_title", trip.getArticle_title());
			tripData.put("trip_abstract", trip.getTrip_abstract());
			tripData.put("likes", trip.getLikes());

			if (trip.getMembervo() == null) {
				MemberVO member = memberDao.findByPrimaryKey(trip.getMemberId());
				trip.setMembervo(member);
			}
			// 取得作者暱稱
			tripData.put("member_nickName", trip.getMembervo().getNickName());

			// 取封面 Base64
			List<Map<String, Object>> photoList = tripphotoService.getOneTripPhotoByTypeAsBase64(trip.getTrip_id(), 0);
			if (!photoList.isEmpty()) {
				String base64Photo = (String) photoList.get(0).get("photo");
				tripData.put("coverImageUrl", base64Photo);
			} else {
				tripData.put("coverImageUrl", null);
			}
			tripDataList.add(tripData);
		}
		return tripDataList;
	}

	// 根據 tripId 查找封面照片
	private TripphotoVO findCoverPhotoByTripId(Integer tripId) {
		// 假設封面照片的類型為 0
		List<Map<String, Object>> photos = tripPhotoDao.getOneTripPhotoByType(tripId, 0); // 0 代表封面照片
		if (!photos.isEmpty()) {
			Map<String, Object> photoData = photos.get(0);
			TripphotoVO tripphotoVO = new TripphotoVO();
			tripphotoVO.setTrip_photo_id((Integer) photoData.get("trip_photo_id"));
			tripphotoVO.setTrip_id((Integer) photoData.get("trip_id"));
			tripphotoVO.setPhoto((byte[]) photoData.get("photo"));
			tripphotoVO.setPhoto_type((Integer) photoData.get("photo_type"));
			return tripphotoVO; // 返回封面照片
		}
		return null; // 如果沒有封面照片則返回 null
	}
}