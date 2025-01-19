package chilltrip.trippage.editor.service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.rowset.serial.SerialException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import chilltrip.location.model.LocationVO;
import chilltrip.location.repository.LocationRepository;
import chilltrip.sub_trip.model.SubtripVO;
import chilltrip.sub_trip.repository.SubTripRepository;
import chilltrip.trip.model.TripVO;
import chilltrip.trip.repository.TripRepository;
import chilltrip.tripactype.model.TripactypeVO;
import chilltrip.tripactype.repository.ItineraryActivityTypeRepository;
import chilltrip.tripactyperela.model.TripactyperelaVO;
import chilltrip.tripactyperela.repository.ItineraryActivityTypeRelationshipRepository;
import chilltrip.triparea.model.TripAreaVO;
import chilltrip.triparea.repository.ItineraryAreaRepository;
import chilltrip.triplocationrelation.model.TriplocationrelationVO;
import chilltrip.triplocationrelation.repository.TripLocationRelationRepository;
import chilltrip.trippage.editor.dto.DaySchedule;
import chilltrip.trippage.editor.dto.LocationDetails;
import chilltrip.trippage.editor.dto.TripCreateRequest;
import chilltrip.tripphoto.model.TripphotoVO;
import chilltrip.tripphoto.repository.TripPhotoRepository;

@Service
@Transactional
public class TripEditorService {

	@Autowired
	private TripRepository tripRepository;
	@Autowired
	private LocationRepository locationRepository;
	@Autowired
	private SubTripRepository subTripRepository;
	@Autowired
	private TripPhotoRepository tripPhotoRepository;
	@Autowired
	private ItineraryAreaRepository itineraryAreaRepository;
	@Autowired
	private ItineraryActivityTypeRepository activityTypeRepository;
	@Autowired
	private TripLocationRelationRepository tripLocationRelationRepository;
	@Autowired
	private ItineraryActivityTypeRelationshipRepository activityTypeRelationshipRepository;

	public Integer createTrip(TripCreateRequest request) {
		// 1. 處理所有Location
		Map<String, Integer> locationIds = processLocations(request);

		// 2. 創建Trip主記錄
		TripVO trip = new TripVO();
		trip.setMemberId(request.getMemberId());
		trip.setArticle_title(request.getArticleTitle());
		trip.setTrip_abstract(request.getAbstractContent());
		trip.setStatus(request.getStatus());
		trip.setCreate_time(new Timestamp(System.currentTimeMillis()));
		trip.setCollections(0);
		trip.setOverall_score(0);
		trip.setOverall_scored_people(0);
		trip.setLocation_number(calculateTotalSpots(request.getDaySchedules()));
		trip.setVisitors_number(0);
		trip.setLikes(0);

		trip = tripRepository.save(trip);

		// 3. 處理每一天的行程
		createDaySchedules(trip.getTrip_id(), request.getDaySchedules(), locationIds);

		// 4. 處理地區關係
		createAreaRelation(trip.getTrip_id(), request.getRegion());

		// 5. 處理活動類型
		createActivityTypeRelation(trip.getTrip_id(), request.getActivityType());

		// 6. 處理封面照片
		if (request.getCoverPhoto() != null) {
			createCoverPhoto(trip.getTrip_id(), request.getCoverPhoto());
		}

		return trip.getTrip_id();
	}

	private Map<String, Integer> processLocations(TripCreateRequest request) {
		Map<String, Integer> locationIds = new HashMap<>();

		for (DaySchedule day : request.getDaySchedules()) {
			for (LocationDetails spot : day.getSpots()) {
				LocationVO location = locationRepository.findByGooglePlaceId(spot.getGooglePlaceId()).orElseGet(() -> {
					LocationVO newLocation = new LocationVO();
					newLocation.setGooglePlaceId(spot.getGooglePlaceId());
					newLocation.setLocation_name(spot.getName());
					newLocation.setAddress(spot.getAddress());
					newLocation.setLatitude(spot.getLatitude());
					newLocation.setLongitude(spot.getLongitude());
					newLocation.setScore(0.0f);
					newLocation.setRatingCount(0);
					newLocation.setComments_number(0);
					return locationRepository.save(newLocation);
				});

				locationIds.put(spot.getGooglePlaceId(), location.getLocationId());
			}
		}

		return locationIds;
	}

	private void createDaySchedules(Integer tripId, List<DaySchedule> daySchedules, Map<String, Integer> locationIds) {
		for (DaySchedule day : daySchedules) {
			// 創建子行程
			SubtripVO subTrip = new SubtripVO();
			subTrip.setTripid(tripId);
			subTrip.setIndex(day.getIndex());
			try {
				subTrip.setContent(day.getContent());
			} catch (SerialException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			subTrip = subTripRepository.save(subTrip);

			// 創建景點關係
			for (int i = 0; i < day.getSpots().size(); i++) {
				LocationDetails spot = day.getSpots().get(i);
				TriplocationrelationVO relation = new TriplocationrelationVO();
				relation.setSub_trip_id(subTrip.getSubtripid());
				relation.setLocation_id(locationIds.get(spot.getGooglePlaceId()));
				relation.setIndex(i + 1);
				relation.setTime_start(Timestamp.valueOf(spot.getStartTime()));
				relation.setTime_end(Timestamp.valueOf(spot.getEndTime()));

				tripLocationRelationRepository.save(relation);
			}
		}
	}

	private void createAreaRelation(Integer tripId, String region) {
		// 先獲取 TripVO 物件
		TripVO trip = tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("找不到對應的行程，ID: " + tripId));

		// 建立 TripAreaVO
		TripAreaVO area = new TripAreaVO();
		area.setTripid(trip); // 設置關聯物件
		area.setRegioncontent(region);

		itineraryAreaRepository.save(area);
	}

	private void createActivityTypeRelation(Integer tripId, String activityType) {
		// 先找到 trip 物件
		TripVO trip = tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("找不到對應的行程，ID: " + tripId));

		// 找到對應的活動類型物件
		TripactypeVO type = activityTypeRepository.findByEventcontent(activityType) // 注意方法名要對應 VO 的命名
				.orElseThrow(() -> new RuntimeException("找不到對應的活動類型：" + activityType));

		// 建立關聯
		TripactyperelaVO relation = new TripactyperelaVO();
		relation.setTripid(trip); // 設置 trip 關聯
		relation.setEventtypeid(type); // 設置 activity type 關聯

		activityTypeRelationshipRepository.save(relation);
	}

	private void createCoverPhoto(Integer tripId, byte[] photoData) {
		if (photoData != null) {
			TripphotoVO photo = new TripphotoVO();
			// 設置關聯
			TripVO trip = tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("找不到對應的行程：" + tripId));
			photo.setTripVO(trip);
			// 設置其他屬性
			photo.setPhoto(photoData);
			photo.setPhoto_type(0); // 0 表示封面照片

			tripPhotoRepository.save(photo);
		}
	}

	private int calculateTotalSpots(List<DaySchedule> daySchedules) {
		return daySchedules.stream().mapToInt(day -> day.getSpots().size()).sum();
	}
}
