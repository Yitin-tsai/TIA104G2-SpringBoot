package chilltrip.tripactyperela.model;

import java.util.List;
import java.util.Map;

public interface TripactyperelaDAO_interface {

	
	public void insert(TripactyperelaVO tripactyperelaVO);
	public void update(TripactyperelaVO tripactyperelaVO);
	public void delete(Integer eventtyperelaid);
	public List<TripactyperelaVO> getAll();
//	public List<TripactyperelaVO> getByCompositeQuery(Map<String, String> map);
//	public List<TripactyperelaVO> getAll(int currentPage);
	public List<TripactyperelaVO> getByeventtypeid(Integer eventtypeid);
	
}
