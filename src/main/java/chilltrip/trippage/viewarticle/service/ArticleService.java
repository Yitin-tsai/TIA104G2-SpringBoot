package chilltrip.trippage.viewarticle.service;

import java.math.BigDecimal; // 用於經緯度
import java.sql.Timestamp; // 處理時間戳
import java.text.SimpleDateFormat; // 格式化時間
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator; // 用於排序
import java.util.HashMap; // 存儲映射
import java.util.List; // 處理列表
import java.util.Map; // 映射接口
import java.util.stream.Collectors; // Stream API
import java.util.Objects; // null 檢查
import org.slf4j.Logger; // 日誌接口
import org.slf4j.LoggerFactory; // 日誌工廠
import org.springframework.beans.factory.annotation.Autowired; // 依賴注入
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service; // 服務層注解
import org.springframework.transaction.annotation.Transactional;

// 你的自定義類
import chilltrip.trip.repository.TripRepository;
import chilltrip.tripcomment.repository.TripCommentRepository;
import chilltrip.trippage.viewarticle.dto.TripDayDTO;
import chilltrip.trippage.viewarticle.dto.TripDaysResponseDTO;
import chilltrip.trippage.viewarticle.dto.TripSpotDTO;

@Service
public class ArticleService {
	
	@Autowired
	private TripRepository tripRepository;
	
	@Autowired
    private TripCommentRepository commentRepository;

	private static final Logger log = LoggerFactory.getLogger(ArticleService.class);

	//===============================文章基礎訊息===========================
	
	public Map<String, Object> getBasicInfo(Integer tripId, Integer currentMemberId) {
		Map<String, Object> basicInfo = tripRepository.findBasicInfo(tripId);

		// 查詢標籤
		List<String> regionTags = tripRepository.findRegionContentsByTripId(tripId);
		List<String> activityTags = tripRepository.findEventContentsByTripId(tripId);

		// 查詢收藏狀態
		boolean isCollected = false;
		if (currentMemberId != null) {
			isCollected = tripRepository.findCollectionStatus(tripId, currentMemberId);
		}

		// 整理資料結構
		Map<String, Object> stats = new HashMap<>();
		stats.put("viewCount", basicInfo.get("viewCount"));
		stats.put("favoriteCount", basicInfo.get("favoriteCount"));
		stats.put("rating", basicInfo.get("rating"));
		stats.put("isCollected", isCollected);

		Map<String, Object> tags = new HashMap<>();
		tags.put("areas", regionTags);
		tags.put("activities", activityTags);

		// 重組回傳資料結構
		Map<String, Object> result = new HashMap<>();
		result.put("tripId", basicInfo.get("tripId"));
		result.put("title", basicInfo.get("title"));
		result.put("authorId", basicInfo.get("authorId"));
		result.put("authorName", basicInfo.get("authorName"));
		result.put("publishTime", basicInfo.get("publishTime"));
		result.put("tripAbstract", basicInfo.get("tripAbstract"));
		result.put("coverPhoto", basicInfo.get("coverPhoto"));
		result.put("stats", stats);
		result.put("tags", tags);

		return result;
	}

	//===============================文章景點、行程區===========================
	
	public TripDaysResponseDTO getTripDaysInfo(Integer tripId) {
		// 1. 先記錄原始資料
		List<Object[]> tripDaysData = tripRepository.findTripDaysDetailsByTripId(tripId);
		log.debug("Raw data size: {}", tripDaysData.size());

		if (tripDaysData.isEmpty()) {
			log.warn("No data found for tripId: {}", tripId);
			return new TripDaysResponseDTO();
		}

		// 2. 檢查分組前的資料
		Map<Integer, List<Object[]>> dayGroups = tripDaysData.stream().collect(Collectors.groupingBy(row -> {
			Number dayNumber = (Number) row[0];
			log.debug("Day number from row: {}", dayNumber);
			return dayNumber.intValue();
		}));

		log.debug("Day groups size: {}", dayGroups.size());

		// 3. 檢查轉換過程
		List<TripDayDTO> days = dayGroups.entrySet().stream().map(entry -> {
			log.debug("Converting day number: {} with {} spots", entry.getKey(), entry.getValue().size());
			return convertToTripDayDTO(entry.getKey(), entry.getValue());
		}).sorted(Comparator.comparing(TripDayDTO::getDayNumber)).collect(Collectors.toList());

		log.debug("Final days list size: {}", days.size());

		TripDaysResponseDTO response = new TripDaysResponseDTO();
		response.setDays(days);
		return response;
	}

