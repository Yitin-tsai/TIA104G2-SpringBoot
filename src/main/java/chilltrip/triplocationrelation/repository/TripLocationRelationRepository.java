package chilltrip.triplocationrelation.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import chilltrip.triplocationrelation.model.TriplocationrelationVO;

@Repository
public interface TripLocationRelationRepository extends JpaRepository<TriplocationrelationVO, Integer> {
    // 基本的 CRUD 操作已由 JpaRepository 提供
}
