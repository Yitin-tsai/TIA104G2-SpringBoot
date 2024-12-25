package chilltrip.tripcomment.model;

import java.util.*;

public interface TripCommentDAO_interface {
          public void insert(TripCommentVO tripCommentVO);
          public void update(TripCommentVO tripCommentVO);
          public void delete(Integer tripCommentId);
          public TripCommentVO findByPrimaryKey(Integer tripCommentId);
          public List<TripCommentVO> getAll();
}
