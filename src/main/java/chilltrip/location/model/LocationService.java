package chilltrip.location.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chilltrip.location.dao.LocationDAO;
import chilltrip.location.repository.LocationRepository;

@Service
public class LocationService {

	private LocationDAO dao;
	private LocationRepository locationRepository;

	@Autowired
	public LocationService(LocationDAO dao, LocationRepository locationRepository) {
		this.dao = dao;
		this.locationRepository = locationRepository;
	}
	
	@Transactional
	public LocationVO addlocationAndGetPk(LocationVO locationVO) {
	    Integer newId = dao.insertAndGetPk(locationVO);
	    if (newId == null) {
	        throw new RuntimeException("Failed to generate new location ID");
	    }
	    locationVO.setLocationId(newId);
	    return locationVO;
	}

	public LocationVO addlocation(LocationVO locationVO) {

		dao.insert(locationVO);
		return locationVO;
	}

	public LocationVO updatelocation(LocationVO locationVO) {

		dao.update(locationVO);
		return locationVO;
	}

	public void deletelocation(Integer locationid) {
		dao.delete(locationid);
	}

	public List<LocationVO> getAllLocation() {
		return dao.getAll();

	}

	public LocationVO getLocationById(Integer locationid) {
		return dao.getById(locationid);

	}

	public List<Map<String, Object>> getLocationByName(String locationname) {
		return dao.getLocationByName(locationname);

	}

	public LocationVO findByGooglePlaceId(String googlePlaceId) {
		return dao.findByGooglePlaceId(googlePlaceId);
	}

	public Page<Map<String, Object>> getLocationDetailsByTripAndMember(Integer memberId, Integer tripId, int page,
			int size) {

		try {
			System.out.println("Service - 開始查詢");
			System.out.println(
					"Service - 參數: memberId=" + memberId + ", tripId=" + tripId + ", page=" + page + ", size=" + size);

			Pageable pageable = PageRequest.of(page, size);
			Page<Object[]> results = locationRepository.findLocationDetailsByMemberIdAndTripId(memberId, tripId,
					pageable);

			System.out.println("Service - 原始查詢結果: " + (results != null ? "不為 null" : "為 null"));
			if (results != null) {
				System.out.println("Service - 結果數量: " + results.getContent().size());
				System.out.println("Service - 第一筆資料: "
						+ (results.getContent().size() > 0 ? Arrays.toString(results.getContent().get(0)) : "無資料"));
			}

			return results.map(row -> {
				try {
					Map<String, Object> spotData = new HashMap<>();
					System.out.println("Service - 正在轉換資料列: " + Arrays.toString(row));
					spotData.put("id", ((Integer) row[0]));
					spotData.put("googlePlaceId", (String) row[1]);
					spotData.put("name", (String) row[2]);
					spotData.put("address", (String) row[3]);
					spotData.put("latitude", ((BigDecimal) row[4]).doubleValue());
					spotData.put("longitude", ((BigDecimal) row[5]).doubleValue());
					spotData.put("score", (Float) row[6]);
					spotData.put("ratingCount", (Integer) row[7]);
					spotData.put("commentsNumber", (Integer) row[8]);
					spotData.put("createTime", ((Timestamp) row[9]).toLocalDateTime());
					spotData.put("tripLocationRelationId", (Integer) row[10]);
					spotData.put("subTripId", (Integer) row[11]);
					spotData.put("tripId", (Integer) row[12]);
					spotData.put("rating", ((BigDecimal) row[13]).doubleValue());
					 System.out.println("reviewCount actual type: " + (row[14] != null ? row[14].getClass().getName() : "null"));
		             System.out.println("reviewCount value: " + row[14]);
		             
		          // 使用更安全的轉換方式
		                Object reviewCount = row[14];
		                if (reviewCount instanceof BigInteger) {
		                    spotData.put("reviewCount", ((BigInteger) reviewCount).longValue());
		                } else if (reviewCount instanceof Long) {
		                    spotData.put("reviewCount", (Long) reviewCount);
		                } else if (reviewCount instanceof Integer) {
		                    spotData.put("reviewCount", ((Integer) reviewCount).longValue());
		                } else {
		                    // 如果都不是上述型別，將值轉為字串後再轉為 Long
		                    spotData.put("reviewCount", Long.valueOf(reviewCount.toString()));
		                }
					return spotData;
				} catch (Exception e) {
					System.err.println("Service - 資料轉換失敗: " + e.getMessage());
					e.printStackTrace();
					throw e;
				}
			});
		} catch (Exception e) {
			System.err.println("Service - 查詢過程發生錯誤: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

}
