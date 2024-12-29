package chilltrip.tripactyperela.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import chilltrip.trip.model.TripService;
import chilltrip.tripactyperela.model.TripactyperelaService;
import chilltrip.tripactyperela.model.TripactyperelaVO;

@RestController
@RequestMapping("/tripactyperela")
public class TripactyperelaController {

	@Autowired
	private TripactyperelaService tripactyperelaSvc;
	@Autowired
	private TripService tripSvc;

	// 新增行程的同時也新增行程活動類型並建立關聯
	@PostMapping("/addTripWithEventTypes")
    public ResponseEntity<String> addTripWithEventTypes(@RequestParam Integer tripId, 
                                                        @RequestParam List<Integer> eventTypeIds) {
        System.out.println("開始新增...");
        System.out.println("tripId為:" + tripId);
        System.out.println("eventTypeIds為:" + eventTypeIds);

        if (eventTypeIds == null || eventTypeIds.isEmpty()) {
            return ResponseEntity.badRequest().body("至少選擇一個活動類型");
        }

        // 創建行程和活動類型的關聯
        boolean relationResult = tripactyperelaSvc.addRelations(tripId, eventTypeIds);
        if (relationResult) {
            return ResponseEntity.ok("行程和活動類型關聯成功");
        } else {
            // 如果關聯創建失敗，回滾行程的創建操作
            tripSvc.deleteTrip(tripId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("行程和活動類型關聯失敗，已回滾行程創建");
        }
    }

	// 新增關聯資料 (將活動類型與行程關聯)
    @PostMapping("/addRelation")
    public ResponseEntity<String> addRelation(@RequestParam Integer tripId, 
                                              @RequestParam List<Integer> eventTypeIds) {
        System.out.println("tripId: " + tripId);
        if (eventTypeIds == null || eventTypeIds.isEmpty()) {
            return ResponseEntity.badRequest().body("未選擇任何活動類型");
        }

        System.out.println("eventTypeIds: " + eventTypeIds);

        boolean result = tripactyperelaSvc.addRelations(tripId, eventTypeIds);
        System.out.println("新增結果: " + result);

        return result ? ResponseEntity.ok("新增成功") : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("新增失敗");
    }

    // 刪除行程與活動類型的關聯
    @PostMapping("/deleteRelation")  
    public ResponseEntity<String> deleteRelation(@RequestParam Integer tripId, 
                                                 @RequestParam Integer eventTypeId) {
        System.out.println("刪除的tripId: " + tripId);
        System.out.println("刪除的eventTypeId: " + eventTypeId);

        boolean result = tripactyperelaSvc.deleteRelationByTripAndEventType(tripId, eventTypeId);
        System.out.println("刪除結果: " + result);

        return result ? ResponseEntity.ok("刪除成功") : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("刪除失敗");
    }

    // 更新行程與活動類型的關聯
    @PostMapping("/updateRelation")
    public ResponseEntity<String> updateRelation(@RequestParam Integer tripId, 
                                                 @RequestParam List<Integer> eventTypeIds) {
        System.out.println("tripId: " + tripId);

        if (eventTypeIds == null || eventTypeIds.isEmpty()) {
            return ResponseEntity.badRequest().body("未選擇任何活動類型");
        }

        System.out.println("eventTypeIds: " + eventTypeIds);

        boolean result = tripactyperelaSvc.updateTripactyperela(tripId, eventTypeIds);
        System.out.println("更新結果: " + result);

        return result ? ResponseEntity.ok("更新成功") : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新失敗");
    }

    // 查詢某個行程的所有關聯
    @GetMapping("/findRelationsByTripId")
    public ResponseEntity<List<TripactyperelaVO>> findRelationsByTripId(@RequestParam Integer tripId) {
        System.out.println("查詢的行程id: " + tripId);

        List<TripactyperelaVO> relations = tripactyperelaSvc.findAllRelationsByTripId(tripId);
        relations.forEach(tripactyperelaVO -> {
            System.out.println("關聯 ID: " + tripactyperelaVO.getRelaid() + ", 行程 ID: "
                    + tripactyperelaVO.getTripid().getTrip_id() + ", 活動類型 ID: "
                    + tripactyperelaVO.getEventtypeid().getEventtypeid());
        });

        return ResponseEntity.ok(relations);
    }
}