package chilltrip.trip.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import chilltrip.sub_trip.dao.SubtripDAO;
import chilltrip.sub_trip.model.SubtripVO;
import chilltrip.trip.repository.TripRepository;

@Service
public class TripService {

	private TripDAO dao;
	private TripRepository tripRepository;
	private SubtripDAO subTripDao;

	@Autowired
	public TripService(TripDAO dao, TripRepository tripRepository, SubtripDAO subTripDao) {
		this.dao = dao;
		this.subTripDao = subTripDao;
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

	public List<TripVO> getAllTrip() {
		return dao.getAllTrip();
	}

	public List<Map<String, Object>> getAllTripPro() {
		return dao.getAllTripPro();
	}

	public List<Map<String, Object>> getByMemberId(Integer memberId) {
		return dao.getByMemberId(memberId);
	}

	public void changeTripStatus(TripVO tripVO) {
		dao.changeTripStatus(tripVO);
	}

	public List<Map<String, Object>> getMemberTripByStatus(Integer memberId, Integer status) {
		return dao.getMemberTripByStatus(memberId, status);
	}

	public List<Map<String, Object>> getTripByLocation(String location_name) {
		return dao.getTripByLocation(location_name);
	}

	public TripVO getById(Integer tripid) {
		return dao.getById(tripid);
	}

	public List<Map<String, Object>> getTripByName(String article_title) {
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
			String title = (tripArray[1] == null || ((String) tripArray[1]).trim().isEmpty()) ? "未命名行程"
					: (String) tripArray[1];
			tripMap.put("title", title);

			// 描述判斷
			String description = (tripArray[2] == null || ((String) tripArray[2]).trim().isEmpty()) ? "沒有行程簡介"
					: (String) tripArray[2];
			tripMap.put("description", description);

			tripMap.put("views", tripArray[3]);
			tripMap.put("likes", tripArray[4]);
			tripMap.put("rating", tripArray[5]);

			// 作者判斷
			String author = (tripArray[6] == null || ((String) tripArray[6]).trim().isEmpty()) ? "未知作者"
					: (String) tripArray[6];
			tripMap.put("author", author);

			tripMap.put("image", tripArray[7]);
			tripMap.put("tags", tags);

			System.out.println(tripMap);
			return tripMap;
		});
	}

	@Transactional(readOnly = true)
	public List<Map<String, Object>> getPopularTrips() {
		List<Object[]> popularTrips = tripRepository.findPopularTrips();
		List<Map<String, Object>> result = new ArrayList<>();

		for (Object[] tripArray : popularTrips) {
			// 獲取並處理標籤
			List<Object[]> tagArrays = tripRepository.findTripTags((Integer) tripArray[0]);
			List<String> tags = new ArrayList<>();

			for (Object[] tagArray : tagArrays) {
				String regionTag = (String) tagArray[0];
				String eventTag = (String) tagArray[1];

				if (regionTag != null && !regionTag.trim().isEmpty()) {
					tags.add(regionTag);
				}
				if (eventTag != null && !eventTag.trim().isEmpty()) {
					tags.add(eventTag);
				}
			}

			Map<String, Object> tripMap = new HashMap<>();
			tripMap.put("id", tripArray[0]);

			// 標題判斷
			String title = (tripArray[1] == null || ((String) tripArray[1]).trim().isEmpty()) ? "未命名行程"
					: (String) tripArray[1];
			tripMap.put("title", title);

			// 描述判斷
			String description = (tripArray[2] == null || ((String) tripArray[2]).trim().isEmpty()) ? "沒有行程簡介"
					: (String) tripArray[2];
			tripMap.put("description", description);

			tripMap.put("views", tripArray[3]);
			tripMap.put("likes", tripArray[4]);
			tripMap.put("rating", tripArray[5]);

			// 作者判斷
			String author = (tripArray[6] == null || ((String) tripArray[6]).trim().isEmpty()) ? "未知作者"
					: (String) tripArray[6];
			tripMap.put("author", author);

			tripMap.put("image", tripArray[7]);
			tripMap.put("tags", tags);

			// 可以選擇是否要把熱門度分數也放進 Map
			tripMap.put("popularityScore", tripArray[8]);

			result.add(tripMap);
		}
		return result;
	}

	@Transactional(readOnly = true)
	public Page<Map<String, Object>> getTripsByArea(String regionContent, Pageable pageable) {
		// 使用地區條件查詢
		Page<Object[]> tripsPage = tripRepository.findByRegionContent(regionContent, pageable);

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
			String title = (tripArray[1] == null || ((String) tripArray[1]).trim().isEmpty()) ? "未命名行程"
					: (String) tripArray[1];
			tripMap.put("title", title);

			// 描述判斷
			String description = (tripArray[2] == null || ((String) tripArray[2]).trim().isEmpty()) ? "沒有行程簡介"
					: (String) tripArray[2];
			tripMap.put("description", description);

			tripMap.put("views", tripArray[3]);
			tripMap.put("likes", tripArray[4]);
			tripMap.put("rating", tripArray[5]);

			// 作者判斷
			String author = (tripArray[6] == null || ((String) tripArray[6]).trim().isEmpty()) ? "未知作者"
					: (String) tripArray[6];
			tripMap.put("author", author);

			tripMap.put("image", tripArray[7]);
			tripMap.put("tags", tags);

			System.out.println(tripMap);
			return tripMap;
		});
	}

	@Transactional(readOnly = true)
	public Page<Map<String, Object>> getTripsByActivity(String eventContent, Pageable pageable) {
		// 使用活動類型條件查詢
		Page<Object[]> tripsPage = tripRepository.findByEventContent(eventContent, pageable);

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
			String title = (tripArray[1] == null || ((String) tripArray[1]).trim().isEmpty()) ? "未命名行程"
					: (String) tripArray[1];
			tripMap.put("title", title);

			// 描述判斷
			String description = (tripArray[2] == null || ((String) tripArray[2]).trim().isEmpty()) ? "沒有行程簡介"
					: (String) tripArray[2];
			tripMap.put("description", description);

			tripMap.put("views", tripArray[3]);
			tripMap.put("likes", tripArray[4]);
			tripMap.put("rating", tripArray[5]);

			// 作者判斷
			String author = (tripArray[6] == null || ((String) tripArray[6]).trim().isEmpty()) ? "未知作者"
					: (String) tripArray[6];
			tripMap.put("author", author);

			tripMap.put("image", tripArray[7]);
			tripMap.put("tags", tags);

			System.out.println(tripMap);
			return tripMap;
		});
	}

	@Transactional(readOnly = true)
	public Page<Map<String, Object>> getTripsByInputText(String keyword, Pageable pageable) {
		// 判斷是否有搜尋關鍵字
		Page<Object[]> tripsPage = (keyword != null && !keyword.trim().isEmpty())
				? tripRepository.findAllTripsWithDetailsByKeyword(keyword, pageable)
				: tripRepository.findAllTripsWithDetails(pageable);

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
			String title = (tripArray[1] == null || ((String) tripArray[1]).trim().isEmpty()) ? "未命名行程"
					: (String) tripArray[1];
			tripMap.put("title", title);

			// 描述判斷
			String description = (tripArray[2] == null || ((String) tripArray[2]).trim().isEmpty()) ? "沒有行程簡介"
					: (String) tripArray[2];
			tripMap.put("description", description);

			tripMap.put("views", tripArray[3]);
			tripMap.put("likes", tripArray[4]);
			tripMap.put("rating", tripArray[5]);

			// 作者判斷
			String author = (tripArray[6] == null || ((String) tripArray[6]).trim().isEmpty()) ? "未知作者"
					: (String) tripArray[6];
			tripMap.put("author", author);

			tripMap.put("image", tripArray[7]);
			tripMap.put("tags", tags);

			return tripMap;
		});
	}

	// 三個參數的查詢條件（go行程：地區、活動、搜索框）
	@Transactional(readOnly = true)
	public Page<Map<String, Object>> searchTrips(String eventContent, String regionContent, String keyword,
			Pageable pageable) {
		
		// 使用新的統一查詢方法
		Page<Object[]> tripsPage = tripRepository.searchTrips(eventContent, regionContent, keyword, pageable);

		return tripsPage.map(this::convertToTripMap);
	}

	// 將重複的轉換邏輯抽取成私有方法
	private Map<String, Object> convertToTripMap(Object[] tripArray) {
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
		String title = (tripArray[1] == null || ((String) tripArray[1]).trim().isEmpty()) ? "未命名行程"
				: (String) tripArray[1];
		tripMap.put("title", title);

		// 描述判斷
		String description = (tripArray[2] == null || ((String) tripArray[2]).trim().isEmpty()) ? "沒有行程簡介"
				: (String) tripArray[2];
		tripMap.put("description", description);

		tripMap.put("views", tripArray[3]);
		tripMap.put("likes", tripArray[4]);
		tripMap.put("rating", tripArray[5]);

		// 作者判斷
		String author = (tripArray[6] == null || ((String) tripArray[6]).trim().isEmpty()) ? "未知作者"
				: (String) tripArray[6];
		tripMap.put("author", author);

		tripMap.put("image", tripArray[7]);
		tripMap.put("tags", tags);

		return tripMap;
	}

	// 獲取用戶的收藏清單（狀態為0的trip）
	public List<Map<String, Object>> getUserLocationCollections(Integer memberId) {
		return dao.getMemberTripByStatus(memberId, chilltrip.trip.model.TripVO.COLLECTION_LOCATION);
	}

	// 獲取用戶的收藏文章清單
	public List<Map<String, Object>> getUserArticleCollections(Integer memberId) {
		return dao.getMemberTripByStatus(memberId, chilltrip.trip.model.TripVO.COLLECTION_ARTICLE);
	}

	// 獲取用戶的草稿文章
	public List<Map<String, Object>> getUserDrafts(Integer memberId) {
		return dao.getMemberTripByStatus(memberId, chilltrip.trip.model.TripVO.DRAFT_ARTICLE);
	}

	// 獲取用戶的公開文章
	public List<Map<String, Object>> getUserPublicArticles(Integer memberId) {
		return dao.getMemberTripByStatus(memberId, chilltrip.trip.model.TripVO.PUBLIC_ARTICLE);
	}

	// 獲取用戶的私人文章
	public List<Map<String, Object>> getUserPrivateArticles(Integer memberId) {
		return dao.getMemberTripByStatus(memberId, chilltrip.trip.model.TripVO.PRIVATE_ARTICLE);
	}

	// 建立收藏景點清單
	@Transactional
	public TripVO createNewCollection(Integer memberId, String collectionName) {
		TripVO tripVO = new TripVO();
		// 設置必要欄位
		tripVO.setMemberId(memberId);
		tripVO.setArticle_title(collectionName);
		tripVO.setStatus(TripVO.COLLECTION_LOCATION); // 設為收藏景點清單
		tripVO.setCreate_time(new Timestamp(System.currentTimeMillis()));

		// 設置預設值
		tripVO.setTrip_abstract(null); // 收藏清單不需要概述
		tripVO.setCollections(0); // 初始收藏數為0
		tripVO.setOverall_score(0); // 初始評分為0
		tripVO.setOverall_scored_people(0); // 初始評分人數為0
		tripVO.setLocation_number(0); // 初始景點數為0
		tripVO.setVisitors_number(0); // 初始瀏覽人數為0
		tripVO.setLikes(0); // 初始點讚數為0

		return addTrip(tripVO); // 使用既有的 addTrip 方法
	}

	// 獲取新的 index
	public int getNewIndex(Integer tripId) throws Exception {
		// 獲取該行程的所有子行程
		List<Map<String, Object>> existingSubTrips = subTripDao.getByTripId(tripId);

		// 計算新的 index
		if (existingSubTrips.isEmpty()) {
			return 0; // 如果是第一個子行程，回傳 0
		} else {
			// 找出目前最大的 index
			int maxIndex = -1;
			for (Map<String, Object> subTrip : existingSubTrips) {
				int index = (Integer) subTrip.get("index");
				if (index > maxIndex) {
					maxIndex = index;
				}
			}
			return maxIndex + 1; // 回傳下一個應該使用的 index
		}
	}

	// 將景點加入收藏
	public void addLocationToTrip(Integer tripId, Integer locationId) throws Exception {
		// 1. 建立子行程
		int newIndex = getNewIndex(tripId);
		SubtripVO subtripVO = new SubtripVO();
		subtripVO.setTripid(tripId);
		subtripVO.setIndex(newIndex);
		subtripVO.setContent(null);

		// 2. 更新行程的景點數量
		updateTripLocationCount(tripId);
	}

	// 更新景點數量
	private void updateTripLocationCount(Integer tripId) {
		TripVO tripVO = dao.getById(tripId);
		if (tripVO != null) {
			// 獲取該行程的所有子行程數量
			List<Map<String, Object>> subTrips = subTripDao.getByTripId(tripId);
			tripVO.setLocation_number(subTrips.size());
			dao.update(tripVO);
		}
	}

}
