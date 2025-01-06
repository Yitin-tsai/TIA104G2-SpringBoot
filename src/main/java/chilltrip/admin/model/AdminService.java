package chilltrip.admin.model;

import java.util.List;

import org.logicalcobwebs.proxool.admin.Admin;
import org.springframework.stereotype.Service;

import chilltrip.member.model.MemberVO;

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
	public AdminVO getOneAdminByPhone(String phone) {
		// TODO Auto-generated method stub
		return dao.getByPhone(phone);
	}
	
	public AdminVO getOneAdminByAccount(String account) {
		// TODO Auto-generated method stub
		return dao.getByAccount(account);
	}
	public boolean checkAccount(String account) {
		return account.equals(dao.getByAccount(account).getAdminaccount());
	}
	public boolean checkPhone(String phone) {
		return phone.equals(dao.getByPhone(phone).getPhone());
	}
	

}
