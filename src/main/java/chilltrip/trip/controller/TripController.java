package chilltrip.trip.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import chilltrip.trip.dto.TripSearchDTO;
import chilltrip.trip.model.TripService;

@RestController
@RequestMapping("/trip")
public class TripController {

	@Autowired
	private TripService tripSvc;

	@GetMapping("/allbase")
	public ResponseEntity<List<Map<String, Object>>> getAllTrips() {
		try {
			List<Map<String, Object>> tripList = tripSvc.getAllTripPro();

			if (tripList == null || tripList.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}

			return ResponseEntity.ok(tripList);

		} catch (Exception e) {
			System.out.println("無法取得所有行程資料: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping("/all")
	public ResponseEntity<Map<String, Object>> getAllTrips(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "12") int size) {

		try {
			Pageable pageable = PageRequest.of(page, size);
			Page<Map<String, Object>> pageResult = tripSvc.getTrips(pageable);

			Map<String, Object> response = new HashMap<>();
			response.put("content", pageResult.getContent());
			response.put("totalPages", pageResult.getTotalPages());
			response.put("totalElements", pageResult.getTotalElements());
			response.put("currentPage", page);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace(); // 在控制台打印詳細錯誤
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("error", e.getClass().getName());
			errorResponse.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// 首頁熱門文章 API
	@GetMapping("/popular")
	public ResponseEntity<Map<String, Object>> getPopularTrips() {
		try {
			List<Map<String, Object>> popularTrips = tripSvc.getPopularTrips();

			Map<String, Object> response = new HashMap<>();
			response.put("content", popularTrips);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace(); // 在控制台打印詳細錯誤
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("error", e.getClass().getName());
			errorResponse.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// 根據活動查詢文章（多表查詢且要分頁）
	@GetMapping("/activityfilter")
	public ResponseEntity<Map<String, Object>> getTripsByEvent(@RequestParam String eventContent,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "12") int size) {

		try {
			// 建立分頁請求
			Pageable pageable = PageRequest.of(page, size);
			// 調用 Service 層的地區查詢方法
			Page<Map<String, Object>> pageResult = tripSvc.getTripsByActivity(eventContent, pageable);

			// 構建回應
			Map<String, Object> response = new HashMap<>();
			response.put("content", pageResult.getContent());
			response.put("totalPages", pageResult.getTotalPages());
			response.put("totalElements", pageResult.getTotalElements());
			response.put("currentPage", page);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("error", e.getClass().getName());
			errorResponse.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// 根據地區查詢文章（多表查詢且要分頁）
	@GetMapping("/areafilter")
	public ResponseEntity<Map<String, Object>> getTripsByArea(@RequestParam String regionContent,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "12") int size) {

		try {
			// 建立分頁請求
			Pageable pageable = PageRequest.of(page, size);
			// 調用 Service 層的地區查詢方法
			Page<Map<String, Object>> pageResult = tripSvc.getTripsByArea(regionContent, pageable);

			// 構建回應
			Map<String, Object> response = new HashMap<>();
			response.put("content", pageResult.getContent());
			response.put("totalPages", pageResult.getTotalPages());
			response.put("totalElements", pageResult.getTotalElements());
			response.put("currentPage", page);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("error", e.getClass().getName());
			errorResponse.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}
	
	//輸入框模糊查詢：作者名稱、文章標題名稱、文章摘要字詞
	@GetMapping("/trips/search")
	public ResponseEntity<Map<String, Object>> searchTrips(
	    @RequestParam String keyword,
	    @RequestParam(defaultValue = "0") int page,
	    @RequestParam(defaultValue = "12") int size
	) {
	    try {
	        Pageable pageable = PageRequest.of(page, size);
	        Page<Map<String, Object>> pageResult = tripSvc.getTripsByInputText(keyword, pageable);

	        Map<String, Object> response = new HashMap<>();
	        response.put("content", pageResult.getContent());
	        response.put("totalPages", pageResult.getTotalPages());
	        response.put("totalElements", pageResult.getTotalElements());
	        response.put("currentPage", page);

	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        e.printStackTrace();
	        Map<String, Object> errorResponse = new HashMap<>();
	        errorResponse.put("error", e.getClass().getName());
	        errorResponse.put("message", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	    }
	}
	
	//search pro --> go頁面的三個參數的複合查詢
	//複雜的請求跟查詢用post --> 其實是因為我把請求標頭搞爆了QQ
	@PostMapping("/search")
	public ResponseEntity<Map<String, Object>> searchTrips(@RequestBody TripSearchDTO searchDTO) {
	    try {
	        Pageable pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize());
	        Page<Map<String, Object>> pageResult = tripSvc.searchTrips(
	            searchDTO.getEventContent(),
	            searchDTO.getRegionContent(),
	            searchDTO.getKeyword(),
	            pageable
	        );

	        Map<String, Object> response = new HashMap<>();
	        response.put("content", pageResult.getContent());
	        response.put("totalPages", pageResult.getTotalPages());
	        response.put("totalElements", pageResult.getTotalElements());
	        response.put("currentPage", searchDTO.getPage());

	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        e.printStackTrace();
	        Map<String, Object> errorResponse = new HashMap<>();
	        errorResponse.put("error", e.getClass().getName());
	        errorResponse.put("message", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                           .body(errorResponse);
	    }
	}
	


}
