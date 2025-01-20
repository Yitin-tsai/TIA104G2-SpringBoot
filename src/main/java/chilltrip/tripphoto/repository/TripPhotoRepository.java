package chilltrip.tripphoto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import chilltrip.tripphoto.model.TripphotoVO;

@Repository
public interface TripPhotoRepository extends JpaRepository<TripphotoVO, Integer> {
    // 基本的 CRUD 操作已由 JpaRepository 提供
}
