package chilltrip.tripphoto.controller;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import chilltrip.tripphoto.model.TripphotoService;
import chilltrip.tripphoto.model.TripphotoVO;

//@WebServlet("/tripphoto/tripphoto.do")
@MultipartConfig
@RestController
@RequestMapping("/tripphoto")
public class TripphotoController {

	@Autowired
	private TripphotoService tripphotoSvc;

	// 新增
	public ResponseEntity<?> addTripphoto(@RequestParam("trip_id") Integer tripId,
			@RequestParam("photo_type") Integer photoType, @RequestParam("photo") MultipartFile photo) {
		try {
			// 檢查是否有檔案
			if (photo != null && !photo.isEmpty()) {
				// 轉換為 byte array
				byte[] photoBytes = photo.getBytes();
				tripphotoSvc.determineFileType(photoBytes);
				// 建立並設置 TripphotoVO
				TripphotoVO tripphotoVO = new TripphotoVO();
				tripphotoVO.setTrip_id(tripId);
				tripphotoVO.setPhoto(photoBytes);
				tripphotoVO.setPhoto_type(photoType);

				// 新增照片
				tripphotoSvc.addTripphoto(tripphotoVO);
				// 回傳成功訊息
				return ResponseEntity.ok()
						.body(Map.of("status", "success", "message", "新增成功！", "photoSize", photoBytes.length));
			} else {
				return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "未接收到照片檔案"));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "error", "message", e.getMessage()));
		}
	}

	// 修改
	@PutMapping("/updateTripphoto")
	public ResponseEntity<?> updateTripphoto(@RequestParam("trip_photo_id") Integer tripPhotoId,
			@RequestParam("trip_id") Integer tripId, @RequestParam("photo_type") Integer photoType,
			@RequestParam("photo") MultipartFile photo) {

		try {
			// 檢查檔案大小
			if (photo.getSize() > 20 * 1024 * 1024) { // 20MB 限制
				return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "檔案大小不能超過 5MB"));
			}

			// 檢查檔案類型
			String contentType = photo.getContentType();
			if (contentType == null || !contentType.startsWith("image/")) {
				return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "只能上傳圖片檔案"));
			}

			if (!photo.isEmpty()) {
				// 轉換為 byte array-->上傳效率較好
				byte[] photoBytes = photo.getBytes();
				tripphotoSvc.determineFileType(photoBytes);

				// 取得原有照片資料
				TripphotoVO tripphotoVO = tripphotoSvc.getTripphotoById(tripPhotoId);
				if (tripphotoVO == null) {
					return ResponseEntity.notFound().build();
				}

				// 更新資料
				tripphotoVO.setTrip_id(tripId);
				tripphotoVO.setPhoto(photoBytes);
				tripphotoVO.setPhoto_type(photoType);

				tripphotoSvc.updateTripphoto(tripphotoVO);

				return ResponseEntity.ok().body(Map.of("status", "success", "message", "修改成功！", "photoSize",
						photoBytes.length, "contentType", contentType));
			} else {
				return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "未接收到照片檔案"));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "error", "message", e.getMessage()));
		}
	}

	@DeleteMapping("/deleteTripphoto/{tripPhotoId}")
	public ResponseEntity<?> deleteTripphoto(@PathVariable Integer tripPhotoId) {
		try {
			// 檢查照片是否存在
			TripphotoVO tripphotoVO = tripphotoSvc.getTripphotoById(tripPhotoId);
			if (tripphotoVO == null) {
				 return ((BodyBuilder) ResponseEntity.notFound())
					        .body(Map.ofEntries(
					            Map.entry("status", "error"),
					            Map.entry("message", "找不到指定的照片")
					        ));
			}

			// 執行刪除
			tripphotoSvc.deleteTripphoto(tripPhotoId);

			return ResponseEntity.ok().body(Map.of("status", "success", "message", "刪除成功！"));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "error", "message", e.getMessage()));
		}
	}

	// 拿到某行程中的所有（封面／內文）照片
	@GetMapping("/getTripPhotosByType")
	public ResponseEntity<?> getTripPhotosByType(@RequestParam("trip_id") Integer tripId,
			@RequestParam("photo_type") Integer photoType) {

		try {
			// 獲取照片列表
			List<Map<String, Object>> tripPhotoList = tripphotoSvc.getOneTripPhotoByTypeAsBase64(tripId, photoType);

			// 檢查是否有找到照片
			if (tripPhotoList.isEmpty()) {
				return ResponseEntity.ok()
						.body(Map.of("status", "success", "message", "沒有找到相關照片", "data", new ArrayList<>()));
			}

			// 直接回傳照片列表
			return ResponseEntity.ok().body(Map.of("status", "success", "message", "取得照片成功", "data", tripPhotoList));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "error", "message", e.getMessage()));
		}
	}

}
