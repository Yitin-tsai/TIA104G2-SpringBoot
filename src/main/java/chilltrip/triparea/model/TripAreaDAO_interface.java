package chilltrip.triparea.model;

import java.util.List;

public interface TripAreaDAO_interface {
	public void insert(TripAreaVO tripAreaVO);
	public void update(TripAreaVO tripAreaVO);
	public void delete(Integer triplocationid);
	public TripAreaVO findByPrimaryKey(Integer triplocationid);
	public List<TripAreaVO> getAll();
}
