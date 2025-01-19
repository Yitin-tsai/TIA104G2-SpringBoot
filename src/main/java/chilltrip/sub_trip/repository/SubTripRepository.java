package chilltrip.sub_trip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import chilltrip.sub_trip.model.SubtripVO;

@Repository
public interface SubTripRepository extends JpaRepository<SubtripVO, Integer> {
	 // 方法一：使用 @Query
    @Query("SELECT s FROM SubtripVO s WHERE s.tripVO.trip_id = :tripId ORDER BY s.index ASC")
    List<SubtripVO> findByTripIdOrderByIndexAsc(@Param("tripId") Integer tripId);
}
