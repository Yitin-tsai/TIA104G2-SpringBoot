package chilltrip.triparea.model;

import java.util.List;

import chilltrip.trip.dao.TripDAO;
import chilltrip.trip.dao.TripDAOImplJDBC;
import chilltrip.trip.model.TripVO;

public class TripAreaService {

	private TripAreaDAO_interface dao;
	private TripDAO tripdao;  // TripDAO_interface 的參數

	public TripAreaService() {
		dao = new TripAreaJDBCDAO();
		tripdao = new TripDAOImplJDBC();
	}

	public TripAreaVO addTripArea(TripAreaVO tripAreaVO) {
		dao.insert(tripAreaVO);
		return tripAreaVO;
	}

	public TripAreaVO updateTripArea(TripAreaVO tripAreaVO) {
		dao.update(tripAreaVO);
		return tripAreaVO;
	}

	public List<TripAreaVO> getAll() {
		return dao.getAll();
	}

	public List<TripAreaVO> getTripBytripArea(String regioncontent) {
		return dao.getTripBytripArea(regioncontent);
	}

	public void deleteTripArea(Integer triplocationid) {
		dao.delete(triplocationid);
	}

	public void addTripAreaToTrip(Integer tripId, String regionContent) {
		try {
			// 從TripDAO 層取得 TripVO 物件
			TripVO tripVO = tripdao.getById(tripId);

			TripAreaVO tripAreaVO = new TripAreaVO();
			tripAreaVO.setTripid(tripVO); // 設定關聯的 TripVO
			tripAreaVO.setRegioncontent(regionContent); // 設定地區標註

			dao.insert(tripAreaVO);
		} catch (Exception e) {
			throw new RuntimeException("無法新增行程標註地區: " + e.getMessage());
		}
	}

	public void removeTripAreaFromTrip(Integer tripId, String regionContent) {
		try {
			// 從TripDAO 層取得 TripVO 物件
			TripVO tripVO = tripdao.getById(tripId);
			
			dao.removeTripAreaFromTrip(tripVO, regionContent);
		}catch(Exception e) {
			throw new RuntimeException("無法刪除行程標註地區: " + e.getMessage());
		}
	}
	
	public void updateTripArea(Integer tripId, String oldRegionContent, String newRegionContent) {
		try {
			dao.updateTripArea(tripId, oldRegionContent, newRegionContent);
		}catch(Exception e) {
			throw new RuntimeException("無法更新行程標註地區: " + e.getMessage());
		}
	}

}
