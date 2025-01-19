package chilltrip.tripactyperela.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import chilltrip.tripactyperela.model.TripactyperelaVO;

@Repository
public interface ItineraryActivityTypeRelationshipRepository extends JpaRepository<TripactyperelaVO, Integer> {
    // 基本的 CRUD 操作已由 JpaRepository 提供
}
