package chilltrip.announce.model;

import static chilltrip.util.Constants.PAGE_MAX_RESULT;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chilltrip.admin.model.AdminDAOImplJDBC;
import chilltrip.admin.model.AdminVO;

@Service
public class AnnounceService {

	private final AnnounceDAOimpl dao;

	@Autowired
	public AnnounceService(AnnounceDAOimpl dao) {
		this.dao = dao;
	}
	
	public AnnounceVO getById(Integer announceid) {
		return dao.getOne(announceid);
		
	}
	

	public AnnounceVO addannounce(AnnounceVO announceVO) {
		announceVO.setCoverphotoFromBase64();
		dao.insert(announceVO);
		return announceVO;
	}

	public void updateannounce(AnnounceVO announceVO) {
		announceVO.setCoverphotoFromBase64();
		dao.update(announceVO);
	}

	public boolean deleteannounce(Integer id) {

		if (dao.delete(id)) {
			return true;
		} else {
			return false;
		}
	}

	public List<AnnounceVO> getAnnounceByAdminid(Integer adminid) {

		List<AnnounceVO> announceList = dao.getByadminid(adminid);
		for (AnnounceVO announce : announceList) {
			if (announce.getCoverphoto() != null) {
				String photo = announce.getCoverphotoAsBase64();
				announce.setCoverphotoBase64(photo);
			}
		}
		return announceList;
	}

	public List<AnnounceVO> getAllAnnounce(int currentPage) {
		List<AnnounceVO> announceList = dao.getAll(currentPage);
		for (AnnounceVO announce : announceList) {
			if (announce.getCoverphoto() != null) {
				String photo = announce.getCoverphotoAsBase64();
				announce.setCoverphotoBase64(photo);
			}
		}
		return announceList;
	}
	public List<AnnounceVO> getAllAnnounce() {
		List<AnnounceVO> announceList = dao.getAll();
		for (AnnounceVO announce : announceList) {
			if (announce.getCoverphoto() != null) {
				String photo = announce.getCoverphotoAsBase64();
				announce.setCoverphotoBase64(photo);
			}
		}
		return announceList;
	}

	public int getPageTotal() {
		long total = dao.getTotal();
		int pageQty = (int) (total % PAGE_MAX_RESULT == 0 ? (total / PAGE_MAX_RESULT) : (total / PAGE_MAX_RESULT + 1));
		return pageQty;
	}

	public List<AnnounceVO> getAnnounceByCompositeQuery(Map<String, String> map) {
		Map<String, String> query = new HashMap<>();
		Set<Map.Entry<String, String>> entry = map.entrySet();

		for (Map.Entry<String, String> row : entry) {
			String key = row.getKey();
			String value = row.getValue();
			if (value == null || value.isEmpty()) {
				continue;
			}
			query.put(key, value);
		}
		System.out.println(query);

		List<AnnounceVO> announceList = dao.getByCompositeQuery(query);
		for (AnnounceVO announce : announceList) {
			if (announce.getCoverphoto() != null) {
				String photo = announce.getCoverphotoAsBase64();
				announce.setCoverphotoBase64(photo);
			}
		}
		return announceList;

	}

}
