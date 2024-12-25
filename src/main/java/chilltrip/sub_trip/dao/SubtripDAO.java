package chilltrip.sub_trip.dao;

import java.util.List;
import java.util.Map;

import chilltrip.sub_trip.model.SubtripVO;

public interface SubtripDAO {
	
	public void insert(SubtripVO subtripVO);
	public void update(SubtripVO subtripVO);
	public void delete(Integer subtripid);
	public List<SubtripVO> getallsubtrip();
	public SubtripVO getBySubtripId(Integer subtrip);
	public List<Map<String, Object>> getByTripId(Integer tripid);
}
