package chilltrip.triparea.model;

import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chilltrip.member.model.MemberDAO_interface;
import chilltrip.member.model.MemberJDBCDAO;
import chilltrip.member.model.MemberVO;
import chilltrip.trip.dao.TripDAO;
import chilltrip.trip.dao.TripDAOImplJDBC;
import chilltrip.trip.model.TripVO;

@Service
public class TripAreaService {

	private TripAreaDAO_interface dao;
	private TripDAO tripdao;  // TripDAO_interface 的參數
	private MemberDAO_interface memberDao;
//	private TripPhotoDAO photoDao;

	@Autowired
	public TripAreaService() {
		dao = new TripAreaJDBCDAO();
		tripdao = new TripDAOImplJDBC();
		memberDao = new MemberJDBCDAO();
//		photoDao = new TripPhotoDAOImpl();
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

	public List<TripAreaVO> getTripBytripArea(String regioncontent) {
		List<TripAreaVO> list = dao.getTripBytripArea(regioncontent);

	    for (TripAreaVO areaVo : list) {
	        TripVO trip = areaVo.getTripid();
	        if (trip != null) {
	            // 若 trip 裡沒有載入作者
	            if (trip.getMembervo() == null) {
	                // 依照 trip.getMemberId() 撈 MemberVO
	                MemberVO member = memberDao.findByPrimaryKey(trip.getMemberId());
	                trip.setMembervo(member);
	            }
	            System.out.println("發文者暱稱: " + trip.getMembervo().getNickName());
	            // 取得封面照
//	            TripPhotoVO cover = photoDao.findCoverPhotoByTripId(trip.getTrip_id());
//	            if (cover != null && cover.getPhoto() != null) {
//	                String base64 = Base64.getEncoder().encodeToString(cover.getPhoto());
//	                trip.setCoverPhotoBase64(base64);
//	            }
	        }
	    }
	    return list;
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
