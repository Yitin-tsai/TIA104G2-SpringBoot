package chilltrip.tripactype.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import chilltrip.tripactype.model.TripactypeVO;

@Repository
public interface ItineraryActivityTypeRepository extends JpaRepository<TripactypeVO, Integer> {
    
	// 尋找特定活動類型內容的紀錄
    Optional<TripactypeVO> findByEventcontent(String eventcontent);
}
