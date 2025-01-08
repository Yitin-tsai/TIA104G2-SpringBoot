package chilltrip.trip.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import chilltrip.trip.model.TripVO;

@Repository
//Repository (DAO) 層
public interface TripRepository extends JpaRepository<TripVO, Integer> {
	// 基本的分頁查詢已經由 JpaRepository 提供
	// 可以直接使用 findAll(Pageable pageable)

	
	// 基本分頁查詢(from trip開始查）
	@Query(value = """
			SELECT
			    trip.trip_id,
			    trip.article_title,
			    trip.abstract,
			    trip.visitors_number,
			    trip.likes,
			    CASE
			        WHEN trip.overall_scored_people > 0
			        THEN CAST(trip.overall_score AS DECIMAL) / CAST(trip.overall_scored_people AS DECIMAL)
			        ELSE 0.0
			    END AS rating,
			    member.nick_name,
			    COALESCE(trip_photo.photo, 'https://via.placeholder.com/350x200?text=NoImage') AS image
			FROM trip
			LEFT JOIN member ON trip.member_id = member.member_id
			LEFT JOIN trip_photo ON trip.trip_id = trip_photo.trip_id AND trip_photo.photo_type = 0
			ORDER BY trip.trip_id DESC
			""", countQuery = "SELECT COUNT(*) FROM trip", nativeQuery = true)
	Page<Object[]> findAllTripsWithDetails(Pageable pageable);

//	@Query("""
//	        SELECT new map(
//	            TripVO.trip_id as id,
//	            TripVO.article_title as title,
//	            TripVO.trip_abstract as description,
//	            TripVO.visitors_number as views,
//	            TripVO.likes as likes,
//	            CASE 
//	                WHEN TripVO.overall_scored_people > 0 
//	                THEN CAST(TripVO.overall_score AS DOUBLE) / CAST(TripVO.overall_scored_people AS DOUBLE)
//	                ELSE 0.0 
//	            END as rating,
//	            MemberVO.nick_name as author,
//	            COALESCE(CAST(TripphotoVO.photo AS STRING), 'https://via.placeholder.com/350x200?text=NoImage') as image
//	        )
//	        FROM TripVO trip
//	        LEFT JOIN MemberVO member 
//	            ON TripVO.member_id = MemberVO.member_id
//	        LEFT JOIN TripphotoVO trip_photo 
//	            ON TripVO.trip_id = TripphotoVO.trip_id 
//	            AND TripphotoVO.photo_type = 0
//	    """)
//	@Query("SELECT new map(" + "trip.trip_id as id, " + "trip.article_title as title, "
//			+ "trip.trip_abstract as description, " + "trip.visitors_number as views, " + "trip.likes as likes, "
//			+ "trip.overall_score as totalScore, " + "trip.overall_scored_people as totalScored, "
//			+ "member.nickName as author " + "trip_photo.photo as image ) " + "FROM trip "
//			+ "LEFT JOIN member ON trip.memberId = member.memberId"
//			+ "LEFT JOIN trip_photo ON trip.trip_id AND trip_photo.photo_type=0 ")

	// 取得行程的標籤
//	 @Query("""
//		        SELECT DISTINCT new map(
//		            COALESCE(TripAreaVO.region_contect, '') as regionTag,
//		            COALESCE(TripactypeVO.event_contect, '') as eventTag
//		        )
//		        FROM TripVO trip
//		        LEFT JOIN TripAreaVO itinerary_area 
//		            ON TripVO.trip_id = TripAreaVO.trip_id
//		        LEFT JOIN TripactyperelaVO relationship 
//		            ON TripVO.trip_id = TripactyperelaVO.trip_id
//		        LEFT JOIN TripactypeVO itinerary_activity_type 
//		            ON TripactyperelaVO.event_type_id = TripactypeVO.event_trip_id
//		        WHERE TripVO.trip_id = :tripId
//		    """)
//	@Query("SELECT new map(" +
//			"itinerary_area.regioncontent as regin, " + 
//			"itinerary_activity_type.eventcontent as eventType) " + 
//			"FROM trip " +
//			"LEFT JOIN itinerary_area ON trip.trip_id = itinerary_area.tripid " +
//			"LEFT JOIN itinerary_activity_type_relationship ON trip.trip_id = itinerary_activity_type_relationship.tripid " +
//			"LEFT JOIN itinerary_activity_type ON itinerary_activity_type_relationship.eventtypeid = itinerary_activity_type.eventtypeid " +
//			"WHERE trip.trip_id=:tripId ")

	@Query(value = """
			SELECT DISTINCT
			    COALESCE(itinerary_area.region_content, '') AS region_tag,
			    COALESCE(itinerary_activity_type.event_content, '') AS event_tag
			FROM trip
			LEFT JOIN itinerary_area ON trip.trip_id = itinerary_area.trip_id
			LEFT JOIN itinerary_activity_type_relationship ON trip.trip_id = itinerary_activity_type_relationship.trip_id
			LEFT JOIN itinerary_activity_type ON itinerary_activity_type_relationship.event_type_id = itinerary_activity_type.event_type_id
			WHERE trip.trip_id = :tripId
			""", nativeQuery = true)
	List<Object[]> findTripTags(@Param("tripId") Integer tripId);
}
