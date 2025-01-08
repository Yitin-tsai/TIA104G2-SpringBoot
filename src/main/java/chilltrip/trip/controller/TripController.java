package chilltrip.trip.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Object> getPopularTrips() {
        return ResponseEntity.ok(tripSvc.getPopularTrips(12));  // 固定返回12篇
    }

}
