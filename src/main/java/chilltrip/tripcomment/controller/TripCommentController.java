package chilltrip.tripcomment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.List;

import chilltrip.tripcomment.model.TripCommentService;
import chilltrip.tripcomment.model.TripCommentVO;

@RestController
@RequestMapping("/tripcomment")
public class TripCommentController {

	@Autowired
	private TripCommentService tripCommentSvc;

	// 顯示行程的所有留言
	@GetMapping("/getAllCommentsByTripId")
	public ResponseEntity<Object> getAllCommentsByTripId(@RequestParam Integer tripId,
			@SessionAttribute(name = "memberId", required = false) Integer memberId) {
		System.out.println("開始準備顯示...");
		System.out.println("tripId為: " + tripId);
		System.out.println("memberId為: " + memberId);

		if (memberId == null) {
			return ResponseEntity.badRequest().body("請先登入");
		}

		List<TripCommentVO> tripCommentList = tripCommentSvc.getCommentsByTripId(tripId);
		if (tripCommentList == null || tripCommentList.isEmpty()) {
			return ResponseEntity.status(404).body("查無該行程留言");
		}

		return ResponseEntity.ok(tripCommentList);
	}

	// 更新留言
	@PostMapping("/updateComment")
	public ResponseEntity<Object> updateComment(@RequestParam Integer tripCommentId,
			@SessionAttribute(name = "memberId", required = false) Integer memberId, @RequestParam Integer score,
			@RequestParam String content, @RequestParam(required = false) MultipartFile photo) {
		System.out.println("tripCommentId為: " + tripCommentId);

		try {
			if (memberId == null) {
				return ResponseEntity.status(401).body("請先登入");
			}

			TripCommentVO tripCommentVO = tripCommentSvc.getTripComment(tripCommentId);
			if (tripCommentVO == null) {
				return ResponseEntity.status(404).body("該留言不存在");
			} else if (!tripCommentVO.getMemberId().equals(memberId)) {
				return ResponseEntity.status(403).body("您不能修改其他用戶的留言");
			}

			byte[] photoBytes = (photo != null) ? photo.getBytes() : tripCommentVO.getPhoto();
			tripCommentSvc.updateTripComment(tripCommentId, memberId, tripCommentVO.getTripId(), score, photoBytes,
					tripCommentVO.getCreateTime(), content);

			return ResponseEntity.ok("留言更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("更新失敗");
		}
	}

	// 新增留言
	@PostMapping("/addComment")
	public ResponseEntity<Object> addComment(@SessionAttribute(name = "memberId", required = false) Integer memberId,
			@RequestParam Integer tripId, @RequestParam Integer score, @RequestParam String content,
			@RequestParam(required = false) MultipartFile photo) {
		System.out.println("新增行程留言: " + "memberId=" + memberId + ", tripId=" + tripId);

		try {
			if (memberId == null) {
				return ResponseEntity.status(401).body("請先登入");
			}

			byte[] photoBytes = (photo != null) ? photo.getBytes() : null;
			Timestamp createTime = new Timestamp(System.currentTimeMillis());

			TripCommentVO tripCommentVO = tripCommentSvc.addTripComment(memberId, tripId, score, photoBytes, createTime,
					content);

			return ResponseEntity.ok("留言新增成功");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("新增留言失敗");
		}
	}

	// 刪除留言
	@PostMapping("/deleteComment")
	public ResponseEntity<Object> deleteComment(@RequestParam Integer tripCommentId,
			@SessionAttribute(name = "memberId", required = false) Integer memberId) {
		System.out.println("刪除行程留言的tripCommentId: " + tripCommentId);

		try {
			if (memberId == null) {
				return ResponseEntity.status(401).body("請先登入");
			}

			TripCommentVO tripCommentVO = tripCommentSvc.getTripComment(tripCommentId);
			if (tripCommentVO == null) {
				return ResponseEntity.status(404).body("該留言不存在");
			} else if (!tripCommentVO.getMemberId().equals(memberId)) {
				return ResponseEntity.status(403).body("您不能刪除其他用戶的留言");
			}

			tripCommentSvc.deleteTripComment(tripCommentId);
			return ResponseEntity.ok("留言刪除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("刪除留言失敗");
		}
	}
}
