package chilltrip.announce.model;

import java.util.List;
import java.util.Map;

public interface AnnounceDAO {
	
	public void insert(AnnounceVO annouceVO);
	public boolean update(AnnounceVO annouceVO);
	public boolean delete(Integer annouceid);
	public List<AnnounceVO> getAll();
	public List<AnnounceVO>  getByCompositeQuery(Map<String, String> map);
	public List<AnnounceVO> getAll(int currentPage);
	public List<AnnounceVO>  getByadminid(Integer adminid);
	long getTotal();
}
