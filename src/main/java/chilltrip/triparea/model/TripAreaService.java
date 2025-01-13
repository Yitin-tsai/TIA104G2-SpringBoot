package chilltrip.triparea.model;

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
import chilltrip.trip.model.TripDAO;
import chilltrip.trip.model.TripDAOImplJDBC;
import chilltrip.trip.model.TripVO;
import chilltrip.tripphoto.dao.TripphotoDAO;
import chilltrip.tripphoto.dao.TripphotoDAOImplJDBC;
import chilltrip.tripphoto.model.TripphotoService;
import chilltrip.tripphoto.model.TripphotoVO;

@Service
public class TripAreaService {

	private TripAreaDAO_interface dao;
	private TripDAO tripdao; // TripDAO_interface 的參數
	private MemberDAO_interface memberDao;
	private TripphotoDAO photoDao;
	private TripphotoService tripphotoService;

	@Autowired
	public TripAreaService() {
		dao = new TripAreaJDBCDAO();
		tripdao = new TripDAOImplJDBC();
		memberDao = new MemberJDBCDAO();
		photoDao = new TripphotoDAOImplJDBC();
		tripphotoService = new TripphotoService(photoDao);
	}

	public TripAreaVO addTripArea(TripAreaVO tripAreaVO) {
		if (tripAreaVO == null || tripAreaVO.getRegioncontent() == null) {
			throw new IllegalArgumentException("行程地區資料不得為空");
		}
		dao.insert(tripAreaVO);
		return tripAreaVO;
	}

	public TripAreaVO updateTripArea(TripAreaVO tripAreaVO) {
		if (tripAreaVO == null || tripAreaVO.getTriplocationid() == null) {
			throw new IllegalArgumentException("更新時必須提供行程地區 ID");
		}
		dao.update(tripAreaVO);
		return tripAreaVO;
	}

	public List<TripAreaVO> getAll() {
		return dao.getAll();
	}

	public List<Map<String, Object>> getTripBytripArea(String regioncontent){
		// 1. 查詢跟地區相關的 TripAreaVO
        List<TripAreaVO> list = dao.getTripBytripArea(regioncontent);

        // 2. 用來存放要回傳給前端的一筆筆 Map
        List<Map<String, Object>> resultList = new ArrayList<>();

        for (TripAreaVO areaVo : list) {
            TripVO trip = areaVo.getTripid();
            if (trip == null) continue;

            // 載入作者
            if (trip.getMembervo() == null) {
                MemberVO member = memberDao.findByPrimaryKey(trip.getMemberId());
                trip.setMembervo(member);
            }

            List<Map<String, Object>> coverPhotos = tripphotoService.getOneTripPhotoByTypeAsBase64(trip.getTrip_id(), 0);
            String base64Photo = null;
            if (!coverPhotos.isEmpty()) {
                // 取第一張
                base64Photo = (String) coverPhotos.get(0).get("photo"); 
            }

            // 3. 組裝 Map
            Map<String, Object> mapData = new HashMap<>();
            mapData.put("trip_id", trip.getTrip_id());
            mapData.put("article_title", trip.getArticle_title());
            mapData.put("trip_abstract", trip.getTrip_abstract());
            mapData.put("visitors_number", trip.getVisitors_number());
            mapData.put("likes", trip.getLikes());
            mapData.put("regionContent", areaVo.getRegioncontent()); // 地區資訊
            mapData.put("member_nickName", trip.getMembervo().getNickName());

            // 放入 base64 照片
            mapData.put("coverImageUrl", base64Photo);

            resultList.add(mapData);
        }

        return resultList;
	}

	public TripphotoVO findCoverPhotoByTripId(Integer tripId) {
		// 呼叫 DAO 的方法，獲取所有照片類型為封面照片 (假設 photo_type = 0 是封面)
		List<Map<String, Object>> photos = photoDao.getOneTripPhotoByType(tripId, 0); // 0 表示封面照片

		if (!photos.isEmpty()) {
			// 若有封面照片，取第一張作為封面
			Map<String, Object> photoData = photos.get(0);

			// 將結果轉換為 TripphotoVO 物件
			TripphotoVO tripphotoVO = new TripphotoVO();
			tripphotoVO.setTrip_photo_id((Integer) photoData.get("trip_photo_id"));
			tripphotoVO.setTrip_id((Integer) photoData.get("trip_id"));
			tripphotoVO.setPhoto((byte[]) photoData.get("photo"));
			tripphotoVO.setPhoto_type((Integer) photoData.get("photo_type"));

			return tripphotoVO; // 返回封面照片
		}

		// 若沒有找到封面照片，則返回 null
		return null;
	}

	public void deleteTripArea(Integer triplocationid) {
		if (triplocationid == null) {
			throw new IllegalArgumentException("刪除行程地區時，必須提供 ID");
		}
		dao.delete(triplocationid);
	}

	public void addTripAreaToTrip(Integer tripId, String regionContent) {
		if (tripId == null || regionContent == null) {
			throw new IllegalArgumentException("行程 ID 或地區名稱不得為空");
		}
		TripVO tripVO = tripdao.getById(tripId);
		if (tripVO == null) {
			throw new IllegalArgumentException("無法找到指定行程 ID 的資料");
		}

		TripAreaVO tripAreaVO = new TripAreaVO();
		tripAreaVO.setTripid(tripVO);
		tripAreaVO.setRegioncontent(regionContent);

		dao.insert(tripAreaVO);
	}

	public void removeTripAreaFromTrip(Integer tripId, String regionContent) {
		if (tripId == null || regionContent == null || regionContent.trim().isEmpty()) {
			throw new IllegalArgumentException("行程 ID 或地區名稱不得為空");
		}
		try {
			// 從 TripDAO 層取得 TripVO 物件
			TripVO tripVO = tripdao.getById(tripId);
			if (tripVO == null) {
				throw new IllegalArgumentException("無法找到指定行程 ID 的資料");
			}

			dao.removeTripAreaFromTrip(tripVO, regionContent);
		} catch (Exception e) {
			throw new RuntimeException("無法刪除行程標註地區: " + e.getMessage());
		}
	}

	public void updateTripArea(Integer tripId, String oldRegionContent, String newRegionContent) {
		if (tripId == null || oldRegionContent == null || newRegionContent == null) {
			throw new IllegalArgumentException("行程 ID 和地區標註內容不得為空");
		}
		dao.updateTripArea(tripId, oldRegionContent, newRegionContent);
	}

}
