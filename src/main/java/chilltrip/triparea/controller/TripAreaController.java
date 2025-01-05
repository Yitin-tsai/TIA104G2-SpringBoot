package chilltrip.triparea.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import chilltrip.triparea.model.TripAreaService;
import chilltrip.triparea.model.TripAreaVO;

@RestController
@RequestMapping("/triparea")
public class TripAreaController{

	@Autowired
	private TripAreaService tripAreaSvc;

	// 查詢行程地區的行程
	@GetMapping("/search")
    public ResponseEntity<List<Map<String,Object>>> getTripByTripArea(@RequestParam String regionContent) {
        List<Map<String,Object>> resultList = tripAreaSvc.getTripBytripArea(regionContent);
        if (resultList == null || resultList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(resultList);
    }

	// 新增地區標註到行程
    @PostMapping("/add")
    public ResponseEntity<String> addTripAreaToTrip(@RequestParam Integer tripId, @RequestParam String regionContent) {
    	System.out.println("新增行程標籤 - 行程 ID: " + tripId + ", 地區: " + regionContent);

        try {
        	tripAreaSvc.addTripAreaToTrip(tripId, regionContent);
            System.out.println("新增成功！");
            return ResponseEntity.ok("行程地區標註新增成功！");
        } catch (Exception e) {
            System.out.println("新增行程標註失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("新增行程地區標註失敗：" + e.getMessage());
        }
    }
    
    // 更新行程的地區標註
    @PutMapping("/update")
    public ResponseEntity<String> updateTripArea(@RequestParam Integer tripId, @RequestParam String oldRegionContent, @RequestParam String newRegionContent) {
    	
    	System.out.println("修改行程標籤 - 行程 ID: " + tripId + ", 舊地區: " + oldRegionContent + ", 新地區: " + newRegionContent);

        try {
        	tripAreaSvc.updateTripArea(tripId, oldRegionContent, newRegionContent);
            System.out.println("更新成功！");
            return ResponseEntity.ok("行程地區標註更新成功！");
        } catch (Exception e) {
            System.out.println("更新行程標註失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("更新行程地區標註失敗：" + e.getMessage());
        }
    }

    // 移除行程中的行程地區標籤
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeTripAreaFromTrip(@RequestParam Integer tripId, @RequestParam String regionContent) {
        try {
            System.out.println("刪除行程標籤 - 行程 ID: " + tripId + ", 地區: " + regionContent);
            tripAreaSvc.removeTripAreaFromTrip(tripId, regionContent);
            return ResponseEntity.ok("行程地區標註刪除成功！");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("刪除失敗：" + e.getMessage());
        }
    }

    // 顯示所有行程地區
    @GetMapping("/all")
    public ResponseEntity<List<String>> getAllTripAreas() {
    	try {
            List<TripAreaVO> allTripAreas = tripAreaSvc.getAll();
            Set<String> uniqueAreas = allTripAreas.stream()
                .map(TripAreaVO::getRegioncontent)
                .collect(Collectors.toSet());
            System.out.println("所有不重復的行程地區：" + uniqueAreas);

            return ResponseEntity.ok(new ArrayList<>(uniqueAreas));
        } catch (Exception e) {
            System.out.println("無法取得所有行程地區資料：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
