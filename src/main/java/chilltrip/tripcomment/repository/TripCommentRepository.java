package chilltrip.tripcomment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import chilltrip.tripcomment.model.TripCommentVO;
import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface TripCommentRepository extends JpaRepository<TripCommentVO, Integer> {
	 
	//文章評論區（一頁十個）
	@Query(value = """
	        SELECT 
	            trip_comment.trip_comment_id,
	            trip_comment.content,
	            trip_comment.score as rating,
	            trip_comment.photo,
	            trip_comment.create_time,
	            member.nick_name as user_name,
	            member.photo as user_avatar,
	            member.member_id as user_id
	        FROM trip_comment 
	        LEFT JOIN member ON trip_comment.member_id = member.member_id
	        WHERE trip_comment.trip_id = :tripId
	        ORDER BY trip_comment.create_time DESC
	        """, 
	        countQuery = """
	            SELECT COUNT(*) 
	            FROM trip_comment 
	            WHERE trip_comment.trip_id = :tripId
	        """,
	        nativeQuery = true)
	    @Transactional(readOnly = true)
	    Page<Object[]> findCommentsByTripIdWithPagination(
	        @Param("tripId") Integer tripId, 
	        Pageable pageable
	    );
		
}