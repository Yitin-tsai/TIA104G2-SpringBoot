package chilltrip.tripactype.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import chilltrip.trip.model.TripVO;
import chilltrip.tripactype.model.TripactypeService;
import chilltrip.tripactype.model.TripactypeVO;
import chilltrip.tripactyperela.model.TripactyperelaService;

@RestController
@RequestMapping("/tripactype")
public class TripactypeController{

	@Autowired
	private TripactypeService tripactypeSvc;
	
	@Autowired
	private TripactyperelaService tripactyperelaSvc;

	// 和 TripactyperelaController 中的 updateRelation 功能重複
	// 用戶編輯行程時更新與活動類型的關聯
//	@PostMapping("/update")
//    public ResponseEntity<String> updateTripactype(@RequestParam Integer tripId, @RequestParam List<Integer> eventTypeIds) {
//        System.out.println("Received update request for tripId: " + tripId + ", eventTypeIds: " + eventTypeIds);
//        if (eventTypeIds.isEmpty()) {
//            System.out.println("未選擇任何活動類型");
//            return ResponseEntity.badRequest().body("未選擇任何活動類型");
//        }
//
//        boolean result = tripactyperelaSvc.updateTripactyperela(tripId, eventTypeIds);
//        if (result) {
//            System.out.println("行程與活動類型關聯更新成功");
//            return ResponseEntity.ok("行程與活動類型關聯更新成功");
//        } else {
//            System.out.println("行程與活動類型關聯更新失敗");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("行程與活動類型關聯更新失敗");
//        }
//    }

	// 和 TripactyperelaController 中的 deleteRelation 功能重複
	// 刪除行程活動類型
//    @DeleteMapping("/delete")
//    public ResponseEntity<String> deleteTripactype(@RequestParam Integer tripId, @RequestParam Integer eventTypeId) {
//        System.out.println("刪除tripId:" + tripId);
//        System.out.println("刪除eventTypeId:" + eventTypeId);
//
//        boolean result = tripactyperelaSvc.deleteRelationByTripAndEventType(tripId, eventTypeId);
//        return result ? ResponseEntity.ok("刪除成功") : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("刪除失敗");
//    }

	
	// 顯示所有活動類型
    @GetMapping("/all")
    public ResponseEntity<List<String>> getAllTripActypes() {
        try {
            List<TripactypeVO> allTripactypes = tripactypeSvc.findAllTripactypes();
            List<String> typeList = allTripactypes.stream()
                .map(TripactypeVO::getEventcontent)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
            System.out.println("所有行程活動類型已經顯示在頁面上: " + typeList);
            return ResponseEntity.ok(typeList);
        } catch (Exception e) {
            System.out.println("無法取得所有行程活動類型資料: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

	
	// 查詢指定行程活動類型的所有行程
    @GetMapping("/trips")
    public ResponseEntity<List<Map<String, Object>>> getTripsByTripActype(@RequestParam String eventType) {
    	System.out.println(eventType);
        List<Map<String, Object>> tripDataList = tripactypeSvc.findTripsByEventType(eventType);
        if (tripDataList == null || tripDataList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(tripDataList);
    }
}
