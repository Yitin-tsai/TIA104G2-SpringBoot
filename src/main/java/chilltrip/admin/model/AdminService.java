package chilltrip.admin.model;

import java.util.List;

import org.logicalcobwebs.proxool.admin.Admin;
import org.springframework.stereotype.Service;

import chilltrip.member.model.MemberVO;

@Service("adminService")
public class AdminService {

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
		if(dao.getByAccount(account)==null) {
			return false;
		}else
		return account.equals(dao.getByAccount(account).getAdminaccount());
	}

	public boolean checkPhone(String phone) {
		if(dao.getByPhone(phone) == null) {
			return false;
		}else
		return phone.equals(dao.getByPhone(phone).getPhone());
	}

	public AdminVO login(String account, String password) throws LoginException {
	    AdminVO admin = dao.getByAccount(account); // 查詢帳號
	    if (admin == null) {
	        throw new LoginException("ACCOUNT_NOT_FOUND"); // 如果帳號不存在，抛出帳號錯誤
	    }
	    // 如果帳號存在，再比對密碼
	    if (admin.getAdminpassword() == null || !admin.getAdminpassword().equals(password)) {
	        throw new LoginException("INVALID_PASSWORD"); // 密碼錯誤
	    }
	    return admin; // 登入成功，回傳 admin
	}


	public class LoginException extends Exception {
		private final String errorType;

		public LoginException(String errorType) {
			this.errorType = errorType;
		}

		public String getErrorType() {
			return errorType;
		}
	}
}
