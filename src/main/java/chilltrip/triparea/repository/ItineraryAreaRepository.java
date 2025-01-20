package chilltrip.triparea.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import chilltrip.triparea.model.TripAreaVO;

@Repository
public interface ItineraryAreaRepository extends JpaRepository<TripAreaVO, Integer> {
    // 基本的 CRUD 操作已由 JpaRepository 提供
}
