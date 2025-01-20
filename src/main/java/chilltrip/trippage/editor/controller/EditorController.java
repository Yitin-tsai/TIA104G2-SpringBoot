package chilltrip.trippage.editor.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import chilltrip.location.model.LocationService;
import chilltrip.location.model.LocationVO;
import chilltrip.trip.model.TripService;
import chilltrip.trippage.editor.dto.DaySchedule;
import chilltrip.trippage.editor.dto.LocationDetails;
import chilltrip.trippage.editor.dto.TripCreateRequest;
import chilltrip.trippage.editor.service.TripEditorService;

@RestController
@RequestMapping("/editor")
public class EditorController {

	@Autowired
	private TripService tripSvc;
	@Autowired
	private LocationService locationSvc;
	@Autowired
	private TripEditorService tripEditorService;

	// 載入景點清單以及渲染景點列表
	// 獲取用戶的收藏清單
	@GetMapping("/user/{memberId}")
	public ResponseEntity<List<Map<String, Object>>> getUserCollections(@PathVariable Integer memberId,
			HttpSession session) {

		// 驗證當前登入用戶是否有權限訪問這個memberId的資料
		Integer currentMemberId = (Integer) session.getAttribute("memberId");
		if (currentMemberId == null || !currentMemberId.equals(memberId)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		List<Map<String, Object>> collections = tripSvc.getUserLocationCollections(memberId);
		return ResponseEntity.ok(collections);
	}

	// 獲取特定收藏清單中的景點
	@GetMapping("/{tripId}/locations")
	public ResponseEntity<Map<String, Object>> getTripLocations(@PathVariable Integer tripId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			HttpSession session) {

		Integer memberId = (Integer) session.getAttribute("memberId");
		if (memberId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		try {
			System.out.println("Controller - 開始處理請求");
			System.out.println("Controller - 參數: tripId=" + tripId + ", memberId=" + memberId + ", page=" + page
					+ ", size=" + size);

			Page<Map<String, Object>> locationsPage = locationSvc.getLocationDetailsByTripAndMember(memberId, tripId,
					page, size);

			System.out.println("Controller - 查詢完成，開始處理回應");

			if (locationsPage == null) {
				System.out.println("Controller - 警告: locationsPage 為 null");
				Map<String, Object> emptyResponse = new HashMap<>();
				emptyResponse.put("content", new ArrayList<>());
				emptyResponse.put("currentPage", page);
				emptyResponse.put("totalItems", 0L);
				emptyResponse.put("totalPages", 0);
				return ResponseEntity.ok(emptyResponse);
			}

			// 確保每個位置資料都包含 id
			List<Map<String, Object>> enhancedContent = locationsPage.getContent().stream().map(location -> {
				Map<String, Object> enhancedLocation = new HashMap<>(location);
				// 確保 id 欄位存在，如果原本的 map 中叫做 location_id 或其他名稱，在這裡做轉換
				if (!enhancedLocation.containsKey("id") && enhancedLocation.containsKey("location_id")) {
					enhancedLocation.put("id", enhancedLocation.get("location_id"));
				}
				// 確保必要的欄位都存在
				if (!enhancedLocation.containsKey("name")) {
					enhancedLocation.put("name", "未命名位置");
				}
				if (!enhancedLocation.containsKey("address")) {
					enhancedLocation.put("address", "地址未提供");
				}
				if (!enhancedLocation.containsKey("rating")) {
					enhancedLocation.put("rating", 0.0);
				}
				if (!enhancedLocation.containsKey("reviewCount")) {
					enhancedLocation.put("reviewCount", 0);
				}
				return enhancedLocation;
			}).collect(Collectors.toList());

			System.out.println("Controller - 查詢結果統計:");
			System.out.println("Controller - 總記錄數: " + locationsPage.getTotalElements());
			System.out.println("Controller - 總頁數: " + locationsPage.getTotalPages());
			System.out.println("Controller - 當前頁內容數: " + enhancedContent.size());

			Map<String, Object> response = new HashMap<>();
			response.put("content", enhancedContent); // 使用增強後的內容
			response.put("currentPage", locationsPage.getNumber());
			response.put("totalItems", locationsPage.getTotalElements());
			response.put("totalPages", locationsPage.getTotalPages());

			System.out.println("Controller - 準備返回結果");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			System.err.println("Controller - 發生錯誤: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// 將地圖景點加入行程後的判斷-->建立過的要更新、沒建立過的要建立
	@PostMapping("/location/check")
	@ResponseBody
	public ResponseEntity<?> checkLocation(@RequestBody Map<String, Object> locationData) {
		try {
			String googlePlaceId = (String) locationData.get("googlePlaceId");

			// 先查詢是否存在
			LocationVO existingLocation = locationSvc.findByGooglePlaceId(googlePlaceId);
			System.out.println("檢查現有地點: " + existingLocation);

			LocationVO resultLocation;
			if (existingLocation != null) {
				// 如果存在，更新資料
				existingLocation.setLocation_name((String) locationData.get("locationName"));
				existingLocation.setAddress((String) locationData.get("address"));
				existingLocation.setLatitude(new BigDecimal(locationData.get("latitude").toString()));
				existingLocation.setLongitude(new BigDecimal(locationData.get("longitude").toString()));
				existingLocation.setUpdateTime(new Timestamp(System.currentTimeMillis()));

				// 更新
				resultLocation = locationSvc.updatelocation(existingLocation);
			} else {
				// 如果不存在，創建新景點
				LocationVO newLocation = new LocationVO();
				newLocation.setGooglePlaceId(googlePlaceId);
				newLocation.setLocation_name((String) locationData.get("locationName"));
				newLocation.setAddress((String) locationData.get("address"));
				newLocation.setLatitude(new BigDecimal(locationData.get("latitude").toString()));
				newLocation.setLongitude(new BigDecimal(locationData.get("longitude").toString()));
				// 設置預設值
				newLocation.setScore(0.0f);
				newLocation.setRatingCount(0);
				newLocation.setComments_number(0);
				newLocation.setCreate_time(new Timestamp(System.currentTimeMillis()));

				// 儲存
				resultLocation = locationSvc.addlocationAndGetPk(newLocation);
			}

			// 驗證是否成功獲取 ID
			if (resultLocation.getLocationId() == null) {
				throw new RuntimeException("無法獲取新建立的位置ID");
			}

			// 確保在建立 response 之前已經有了有效的 ID
			if (resultLocation.getLocationId() == null) {
				throw new RuntimeException("位置ID未正確生成");
			}

			// 將 LocationVO 轉換為前端需要的格式
			Map<String, Object> response = new HashMap<>();
			response.put("id", resultLocation.getLocationId()); // 確保包含 ID
			response.put("googlePlaceId", resultLocation.getGooglePlaceId());
			response.put("name", resultLocation.getLocation_name());
			response.put("address", resultLocation.getAddress());
			response.put("latitude", resultLocation.getLatitude());
			response.put("longitude", resultLocation.getLongitude());
			response.put("rating", resultLocation.getScore());
			response.put("reviewCount", resultLocation.getRatingCount());

			System.out.println("resultLocation: " + resultLocation);
			System.out.println("locationId: " + resultLocation.getLocationId());
			System.out.println("正在返回位置資料: " + response);
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			System.err.println("處理景點資料時發生錯誤: " + e.getMessage());
			e.printStackTrace();
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("error", "處理景點資料時發生錯誤: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		
		}
	}

	@PostMapping("/publish")
	public ResponseEntity<?> publishTrip(@RequestBody Map<String, Object> rawRequest) {
		try {
			// 創建 TripCreateRequest 對象
			TripCreateRequest request = new TripCreateRequest();

			// 處理圖片數據
			String base64Photo = (String) rawRequest.get("coverPhoto");
			if (base64Photo != null && !base64Photo.isEmpty()) {
				// 移除 MIME 類型前綴
				if (base64Photo.contains(",")) {
					base64Photo = base64Photo.split(",")[1];
				}
				request.setCoverPhoto(Base64.getDecoder().decode(base64Photo));
			}

			// 設置其他屬性
			request.setMemberId((Integer) rawRequest.get("memberId"));
			request.setArticleTitle((String) rawRequest.get("articleTitle"));
			request.setAbstractContent((String) rawRequest.get("abstractContent"));
			request.setStatus((Integer) rawRequest.get("status"));
			request.setActivityType((String) rawRequest.get("activityType"));
			request.setRegion((String) rawRequest.get("region"));

			// 處理 daySchedules
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> daySchedulesRaw = (List<Map<String, Object>>) rawRequest.get("daySchedules");
			if (daySchedulesRaw != null) {
				List<DaySchedule> daySchedules = convertToDaySchedules(daySchedulesRaw);
				request.setDaySchedules(daySchedules);
			}

			// 呼叫 service 處理
			Integer tripId = tripEditorService.createTrip(request);

			// 回傳成功響應
			Map<String, Object> response = new HashMap<>();
			response.put("status", "success");
			response.put("tripId", tripId);
			response.put("redirectUrl", "/trip/" + tripId);

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			// 錯誤處理
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("error", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// 轉換 daySchedules 的輔助方法
	private List<DaySchedule> convertToDaySchedules(List<Map<String, Object>> rawSchedules) {
		List<DaySchedule> result = new ArrayList<>();
		for (Map<String, Object> raw : rawSchedules) {
			DaySchedule schedule = new DaySchedule();
			schedule.setIndex((Integer) raw.get("index"));
			schedule.setContent((String) raw.get("content"));

			@SuppressWarnings("unchecked")
			List<Map<String, Object>> spotsRaw = (List<Map<String, Object>>) raw.get("spots");
			if (spotsRaw != null) {
				List<LocationDetails> spots = new ArrayList<>();
				for (Map<String, Object> spotRaw : spotsRaw) {
					LocationDetails spot = new LocationDetails();
					spot.setGooglePlaceId((String) spotRaw.get("googlePlaceId"));
					spot.setName((String) spotRaw.get("name"));
					spot.setAddress((String) spotRaw.get("address"));
					// 處理緯度（latitude）
					Object latObj = spotRaw.get("latitude");
					if (latObj != null) {
						if (latObj instanceof Number) {
							spot.setLatitude(new BigDecimal(latObj.toString()));
						} else if (latObj instanceof String) {
							spot.setLatitude(new BigDecimal((String) latObj));
						}
					}

					// 處理經度（longitude）
					Object lngObj = spotRaw.get("longitude");
					if (lngObj != null) {
						if (lngObj instanceof Number) {
							spot.setLongitude(new BigDecimal(lngObj.toString()));
						} else if (lngObj instanceof String) {
							spot.setLongitude(new BigDecimal((String) lngObj));
						}
					}

					// 處理開始時間
					String startTimeStr = (String) spotRaw.get("startTime");
					if (startTimeStr != null) {
						spot.setStartTime(LocalDateTime.parse(startTimeStr));
					}

					// 處理結束時間
					String endTimeStr = (String) spotRaw.get("endTime");
					if (endTimeStr != null) {
						spot.setEndTime(LocalDateTime.parse(endTimeStr));
					}
					spots.add(spot);
				}
				schedule.setSpots(spots);
			}
			result.add(schedule);
		}
		return result;
	}
}
