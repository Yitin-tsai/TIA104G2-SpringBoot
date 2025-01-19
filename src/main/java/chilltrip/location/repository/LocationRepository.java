package chilltrip.location.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import chilltrip.location.model.LocationVO;

@Repository
public interface LocationRepository extends JpaRepository<LocationVO, Integer> {
    Optional<LocationVO> findByGooglePlaceId(String googlePlaceId);
    
    
    //載入某個會員的某個景點收藏夾中的景點清單
    @Query(value = """
            SELECT 
                location.location_id,
                location.google_place_id,
                location.location_name,
                location.address,
                location.latitude,
                location.longitude,
                location.score,
                location.rating_count,
                location.comments_number,
                location.create_time,
                trip_location_relation.trip_location_relation_id,
                sub_trip.sub_trip_id,
                trip.trip_id,
                COALESCE(AVG(location_comment.score), 0) as average_comment_score,
                COUNT(DISTINCT location_comment.location_comment_id) as total_comment_count
            FROM trip
            JOIN sub_trip ON trip.trip_id = sub_trip.trip_id
            JOIN trip_location_relation ON sub_trip.sub_trip_id = trip_location_relation.sub_trip_id
            JOIN location ON trip_location_relation.location_id = location.location_id
            LEFT JOIN location_comment ON location.location_id = location_comment.location_id
            WHERE trip.member_id = :memberId 
            AND trip.trip_id = :tripId
            AND trip.status = 0
            GROUP BY 
                location.location_id,
                location.google_place_id,
                location.location_name,
                location.address,
                location.latitude,
                location.longitude,
                location.score,
                location.rating_count,
                location.comments_number,
                location.create_time,
                trip_location_relation.trip_location_relation_id,
                sub_trip.sub_trip_id,
                trip.trip_id,
                sub_trip.index
            ORDER BY sub_trip.index ASC
            """, 
            countQuery = """
                SELECT COUNT(DISTINCT location.location_id) 
                FROM trip
                JOIN sub_trip ON trip.trip_id = sub_trip.trip_id
                JOIN trip_location_relation ON sub_trip.sub_trip_id = trip_location_relation.sub_trip_id
                JOIN location ON trip_location_relation.location_id = location.location_id
                WHERE trip.member_id = :memberId 
                AND trip.trip_id = :tripId
                AND trip.status = 0
            """, 
            nativeQuery = true)
        Page<Object[]> findLocationDetailsByMemberIdAndTripId(
            @Param("memberId") Integer memberId, 
            @Param("tripId") Integer tripId,
            Pageable pageable
        );
}

