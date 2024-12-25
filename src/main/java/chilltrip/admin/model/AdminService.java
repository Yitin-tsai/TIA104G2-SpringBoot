package chilltrip.admin.model;

import java.util.List;

import org.springframework.stereotype.Service;

@Service("adminService")
public class AdminService  {

	private AdminDAO dao;

	public AdminService() {
		dao = new AdminDAOImplJDBC();
	}

	public AdminVO addAdmin(String email, String adminaccount, String adminpassword, String adminname, String phone,
			Integer status, String adminnickname) {
		AdminVO adminVO = new AdminVO();

		adminVO.setEmail(email);
		adminVO.setAdminaccount(adminaccount);
		adminVO.setAdminpassword(adminpassword);
		adminVO.setAdminname(adminname);
		adminVO.setPhone(phone);
		adminVO.setStatus(status);
		adminVO.setAdminnickname(adminnickname);
		dao.insert(adminVO);

		return adminVO;
	}

	
	public AdminVO updateAdmin(Integer adminid, String email, String adminaccount, String adminpassword,
			String adminname, String phone, Integer status, String adminnickname) {
		AdminVO adminVO = new AdminVO();
		
		adminVO.setAdminid(adminid);
		adminVO.setEmail(email);
		adminVO.setAdminaccount(adminaccount);
		adminVO.setAdminpassword(adminpassword);
		adminVO.setAdminname(adminname);
		adminVO.setPhone(phone);
		adminVO.setStatus(status);
		adminVO.setAdminnickname(adminnickname);
		dao.update(adminVO);

		return adminVO;
	}


	public void deleteAdmin(Integer adminid) {
		dao.delete(adminid);

	}


	public List<AdminVO> getAll() {
		return dao.getAll();
	}

	
	public AdminVO getOneAdmin(Integer adminid) {
		// TODO Auto-generated method stub
		return dao.getById(adminid);
	}

}
