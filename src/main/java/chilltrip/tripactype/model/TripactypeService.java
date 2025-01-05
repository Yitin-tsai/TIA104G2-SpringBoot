package chilltrip.tripactype.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chilltrip.member.model.MemberDAO_interface;
import chilltrip.member.model.MemberJDBCDAO;
import chilltrip.member.model.MemberVO;
import chilltrip.trip.model.TripVO;

@Service
public class TripactypeService {

	private TripactypeDAO_interface dao;
	private MemberDAO_interface memberDao;
//	private TripPhotoDAO tripPhotoDao;

	@Autowired
	public TripactypeService() {
		dao = new TripactypeDAO();
		memberDao = new MemberJDBCDAO();
//		tripPhotoDao = new TripPhotoDAOImpl();
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

	public List<TripVO> findTripsByEventType(String eventType) {
		List<TripVO> trips = dao.getTripsByEventType(eventType);

		for (TripVO trip : trips) {
			// 作者
			if (trip.getMembervo() == null) {
				MemberVO member = memberDao.findByPrimaryKey(trip.getMemberId());
				trip.setMembervo(member);
			}
			System.out.println("發文者暱稱: " + trip.getMembervo().getNickName());

			// 封面照
//	        TripPhotoVO cover = tripPhotoDao.findCoverPhotoByTripId(trip.getTrip_id());
//	        if (cover != null && cover.getPhoto() != null) {
//	            String base64 = Base64.getEncoder().encodeToString(cover.getPhoto());
//	            trip.setCoverPhotoBase64(base64);
//	        }
		}

		return trips;
	}

}
