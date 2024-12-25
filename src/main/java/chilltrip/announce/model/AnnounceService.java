package chilltrip.announce.model;

import static chilltrip.util.Constants.PAGE_MAX_RESULT;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chilltrip.admin.model.AdminDAOImplJDBC;
import chilltrip.admin.model.AdminVO;



public class AnnounceService {
 
	private AnnounceDAO dao;
	 
	public AnnounceService() {
		 dao = new AnnounceDAOimpl();
	}
	

	public AnnounceVO addannounce(AnnounceVO announceVO) {
		
		dao.insert(announceVO);
		return announceVO;
	}


	public AnnounceVO updateannounce(AnnounceVO announceVO) {
	
		dao.update(announceVO);
		return announceVO;
	}


	public void deleteannounce(Integer id) {
  
		dao.delete(id);	
	}

	
	public List<AnnounceVO> getAnnounceByAdminid(Integer adminid) {
		
		
		return dao.getByadminid(adminid);
	}


	public List<AnnounceVO> getAllAnnounce(int currentPage) {
		
		return dao.getAll();
	}


	public int getPageTotal() {
		long total = dao.getTotal();
		int pageQty = (int)(total % PAGE_MAX_RESULT == 0 ? (total / PAGE_MAX_RESULT) : (total / PAGE_MAX_RESULT + 1));
		return pageQty;
	}


	public List<AnnounceVO> getAnnounceByCompositeQuery(Map<String, String[]> map) {
		Map<String, String> query = new HashMap<>();
		Set<Map.Entry<String, String[]>> entry =map.entrySet();
		
		for(Map.Entry<String, String[]> row : entry) {
			String key = row.getKey();
			
			if("action".equals(key)) {
				continue;
			}
			
			String value = row.getValue()[0];
			if(value == null|| value.isEmpty()) {
				continue;
			}
			query.put(key,value);
		}
		System.out.println(query);
		
		return dao.getByCompositeQuery(query);
		
	}

}
