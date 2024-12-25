package chilltrip.admin.model;

import java.util.List;

import chilltrip.announce.model.AnnounceVO;

public interface AdminDAO {

	public void insert(AdminVO adminVO);
	public void update(AdminVO adminVO);
	public void delete(Integer adminid);
	public List<AdminVO> getAll();
	public AdminVO getById(Integer adminid);
	
	
}