	// 確保 content 有被正確處理
	private TripDayDTO convertToTripDayDTO(Integer dayNumber, List<Object[]> dayData) {
		TripDayDTO dayDTO = new TripDayDTO();
		try {
			dayDTO.setDayNumber(dayNumber);

			// 直接取得文字內容
			Object contentObj = dayData.get(0)[1];
			if (contentObj != null) {
				dayDTO.setDayContent(contentObj.toString());
			} else {
				dayDTO.setDayContent("");
			}

			// 添加調試日誌
			log.debug("Converting content for day {}: {}", dayNumber, dayDTO.getDayContent());

			List<TripSpotDTO> spots = dayData.stream().map(row -> {
				TripSpotDTO spot = convertToTripSpotDTO(row);
				log.debug("Converted spot: {}", spot); // 添加調試日誌
				return spot;
			}).filter(Objects::nonNull).sorted(Comparator.comparing(TripSpotDTO::getSpotOrder))
					.collect(Collectors.toList());

			dayDTO.setSpots(spots);

		} catch (Exception e) {
			log.error("Error converting day data: " + e.getMessage(), e);
		}
		return dayDTO;
	}

	private TripSpotDTO convertToTripSpotDTO(Object[] row) {
		TripSpotDTO spotDTO = new TripSpotDTO();
		try {
			log.debug("Converting row: {}", Arrays.toString(row)); // 添加調試日誌

			spotDTO.setName(row[2] != null ? row[2].toString() : "");
			spotDTO.setAddress(row[3] != null ? row[3].toString() : "");
			spotDTO.setGooglePlaceId(row[4] != null ? row[4].toString() : "");

			// 經緯度處理
			spotDTO.setLatitude(row[5] != null ? new BigDecimal(row[5].toString()) : BigDecimal.ZERO);
			spotDTO.setLongitude(row[6] != null ? new BigDecimal(row[6].toString()) : BigDecimal.ZERO);

			// 時間處理
			spotDTO.setStartTime(row[7] != null ? formatTimestamp((Timestamp) row[7]) : "");
			spotDTO.setEndTime(row[8] != null ? formatTimestamp((Timestamp) row[8]) : "");

			spotDTO.setSpotOrder(row[9] != null ? Integer.parseInt(row[9].toString()) : 0);

			log.debug("Converted spot DTO: name={}, address={}", spotDTO.getName(), spotDTO.getAddress());

		} catch (Exception e) {
			log.error("Error converting spot data: " + e.getMessage(), e);
		}
		return spotDTO;
	}

	private String formatTimestamp(Timestamp timestamp) {
		if (timestamp == null)
			return "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			return sdf.format(timestamp);
		} catch (Exception e) {
			log.error("Error formatting timestamp: {}", e.getMessage());
			return "";
		}
	}
	
	//===============================留言區===========================
	
	@Transactional(readOnly = true)
    public Map<String, Object> getComments(Integer tripId, int page) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.debug("Getting comments for tripId: {} page: {}", tripId, page);
            PageRequest pageRequest = PageRequest.of(page, 10); 

            Page<Object[]> commentPage = commentRepository.findCommentsByTripIdWithPagination(
                tripId, pageRequest);
            
            log.debug("Found {} comments", commentPage.getTotalElements());

            List<Map<String, Object>> comments = new ArrayList<>();
            for (Object[] row : commentPage.getContent()) {
                Map<String, Object> comment = new HashMap<>();
                try {
                    comment.put("id", row[0]);
                    comment.put("content", row[1]);
                    comment.put("rating", row[2]);
                    comment.put("photos", row[3]);
                    
                    // 時間格式化
                    Timestamp createTime = (Timestamp)row[4];
                    if (createTime != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        comment.put("createdAt", sdf.format(createTime));
                    } else {
                        comment.put("createdAt", "");
                    }
                    
                    comment.put("userName", row[5]);
                    comment.put("userAvatar", row[6]);
                    comment.put("userId", row[7]);
                    
                    comments.add(comment);
                } catch (Exception e) {
                    log.error("Error processing comment row: {}", Arrays.toString(row), e);
                }
            }
            
            response.put("comments", comments);
            response.put("currentPage", commentPage.getNumber());
            response.put("totalPages", commentPage.getTotalPages());
            response.put("totalComments", commentPage.getTotalElements());
            
        } catch (Exception e) {
            log.error("Error getting comments", e);
            throw new RuntimeException("Failed to load comments: " + e.getMessage());
        }
        
        return response;
    }



}
