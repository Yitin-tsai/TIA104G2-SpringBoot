package chilltrip.tripactype.model;

import java.util.List;


public interface TripactypeDAO_interface {
	
	public void insert(TripactypeVO tripactypeVO);
	public void update(TripactypeVO tripactypeVO);
	public void delete(Integer eventtypeid);
	public List<TripactypeVO> getAll();
	public TripactypeVO getByid(Integer eventtypeid);
	
}
